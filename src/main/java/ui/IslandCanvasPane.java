package ui;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;

class IslandCanvasPane extends StackPane {

    private final IslandCanvas canvas;
    private final FreeTileCanvas freeTileCanvas;

    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;
    private double scale;

    IslandCanvasPane(IslandCanvas canvas) {
        this.canvas = canvas;
        this.mouseX = 0;
        this.mouseY = 0;
        this.scale = 1;
        getChildren().add(canvas);
        freeTileCanvas = null;

        BackgroundFill backgroundFill = new BackgroundFill(
                IslandCanvas.BG_COLOR,
                CornerRadii.EMPTY,
                Insets.EMPTY);
        setBackground(new Background(backgroundFill));

        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnScroll(this::scroll);
    }
 
    IslandCanvasPane(IslandCanvas canvas, FreeTileCanvas freeTileCanvas) {
        this.canvas = canvas;
        this.freeTileCanvas = freeTileCanvas;
        this.mouseX = 0;
        this.mouseY = 0;
        this.scale = 1;
        getChildren().add(canvas);
        //getChildren().add(freeTileCanvas);

        BackgroundFill backgroundFill = new BackgroundFill(
                IslandCanvas.BG_COLOR,
                CornerRadii.EMPTY,
                Insets.EMPTY);
        setBackground(new Background(backgroundFill));

        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnScroll(this::scroll);
    }

    @Override
    protected void layoutChildren() {
        final int top = (int)snappedTopInset();
        final int right = (int)snappedRightInset();
        final int bottom = (int)snappedBottomInset();
        final int left = (int)snappedLeftInset();
        final int w = (int)getWidth() - left - right;
        final int h = (int)getHeight() - top - bottom;

        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w * 3);
            canvas.setHeight(h * 3);
            canvas.redraw();
        }
    }

    private void mousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        mouseXBeforeDrag = mouseX = event.getX();
        mouseYBeforeDrag = mouseY = event.getY();
    }

    private void mouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        canvas.setTranslateX(canvas.getTranslateX()
                + event.getX() - mouseX);
        canvas.setTranslateY(canvas.getTranslateY()
                + event.getY() - mouseY);
        mouseX = event.getX();
        mouseY = event.getY();
    }

    private void mouseReleased(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        canvas.ox += mouseXBeforeDrag - event.getX();
        canvas.oy += mouseYBeforeDrag - event.getY();
        canvas.redraw();
    }

    private void scroll(ScrollEvent event) {
        if (canvas.getScaleX() > 1.5
                || canvas.getScaleX() < (1/1.5)) {
            canvas.scale *= canvas.getScaleX();
            canvas.setScaleX(1);
            canvas.setScaleY(1);
            canvas.redraw();
        }
        else {
            double factor = event.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
            canvas.setScaleX(canvas.getScaleX() * factor);
            canvas.setScaleY(canvas.getScaleY() * factor);
        }
    }
}
