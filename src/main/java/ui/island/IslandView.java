package ui.island;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import map.Island;
import theme.IslandTheme;

public class IslandView extends StackPane {

    private static final double DRAGGEDLIMIT = 100;
    private final Grid grid;

    private final IslandCanvas islandCanvas;
    private final PlacementOverlay placementOverlay;

    private boolean mousePressed;
    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

    private boolean mouseDragged;
    private double signedDraggedX;
    private double signedDraggedY;

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
        this.mouseX = 0;
        this.mouseY = 0;

        this.mouseDragged = false;
        this.signedDraggedX = 0;
        this.signedDraggedY = 0;

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
        setBackground(IslandTheme.getCurrent().getIslandBackground());
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
            mouseXBeforeDrag = mouseX = event.getX();
            mouseYBeforeDrag = mouseY = event.getY();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (!mouseDragged) {

            this.signedDraggedX  = mouseXBeforeDrag - event.getX();
            this.signedDraggedY = mouseYBeforeDrag - event.getY();

            double absX = Math.abs(signedDraggedX);
            double absY = Math.abs(signedDraggedY);
            if (absX*absX + absY*absY > DRAGGEDLIMIT) {
                mouseXBeforeDrag -= signedDraggedX;
                mouseYBeforeDrag -= signedDraggedY;
                mouseDragged = true;
            } else {
                return;
            }
        } else {

            islandCanvas.setTranslateX(islandCanvas.getTranslateX() + signedDraggedX + event.getX() - mouseX);
            islandCanvas.setTranslateY(islandCanvas.getTranslateY() + signedDraggedY + event.getY() - mouseY);

            placementOverlay.setTranslateX(placementOverlay.getTranslateX() + signedDraggedX + event.getX() - mouseX);
            placementOverlay.setTranslateY(placementOverlay.getTranslateY() + signedDraggedY + event.getY() - mouseY);

            mouseX = event.getX();
            mouseY = event.getY();
            signedDraggedX = 0;
            signedDraggedY = 0;
        }
    }

    private void mouseReleased(MouseEvent event) {
        signedDraggedX = 0;
        signedDraggedY = 0;
        mousePressed = false;
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (mouseDragged) {
            grid.translate(mouseXBeforeDrag - event.getX(), mouseYBeforeDrag - event.getY());
            islandCanvas.redraw();
            mouseDragged = false;
        }
    }

    private void scroll(ScrollEvent event) {
        if (mousePressed) {
            return;
        }

        if (grid.scale(event.getDeltaY() > 0 ? 1.1 : 1 / 1.1)) {
            islandCanvas.redraw();
            placementOverlay.redraw();
        }
    }

    public void redrawIsland() {
        islandCanvas.redraw();
    }

    public boolean isMouseDragged() {
        return mouseDragged;
    }
}
