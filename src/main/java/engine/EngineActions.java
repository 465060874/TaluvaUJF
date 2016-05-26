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

    List<SeaTileAction> seaTiles;
    List<VolcanoTileAction> volcanosTiles;
    List<PlaceBuildingAction> placeBuildings;
    List<PlaceBuildingAction> newPlaceBuildings;
    List<ExpandVillageAction> expandVillages;
    List<ExpandVillageAction> newExpandVillages;

    EngineActions(Engine engine) {
        this.engine = engine;

        this.seaTiles = ImmutableList.of();
        this.volcanosTiles = ImmutableList.of();
        this.placeBuildings = ImmutableList.of();
        this.newPlaceBuildings = ImmutableList.of();
        this.expandVillages = ImmutableList.of();
        this.newExpandVillages = ImmutableList.of();
    }

    EngineActions(Engine engine, EngineActions actions) {
        this.engine = engine;

        this.seaTiles = actions.seaTiles;
        this.volcanosTiles = actions.volcanosTiles;
        this.placeBuildings = actions.placeBuildings;
        this.expandVillages = actions.expandVillages;
    }

    void updateAll() {
        if (engine.getIsland().isEmpty()) {
            VolcanoTile tile = engine.getVolcanoTileStack().current();
            Hex originHex = Hex.at(0, 0);
            this.seaTiles = ImmutableList.of(new SeaTileAction(tile, originHex, Orientation.NORTH));
            this.volcanosTiles = ImmutableList.of();
            this.placeBuildings = ImmutableList.of();
            this.expandVillages = ImmutableList.of();
        }
        else {
            updateSeaTiles();
            updateVolcanoTiles();
            updatePlaceBuildings();
            updateExpandVillages();
        }

        this.newPlaceBuildings= ImmutableList.of();
        this.newExpandVillages = ImmutableList.of();
    }

    void updateWithNewTile(TileAction action) {
        updatePlaceBuildings();
        updateExpandVillages();
        updateNewPlaceBuildings(action);
        updateNewExpandVillages(action);
    }

    private void updateSeaTiles() {
        Island island = engine.getIsland();
        VolcanoTile tile = engine.getVolcanoTileStack().current();

        HexMap<List<SeaTileAction>> seaTilesMap = HexMap.create();
        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                if (!SeaPlacementRules.validate(island, tile, hex, orientation).isValid()) {
                    continue;
                }

                List<SeaTileAction> list = seaTilesMap.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    seaTilesMap.put(hex, list);
                }

                list.add(new SeaTileAction(tile, hex, orientation));
            }
        }

        ImmutableList.Builder<SeaTileAction> builder = ImmutableList.builder();
        for (List<SeaTileAction> actions : seaTilesMap.values()) {
            builder.addAll(actions);
        }
        this.seaTiles = builder.build();
    }

    private void updateVolcanoTiles() {
        Island island = engine.getIsland();
        HexMap<List<VolcanoTileAction>> volcanoTilesMap = HexMap.create();

        VolcanoTile tile = engine.getVolcanoTileStack().current();
        for (Hex hex : island.getVolcanos()) {
            for (Orientation orientation : Orientation.values()) {
                if (!VolcanoPlacementRules.validate(island, tile, hex, orientation).isValid()) {
                    continue;
                }

                List<VolcanoTileAction> list = volcanoTilesMap.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    volcanoTilesMap.put(hex, list);
                }

                list.add(new VolcanoTileAction(tile, hex, orientation));
            }
        }

        ImmutableList.Builder<VolcanoTileAction> builder = ImmutableList.builder();
        for (List<VolcanoTileAction> actions : volcanoTilesMap.values()) {
            builder.addAll(actions);
        }
        this.volcanosTiles = builder.build();
    }

    private void updatePlaceBuildings() {
        Island island = engine.getIsland();

        HexMap<List<PlaceBuildingAction>> buildsMap = HexMap.create();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE) {
                boolean hutValid = BuildRules.validate(engine, BuildingType.HUT, hex);
                boolean templeValid = BuildRules.validate(engine, BuildingType.TEMPLE, hex);
                boolean towerValid = BuildRules.validate(engine, BuildingType.TOWER, hex);

                if (!hutValid && !templeValid && !towerValid) {
                    continue;
                }

                List<PlaceBuildingAction> list = buildsMap.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    buildsMap.put(hex, list);
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

        ImmutableList.Builder<PlaceBuildingAction> builder = ImmutableList.builder();
        for (List<PlaceBuildingAction> actions : buildsMap.values()) {
            builder.addAll(actions);
        }
        this.placeBuildings = builder.build();
    }

    private void updateExpandVillages() {
        Island island = engine.getIsland();
        Iterable<Village> villages = island.getVillages(engine.getCurrentPlayer().getColor());

        HexMap<List<ExpandVillageAction>> expandsMap = HexMap.create();
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
                        && ExpandRules.validate(engine, village, fieldType)) {
                    actions.add(new ExpandVillageAction(firstHex, fieldType));
                }
            }
            if (!actions.isEmpty()) {
                for (Hex hex : village.getHexes()) {
                    expandsMap.put(hex, actions);
                }
            }
        }

        ImmutableList.Builder<ExpandVillageAction> builder = ImmutableList.builder();
        for (List<ExpandVillageAction> actions : expandsMap.values()) {
            builder.addAll(actions);
        }
        this.expandVillages = builder.build();
    }

    private void updateNewPlaceBuildings(TileAction action) {
        ImmutableList.Builder<PlaceBuildingAction> builder = ImmutableList.builder();
        Hex leftHex = action.getLeftHex();
        Hex rightHex = action.getLeftHex();
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            if (BuildRules.validate(engine, type, leftHex)) {
                builder.add(new PlaceBuildingAction(type, leftHex));
            }
            if (BuildRules.validate(engine, type, rightHex)) {
                builder.add(new PlaceBuildingAction(type, rightHex));
            }
        }

        this.newPlaceBuildings = builder.build();
    }

    private void updateNewExpandVillages(TileAction action) {
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
            Building building = island.getField(hex).getBuilding();
            if (building.getType() != BuildingType.NONE
                    && building.getColor() == color) {
                villageExpansion.put(island.getVillage(hex), leftFieldType);
            }
        }
        for (Hex hex : rightHex.getNeighborhood()) {
            Building building = island.getField(hex).getBuilding();
            if (building.getType() != BuildingType.NONE
                    && building.getColor() == color) {
                villageExpansion.put(island.getVillage(hex), rightFieldType);
            }
        }

        for (Map.Entry<Village, FieldType> entry : villageExpansion.entries()) {
            Village village = entry.getKey();
            FieldType fieldType = entry.getValue();
            if (ExpandRules.validate(engine, village, fieldType)) {
                builder.add(new ExpandVillageAction(village, fieldType));
            }
        }

        this.newExpandVillages = builder.build();
    }
}
