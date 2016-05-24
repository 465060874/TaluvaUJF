package ui;

import engine.Engine;
import javafx.scene.layout.StackPane;
import ui.island.IslandView;

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
    }
}
