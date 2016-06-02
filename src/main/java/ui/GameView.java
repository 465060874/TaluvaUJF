package ui;

import engine.Engine;
import engine.EngineObserver;
import engine.EngineStatus;
import engine.Player;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import ui.hud.Hud;
import ui.island.Grid;
import ui.island.IslandView;
import ui.island.Placement;
import theme.IslandTheme;

import java.util.List;

public class GameView extends StackPane implements EngineObserver {

    private final Engine engine;
    private final Placement placement;
    private final IslandView islandView;
    private final Hud hud;

    public GameView(Engine engine) {
        this.engine = engine;
        Grid grid = new Grid();
        this.placement = new Placement(engine, grid);
        this.islandView = new IslandView(engine.getIsland(), grid, placement, false);
        this.hud = new Hud(engine);
        engine.registerObserver(this);

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

    @Override
    public void onStart() {
    }

    @Override
    public void onCancelTileStep() {
    }

    @Override
    public void onCancelBuildStep() {
    }

    @Override
    public void onTileStackChange() {
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
    public void onTilePlacementOnSea(SeaTileAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onEliminated(Player eliminated) {
    }

    @Override
    public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
    }

    public boolean isMouseDragged(){
        return islandView.isMouseDragThresholdReached();
    }
}
