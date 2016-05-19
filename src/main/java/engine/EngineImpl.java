package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.*;
import engine.rules.BuildRules;
import engine.rules.SeaPlacementRules;
import engine.rules.VolcanoPlacementRules;
import map.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

class EngineImpl implements Engine {

    private final List<EngineObserver> observers;
    private final Random random;

    private final Gamemode gamemode;
    private final Island island;
    private final VolcanoTileStack volcanoTileStack;
    private final ImmutableList<Player> players;

    private int turn;
    private boolean placeTile;
    private List<StepSave> stepSaves;

    private HexMap<List<SeaPlacement>> seaPlacements;
    private HexMap<List<VolcanoPlacement>> volcanosPlacements;
    private HexMap<List<BuildAction>> buildActions;
    private HexMap<List<ExpandAction>> expandActions;

    /**
     * Package-protected, voir la classe EngineBuilder
     */
    EngineImpl(EngineBuilder builder) {
        this.observers = new ArrayList<>();
        this.random = new Random(builder.seed);

        this.gamemode = builder.gamemode;
        this.island = builder.island;
        this.volcanoTileStack = builder.volcanoTileStackFactory.create(gamemode, random);

        Map<PlayerHandler.Factory, PlayerHandler> playerHandlersMap = new IdentityHashMap<>();
        for (PlayerHandler.Factory factory : builder.players.values()) {
            if (!playerHandlersMap.containsKey(factory)) {
                playerHandlersMap.put(factory, factory.create(this));
            }
        }

        List<Player> playersBuilder = new ArrayList<>();
        for (Map.Entry<PlayerColor, PlayerHandler.Factory> entry : builder.players.entrySet()) {
            PlayerHandler handler = playerHandlersMap.get(entry.getValue());
            playersBuilder.add(new Player(entry.getKey(), handler));
        }
        Collections.shuffle(playersBuilder, random);
        this.players = ImmutableList.copyOf(playersBuilder);

        this.turn = 0;
        this.placeTile = false;
        this.stepSaves = new ArrayList<>(volcanoTileStack.size() * 2 + 2);

        this.seaPlacements = HexMap.create();
        this.volcanosPlacements = HexMap.create();
        this.buildActions = HexMap.create();
        this.expandActions = HexMap.create();
    }

    private EngineImpl(EngineImpl engine) {
        this.observers = new ArrayList<>();
        this.random = engine.getRandom();

        this.gamemode = engine.getGamemode();
        this.island = engine.island.copy();
        this.volcanoTileStack = engine.volcanoTileStack.copy(random);

        ImmutableList.Builder<Player> players = ImmutableList.builder();
        for (Player player : engine.getPlayers()) {
            players.add(player.copyWithDummyHandler());
        }
        this.players = players.build();
        this.turn = engine.turn;
        this.placeTile = engine.placeTile;

        this.seaPlacements = engine.seaPlacements;
        this.volcanosPlacements = engine.volcanosPlacements;
        this.buildActions = engine.buildActions;
        this.expandActions = engine.expandActions;
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
        VolcanoTile tile = volcanoTileStack.current();
        island.putTile(tile, Hex.at(0, 0), Orientation.NORTH);
        nextStep();
    }

    @Override
    public Engine copyWithoutObservers() {
        return new EngineImpl(this);
    }

    @Override
    public synchronized void cancelLastStep() {
        getCurrentPlayer().getHandler().cancel();
        if (placeTile) {
            turn--;
        }
        placeTile = !placeTile;

        StepSave save = stepSaves.remove(stepSaves.size() - 1);
        save.restore(this);
    }

    private void nextStep() {
        if (placeTile) {
            placeTile = false;

            updateBuildActions();
            if (buildActions.size() == 0) {
                return;
            }
            updateExpandActions();

            observers.forEach(EngineObserver::onBuildStepStart);
            getCurrentPlayer().getHandler().startBuildStep();
        }
        else {
            turn++;
            placeTile = true;
            volcanoTileStack.next();
            if (volcanoTileStack.isEmpty()) {
                return;
            }

            observers.forEach(EngineObserver::onTileStackChange);

            updateSeaPlacements();
            updateVolcanoPlacements();

            observers.forEach(EngineObserver::onTileStepStart);
            getCurrentPlayer().getHandler().startTileStep();
        }
    }

    private void updateSeaPlacements() {
        HexMap<List<SeaPlacement>> tmpSeaPlacements = HexMap.create();

        VolcanoTile tile = volcanoTileStack.current();
        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                if (!SeaPlacementRules.validate(island, tile, hex, orientation)) {
                    continue;
                }

                List<SeaPlacement> list = tmpSeaPlacements.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpSeaPlacements.put(hex, list);
                }

