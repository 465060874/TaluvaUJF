package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

class StackTileIcon extends Canvas {

    private PlacementOverlay placementOverlay;

    StackTileIcon(PlacementOverlay placementOverlay) {
        super(0, 0);
        this.placementOverlay = placementOverlay;
    }


    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

}
