package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import data.BuildingType;
import data.PlayerColor;
import engine.action.*;
import engine.log.EngineLogger;
import engine.rules.*;
import engine.tilestack.VolcanoTileStack;
import map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;
import static java.util.stream.Collectors.toList;

class EngineImpl implements Engine {

    static final boolean DEBUG = true;

    private static final int TILES_PER_PLAYER = 12;

    private final EngineLogger logger;
    private final long seed;
    private final Random random;

    private final List<EngineObserver> observers;

    private final Gamemode gamemode;
    private final Island island;
    final ImmutableList<Player> players;
    private final VolcanoTileStack volcanoTileStack;

    EngineStatus status;
    int playerIndex;
    private PlayerTurn playerTurn;

    private final List<ActionSave> undoList;
    private final List<ActionSave> redoList;
    private final EngineActions actions;

    /**
     * Package-protected, voir la classe EngineBuilder
     */
    EngineImpl(EngineBuilder<?> builder) {
        this.logger = EngineLogger.create(builder.logLevel);
        this.seed = builder.seed;
        this.random = new Random(this.seed);

        this.observers = new ArrayList<>();

        this.gamemode = builder.gamemode;
        this.island = builder.island;
        this.players = builder.createPlayers(this);
        this.volcanoTileStack = builder.volcanoTileStackFactory.create(players.size() * TILES_PER_PLAYER, random);

        this.status = EngineStatus.PENDING_START;
        this.playerIndex = 0;

        this.undoList = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        this.redoList = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        this.actions = new EngineActions(this);
    }

    private EngineImpl(EngineImpl engine) {
        this.logger = engine.logger;
        this.seed = engine.random.nextLong();
        this.random = new Random(this.seed);

        this.observers = new ArrayList<>();

        this.gamemode = engine.gamemode;
        this.island = engine.island.copy();
        ImmutableList.Builder<Player> players = ImmutableList.builder();
        for (Player player : engine.getPlayers()) {
            players.add(player.copyWithDummyHandler());
        }
        this.players = players.build();
        this.volcanoTileStack = engine.volcanoTileStack.copyShuffled(random);

        this.status = engine.status instanceof EngineStatus.Running
                ? (engine.running()).copy()
                : engine.status;
        this.playerIndex = engine.playerIndex;
        this.playerTurn = PlayerHandler.dummyTurn();

        this.undoList = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        undoList.addAll(engine.undoList);
        this.redoList = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        redoList.addAll(engine.undoList);
        this.actions = new EngineActions(this, engine.actions);
    }

    @Override
    public EngineLogger logger() {
        return logger;
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public void registerObserver(EngineObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(EngineObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Gamemode getGamemode() {
        return gamemode;
    }

    @Override
    public Island getIsland() {
        return island;
    }

    @Override
    public VolcanoTileStack getVolcanoTileStack() {
        return volcanoTileStack;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void start() {
        logger.warning("[Engine] Starting with seed {0}", Long.toString(seed));

        this.status = new EngineStatus.Running();
        this.playerIndex = 0;
        observers.forEach(EngineObserver::onStart);

        volcanoTileStack.next();
        observers.forEach(EngineObserver::onTileStackChange);

        actions.updateAll();

        observers.forEach(EngineObserver::onTileStepStart);
        this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, EngineStatus.TurnStep.TILE);
    }

    @Override
    public Engine copyWithoutObservers() {
        return new EngineImpl(this);
    }

    @Override
    public void cancelLastStep() {
        cancelUntil(e -> true);
    }

    @Override
    public boolean canUndo() {
        return !(status.getTurn() == 0 || status.getTurn() == 1 && status.getStep() == EngineStatus.TurnStep.TILE);
    }

    @Override
    public synchronized void cancelUntil(Predicate<Engine> predicate) {
        checkState(status != EngineStatus.PENDING_START);

        playerTurn.cancel();
        boolean volcanoTileStackChanged = false;
        do {
            if (status.getStep() == EngineStatus.TurnStep.TILE) {
                volcanoTileStackChanged = true;
                volcanoTileStack.previous();
                observers.forEach(EngineObserver::onCancelTileStep);
            }
            else {
                observers.forEach(EngineObserver::onCancelBuildStep);
            }

            ActionSave save = undoList.remove(undoList.size() - 1);
            redoList.add(save.revert(this));
        } while (!predicate.test(this) && !status.isFirst());

        if (volcanoTileStackChanged) {
            observers.forEach(EngineObserver::onTileStackChange);
        }

        actions.updateAll();
        observers.forEach(status.getStep() == EngineStatus.TurnStep.TILE
                ? EngineObserver::onTileStepStart
                : EngineObserver::onBuildStepStart);
        this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, status.getStep());
    }

    @Override
    public boolean canRedo() {
        return redoList.size() > 0;
    }

    @Override
    public void redoUntil(Predicate<Engine> predicate) {
        checkState(status != EngineStatus.PENDING_START);

        playerTurn.cancel();
        boolean volcanoTileStackChanged = false;
        while (!redoList.isEmpty()) {
            if (status.getStep() == EngineStatus.TurnStep.TILE) {
                volcanoTileStackChanged = true;
                volcanoTileStack.next();
            }

            ActionSave save = redoList.remove(redoList.size() - 1);
            undoList.add(save.revert(this));

            if (predicate.test(this)) {
                break;
            }
        }

        if (volcanoTileStackChanged) {
            observers.forEach(EngineObserver::onTileStackChange);
        }

        actions.updateAll();
        observers.forEach(status.getStep() == EngineStatus.TurnStep.TILE
                ? EngineObserver::onTileStepStart
                : EngineObserver::onBuildStepStart);
        this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, status.getStep());
    }

    private void nextStep() {
        verify(status instanceof EngineStatus.Running);

        this.status = running().next();
        redoList.clear();
        if (running().getStep() == EngineStatus.TurnStep.TILE) {
            do { playerIndex++; } while (getCurrentPlayer().isEliminated());

            volcanoTileStack.next();
            observers.forEach(EngineObserver::onTileStackChange);
            if (checkTileStackEmpty()) {
                return;
            }

            actions.updateAll();

            observers.forEach(EngineObserver::onTileStepStart);
            this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, EngineStatus.TurnStep.TILE);
        }
        else {
            actions.updateAll();
            if (checkElimination()) {
                return;
            }

            observers.forEach(EngineObserver::onBuildStepStart);
        }
    }

