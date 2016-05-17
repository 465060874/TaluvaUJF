package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;
import engine.rules.BuildRules;
import map.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    private HexMap<SeaPlacement> seaPlacements;
    private HexMap<VolcanoPlacement> volcanosPlacements;
    private HexMap<BuildAction> buildActions;
    private HexMap<ExpandAction> expandActions;

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
        HexMap<SeaPlacement> tmpSeaPlacements = HexMap.create();

        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                tmpSeaPlacements.put(hex, new SeaPlacement(hex, orientation));
            }
        }
        this.seaPlacements = tmpSeaPlacements;
    }

    private void updateVolcanoPlacements() {
        HexMap<VolcanoPlacement> tmpVolcanosPlacements = HexMap.create();

        for (Hex hex : island.getVolcanos()) {
            Orientation volcanoOrientation = island.getField(hex).getOrientation();
            for (Orientation orientation : Orientation.values()) {
                if (orientation != volcanoOrientation) {
                    volcanosPlacements.put(hex, new VolcanoPlacement(hex, orientation));
                }
            }
        }

        this.volcanosPlacements = tmpVolcanosPlacements;
    }

    private void updateBuildActions() {
        HexMap<BuildAction> tmpBuildActions = HexMap.create();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE) {
                if (BuildRules.validate(this, BuildingType.HUT, hex)) {
                    tmpBuildActions.put(hex, new BuildAction(BuildingType.HUT, hex));
                }
                if (BuildRules.validate(this, BuildingType.TEMPLE, hex)) {
                    tmpBuildActions.put(hex, new BuildAction(BuildingType.TEMPLE, hex));
                }
                if (BuildRules.validate(this, BuildingType.TOWER, hex)) {
                    tmpBuildActions.put(hex, new BuildAction(BuildingType.TOWER, hex));
                }
            }
        }
        this.buildActions = tmpBuildActions;
    }

    private void updateExpandActions() {
        HexMap<ExpandAction> tmpExpandActions = HexMap.create();
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

            for (FieldType fieldType : FieldType.values()) {
                if (types[fieldType.ordinal()]) {
                    for (Hex hex : village.getHexes()) {
                        expandActions.put(hex, new ExpandAction(village, fieldType));
                    }
                }
            }
        }

        this.expandActions = tmpExpandActions;
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(turn % players.size());
    }

    @Override
    public HexMap<SeaPlacement> getSeaPlacements() {
        return seaPlacements;
    }

    @Override
    public HexMap<VolcanoPlacement> getVolcanoPlacements() {
        return volcanosPlacements;
    }

    @Override
    public HexMap<BuildAction> getBuildActions() {
        return buildActions;
    }

    @Override
    public HexMap<ExpandAction> getExpandActions() {
        return expandActions;
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
