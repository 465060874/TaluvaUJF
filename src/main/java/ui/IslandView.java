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

    Grid grid;

    final IslandCanvas islandCanvas;
    private final FreeTileOverlay freeTileOverlay;

    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

    IslandView(Island island, boolean debug) {
        this.grid = new Grid(0.0, 0.0, 1);
        this.freeTileOverlay = new FreeTileOverlay(island, grid);
        this.islandCanvas = new IslandCanvas(island, grid, debug);
        this.mouseX = 0;
        this.mouseY = 0;

        getChildren().add(islandCanvas);
        getChildren().add(freeTileOverlay);

        BackgroundFill backgroundFill = new BackgroundFill(
                IslandCanvas.BG_COLOR,
                CornerRadii.EMPTY,
                Insets.EMPTY);
        setBackground(new Background(backgroundFill));

        setOnMouseMoved(this::mouseMoved);
        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnScroll(this::scroll);
        islandCanvas.redraw();
    }

    @Override
    protected void layoutChildren() {
        final int top = (int)snappedTopInset();
        final int right = (int)snappedRightInset();
        final int bottom = (int)snappedBottomInset();
        final int left = (int)snappedLeftInset();
        final int w = (int)getWidth() - left - right;
        final int h = (int)getHeight() - top - bottom;

        islandCanvas.setLayoutX(left);
        islandCanvas.setLayoutY(top);
        if (w != islandCanvas.getWidth() || h != islandCanvas.getHeight()) {
            islandCanvas.setWidth(w * 3);
            islandCanvas.setHeight(h * 3);
            islandCanvas.redraw();
        }

        freeTileOverlay.setLayoutX(left);
        freeTileOverlay.setLayoutY(top);
        if (w != freeTileOverlay.getWidth() || h != freeTileOverlay.getHeight()) {
            freeTileOverlay.setWidth(w);
            freeTileOverlay.setHeight(h);
            freeTileOverlay.redraw();
        }
    }

    private void mouseMoved(MouseEvent event) {
        islandCanvas.setVisible(true);
        freeTileOverlay.setVisible(true);
        islandCanvas.mouseMovedAction(event, getWidth(), getHeight());
        freeTileOverlay.mouseMovedAction(event, getWidth(), getHeight());
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

        islandCanvas.setTranslateX(islandCanvas.getTranslateX()
                + event.getX() - mouseX);
        islandCanvas.setTranslateY(islandCanvas.getTranslateY()
                + event.getY() - mouseY);

        freeTileOverlay.mouseMovedAction(event, getWidth(), getHeight());

        mouseX = event.getX();
        mouseY = event.getY();
    }

    private void mouseReleased(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        grid.setOx(grid.getOx() + mouseXBeforeDrag - event.getX());
        grid.setOy(grid.getOy() + mouseYBeforeDrag - event.getY());
        islandCanvas.redraw();
    }

    private void scroll(ScrollEvent event) {
        double factor = event.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
        grid.setScale(grid.getScale() * factor);
        islandCanvas.redraw();
        freeTileOverlay.redraw();
    }

}