    private boolean checkElimination() {
        if (!actions.placeBuildings.isEmpty()
                || !actions.expandVillages.isEmpty()) {
            return false;
        }

        Player eliminated = getCurrentPlayer();
        eliminated.setEliminated();
        observers.forEach(o -> o.onEliminated(eliminated));

        Predicate<Player> isEliminated = Player::isEliminated;
        List<Player> remainingPlayers = players.stream()
                .filter(isEliminated.negate())
                .collect(toList());
        verify(remainingPlayers.size() > 0);

        if (remainingPlayers.size() == 1) {
            this.status = (running()).finished(
                    EngineStatus.FinishReason.LAST_STANDING,
                    remainingPlayers);
            observers.forEach(o -> o.onWin(EngineStatus.FinishReason.LAST_STANDING, remainingPlayers));
        }
        else {
            nextStep();
        }

        return true;
    }

    private boolean checkTileStackEmpty() {
        if (!volcanoTileStack.isEmpty()) {
            return false;
        }

        List<Player> winners = winnersByScore();
        this.status = (running()).finished(
                EngineStatus.FinishReason.NO_MORE_TILES,
                winners);
        observers.forEach(o -> o.onWin(EngineStatus.FinishReason.NO_MORE_TILES, winners));
        return true;
    }

    private List<Player> playerWithMinimumBuildingOfType(Iterable<Player> candidates, BuildingType type) {
        List<Player> players = new ArrayList<>();
        int minCount = Integer.MAX_VALUE;
        for (Player player : candidates) {
            int count = player.getBuildingCount(type);
            if (count < minCount) {
                players = Lists.newArrayList(player);
                minCount = count;
            }
            else if (count == minCount) {
                players.add(player);
            }
        }

        return players;
    }

    private List<Player> winnersByScore() {
        List<Player> candidates = playerWithMinimumBuildingOfType(players, BuildingType.TEMPLE);
        if (candidates.size() > 1) {
            candidates = playerWithMinimumBuildingOfType(candidates, BuildingType.TOWER);
            if (candidates.size() > 1) {
                candidates = playerWithMinimumBuildingOfType(candidates, BuildingType.HUT);
            }
        }

        return candidates;
    }

    @Override
    public EngineStatus getStatus() {
        return status;
    }

    private EngineStatus.Running running() {
        return (EngineStatus.Running) status;
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(playerIndex % players.size());
    }

    @Override
    public List<SeaTileAction> getSeaTileActions() {
        checkState(status instanceof EngineStatus.Running, "Requesting actions while the game is not running");
        return actions.seaTiles;
    }

    @Override
    public List<VolcanoTileAction> getVolcanoTileActions() {
        checkState(status instanceof EngineStatus.Running, "Requesting actions while the game is not running");
        return actions.volcanosTiles;
    }

    @Override
    public List<PlaceBuildingAction> getPlaceBuildingActions() {
        checkState(status instanceof EngineStatus.Running, "Requesting actions while the game is not running");
        return actions.placeBuildings;
    }

    @Override
    public List<PlaceBuildingAction> getNewPlaceBuildingActions(TileAction action) {
        return actions.getNewPlaceBuildingActions(action);
    }

    @Override
    public List<ExpandVillageAction> getExpandVillageActions() {
        checkState(status instanceof EngineStatus.Running, "Requesting actions while the game is not running");
        return actions.expandVillages;
    }

    @Override
    public List<ExpandVillageAction> getNewExpandVillageActions(TileAction action) {
        return actions.getNewExpandVillageActions(action);
    }

