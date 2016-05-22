package ui;

import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.rules.PlacementRules;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

public class Placement {

    public enum Mode {
        NONE,
        TILE,
        BUILDING;
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

    public Placement(Island island, Grid grid) {
        this.island = island;
        this.grid = grid;

        this.mode = Mode.NONE;
        this.valid = false;
        this.hex = Hex.at(0, 0);

        this.tileFields = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);
        this.tileOrientation = Orientation.NORTH;

        this.buildingType = BuildingType.HUT;
        this.buildingColor = PlayerColor.RED;
    }

    public void cyclePrimary() {
        if (mode == Mode.NONE) {
            mode = Mode.TILE;
            updateValidTile();
        }
        else if (mode == Mode.TILE) {
            mode = Mode.BUILDING;
            updateValidBuilding();
        }
        else {
            mode = Mode.NONE;
            islandCanvas.redraw();
            placementOverlay.redraw();
        }
    }

    public void cycleSecondary() {
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

    public void updateMouse(double x, double y, double width, double height) {
        this.mouseX = x;
        this.mouseY = y;

        Hex newHex = grid.getHex(x, y, width, height);

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
    }

    private void updateValidTile() {
        boolean wasValid = valid;
        this.valid = PlacementRules.validate(island, tileFields, hex, tileOrientation).isValid();

        if (wasValid != valid) {
            placementOverlay.redraw();
            islandCanvas.redraw();
        }
        else {
            redraw();
        }
    }

    private void updateValidBuilding() {
        boolean wasValid = valid;
        Field field = island.getField(hex);
        this.valid = field != Field.SEA
                && field.getBuilding().getType() == BuildingType.NONE;
        if (wasValid != valid) {
            placementOverlay.redraw();
            islandCanvas.redraw();
        }
        else {
            redraw();
        }
    }

    void redraw() {
        if (valid) {
            islandCanvas.redraw();
        }
        else {
            placementOverlay.redraw();
        }
    }
}
