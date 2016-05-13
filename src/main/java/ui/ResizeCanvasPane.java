package ui;

import javafx.scene.layout.Pane;

class ResizeCanvasPane extends Pane {

    private final MapCanvas canvas;
 
    ResizeCanvasPane(MapCanvas canvas) {
        this.canvas = canvas;
        getChildren().add(canvas);
    }
 
    @Override protected void layoutChildren() {
        final int top = (int)snappedTopInset();
        final int right = (int)snappedRightInset();
        final int bottom = (int)snappedBottomInset();
        final int left = (int)snappedLeftInset();
        final int w = (int)getWidth() - left - right;
        final int h = (int)getHeight() - top - bottom;

        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            canvas.redraw();
        }
    }
}