    @Override
    public void action(Action action) {
        checkNotNull(action);
        if (action instanceof SeaTileAction) {
            placeOnSea((SeaTileAction) action);
        }
        else if (action instanceof VolcanoTileAction) {
            placeOnVolcano((VolcanoTileAction) action);
        }
        else if (action instanceof PlaceBuildingAction) {
            build((PlaceBuildingAction) action);
        }
        else if (action instanceof ExpandVillageAction) {
            expand((ExpandVillageAction) action);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public synchronized void placeOnSea(SeaTileAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(status.getStep() == EngineStatus.TurnStep.TILE, "Can't place a tile during building step");

        if (DEBUG) {
            Problems problems = SeaTileRules.validate(island,
                    volcanoTileStack.current(),
                    action.getVolcanoHex(),
                    action.getOrientation());
            if (!problems.isValid()) {
                throw new IllegalStateException(problems.toString());
            }
        }

        undoList.add(new ActionSave.Tile(this, action));

        island.putTile(volcanoTileStack.current(), action.getVolcanoHex(), action.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnSea(action));
        nextStep();
    }

    @Override
    public synchronized void placeOnVolcano(VolcanoTileAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(status.getStep() == EngineStatus.TurnStep.TILE,
                "Can't place a tile during building step");

        if (DEBUG) {
            Problems problems = VolcanoTileRules.validate(island,
                    volcanoTileStack.current(),
                    action.getVolcanoHex(),
                    action.getOrientation());
            if (!problems.isValid()) {
                throw new IllegalStateException(problems.toString());
            }
        }

        undoList.add(new ActionSave.Tile(this, action));

        island.putTile(volcanoTileStack.current(), action.getVolcanoHex(), action.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnVolcano(action));
        nextStep();
    }

    @Override
    public synchronized void build(PlaceBuildingAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(status.getStep() == EngineStatus.TurnStep.BUILD,
                "Can't build during tile placement step");

        if (DEBUG) {
            Problems problems = PlaceBuildingRules.validate(this,
                    action.getType(),
                    action.getHex());
            if (!problems.isValid()) {
                throw new IllegalStateException(problems.toString());
            }
        }

        undoList.add(new ActionSave.Build(this, action));

        PlayerColor color = getCurrentPlayer().getColor();
        island.putBuilding(action.getHex(), Building.of(action.getType(), color));
        int buildingCount = island.getField(action.getHex()).getBuildingCount();
        getCurrentPlayer().decreaseBuildingCount(action.getType(), buildingCount);

        observers.forEach(o -> o.onBuild(action));
        checkBuildingCounts();
    }

    @Override
    public synchronized void expand(ExpandVillageAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(status.getStep() == EngineStatus.TurnStep.BUILD, "Can't expand during tile placement step");

        if (DEBUG) {
            Problems problems = ExpandVillageRules.validate(this,
                    action.getVillage(island),
                    action.getFieldType());
            if (!problems.isValid()) {
                throw new IllegalStateException(problems.toString());
            }
        }

        undoList.add(new ActionSave.Build(this, action));

        PlayerColor color = getCurrentPlayer().getColor();
        Building building = Building.of(BuildingType.HUT, color);
        int buildingCount = 0;
        Village village = getIsland().getVillage(action.getVillageHex());
        for (Hex hex : village.getExpandableHexes().get(action.getFieldType())) {
            island.putBuilding(hex, building);
            buildingCount += island.getField(hex).getBuildingCount();
        }
        getCurrentPlayer().decreaseBuildingCount(BuildingType.HUT, buildingCount);

        observers.forEach(o -> o.onExpand(action));
        checkBuildingCounts();
    }

    private void checkBuildingCounts() {
        Player player = getCurrentPlayer();
        int remainingBuildingTypes = remainingBuildingTypeCount(player);

        if (gamemode == Gamemode.TeamVsTeam) {
            Player teammate = players.get((playerIndex + 2) % players.size());
            int teammateRemainingBuildingTypes = remainingBuildingTypeCount(teammate);
            if (remainingBuildingTypes + teammateRemainingBuildingTypes <= 3) {
                ImmutableList<Player> winners = ImmutableList.of(player, teammate);
                this.status = running().finished(
                        EngineStatus.FinishReason.TEAM_THREE_BUILDING_TYPES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.TWO_BUILDING_TYPES, winners));
                return;
            }
        }
        else {
            if (remainingBuildingTypes <= 1) {
                ImmutableList<Player> winners = ImmutableList.of(player);
                this.status = running().finished(
                        EngineStatus.FinishReason.TWO_BUILDING_TYPES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.TWO_BUILDING_TYPES, winners));
                return;
            }
        }

        nextStep();
    }

    private int remainingBuildingTypeCount(Player player) {
        int remainingBuildingTypeCount = 0;
        if (player.getBuildingCount(BuildingType.HUT) > 0) {
            remainingBuildingTypeCount++;
        }
        if (player.getBuildingCount(BuildingType.TEMPLE) > 0) {
            remainingBuildingTypeCount++;
        }
        if (player.getBuildingCount(BuildingType.TOWER) > 0) {
            remainingBuildingTypeCount++;
        }
        return remainingBuildingTypeCount;
    }
}