                list.add(new SeaPlacement(hex, orientation));
            }
        }
        this.seaPlacements = tmpSeaPlacements;
    }

    private void updateVolcanoPlacements() {
        HexMap<List<VolcanoPlacement>> tmpVolcanosPlacements = HexMap.create();

        VolcanoTile tile = volcanoTileStack.current();
        for (Hex hex : island.getVolcanos()) {
            for (Orientation orientation : Orientation.values()) {
                if (!VolcanoPlacementRules.validate(island, tile, hex, orientation)) {
                    continue;
                }

                List<VolcanoPlacement> list = tmpVolcanosPlacements.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpVolcanosPlacements.put(hex, list);
                }

                list.add(new VolcanoPlacement(hex, orientation));
            }
        }

        this.volcanosPlacements = tmpVolcanosPlacements;
    }

    private void updateBuildActions() {
        HexMap<List<BuildAction>> tmpBuildActions = HexMap.create();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE) {
                boolean hutValid = BuildRules.validate(this, BuildingType.HUT, hex);
                boolean templeValid = BuildRules.validate(this, BuildingType.TEMPLE, hex);
                boolean towerValid = BuildRules.validate(this, BuildingType.TOWER, hex);

                if (!hutValid && !templeValid && !towerValid) {
                    continue;
                }

                List<BuildAction> list = tmpBuildActions.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    tmpBuildActions.put(hex, list);
                }

                if (hutValid) {
                    list.add(new BuildAction(BuildingType.HUT, hex));
                }
                if (templeValid) {
                    list.add(new BuildAction(BuildingType.TEMPLE, hex));
                }
                if (towerValid) {
                    list.add(new BuildAction(BuildingType.TOWER, hex));
                }
            }
        }

        this.buildActions = tmpBuildActions;
    }

    private void updateExpandActions() {
        HexMap<List<ExpandAction>> tmpExpandActions = HexMap.create();
        Iterable<Village> villages = island.getVillages(getCurrentPlayer().getColor());
        for (Village village : villages) {
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

            List<ExpandAction> actions = new ArrayList<>(FieldType.values().length);
            for (FieldType fieldType : FieldType.values()) {
                if (types[fieldType.ordinal()]) {
                    actions.add(new ExpandAction(village, fieldType));
                }
            }
            for (Hex hex : village.getHexes()) {
                expandActions.put(hex, actions);
            }
        }

        this.expandActions = tmpExpandActions;
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(turn % players.size());
    }

    @Override
    public HexMap<? extends Iterable<SeaPlacement>> getSeaPlacements() {
        return seaPlacements;
    }

    @Override
    public HexMap<? extends Iterable<VolcanoPlacement>> getVolcanoPlacements() {
        return volcanosPlacements;
    }

    @Override
    public HexMap<? extends Iterable<BuildAction>> getBuildActions() {
        return buildActions;
    }

    @Override
    public HexMap<? extends Iterable<ExpandAction>> getExpandActions() {
        return expandActions;
    }

    @Override
    public void place(Placement placement) {
        if (placement instanceof SeaPlacement) {
            placeOnSea((SeaPlacement) placement);
        }
        else if (placement instanceof VolcanoPlacement) {
            placeOnVolcano((VolcanoPlacement) placement);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public synchronized void placeOnSea(SeaPlacement placement) {
        checkState(placeTile, "Can't place a tile during building step");

        stepSaves.add(new PlacementSave(this, placement));
        island.putTile(volcanoTileStack.current(), placement.getHex1(), placement.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnSea(placement));
        nextStep();
    }

    @Override
    public synchronized void placeOnVolcano(VolcanoPlacement placement) {
        checkState(placeTile, "Can't place a tile during building step");

        stepSaves.add(new PlacementSave(this, placement));
        island.putTile(volcanoTileStack.current(), placement.getVolcanoHex(), placement.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnVolcano(placement));
        nextStep();
    }

    @Override
    public void action(Action action) {
        if (action instanceof BuildAction) {
            build((BuildAction) action);
        }
        else if (action instanceof ExpandAction) {
            expand((ExpandAction) action);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public synchronized void build(BuildAction action) {
        checkState(!placeTile, "Can't build during tile placement step");

        stepSaves.add(new ActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        island.putBuilding(action.getHex(), FieldBuilding.of(action.getType(), color));

        observers.forEach(o -> o.onBuild(action));
        nextStep();
    }

    @Override
    public synchronized void expand(ExpandAction action) {
        checkState(!placeTile, "Can't expand during tile placement step");

        stepSaves.add(new ActionSave(this, action));
        PlayerColor color = getCurrentPlayer().getColor();
        FieldBuilding building = FieldBuilding.of(BuildingType.HUT, color);
        for (Hex hex : action.getExpandHexes()) {
            island.putBuilding(hex, building);
        }

        observers.forEach(o -> o.onExpand(action));
        nextStep();
    }

    private interface StepSave {

        void restore(EngineImpl engine);
    }

    private static class PlacementSave implements StepSave {

        private final ImmutableMap<Hex, Field> islandDiff;

        PlacementSave(EngineImpl engine, SeaPlacement placement) {
            this.islandDiff = ImmutableMap.of(
                    placement.getHex1(), engine.island.getField(placement.getHex1()),
                    placement.getHex2(), engine.island.getField(placement.getHex2()),
                    placement.getHex3(), engine.island.getField(placement.getHex3()));
        }

        PlacementSave(EngineImpl engine, VolcanoPlacement placement) {
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

        ActionSave(EngineImpl engine, BuildAction action) {
            Field field = engine.island.getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        ActionSave(EngineImpl engine, ExpandAction action) {
            ImmutableMap.Builder<Hex, Field> islandsDiffBuilder = ImmutableMap.builder();
            for (Hex hex : action.getVillage().getExpandableHexes().get(action.getFieldType())) {
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
