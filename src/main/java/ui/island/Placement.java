package ui.island;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.Engine;
import engine.action.*;
import engine.rules.*;
import map.Field;
import map.Hex;
import map.Orientation;
import map.Village;
import ui.hud.Hud;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO: Retravailler pour en faire un model depuis lesquelles
 * TODO: les differents composants grpahiques tirent leurs
 * TODO: informations avec le système Observer/Observable
 */
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
    Hud hud;

    double mouseX;
    double mouseY;

    Mode mode;
    Mode saveMode;

    Problem problem;
    Set<Hex> validHexes;
    Hex hex;

    VolcanoTile tile;
    Orientation tileOrientation;

    BuildingType buildingType;
    PlayerColor buildingColor;

    Village expansionVillage;
    FieldType expansionFieldType;
    Set<Hex> expansionHexes;

    private ArrayList<Hex> lastPlacedHexes;
    private ArrayList<Hex> lastPlacedBuildings;

    public Placement(Engine engine, Grid grid) {
        this.engine = engine;
        this.grid = grid;

        this.mode = Mode.NONE;
        this.saveMode = Mode.NONE;
        this.problem = Problem.NONE;
        this.hex = Hex.at(0, 0);
        this.lastPlacedHexes = new ArrayList<>(3);
        this.lastPlacedBuildings = new ArrayList<>();
    }

    public void initHud(Hud hud) {
        this.hud = hud;
    }

    public PlayerColor getActivePlayerColor() {
        return engine.getCurrentPlayer().getColor();
    }

    public boolean isValid() {
        return problem.isValid();
    }

    public Problem getProblem() {
        return problem;
    }

    public Hex getHex() {
        return hex;
    }

    public Action getAction() {
        checkState(mode != Mode.NONE && isValid());
        if (mode == Mode.TILE) {

            return engine.getIsland().getField(hex) == Field.SEA
                    ? new SeaTileAction(hex, tileOrientation)
                    : new VolcanoTileAction(hex, tileOrientation);
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
        if (problem.isValid()) {
            islandCanvas.redraw();
        }
        else {
            placementOverlay.redraw();
        }

        this.problem = Problem.NONE;
        hud.updateProblems();
    }

    public void cycleTileOrientationOrBuildingType() {
        if (mode == Mode.TILE) {
            tileOrientation = tileOrientation.nextClockWise();
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            buildingType = buildingType.nextBuilding();
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
            if (!problem.isValid()) {
                placementOverlay.redraw();
            }
            return;
        }

        Field oldField = engine.getIsland().getField(hex);
        hex = newHex;
        if (mode == Mode.TILE) {
            updateValidTile();
        }
        else if (mode == Mode.BUILDING) {
            updateValidBuilding();
            Field field = engine.getIsland().getField(hex);
            if (oldField.hasBuilding() != field.hasBuilding()) {
                islandCanvas.redraw();
            }
        }
        else if (mode == Mode.EXPAND_VILLAGE) {
            updateValidExpansion();
        }
    }

    private void updateValidTile() {
        boolean wasValid = isValid();
        problem = TileRules.validate(engine.getIsland(), hex, tileOrientation);
        hud.updateProblems();

        redrawWhatsNecessary(wasValid);
    }

    private void updateValidBuilding() {
        boolean wasValid = isValid();
        this.problem = PlaceBuildingRules.validate(engine, buildingType, hex);
        hud.updateProblems();
        redrawWhatsNecessary(wasValid);
    }

    private void redrawWhatsNecessary(boolean wasValid) {
        if (wasValid != isValid()) {
            placementOverlay.redraw();
            islandCanvas.redraw();
        }
        else if (isValid()) {
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
                problem = ExpandVillageRules.validate(engine, expansionVillage, expansionFieldType);
                hud.updateProblems();
                return;
            }
        }

        expansionFieldType = null;
        expansionHexes = ImmutableSet.of();
        problem = Problem.EXPAND_NO_ADJACENT_TILE;
        hud.updateProblems();
        islandCanvas.redraw();
    }

    public boolean changeForbiddenPlacementDraw() {
        boolean active;
        if (islandCanvas.hasForbiddenPlacementVisible()) {
            islandCanvas.setForbiddenPlacementInvisible();
            active = false;
        } else {
            islandCanvas.setForbiddenPlacementVisible();
            active = true;
        }
        islandCanvas.redraw();
        return active;
    }

    public boolean changeForbiddenBuildingsDraw() {
        boolean active;
        if (islandCanvas.hasForbiddenBuildingsVisible()) {
            islandCanvas.setForbiddenBuildingsInvisible();
            active = false;
        } else {
            islandCanvas.setForbiddenBuildingsVisible();
            active = true;
        }
        islandCanvas.redraw();
        return active;
    }

    public boolean isLastPlaced(Hex hex) {
        return lastPlacedHexes.contains(hex);
    }

    public void setLastPlacedHexes(Hex volcanoHex, Hex leftHex, Hex rightHex) {
        lastPlacedHexes = new ArrayList<>(3);
        lastPlacedHexes.add(volcanoHex);
        lastPlacedHexes.add(leftHex);
        lastPlacedHexes.add(rightHex);
    }

    public void deleteLastPlacedHexes() {
        lastPlacedHexes = new ArrayList<>(3);
    }

    public boolean isLastPlacedBuildings(Hex hex) {
        return lastPlacedBuildings.contains(hex);
    }

    public void setLastPlacedBuildings(Hex hex) {
        lastPlacedBuildings = new ArrayList<>();
        lastPlacedBuildings.add(hex);
    }

    public void setLastPlacedBuildings(Set<Hex> hices) {
        lastPlacedBuildings = new ArrayList<>();
        lastPlacedBuildings.addAll(hices);
    }

    public void deleteLastPlacedBuildings() {
        lastPlacedBuildings = new ArrayList<>();
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
