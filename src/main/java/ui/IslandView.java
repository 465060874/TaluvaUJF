package ui;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import map.Island;

class IslandView extends StackPane {

    final IslandCanvas canvas;
    private final FreeTileOverlay freeTileOverlay;

    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

    IslandView(Island island, boolean debug) {
        this.canvas = new IslandCanvas(island, debug);
        this.freeTileOverlay = new FreeTileOverlay(island, debug);
        this.mouseX = 0;
        this.mouseY = 0;

        getChildren().add(canvas);
        getChildren().add(freeTileOverlay);

        freeTileOverlay.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (!freeTileOverlay.isActive()) {
                freeTileOverlay.setVisible(true);
                freeTileOverlay.redraw();
                System.out.println("freeTileEvent");
            }
        });

        BackgroundFill backgroundFill = new BackgroundFill(
                IslandCanvas.BG_COLOR,
                CornerRadii.EMPTY,
                Insets.EMPTY);
        setBackground(new Background(backgroundFill));

        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnScroll(this::scroll);
        canvas.redraw();
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

        freeTileOverlay.setLayoutX(left);
        freeTileOverlay.setLayoutY(top);
        if (w != freeTileOverlay.getWidth() || h != freeTileOverlay.getHeight()) {
            freeTileOverlay.setWidth(w);
            freeTileOverlay.setHeight(h);
            freeTileOverlay.redraw();
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

        freeTileOverlay.setTranslateX(freeTileOverlay.getTranslateX()
                + event.getX() - mouseX);
        freeTileOverlay.setTranslateY(freeTileOverlay.getTranslateY()
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
        freeTileOverlay.ox += mouseXBeforeDrag - event.getX();
        freeTileOverlay.oy += mouseYBeforeDrag - event.getY();
        canvas.redraw();
    }

    private void scroll(ScrollEvent event) {
        double factor = event.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
        canvas.scale *= factor;
        canvas.redraw();
        freeTileOverlay.scale *= factor;
        freeTileOverlay.redraw();
    }
}
