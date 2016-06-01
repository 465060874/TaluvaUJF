package ui.island;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import map.Island;
import theme.IslandTheme;

public class IslandView extends StackPane {

    private static final double DRAG_TRESHOLD = 100;
    private final Grid grid;

    private final IslandCanvas islandCanvas;
    private final PlacementOverlay placementOverlay;

    private boolean mousePressed;
    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double lastMouseX;
    private double lastMouseY;

    private boolean mouseDragThresholdReached;

    public IslandView(Island island, boolean debug) {
        this(island, new Grid(), new Placement(null, null), debug);
    }

    public IslandView(Island island, Grid grid, Placement placement, boolean debug) {
        this.grid = grid;
        this.placementOverlay = new PlacementOverlay(island, grid, placement);
        this.islandCanvas = new IslandCanvas(island, grid, placement, debug);
        placement.islandCanvas = islandCanvas;
        placement.placementOverlay = placementOverlay;

        this.mousePressed = false;
        this.lastMouseX = 0;
        this.lastMouseY = 0;

        this.mouseDragThresholdReached = false;

        getChildren().add(islandCanvas);
        getChildren().add(placementOverlay);

        IslandTheme.addListener(this::updateTheme);
        updateTheme();

        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnScroll(this::scroll);
    }

    private void updateTheme() {
        islandCanvas.redraw();
        placementOverlay.redraw();
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

        placementOverlay.setLayoutX(left);
        placementOverlay.setLayoutY(top);
        if (w != placementOverlay.getWidth() || h != placementOverlay.getHeight()) {
            placementOverlay.setWidth(w);
            placementOverlay.setHeight(h);
            placementOverlay.redraw();
        }
    }

    private void mousePressed(MouseEvent event) {
        mousePressed = true;
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseXBeforeDrag = lastMouseX = event.getX();
            mouseYBeforeDrag = lastMouseY = event.getY();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (!mouseDragThresholdReached) {
            double x = Math.abs(mouseXBeforeDrag - event.getX());
            double y = Math.abs(mouseYBeforeDrag - event.getY());
            if (x*x + y*y > DRAG_TRESHOLD) {
                mouseXBeforeDrag = event.getX();
                mouseYBeforeDrag = event.getY();
                mouseDragThresholdReached = true;
            }
        }
        else {
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;

            islandCanvas.setTranslateX(islandCanvas.getTranslateX() + deltaX);
            islandCanvas.setTranslateY(islandCanvas.getTranslateY() + deltaY);
            placementOverlay.setTranslateX(placementOverlay.getTranslateX() + deltaX);
            placementOverlay.setTranslateY(placementOverlay.getTranslateY() + deltaY);
        }

        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    private void mouseReleased(MouseEvent event) {
        mousePressed = false;
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (mouseDragThresholdReached) {
            grid.translate(mouseXBeforeDrag - event.getX(), mouseYBeforeDrag - event.getY());
            mouseDragThresholdReached = false;

            islandCanvas.redraw();
        }
    }

    private void scroll(ScrollEvent event) {
        if (mousePressed) {
            return;
        }

        double scaleFactor = event.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
        if (grid.scale(scaleFactor)) {
            islandCanvas.redraw();
            placementOverlay.redraw();
        }
    }

    public void redrawIsland() {
        islandCanvas.redraw();
    }

    public boolean isMouseDragThresholdReached() {
        return mouseDragThresholdReached;
    }
}
