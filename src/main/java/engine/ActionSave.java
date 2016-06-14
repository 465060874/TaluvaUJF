package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.BuildingType;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.TileAction;
import map.Field;
import map.Hex;
import map.Village;

import java.util.Map;

/**
 * Encapsulation de la sauvegarde des coups joués permettant
 * l'"annuler" et le "refaire"
 */
abstract class ActionSave {

    private final EngineStatus status;
    private final int playerIndex;
    private final ImmutableList<Player> players;

    protected ActionSave(EngineImpl engine) {
        this.status = engine.status;
        this.playerIndex = engine.playerIndex;

        ImmutableList.Builder<Player> playersBuilder = ImmutableList.builder();
        for (Player player : engine.players) {
            playersBuilder.add(player.copy());
        }
        this.players = playersBuilder.build();
    }

    /**
     * Annule les effets de l'action sauvegardé par cette instance
     * et retourne une sauvegarde inverse permettant de refaire
     * l'action
     */
    abstract ActionSave revert(EngineImpl engine);

    void commonRevert(EngineImpl engine) {
        engine.status = status;
        engine.playerIndex = playerIndex;
        engine.players = players;
    }

    static class Tile extends ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;

        Tile(EngineImpl engine, TileAction placement) {
            super(engine);
            this.islandDiff = ImmutableMap.of(
                    placement.getVolcanoHex(), engine.getIsland().getField(placement.getVolcanoHex()),
                    placement.getLeftHex(), engine.getIsland().getField(placement.getLeftHex()),
                    placement.getRightHex(), engine.getIsland().getField(placement.getRightHex()));
        }

        private Tile(EngineImpl engine, Tile tile) {
            super(engine);

            ImmutableMap.Builder<Hex, Field> builder = ImmutableMap.builder();
            for (Map.Entry<Hex, Field> entry : tile.islandDiff.entrySet()) {
                Hex hex = entry.getKey();
                builder.put(hex, engine.getIsland().getField(hex));
            }
            this.islandDiff = builder.build();
        }

        @Override
        ActionSave revert(EngineImpl engine) {
            ActionSave reverse = new Tile(engine, this);

            commonRevert(engine);
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }

            return reverse;
        }
    }

    static class Build extends ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;
        private final BuildingType buildingType;
        private final int buildingCount;

        Build(EngineImpl engine, PlaceBuildingAction action) {
            super(engine);

            Field field = engine.getIsland().getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        Build(EngineImpl engine, ExpandVillageAction action) {
            super(engine);

            ImmutableMap.Builder<Hex, Field> islandsDiffBuilder = ImmutableMap.builder();
            Village village = engine.getIsland().getVillage(action.getVillageHex());
            for (Hex hex : village.getExpandableHexes().get(action.getFieldType())) {
                Field field = engine.getIsland().getField(hex);
                islandsDiffBuilder.put(hex, field);
            }

            this.islandDiff = islandsDiffBuilder.build();
            this.buildingType = BuildingType.HUT;
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(BuildingType.HUT);
        }

        private Build(EngineImpl engine, Build build) {
            super(engine);
            this.buildingType = build.buildingType;
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(buildingType);

            ImmutableMap.Builder<Hex, Field> builder = ImmutableMap.builder();
            for (Map.Entry<Hex, Field> entry : build.islandDiff.entrySet()) {
                Hex hex = entry.getKey();
                builder.put(hex, engine.getIsland().getField(hex));
            }
            this.islandDiff = builder.build();
        }

        @Override
        ActionSave revert(EngineImpl engine) {
            ActionSave reverse = new Build(engine, this);

            commonRevert(engine);
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }

            engine.getCurrentPlayer().updateBuildingCount(buildingType, buildingCount);

            return reverse;
        }
    }
}
