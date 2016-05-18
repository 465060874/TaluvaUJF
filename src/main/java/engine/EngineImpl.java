package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.*;
import engine.rules.BuildRules;
import map.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

class EngineImpl implements Engine {

    private final List<EngineObserver> observers;
    private final Random random;

    private final Gamemode gamemode;
    private final Island island;
    private final TileStack volcanoTileStack;

    private List<Player> players;
    private int turn;
    private boolean placeTile;

    private HexMap<List<SeaPlacement>> seaPlacements;
    private HexMap<List<VolcanoPlacement>> volcanosPlacements;
    private HexMap<List<BuildAction>> buildActions;
    private HexMap<List<ExpandAction>> expandActions;

    EngineImpl(Gamemode gamemode, Island island, TileStack volcanoTileStack) {
        this(System.nanoTime(), gamemode, island, volcanoTileStack);
    }

    EngineImpl(long seed, Gamemode gamemode, Island island, TileStack volcanoTileStack) {
        this.observers = new ArrayList<>();
        this.random = new Random(seed);

        this.gamemode = gamemode;
        this.island = island;
        this.volcanoTileStack = volcanoTileStack;

        this.players = null;
        this.turn = 0;
        this.placeTile = false;

        this.seaPlacements = HexMap.create();
        this.volcanosPlacements = HexMap.create();
        this.buildActions = HexMap.create();
        this.expandActions = HexMap.create();
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public void init(Player... players) {
        checkArgument(players != null, "Player already initialized");

        checkArgument(gamemode.getPlayerCount() == players.length,
                "Gamemode " + gamemode + " expected " + gamemode.getPlayerCount() + "players, got " + players.length);
        List<Player> tmpPlayers = Lists.newArrayList(players);
        Collections.shuffle(tmpPlayers, random);
        this.players = ImmutableList.copyOf(tmpPlayers);

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
    public TileStack getVolcanoTileStack() {
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
        return null;
    }

    @Override
    public void cancelLastStep() {

    }

    private void nextStep() {
        if (placeTile) {
            placeTile = false;

            updateBuildActions();
            updateExpandActions();

            observers.forEach(EngineObserver::onBuildStepStart);
        }
        else {
            turn++;
            placeTile = true;
            volcanoTileStack.next();
            observers.forEach(EngineObserver::onTileStackChange);

            updateSeaPlacements();
            updateVolcanoPlacements();

            observers.forEach(EngineObserver::onTileStepStart);
        }
    }

    private void updateSeaPlacements() {
        HexMap<List<SeaPlacement>> tmpSeaPlacements = HexMap.create();

        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                List<SeaPlacement> list = tmpSeaPlacements.get(hex);
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

        for (Hex hex : island.getVolcanos()) {
            Orientation volcanoOrientation = island.getField(hex).getOrientation();
            for (Orientation orientation : Orientation.values()) {
                if (orientation != volcanoOrientation) {
                    List<VolcanoPlacement> list = tmpVolcanosPlacements.get(hex);
                    if (list == null) {
                        list = new ArrayList<>();
                        tmpVolcanosPlacements.put(hex, list);
                    }

                    list.add(new VolcanoPlacement(hex, orientation));
                }
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
                    break;
                }

                List<BuildAction> list = tmpBuildActions.get(hex);
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
    public void placeOnSea(SeaPlacement placement) {
        checkState(placeTile, "Can't place a tile during building step");

        island.putTile(volcanoTileStack.current(), placement.getHex1(), placement.getOrientation());

        observers.forEach(o -> o.onTilePlacementOnSea(placement));
        nextStep();
    }

    @Override
    public void placeOnVolcano(VolcanoPlacement placement) {
        checkState(placeTile, "Can't place a tile during building step");

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
    public void build(BuildAction action) {
        checkState(!placeTile, "Can't build during tile placement step");

        PlayerColor color = getCurrentPlayer().getColor();
        island.putBuilding(action.getHex(), FieldBuilding.of(action.getType(), color));

        observers.forEach(o -> o.onBuild(action));
        nextStep();
    }

    @Override
    public void expand(ExpandAction action) {
        checkState(!placeTile, "Can't expand during tile placement step");

        PlayerColor color = getCurrentPlayer().getColor();
        FieldBuilding building = FieldBuilding.of(BuildingType.HUT, color);
        for (Hex hex : action.getExpandHexes()) {
            island.putBuilding(hex, building);
        }

        observers.forEach(o -> o.onExpand(action));
        nextStep();
    }
}
