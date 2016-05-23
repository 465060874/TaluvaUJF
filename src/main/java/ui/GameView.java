package ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class GameView extends StackPane {
    IslandView islandView;
    Hud hud;

    public GameView(IslandView islandView, Hud hud) {
        this.islandView = islandView;
        this.hud = hud;

        this.getChildren().add(islandView);
        this.getChildren().add(hud);
        turnOffPickOnBoundsFor(hud, true);
    }

    private boolean turnOffPickOnBoundsFor(Node n, boolean plotContent) {
        boolean result = false;
        boolean plotContentFound = false;
        n.setPickOnBounds(false);
        if(!plotContent){
            if(containsStyle(n)){
                plotContentFound = true;
                result=true;
            }
            if (n instanceof Parent) {
                for (Node c : ((Parent) n).getChildrenUnmodifiable()) {
                    if(turnOffPickOnBoundsFor(c,plotContentFound)){
                        result = true;
                    }
                }
            }
            n.setMouseTransparent(!result);
        }
        return result;
    }

    private boolean containsStyle(Node node){
        boolean result = false;
        for (String object : node.getStyleClass()) {
            if(object.equals("plot-content")){
                result = true;
                break;
            }
        }
        return result;
    }
}
