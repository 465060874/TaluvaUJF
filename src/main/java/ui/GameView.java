package ui;

import engine.Engine;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import ui.hud.Hud;
import ui.island.IslandView;
import ui.theme.Theme;

public class GameView extends StackPane {

    private final Engine engine;
    private final IslandView islandView;
    private final Hud hud;

    public GameView(Engine engine) {
        this.engine = engine;
        this.islandView = new IslandView(engine.getIsland(), false);
        this.hud = new Hud(engine);

        hud.setPickOnBounds(false);
        getChildren().addAll(islandView, hud);

        setOnKeyPressed(this::keyPressed);
    }

    private void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.T) {
            Theme.change();
        }
    }
}
