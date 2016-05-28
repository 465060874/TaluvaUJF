package ui.island;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.Engine;
import engine.action.*;
import engine.rules.PlaceBuildingRules;
import engine.rules.TileRules;
import map.Field;
import map.Hex;
import map.Orientation;
import map.Village;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class Placement {


    public enum Mode {
        NONE,
        TILE,
        BUILDING,
        EXPAND_VILLAGE;
    }

    private final Engine engine;
    private final Grid grid;

    IslandCanvas islandCanvas;
    PlacementOverlay placementOverlay;

    double mouseX;
    double mouseY;

    Mode mode;
    Mode saveMode;

    boolean valid;
    Set<Hex> validHexes;
    Hex hex;

    VolcanoTile tile;
    Orientation tileOrientation;

    BuildingType buildingType;
    PlayerColor buildingColor;

    Village expansionVillage;
    FieldType expansionFieldType;
    Set<Hex> expansionHexes;

    public Placement(Engine engine, Grid grid) {
        this.engine = engine;
        this.grid = grid;

        this.mode = Mode.NONE;
        this.saveMode = Mode.NONE;
        this.valid = false;
        this.hex = Hex.at(0, 0);
    }

    public boolean isValid() {
        return valid;
    }

    public Hex getHex() {
        return hex;
    }

    public Action getAction() {
        checkState(mode != Mode.NONE && valid);
        if (mode == Mode.TILE) {
            return engine.getIsland().getField(hex) == Field.SEA
                    ? new SeaTileAction(tile, hex, tileOrientation)
                    : new VolcanoTileAction(tile, hex, tileOrientation);
        }
        else if (mode == Mode.BUILDING) {
            return new PlaceBuildingAction(buildingType, hex);
        }
        else if (mode == Mode.EXPAND_VILLAGE) {
            return new ExpandVillageAction(expansionVillage, engine.getIsland().getField(hex).getType());
        }

        throw new IllegalStateException();
    }

    public void placeTile(VolcanoTile tile) {
        this.mode = Mode.TILE;
        this.tile = tile;
        this.tileOrientation = Orientation.NORTH;

        this.validHexes = new HashSet<>();
        for (TileAction action : engine.getVolcanoTileActions()) {
            validHexes.add(action.getVolcanoHex());
            validHexes.add(action.getLeftHex());
            validHexes.add(action.getRightHex());
        }

        updateValidTile();
        islandCanvas.redraw();
    }

    public void build(PlayerColor color) {
        this.mode = Mode.BUILDING;
        this.buildingType = BuildingType.HUT;
        this.buildingColor = color;

        this.validHexes = new HashSet<>();
        for (PlaceBuildingAction action : engine.getPlaceBuildingActions()) {
            validHexes.add(action.getHex());
        }
        for (ExpandVillageAction action : engine.getExpandVillageActions()) {
            validHexes.addAll(action.getVillage(engine.getIsland()).getHexes());
        }

        updateValidBuilding();
        islandCanvas.redraw();
    }

    public void expand(Village village) {
        this.mode = Mode.EXPAND_VILLAGE;
        this.expansionFieldType = null;
        this.expansionVillage = village;
        this.expansionHexes = ImmutableSet.of();

        placementOverlay.redraw();
        islandCanvas.redraw();
    }

    public void cancel() {
        this.mode = Mode.NONE;
        if (valid) {
            islandCanvas.redraw();
        }
        else {
            placementOverlay.redraw();
        }
    }

    public void cycleTileOrientationOrBuildingType() {
        if (mode == Mode.TILE) {
            tileOrientation = tileOrientation.clockWise();
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            if (buildingType == BuildingType.HUT) {
                buildingType = BuildingType.TEMPLE;
            }
            else if (buildingType == BuildingType.TEMPLE) {
                buildingType = BuildingType.TOWER;
            }
            else if (buildingType == BuildingType.TOWER) {
                buildingType = BuildingType.HUT;
            }
            updateValidBuilding();
        }
        else if (mode == Mode.EXPAND_VILLAGE) {
            mode = Mode.BUILDING;
            updateValidExpansion();
        }
    }

    public void updateMouse(double x, double y, double width, double height) {
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
            updateValidExpansion();
        }
    }

    private void updateValidTile() {
        boolean wasValid = valid;
        this.valid = TileRules.validate(engine.getIsland(), tile, hex, tileOrientation).isValid();

        redrawWhatsNecessary(wasValid);
    }

    private void updateValidBuilding() {
        boolean wasValid = valid;
        this.valid = PlaceBuildingRules.validate(engine, buildingType, hex);

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

    private void updateValidExpansion() {
        for (Map.Entry<FieldType, Set<Hex>> entry : Multimaps.asMap(expansionVillage.getExpandableHexes()).entrySet()) {
            if (entry.getValue().contains(hex)) {
                if (expansionFieldType == entry.getKey()) {
                    return;
                }

                expansionFieldType = entry.getKey();
                expansionHexes = ImmutableSet.copyOf(entry.getValue());
                islandCanvas.redraw();
                valid = !expansionHexes.isEmpty();
                return;
            }
        }

        expansionFieldType = null;
        expansionHexes = ImmutableSet.of();
        valid = false;
        islandCanvas.redraw();
    }

    public void saveMode() {
        /*saveMode = mode;
        mode = Mode.NONE;
        updateValidTile();*/
    }

    public void restoreMode() {
        /*mode = saveMode;
        if (mode == Mode.TILE) {
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            updateValidBuilding();
        }*/
    }
}
