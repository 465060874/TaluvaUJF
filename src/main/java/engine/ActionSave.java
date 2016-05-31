package engine;

import com.google.common.collect.ImmutableMap;
import data.BuildingType;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.TileAction;
import map.Field;
import map.Hex;
import map.Village;

import java.util.Map;

interface ActionSave {

    void revert(EngineImpl engine);

    class Tile implements ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;

        Tile(Engine engine, TileAction placement) {
            this.islandDiff = ImmutableMap.of(
                    placement.getVolcanoHex(), engine.getIsland().getField(placement.getVolcanoHex()),
                    placement.getLeftHex(), engine.getIsland().getField(placement.getLeftHex()),
                    placement.getRightHex(), engine.getIsland().getField(placement.getRightHex()));
        }

        @Override
        public void revert(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }
        }
    }

    class Build implements ActionSave {

        private final ImmutableMap<Hex, Field> islandDiff;
        private final BuildingType buildingType;
        private final int buildingCount;

        Build(Engine engine, PlaceBuildingAction action) {
            Field field = engine.getIsland().getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        Build(Engine engine, ExpandVillageAction action) {
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

        @Override
        public void revert(EngineImpl engine) {
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }

            engine.getCurrentPlayer().updateBuildingCount(buildingType, buildingCount);
        }
    }
}
