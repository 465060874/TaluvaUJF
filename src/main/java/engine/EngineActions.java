package engine;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.*;
import engine.rules.ExpandVillageRules;
import engine.rules.PlaceBuildingRules;
import engine.rules.SeaTileRules;
import engine.rules.VolcanoTileRules;
import map.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Cette classe s'occupe d'analyser tous les coups jouables à un instant donné
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

        HashSet<SeaTileAction> seaTilesMap = new HashSet<>();

        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                if (!SeaTileRules.validate(island, tile, hex, orientation).isValid()) {
                    continue;
                }

                for (int rotation = 0; rotation < 3; rotation++) {
                    seaTilesMap.add(new SeaTileAction(tile, hex, orientation));
                    hex = hex.getLeftNeighbor(orientation);
                    orientation = orientation.leftRotation();
                }
            }
        }

        ImmutableList.Builder<SeaTileAction> builder = ImmutableList.builder();
        builder.addAll(seaTilesMap);

        this.seaTiles = builder.build();
    }

    private void updateVolcanoTiles() {
        Island island = engine.getIsland();
        HexMap<List<VolcanoTileAction>> volcanoTilesMap = HexMap.create();

        VolcanoTile tile = engine.getVolcanoTileStack().current();
        for (Hex hex : island.getVolcanos()) {
            for (Orientation orientation : Orientation.values()) {
                if (!VolcanoTileRules.validate(island, tile, hex, orientation).isValid()) {
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
            if (!field.hasBuilding()) {
                boolean hutValid = PlaceBuildingRules.validate(engine, BuildingType.HUT, hex).isValid();
                boolean templeValid = PlaceBuildingRules.validate(engine, BuildingType.TEMPLE, hex).isValid();
                boolean towerValid = PlaceBuildingRules.validate(engine, BuildingType.TOWER, hex).isValid();

                if (!hutValid && !templeValid && !towerValid) {
                    continue;
                }

                List<PlaceBuildingAction> list = buildsMap.getOrDefault(hex, null);
                if (list == null) {
                    list = new ArrayList<>();
                    buildsMap.put(hex, list);
                }

                if (hutValid) {
                    list.add(new PlaceBuildingAction(false, BuildingType.HUT, hex));
                }
                if (templeValid) {
                    list.add(new PlaceBuildingAction(false, BuildingType.TEMPLE, hex));
                }
                if (towerValid) {
                    list.add(new PlaceBuildingAction(false, BuildingType.TOWER, hex));
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

        ImmutableList.Builder<ExpandVillageAction> builder = ImmutableList.builder();
        for (Village village : villages) {
            for (FieldType fieldType : FieldType.values()) {
                if (fieldType == FieldType.VOLCANO) {
                    continue;
                }

                if (ExpandVillageRules.validate(engine, village, fieldType).isValid()) {
                    builder.add(new ExpandVillageAction(false, village, fieldType));
                }
            }
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

            if (PlaceBuildingRules.validate(engine, type, leftHex).isValid()) {
                builder.add(new PlaceBuildingAction(true, type, leftHex));
            }
            if (PlaceBuildingRules.validate(engine, type, rightHex).isValid()) {
                builder.add(new PlaceBuildingAction(true, type, rightHex));
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
            if (island.getField(hex).hasBuilding(color)) {
                villageExpansion.put(island.getVillage(hex), leftFieldType);
            }
        }
        for (Hex hex : rightHex.getNeighborhood()) {
            if (island.getField(hex).hasBuilding(color)) {
                villageExpansion.put(island.getVillage(hex), rightFieldType);
            }
        }

        for (Map.Entry<Village, FieldType> entry : villageExpansion.entries()) {
            Village village = entry.getKey();
            FieldType fieldType = entry.getValue();
            if (ExpandVillageRules.validate(engine, village, fieldType).isValid()) {
                builder.add(new ExpandVillageAction(true, village, fieldType));
            }
        }

        this.newExpandVillages = builder.build();
    }
}
