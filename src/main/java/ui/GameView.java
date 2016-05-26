package ui;

import engine.Engine;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import ui.hud.Hud;
import ui.island.Grid;
import ui.island.IslandView;
import ui.island.Placement;
import ui.theme.Theme;

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
        this.hud = new Hud(engine);

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
            Theme.change();
        }
    }

    public Placement getPlacement() {
        return placement;
    }
}
