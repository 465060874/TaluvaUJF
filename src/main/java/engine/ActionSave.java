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

abstract class ActionSave {

    private final EngineStatus status;
    final Player player;
    private final boolean[] eliminated;

    protected ActionSave(EngineImpl engine) {
        this.status = engine.status;
        this.player = engine.getCurrentPlayer();

        this.eliminated = new boolean[engine.players.size()];
        for (int i = 0; i < engine.players.size(); i++) {
            Player player = engine.players.get(i);
            eliminated[i] = player.isEliminated();
        }
    }

    void revert(EngineImpl engine) {
        engine.status = status;
        ImmutableList<Player> players = engine.players;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.eliminated = eliminated[i];
        }
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

        @Override
        public void revert(EngineImpl engine) {
            super.revert(engine);
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }
        }
    }

    static class Build extends ActionSave {

        private final int playerIndex;
        private final ImmutableMap<Hex, Field> islandDiff;
        private final BuildingType buildingType;
        private final int buildingCount;

        Build(EngineImpl engine, PlaceBuildingAction action) {
            super(engine);
            this.playerIndex = engine.playerIndex;

            Field field = engine.getIsland().getField(action.getHex());
            this.islandDiff = ImmutableMap.of(action.getHex(), field);
            this.buildingType = action.getType();
            this.buildingCount = engine.getCurrentPlayer().getBuildingCount(action.getType());
        }

        Build(EngineImpl engine, ExpandVillageAction action) {
            super(engine);
            this.playerIndex = engine.playerIndex;

            ImmutableMap.Builder<Hex, Field> islandsDiffBuilder = ImmutableMap.builder();
            Village village = engine.getIsland().getVillage(action.getVillageHex());
            for (Hex hex : village.getExpandableHexes().get(action.getFieldType())) {
                Field field = engine.getIsland().getField(hex);
                islandsDiffBuilder.put(hex, field);
            }

            this.islandDiff = islandsDiffBuilder.build();
            this.buildingType = BuildingType.HUT;
            this.buildingCount = player.getBuildingCount(BuildingType.HUT);
        }

        @Override
        public void revert(EngineImpl engine) {
            super.revert(engine);
            for (Map.Entry<Hex, Field> entry : islandDiff.entrySet()) {
                engine.getIsland().putField(entry.getKey(), entry.getValue());
            }

            player.updateBuildingCount(buildingType, buildingCount);
            engine.playerIndex = playerIndex;
        }
    }
}
