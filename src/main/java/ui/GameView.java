package ui;

import engine.Engine;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import theme.IslandTheme;
import ui.hud.Hud;
import ui.island.Grid;
import ui.island.IslandView;
import ui.island.Placement;

public class GameView extends StackPane {

    private final Engine engine;
    private final Placement placement;
    private final IslandView islandView;
    private final Hud hud;

    public GameView(Engine engine) {
        this.engine = engine;
        Grid grid = new Grid();
        this.placement = new Placement(engine, grid);
        this.islandView = new IslandView(engine.getIsland(), grid, placement, false);
        this.hud = new Hud(engine, placement);
        engine.registerObserver(new EngineObserver());

        hud.setPickOnBounds(false);
        getChildren().addAll(islandView, hud);

        setOnMouseMoved(this::mouseMoved);
        setOnMouseExited(this::mouseExited);
        setOnMouseEntered(this::mouseEntered);
        setOnKeyPressed(this::keyPressed);
    }

    private void mouseExited(MouseEvent event) {
        placement.saveMode();
    }

    private void mouseEntered(MouseEvent event) {
        placement.restoreMode();
    }

    private void mouseMoved(MouseEvent event) {
        placement.updateMouse(event.getX(), event.getY(), getWidth(), getHeight());
    }

    private void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.T) {
            IslandTheme.change();
        }
    }

    public Placement getPlacement() {
        return placement;
    }

    public Button getHomeButton() {
        return hud.getHomeButton();
    }

    public Button getSaveButton() {
        return hud.getSaveButton();
    }

    public boolean isMouseDragged(){
        return islandView.isMouseDragThresholdReached();
    }

    private class EngineObserver extends engine.EngineObserver.Dummy {

        @Override
        public void onCancelTileStep() {
            placement.deleteLastPlacedHexes();
        }

        @Override
        public void onCancelBuildStep() {
            placement.deleteLastPlacedBuildings();
        }

        @Override
        public void onTileStepStart() {
            islandView.redrawIsland();
        }

        @Override
        public void onBuildStepStart() {
            islandView.redrawIsland();
        }

        @Override
        public void onSeaTileAction(SeaTileAction action) {
            placement.setLastPlacedHexes(action.getVolcanoHex(), action.getLeftHex(), action.getRightHex());
            islandView.redrawIsland();
        }

        @Override
        public void onVolcanoTileAction(VolcanoTileAction action) {
            placement.setLastPlacedHexes(action.getVolcanoHex(), action.getLeftHex(), action.getRightHex());
            islandView.redrawIsland();
        }

        @Override
        public void onPlaceBuildingAction(PlaceBuildingAction action) {
            placement.setLastPlacedBuildings(action.getHex());
            islandView.redrawIsland();
        }

        @Override
        public void onExpandVillageAction(ExpandVillageAction action) {
            islandView.redrawIsland();
        }

        @Override
        public void beforeExpandVillageAction(ExpandVillageAction action) {
            placement.setLastPlacedBuildings(action.getVillage(engine.getIsland()).getExpandableHexes().get(action.getFieldType()));
        }
    }
}
