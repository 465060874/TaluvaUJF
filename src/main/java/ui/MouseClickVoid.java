package ui;

import javafx.scene.Node;
import javafx.scene.Parent;

public class MouseClickVoid {

    /*public static boolean turnOffPickOnBoundsFor(Node n, boolean plotContent) {
        boolean result = false;
        boolean plotContentFound = false;
        n.setPickOnBounds(false);
        if (!plotContent) {
            if (containsStyle(n)) {
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

    private static boolean containsStyle(Node node){
        boolean result = false;
        for (String object : node.getStyleClass()) {
            if(object.equals("plot-content")){
                result = true;
                break;
            }
        }
        return result;
    }*/

    /*public static void install(Node node) {
        node.addEventFilter(MOUSE_);
    }*/
}
