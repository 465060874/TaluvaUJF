package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import data.BuildingType;
import data.PlayerColor;
import engine.action.*;
import engine.log.EngineLogger;
import engine.tilestack.VolcanoTileStack;
import map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;
import static java.util.stream.Collectors.toList;

class EngineImpl implements Engine {

    private static final int TILES_PER_PLAYER = 12;

    private final EngineLogger logger;
    private final long seed;
    private final Random random;

    private final List<EngineObserver> observers;

    private final Gamemode gamemode;
    private final Island island;
    private final ImmutableList<Player> players;
    private final VolcanoTileStack volcanoTileStack;

    private EngineStatus status;
    private int playerIndex;
    private PlayerTurn playerTurn;

    private final List<ActionSave> actionSaves;
    private final EngineActions actions;

    /**
     * Package-protected, voir la classe EngineBuilder
     */
    EngineImpl(EngineBuilder<?> builder) {
        this.logger = EngineLogger.create(builder.logLevel);
        this.seed = builder.seed;
        this.random = new Random(builder.seed);

        this.observers = new ArrayList<>();

        this.gamemode = builder.gamemode;
        this.island = builder.island;
        this.players = builder.createPlayers(this);
        this.volcanoTileStack = builder.volcanoTileStackFactory.create(players.size() * TILES_PER_PLAYER, random);

        this.status = EngineStatus.PENDING_START;
        this.playerIndex = 0;

        this.actionSaves = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        this.actions = new EngineActions(this);
    }

    private EngineImpl(EngineImpl engine) {
        this.logger = engine.logger;
        this.seed = engine.seed;
        this.random = engine.random;

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
                ? ((EngineStatus.Running) engine.status).copy()
                : engine.status;
        this.playerIndex = engine.playerIndex;
        this.playerTurn = PlayerHandler.dummy().startTurn(this, EngineStatus.TurnStep.TILE);

        this.actionSaves = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        actionSaves.addAll(engine.actionSaves);
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
        this.status = new EngineStatus.Running();
        this.playerIndex = 0;
        observers.forEach(EngineObserver::onStart);

        volcanoTileStack.next();
        observers.forEach(o -> o.onTileStackChange(false));

        actions.updateAll();

        observers.forEach(o -> o.onTileStepStart(false));
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

    public synchronized void cancelUntil(Predicate<Engine> predicate) {
        checkState(status != EngineStatus.PENDING_START);

        EngineStatus.Running running;
        if (status instanceof EngineStatus.Finished) {
            running = new EngineStatus.Running();
            running.turn = status.getTurn();
            running.step = status.getStep();
            status = running;
        }
        else {
            running = (EngineStatus.Running) status;
        }

        if (running.turn == 0 && running.step == EngineStatus.TurnStep.TILE) {
            return;
        }

        playerTurn.cancel();
        boolean volcanoTileStackChanged = false;
        do {
            if (running.step == EngineStatus.TurnStep.TILE) {
                running.turn--;
                do {
                    playerIndex = playerIndex == 0
                            ? players.size()
                            : playerIndex - 1;
                } while (getCurrentPlayer().isEliminated());

                ActionSave save = actionSaves.remove(actionSaves.size() - 1);
                save.revert(this);

                volcanoTileStack.previous();
                volcanoTileStackChanged = true;

                running.step = EngineStatus.TurnStep.BUILD;
            }
            else {
                ActionSave save = actionSaves.remove(actionSaves.size() - 1);
                save.revert(this);

                running.step = EngineStatus.TurnStep.TILE;
            }
        } while (!predicate.test(this) && !(running.turn == 0 && running.step == EngineStatus.TurnStep.TILE));

        if (volcanoTileStackChanged) {
            observers.forEach(o -> o.onTileStackChange(true));
        }

        actions.updateAll();
        observers.forEach(running.step == EngineStatus.TurnStep.TILE
                ? o -> o.onTileStepStart(true)
                : o -> o.onBuildStepStart(true));
        this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, running.step);
    }

    private void nextStep() {
        verify(status instanceof EngineStatus.Running);

        EngineStatus.Running running = (EngineStatus.Running) status;
        if (running.step == EngineStatus.TurnStep.TILE) {
            running.step = EngineStatus.TurnStep.BUILD;

            if (actions.placeBuildings.isEmpty()
                    && actions.newPlaceBuildings.isEmpty()
                    && actions.expandVillages.isEmpty()
                    && actions.newExpandVillages.isEmpty()) {
                Player eliminated = getCurrentPlayer();
                eliminated.setEliminated();
                observers.forEach(o -> o.onEliminated(eliminated));

                Predicate<Player> isEliminated = Player::isEliminated;
                List<Player> remainingPlayers = players.stream()
                        .filter(isEliminated.negate())
                        .collect(toList());
                verify(remainingPlayers.size() > 0);

                if (remainingPlayers.size() == 1) {
                    this.status = new EngineStatus.Finished((EngineStatus.Running) status,
                            EngineStatus.FinishReason.LAST_STANDING,
                            remainingPlayers);
                    observers.forEach(o -> o.onWin(EngineStatus.FinishReason.LAST_STANDING, remainingPlayers));
                }
                else {
                    nextStep();
                }
                return;
            }

            observers.forEach(o -> o.onBuildStepStart(true));
        }
        else {
            running.turn++;
            do {
                playerIndex++;
            } while (getCurrentPlayer().isEliminated());

            running.step = EngineStatus.TurnStep.TILE;
            volcanoTileStack.next();

            if (volcanoTileStack.isEmpty()) {
                List<Player> winners = winnersByScore();
                this.status = new EngineStatus.Finished((EngineStatus.Running) status,
                        EngineStatus.FinishReason.NO_MORE_TILES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.NO_MORE_TILES, winners));
                return;
            }
            observers.forEach(o -> o.onTileStackChange(true));

            actions.updateAll();

            observers.forEach(o -> o.onTileStepStart(true));
            this.playerTurn = getCurrentPlayer().getHandler().startTurn(this, EngineStatus.TurnStep.TILE);
        }
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
    public List<PlaceBuildingAction> getNewPlaceBuildingActions() {
        return actions.newPlaceBuildings;
    }

