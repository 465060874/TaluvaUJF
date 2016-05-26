package ui.island;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.rules.TileRules;
import map.*;

import java.util.Map;
import java.util.Set;

public class Placement {

    private Mode saveMode;

    public enum Mode {
        NONE,
        TILE,
        BUILDING,
        EXPAND_VILLAGE;
    }

    private final Island island;
    private final Grid grid;

    IslandCanvas islandCanvas;
    PlacementOverlay placementOverlay;

    double mouseX;
    double mouseY;

    Mode mode;
    boolean valid;
    Hex hex;

    VolcanoTile tileFields;
    Orientation tileOrientation;

    BuildingType buildingType;
    PlayerColor buildingColor;

    Village expansionVillage;
    FieldType expansionFieldType;
    Set<Hex> expansionHexes;

    public Placement(Island island, Grid grid) {
        this.island = island;
        this.grid = grid;

        this.mode = Mode.NONE;
        this.saveMode = Mode.NONE;
        this.valid = false;
        this.hex = Hex.at(0, 0);

        this.tileFields = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);
        this.tileOrientation = Orientation.NORTH;

        this.buildingType = BuildingType.HUT;
        this.buildingColor = PlayerColor.RED;
    }

    public void cycleMode() {
        if (mode == Mode.NONE) {
            mode = Mode.TILE;
            updateValidTile();
        }
        else if (mode == Mode.TILE) {
            mode = Mode.BUILDING;
            updateValidBuilding();
            islandCanvas.redraw();
        }
        else {
            if (mode == Mode.BUILDING && island.getField(hex).getBuilding().getType() != BuildingType.NONE) {
                expand(island.getVillage(hex));
            }
            else {
                mode = Mode.NONE;
                islandCanvas.redraw();
                placementOverlay.redraw();
            }
        }
    }

    public void expand(Village village) {
        this.mode = Mode.EXPAND_VILLAGE;
        this.expansionFieldType = null;
        this.expansionVillage = village;
        this.expansionHexes = ImmutableSet.of();

        placementOverlay.redraw();
        islandCanvas.redraw();
    }

    void cycleTileOrientationOrBuildingTypeAndColor() {
        if (mode == Mode.TILE) {
            tileOrientation = tileOrientation.clockWise();
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            if (buildingType == BuildingType.HUT) {
                buildingType = BuildingType.TEMPLE;
                buildingColor = PlayerColor.values()[(buildingColor.ordinal() + 1) % PlayerColor.values().length];
            }
            else if (buildingType == BuildingType.TEMPLE) {
                buildingType = BuildingType.TOWER;
            }
            else if (buildingType == BuildingType.TOWER) {
                buildingType = BuildingType.HUT;
                buildingColor = PlayerColor.values()[(buildingColor.ordinal() + 1) % PlayerColor.values().length];
            }
            updateValidBuilding();
        }
    }

    void updateMouse(double x, double y, double width, double height) {
        this.mouseX = x;
        this.mouseY = y;

        Hex newHex = grid.xyToHex(x, y, width, height);

        if (newHex.equals(hex)) {
            if (!valid) {
                placementOverlay.redraw();
            }
            return;
        }

        hex = newHex;
        if (mode == Mode.TILE) {
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            updateValidBuilding();
        }
        else if (mode == Mode.EXPAND_VILLAGE) {
            updateExpandedHexes();
        }
    }

    private void updateValidTile() {
        boolean wasValid = valid;
        this.valid = TileRules.validate(island, tileFields, hex, tileOrientation).isValid();

        redrawWhatsNecessary(wasValid);
    }

    private void updateValidBuilding() {
        boolean wasValid = valid;
        Field field = island.getField(hex);
        this.valid = field != Field.SEA
                && field.getBuilding().getType() == BuildingType.NONE;

        redrawWhatsNecessary(wasValid);
    }

    private void redrawWhatsNecessary(boolean wasValid) {
        if (wasValid != valid) {
            placementOverlay.redraw();
            islandCanvas.redraw();
        }
        else if (valid) {
            islandCanvas.redraw();
        }
        else {
            placementOverlay.redraw();
        }
    }

    private void updateExpandedHexes() {
        for (Map.Entry<FieldType, Set<Hex>> entry : Multimaps.asMap(expansionVillage.getExpandableHexes()).entrySet()) {
            if (entry.getValue().contains(hex)) {
                if (expansionFieldType == entry.getKey()) {
                    return;
                }

                expansionFieldType = entry.getKey();
                expansionHexes = ImmutableSet.copyOf(entry.getValue());
                islandCanvas.redraw();
                return;
            }
        }

        expansionFieldType = null;
        expansionHexes = ImmutableSet.of();
        islandCanvas.redraw();
    }

    void saveMode() {
        saveMode = mode;
        mode = Mode.NONE;
        updateValidTile();
    }

    void restoreMode() {
        mode = saveMode;
        if (mode == Mode.TILE) {
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            updateValidBuilding();
        }
    }
}
