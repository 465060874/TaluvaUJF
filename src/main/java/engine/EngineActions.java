package engine;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cette classe s'occupe d'analyser tous les coups jouables à un instant donné
 */
class EngineActions {

    private final Engine engine;

    List<SeaTileAction> seaTiles;
    List<VolcanoTileAction> volcanosTiles;
    List<PlaceBuildingAction> placeBuildings;
    List<ExpandVillageAction> expandVillages;

    EngineActions(Engine engine) {
        this.engine = engine;

        this.seaTiles = ImmutableList.of();
        this.volcanosTiles = ImmutableList.of();
        this.placeBuildings = ImmutableList.of();
        this.expandVillages = ImmutableList.of();
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
            Hex originHex = Hex.at(0, 0);
            this.seaTiles = ImmutableList.of(new SeaTileAction(originHex, Orientation.NORTH));
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
    }

    private void updateSeaTiles() {
        if (engine.getVolcanoTileStack().isEmpty()) {
            seaTiles = ImmutableList.of();
            return;
        }

        Island island = engine.getIsland();

        Set<SeaTileAction> set = new HashSet<>();
        for (Hex hex : island.getCoast()) {
            for (Orientation orientation : Orientation.values()) {
                if (!SeaTileRules.validate(island, hex, orientation).isValid()) {
                    continue;
                }

                for (int rotation = 0; rotation < 3; rotation++) {
                    set.add(new SeaTileAction(hex, orientation));
                    hex = hex.getLeftNeighbor(orientation);
                    orientation = orientation.leftRotation();
                }
            }
        }

        ImmutableList<SeaTileAction> list = ImmutableList.copyOf(set);

        this.seaTiles = EngineImpl.DEBUG
                ? Ordering.natural().immutableSortedCopy(list)
                : list;
    }

    private void updateVolcanoTiles() {
        if (engine.getVolcanoTileStack().isEmpty()) {
            volcanosTiles = ImmutableList.of();
            return;
        }

        Island island = engine.getIsland();
        VolcanoTile tile = engine.getVolcanoTileStack().current();

        ImmutableList.Builder<VolcanoTileAction> builder = ImmutableList.builder();
        for (Hex hex : island.getVolcanos()) {
            for (Orientation orientation : Orientation.values()) {
                if (!VolcanoTileRules.validate(island, hex, orientation).isValid()) {
                    continue;
                }

                builder.add(new VolcanoTileAction(hex, orientation));
            }
        }

        this.volcanosTiles = EngineImpl.DEBUG
                ? Ordering.natural().immutableSortedCopy(builder.build())
                : builder.build();
    }

    private void updatePlaceBuildings() {
        Island island = engine.getIsland();

        ImmutableList.Builder<PlaceBuildingAction> builder = ImmutableList.builder();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (!field.hasBuilding()) {

                if (PlaceBuildingRules.validate(engine, BuildingType.HUT, hex).isValid()) {
                    builder.add(new PlaceBuildingAction(BuildingType.HUT, hex));
                }
                if (PlaceBuildingRules.validate(engine, BuildingType.TEMPLE, hex).isValid()) {
                    builder.add(new PlaceBuildingAction(BuildingType.TEMPLE, hex));
                }
                if (PlaceBuildingRules.validate(engine, BuildingType.TOWER, hex).isValid()) {
                    builder.add(new PlaceBuildingAction(BuildingType.TOWER, hex));
                }
            }
        }

        this.placeBuildings = EngineImpl.DEBUG
                ? Ordering.natural().immutableSortedCopy(builder.build())
                : builder.build();
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
                    builder.add(new ExpandVillageAction(village, fieldType));
                }
            }
        }

        this.expandVillages = builder.build();
    }

    List<PlaceBuildingAction> getNewPlaceBuildingActions(TileAction action) {
        ImmutableList.Builder<PlaceBuildingAction> builder = ImmutableList.builder();
        Hex leftHex = action.getLeftHex();
        Hex rightHex = action.getLeftHex();
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            if (PlaceBuildingRules.validate(engine, type, leftHex).isValid()) {
                builder.add(new PlaceBuildingAction(type, leftHex));
            }
            if (PlaceBuildingRules.validate(engine, type, rightHex).isValid()) {
                builder.add(new PlaceBuildingAction(type, rightHex));
            }
        }

        return builder.build();
    }

    List<ExpandVillageAction> getNewExpandVillageActions(TileAction action) {
        ImmutableList.Builder<ExpandVillageAction> builder = ImmutableList.builder();

        Island island = engine.getIsland();
        PlayerColor color = engine.getCurrentPlayer().getColor();
        Hex leftHex = action.getLeftHex();
        Hex rightHex = action.getRightHex();
        FieldType leftFieldType = island.getField(leftHex).getType();
        FieldType rightFieldType = island.getField(rightHex).getType();

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
                ExpandVillageAction newAction = new ExpandVillageAction(village, fieldType);
                builder.add(newAction);
            }
        }

        return EngineImpl.DEBUG
                ? Ordering.natural().immutableSortedCopy(builder.build())
                : builder.build();
    }
}