    @Override
    public List<ExpandVillageAction> getExpandVillageActions() {
        checkState(status instanceof EngineStatus.Running, "Requesting actions while the game is not running");
        return actions.expandVillages;
    }

    @Override
    public List<ExpandVillageAction> getNewExpandVillageActions() {
        return actions.newExpandVillages;
    }

    @Override
    public void action(Action action) {
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
        checkState(((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Can't place a tile during building step");

        actionSaves.add(new TileActionSave(this, action));
        island.putTile(volcanoTileStack.current(), action.getVolcanoHex(), action.getOrientation());

        actions.updateWithNewTile(action);
        observers.forEach(o -> o.onTilePlacementOnSea(action));
        nextStep();
    }

    @Override
    public synchronized void placeOnVolcano(VolcanoTileAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Can't place a tile during building step");

        actionSaves.add(new TileActionSave(this, action));
        island.putTile(volcanoTileStack.current(), action.getVolcanoHex(), action.getOrientation());

        actions.updateWithNewTile(action);
        observers.forEach(o -> o.onTilePlacementOnVolcano(action));
        nextStep();
    }

    @Override
    public synchronized void build(PlaceBuildingAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Can't build during tile placement step");

        actionSaves.add(new BuildActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        island.putBuilding(action.getHex(), Building.of(action.getType(), color));
        int buildingCount = action.getType() == BuildingType.HUT
                ? island.getField(action.getHex()).getLevel()
                : 1;
        getCurrentPlayer().decreaseBuildingCount(action.getType(), buildingCount);

        observers.forEach(o -> o.onBuild(action));
        checkBuildingCounts();
    }

    @Override
    public synchronized void expand(ExpandVillageAction action) {
        checkState(status instanceof EngineStatus.Running, "Can't do an action while the game is not running");
        checkState(((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Can't expand during tile placement step");

        actionSaves.add(new BuildActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        Building building = Building.of(BuildingType.HUT, color);
        int buildingCount = 0;
        Village village = getIsland().getVillage(action.getVillageHex());
        for (Hex hex : village.getExpandableHexes().get(action.getFieldType())) {
            island.putBuilding(hex, building);
            buildingCount += island.getField(hex).getLevel();
        }
        getCurrentPlayer().decreaseBuildingCount(BuildingType.HUT, buildingCount);

        observers.forEach(o -> o.onExpand(action));
        checkBuildingCounts();
    }

    private void checkBuildingCounts() {
        Player player = getCurrentPlayer();
        int remainingBuildingTypes = remainingBuildingsType(player);

        if (gamemode == Gamemode.TeamVsTeam) {
            Player teammate = players.get((playerIndex + 2) % players.size());
            int teammateRemainingBuildingTypes = remainingBuildingsType(teammate);
            if (remainingBuildingTypes + teammateRemainingBuildingTypes <= 3) {
                ImmutableList<Player> winners = ImmutableList.of(player, teammate);
                this.status = new EngineStatus.Finished((EngineStatus.Running) status,
                        EngineStatus.FinishReason.TEAM_THREE_BUILDING_TYPES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.TWO_BUILDING_TYPES, winners));
                return;
            }
        }
        else {
            if (remainingBuildingTypes <= 1) {
                ImmutableList<Player> winners = ImmutableList.of(player);
                this.status = new EngineStatus.Finished((EngineStatus.Running) status,
                        EngineStatus.FinishReason.TWO_BUILDING_TYPES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.TWO_BUILDING_TYPES, winners));
                return;
            }
        }

        nextStep();
    }

    private int remainingBuildingsType(Player player) {
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

    private interface ActionSave {

        void revert(EngineImpl engine);
    }

    private static class TileActionSave implements ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;

        TileActionSave(EngineImpl engine, TileAction placement) {
            this.islandDiff = ImmutableMap.of(
                    placement.getVolcanoHex(), engine.island.getField(placement.getVolcanoHex()),
                    placement.getLeftHex(), engine.island.getField(placement.getLeftHex()),
                    placement.getRightHex(), engine.island.getField(placement.getRightHex()));
        }

        @Override
        public void revert(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.island.putField(entry.getKey(), entry.getValue());
            }
        }
    }

    private static class BuildActionSave implements ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;
        private final BuildingType buildingType;
        private final int buildingCount;

        BuildActionSave(EngineImpl engine, PlaceBuildingAction action) {
            Field field = engine.island.getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        BuildActionSave(EngineImpl engine, ExpandVillageAction action) {
            ImmutableMap.Builder<Hex, Field> islandsDiffBuilder = ImmutableMap.builder();
            Village village = engine.getIsland().getVillage(action.getVillageHex());
            for (Hex hex : village.getExpandableHexes().get(action.getFieldType())) {
                Field field = engine.island.getField(hex);
                islandsDiffBuilder.put(hex, field);
            }

            this.islandDiff = islandsDiffBuilder.build();
            this.buildingType = BuildingType.HUT;
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(BuildingType.HUT);
        }

        @Override
        public void revert(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.island.putField(entry.getKey(), entry.getValue());
            }

            engine.getCurrentPlayer().updateBuildingCount(buildingType, buildingCount);
        }
    }
}
