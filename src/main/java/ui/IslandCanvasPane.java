package ui;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;

class IslandCanvasPane extends Pane {

    private final IslandCanvas canvas;

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
        canvas.scale = event.getDeltaY() > 0
                ? canvas.scale * 1.1
                : canvas.scale / 1.1;
        canvas.redraw();
    }
}