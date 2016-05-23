package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.*;
import engine.log.EngineLogger;
import engine.log.EngineLoggerSetup;
import engine.rules.BuildRules;
import engine.rules.ExpandRules;
import engine.rules.SeaPlacementRules;
import engine.rules.VolcanoPlacementRules;
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
    private List<StepSave> stepSaves;

    private HexMap<List<SeaTileAction>> seaPlacements;
    private HexMap<List<VolcanoTileAction>> volcanosPlacements;
    private HexMap<List<PlaceBuildingAction>> buildActions;
    private HexMap<List<ExpandVillageAction>> expandActions;

    /**
     * Package-protected, voir la classe EngineBuilder
     */
    EngineImpl(EngineBuilder<?> builder) {
        this.logger = EngineLoggerSetup.setup(builder.logLevel);
        this.seed = builder.seed;
        this.random = new Random(builder.seed);

        this.observers = new ArrayList<>();

        this.gamemode = builder.gamemode;
        this.island = builder.island;
        this.players = builder.createPlayers(this);
        this.volcanoTileStack = builder.volcanoTileStackFactory.create(players.size() * TILES_PER_PLAYER, random);

        this.status = EngineStatus.PENDING_START;
        this.playerIndex = 0;
        this.stepSaves = new ArrayList<>(volcanoTileStack.size() * 2 + 2);

        this.seaPlacements = HexMap.create();
        this.volcanosPlacements = HexMap.create();
        this.buildActions = HexMap.create();
        this.expandActions = HexMap.create();
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
        this.stepSaves = new ArrayList<>(volcanoTileStack.size() * 2 + 2);
        stepSaves.addAll(engine.stepSaves);

        this.seaPlacements = engine.seaPlacements;
        this.volcanosPlacements = engine.volcanosPlacements;
        this.buildActions = engine.buildActions;
        this.expandActions = engine.expandActions;
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
        status = new EngineStatus.Running();
        playerIndex = 0;
        observers.forEach(EngineObserver::onStart);

        volcanoTileStack.next();
        observers.forEach(o -> o.onTileStackChange(false));

        updateSeaPlacements();
        updateVolcanoPlacements();

        observers.forEach(o -> o.onTileStepStart(false));
        getCurrentPlayer().getHandler().startTileStep();
    }

    @Override
    public Engine copyWithoutObservers() {
        return new EngineImpl(this);
    }

    @Override
    public synchronized void cancelLastStep() {
        checkState(status != EngineStatus.PENDING_START);
        EngineStatus.Running running;
        if (status instanceof EngineStatus.Finished) {
            running = new EngineStatus.Running();
            running.turn = status.getTurn();
            running.step = EngineStatus.TurnStep.TILE;
        }
        else {
            running = (EngineStatus.Running) status;
        }

        if (running.turn == 0 && running.step == EngineStatus.TurnStep.TILE) {
            return;
        }

        getCurrentPlayer().getHandler().cancel();
        StepSave save = stepSaves.remove(stepSaves.size() - 1);
        save.restore(this);

        if (running.step == EngineStatus.TurnStep.TILE) {
            running.turn--;
            do {
                playerIndex--;
            } while (getCurrentPlayer().isEliminated());

            volcanoTileStack.previous();
            observers.forEach(o -> o.onTileStackChange(true));

            running.step = EngineStatus.TurnStep.BUILD;
            updateBuildActions();
            updateExpandActions();

            observers.forEach(o -> o.onBuildStepStart(true));
            getCurrentPlayer().getHandler().startBuildStep();
        }
        else {
            running.step = EngineStatus.TurnStep.TILE;
            updateSeaPlacements();
            updateVolcanoPlacements();

            observers.forEach(o -> o.onTileStepStart(true));
            getCurrentPlayer().getHandler().startTileStep();
        }
    }

    private void nextStep() {
        verify(status instanceof EngineStatus.Running);

        EngineStatus.Running running = (EngineStatus.Running) status;
        if (running.step == EngineStatus.TurnStep.TILE) {
            running.step = EngineStatus.TurnStep.BUILD;

            updateBuildActions();
            updateExpandActions();
            if (buildActions.size() == 0 && expandActions.size() == 0) {
                Player eliminated = getCurrentPlayer();
                eliminated.setEliminated();
                observers.forEach(o -> o.onEliminated(eliminated));

                Predicate<Player> isEliminated = Player::isEliminated;
                List<Player> remainingPlayers = players.stream()
                        .filter(isEliminated.negate())
                        .collect(toList());
                verify(remainingPlayers.size() > 0);

                if (remainingPlayers.size() == 1) {
                    this.status = new EngineStatus.Finished(status.getTurn(),
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
            getCurrentPlayer().getHandler().startBuildStep();
        }
        else {
            running.turn++;
            do {
                playerIndex++;
            } while (getCurrentPlayer().isEliminated());

            running.step = EngineStatus.TurnStep.TILE;
            volcanoTileStack.next();
            if (volcanoTileStack.isEmpty()) {
                List<Player> candidates = playerWithMinimumBuildingOfType(players, BuildingType.TEMPLE);
                if (candidates.size() > 1) {
                    candidates = playerWithMinimumBuildingOfType(candidates, BuildingType.TOWER);
                    if (candidates.size() > 1) {
                        candidates = playerWithMinimumBuildingOfType(candidates, BuildingType.HUT);
                    }
                }

                List<Player> winners = candidates;
                this.status = new EngineStatus.Finished(status.getTurn(),
                        EngineStatus.FinishReason.NO_MORE_TILES,
                        winners);
                observers.forEach(o -> o.onWin(EngineStatus.FinishReason.NO_MORE_TILES, winners));
                return;
            }

            observers.forEach(o -> o.onTileStackChange(true));

            updateSeaPlacements();
            updateVolcanoPlacements();

            observers.forEach(o -> o.onTileStepStart(true));
            getCurrentPlayer().getHandler().startTileStep();
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

    private void updateSeaPlacements() {
        if (status.getTurn() == 0) {
            Hex originHex = Hex.at(0, 0);
            seaPlacements = HexMap.create();
            seaPlacements.put(originHex, ImmutableList.of(new SeaTileAction(originHex, Orientation.NORTH)));
            return;
        }

        HexMap<List<SeaTileAction>> tmpSeaPlacements = HexMap.create();

        VolcanoTile tile = volcanoTileStack.current();
        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                if (!SeaPlacementRules.validate(island, tile, hex, orientation).isValid()) {
                    continue;
                }

                List<SeaTileAction> list = tmpSeaPlacements.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpSeaPlacements.put(hex, list);
                }

                list.add(new SeaTileAction(hex, orientation));
            }
        }

        this.seaPlacements = tmpSeaPlacements;
    }

    private void updateVolcanoPlacements() {
        if (status.getTurn() == 0) {
            volcanosPlacements = HexMap.create();
            return;
        }

        HexMap<List<VolcanoTileAction>> tmpVolcanosPlacements = HexMap.create();

        VolcanoTile tile = volcanoTileStack.current();
        for (Hex hex : island.getVolcanos()) {
            for (Orientation orientation : Orientation.values()) {
                if (!VolcanoPlacementRules.validate(island, tile, hex, orientation).isValid()) {
                    continue;
                }

                List<VolcanoTileAction> list = tmpVolcanosPlacements.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpVolcanosPlacements.put(hex, list);
                }

                list.add(new VolcanoTileAction(hex, orientation));
            }
        }

        this.volcanosPlacements = tmpVolcanosPlacements;
    }

    private void updateBuildActions() {
        HexMap<List<PlaceBuildingAction>> tmpBuildActions = HexMap.create();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE) {
                boolean hutValid = BuildRules.validate(this, BuildingType.HUT, hex);
                boolean templeValid = BuildRules.validate(this, BuildingType.TEMPLE, hex);
                boolean towerValid = BuildRules.validate(this, BuildingType.TOWER, hex);

                if (!hutValid && !templeValid && !towerValid) {
                    continue;
                }

                List<PlaceBuildingAction> list = tmpBuildActions.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpBuildActions.put(hex, list);
                }

                if (hutValid) {
                    list.add(new PlaceBuildingAction(BuildingType.HUT, hex));
                }
                if (templeValid) {
                    list.add(new PlaceBuildingAction(BuildingType.TEMPLE, hex));
                }
                if (towerValid) {
                    list.add(new PlaceBuildingAction(BuildingType.TOWER, hex));
                }
            }
        }

        this.buildActions = tmpBuildActions;
    }

    private void updateExpandActions() {
        HexMap<List<ExpandVillageAction>> tmpExpandActions = HexMap.create();
        Iterable<Village> villages = island.getVillages(getCurrentPlayer().getColor());
        for (Village village : villages) {
            Hex firstHex = village.getHexes().iterator().next();
            boolean[] types = new boolean[FieldType.values().length];
            for (Hex hex : village.getHexes()) {
                final Iterable<Hex> neighborhood = hex.getNeighborhood();
                for (Hex neighbor : neighborhood) {
                    Field field = island.getField(neighbor);
                    if (field != Field.SEA
                            && field.getType().isBuildable()
                            && field.getBuilding().getType() == BuildingType.NONE) {
                        types[field.getType().ordinal()] = true;
                    }
                }
            }

            List<ExpandVillageAction> actions = new ArrayList<>(FieldType.values().length);
            for (FieldType fieldType : FieldType.values()) {
                if (types[fieldType.ordinal()]
                        && ExpandRules.canExpandVillage(this, village, fieldType)) {
                    actions.add(new ExpandVillageAction(firstHex, fieldType));
                }
            }
            if (!actions.isEmpty()) {
                for (Hex hex : village.getHexes()) {
                    tmpExpandActions.put(hex, actions);
                }
            }

            this.expandActions = tmpExpandActions;
        }
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
    public HexMap<? extends Iterable<SeaTileAction>> getSeaPlacements() {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Requesting sea placements during building step");
        return seaPlacements;
    }

    @Override
    public HexMap<? extends Iterable<VolcanoTileAction>> getVolcanoPlacements() {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Requesting volcano placements during building step");
        return volcanosPlacements;
    }

    @Override
    public HexMap<? extends Iterable<PlaceBuildingAction>> getBuildActions() {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Requesting build actions during tile placements");
        return buildActions;
    }

    @Override
    public HexMap<? extends Iterable<ExpandVillageAction>> getExpandActions() {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Requesting expand actions during tile placements");
        return expandActions;
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
    public synchronized void placeOnSea(SeaTileAction placement) {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Can't place a tile during building step");

        stepSaves.add(new PlacementSave(this, placement));
        island.putTile(volcanoTileStack.current(), placement.getHex1(), placement.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnSea(placement));
        nextStep();
    }

    @Override
    public synchronized void placeOnVolcano(VolcanoTileAction placement) {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.TILE,
                "Can't place a tile during building step");

        stepSaves.add(new PlacementSave(this, placement));
        island.putTile(volcanoTileStack.current(), placement.getVolcanoHex(), placement.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnVolcano(placement));
        nextStep();
    }

    @Override
    public synchronized void build(PlaceBuildingAction action) {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Can't build during tile placement step");

        stepSaves.add(new ActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        island.putBuilding(action.getHex(), FieldBuilding.of(action.getType(), color));
        int buildingCount = action.getType() == BuildingType.HUT
                ? island.getField(action.getHex()).getLevel()
                : 1;
        getCurrentPlayer().decreaseBuildingCount(action.getType(), buildingCount);

        observers.forEach(o -> o.onBuild(action));
        checkBuildingCounts();
    }

    @Override
    public synchronized void expand(ExpandVillageAction action) {
        checkState(status instanceof EngineStatus.Running
                        && ((EngineStatus.Running) status).step == EngineStatus.TurnStep.BUILD,
                "Can't expand during tile placement step");

        stepSaves.add(new ActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        FieldBuilding building = FieldBuilding.of(BuildingType.HUT, color);
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

        if (remainingBuildingTypeCount <= 1) {
            ImmutableList<Player> winners = ImmutableList.of(player);
            this.status = new EngineStatus.Finished(status.getTurn(),
                    EngineStatus.FinishReason.TWO_BUILDING_TYPES,
                    winners);
            observers.forEach(o -> o.onWin(EngineStatus.FinishReason.TWO_BUILDING_TYPES, winners));
        }
        else {
            nextStep();
        }
    }

    private interface StepSave {

        void restore(EngineImpl engine);
    }

    private static class PlacementSave implements StepSave {

        private final ImmutableMap<Hex, Field> islandDiff;

        PlacementSave(EngineImpl engine, SeaTileAction placement) {
            this.islandDiff = ImmutableMap.of(
                    placement.getHex1(), engine.island.getField(placement.getHex1()),
                    placement.getHex2(), engine.island.getField(placement.getHex2()),
                    placement.getHex3(), engine.island.getField(placement.getHex3()));
        }

        PlacementSave(EngineImpl engine, VolcanoTileAction placement) {
            this.islandDiff = ImmutableMap.of(
                    placement.getVolcanoHex(), engine.island.getField(placement.getVolcanoHex()),
                    placement.getLeftHex(), engine.island.getField(placement.getLeftHex()),
                    placement.getRightHex(), engine.island.getField(placement.getRightHex()));
        }

        @Override
        public void restore(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.island.putField(entry.getKey(), entry.getValue());
            }
        }
    }

    private static class ActionSave implements StepSave {

        private final ImmutableMap<Hex, Field> islandDiff;
        private final BuildingType buildingType;
        private final int buildingCount;

        ActionSave(EngineImpl engine, PlaceBuildingAction action) {
            Field field = engine.island.getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        ActionSave(EngineImpl engine, ExpandVillageAction action) {
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
        public void restore(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.island.putField(entry.getKey(), entry.getValue());
            }

            engine.getCurrentPlayer().updateBuildingCount(buildingType, buildingCount);
        }
    }
}
