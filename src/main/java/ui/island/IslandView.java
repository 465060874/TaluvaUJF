package ui.island;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import map.Island;
import theme.IslandTheme;

public class IslandView extends StackPane {

    private final Grid grid;

    private final IslandCanvas islandCanvas;
    private final PlacementOverlay placementOverlay;

    private boolean mousePressed;
    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

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

        islandCanvas.setTranslateX(islandCanvas.getTranslateX() + event.getX() - mouseX);
        islandCanvas.setTranslateY(islandCanvas.getTranslateY() + event.getY() - mouseY);

        placementOverlay.setTranslateX(placementOverlay.getTranslateX() + event.getX() - mouseX);
        placementOverlay.setTranslateY(placementOverlay.getTranslateY() + event.getY() - mouseY);

        mouseX = event.getX();
        mouseY = event.getY();
    }

    private void mouseReleased(MouseEvent event) {
        mousePressed = false;
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        grid.translate(mouseXBeforeDrag - event.getX(), mouseYBeforeDrag - event.getY());
        islandCanvas.redraw();
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
}
