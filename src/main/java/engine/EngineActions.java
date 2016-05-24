package engine;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.*;
import engine.rules.BuildRules;
import engine.rules.ExpandRules;
import engine.rules.SeaPlacementRules;
import engine.rules.VolcanoPlacementRules;
import map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class keeps lists of all possible actions for the current player
 */
class EngineActions {

    private final Engine engine;
    HexMap<List<SeaTileAction>> seaTile;
    HexMap<List<VolcanoTileAction>> volcanosTile;
    HexMap<List<PlaceBuildingAction>> build;
    HexMap<List<ExpandVillageAction>> expand;

    EngineActions(Engine engine) {
        this.engine = engine;

        this.seaTile = HexMap.create();
        this.volcanosTile = HexMap.create();
        this.build = HexMap.create();
        this.expand = HexMap.create();
    }

    EngineActions(EngineActions actions) {
        this.engine = actions.engine;

        this.seaTile = actions.seaTile;
        this.volcanosTile = actions.volcanosTile;
        this.build = actions.build;
        this.expand = actions.expand;
    }

    void updateAll() {
        updateSeaTile();
        updateVolcanoTile();
        updateBuild();
        updateExpand();
    }

    void updateBuilding() {
        updateBuild();
        updateExpand();
    }

    private void updateSeaTile() {
        VolcanoTile tile = engine.getVolcanoTileStack().current();
        if (engine.getStatus().getTurn() == 0) {
            Hex originHex = Hex.at(0, 0);
            seaTile = HexMap.create();
            seaTile.put(originHex, ImmutableList.of(new SeaTileAction(tile, originHex, Orientation.NORTH)));
            return;
        }

        Island island = engine.getIsland();
        HexMap<List<SeaTileAction>> tmpSeaPlacements = HexMap.create();

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

                list.add(new SeaTileAction(tile, hex, orientation));
            }
        }

        this.seaTile = tmpSeaPlacements;
    }

    private void updateVolcanoTile() {
        if (engine.getStatus().getTurn() == 0) {
            volcanosTile = HexMap.create();
            return;
        }

        Island island = engine.getIsland();
        HexMap<List<VolcanoTileAction>> tmpVolcanosPlacements = HexMap.create();

        VolcanoTile tile = engine.getVolcanoTileStack().current();
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

                list.add(new VolcanoTileAction(tile, hex, orientation));
            }
        }

        this.volcanosTile = tmpVolcanosPlacements;
    }

    private void updateBuild() {
        Island island = engine.getIsland();
        HexMap<List<PlaceBuildingAction>> tmpBuildActions = HexMap.create();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE) {
                boolean hutValid = BuildRules.validate(engine, BuildingType.HUT, hex);
                boolean templeValid = BuildRules.validate(engine, BuildingType.TEMPLE, hex);
                boolean towerValid = BuildRules.validate(engine, BuildingType.TOWER, hex);

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

        this.build = tmpBuildActions;
    }

    private void updateExpand() {
        Island island = engine.getIsland();
        HexMap<List<ExpandVillageAction>> tmpExpandActions = HexMap.create();
        Iterable<Village> villages = island.getVillages(engine.getCurrentPlayer().getColor());
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
                        && ExpandRules.canExpandVillage(engine, village, fieldType)) {
                    actions.add(new ExpandVillageAction(firstHex, fieldType));
                }
            }
            if (!actions.isEmpty()) {
                for (Hex hex : village.getHexes()) {
                    tmpExpandActions.put(hex, actions);
                }
            }
        }

        this.expand = tmpExpandActions;
    }

    List<PlaceBuildingAction> getBuildActions(TileAction action) {
        ImmutableList.Builder<PlaceBuildingAction> builder = ImmutableList.builder();
        Hex leftHex = action.getLeftHex();
        Hex rightHex = action.getLeftHex();
        for (BuildingType type : BuildingType.values()) {
            if (BuildRules.validate(engine, type, leftHex)) {
                builder.add(new PlaceBuildingAction(type, leftHex));
            }
            if (BuildRules.validate(engine, type, rightHex)) {
                builder.add(new PlaceBuildingAction(type, rightHex));
            }
        }

        return builder.build();
    }

    List<ExpandVillageAction> getExpandActions(TileAction action) {
        ImmutableList.Builder<ExpandVillageAction> builder = ImmutableList.builder();

        Island island = engine.getIsland();
        PlayerColor color = engine.getCurrentPlayer().getColor();
        Hex leftHex = action.getLeftHex();
        Hex rightHex = action.getLeftHex();
        FieldType leftFieldType = action.getLeftFieldType();
        FieldType rightFieldType = action.getRightFieldType();

        // NB: Village do not implements hashCode/equals
        // This store them by identity instead of equality, which is what we want
        HashMultimap<Village, FieldType> villageExpansion = HashMultimap.create();
        for (Hex hex : leftHex.getNeighborhood()) {
            FieldBuilding building = island.getField(hex).getBuilding();
            if (building.getType() != BuildingType.NONE
                    && building.getColor() == color) {
                villageExpansion.put(island.getVillage(hex), leftFieldType);
            }
        }
        for (Hex hex : rightHex.getNeighborhood()) {
            FieldBuilding building = island.getField(hex).getBuilding();
            if (building.getType() != BuildingType.NONE
                    && building.getColor() == color) {
                villageExpansion.put(island.getVillage(hex), rightFieldType);
            }
        }

        for (Map.Entry<Village, FieldType> entry : villageExpansion.entries()) {
            Village village = entry.getKey();
            FieldType fieldType = entry.getValue();
            if (ExpandRules.canExpandVillage(engine, village, fieldType)) {
                builder.add(new ExpandVillageAction(village, fieldType));
            }
        }

        return builder.build();
    }
}
