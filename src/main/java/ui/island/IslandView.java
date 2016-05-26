package ui.island;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import map.Island;
import ui.theme.Theme;

public class IslandView extends StackPane {

    private Grid grid;
    private Placement placement;

    final IslandCanvas islandCanvas;
    private final PlacementOverlay placementOverlay;

    private boolean mousePressed;
    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

    public IslandView(Island island, boolean debug) {
        this.grid = new Grid(0.0, 0.0, 1);
        this.placement = new Placement(island, grid);
        this.placementOverlay = new PlacementOverlay(island, grid, placement);
        this.islandCanvas = new IslandCanvas(island, grid, placement, debug);
        placement.placementOverlay = placementOverlay;
        placement.islandCanvas = islandCanvas;

        this.mousePressed = false;
        this.mouseX = 0;
        this.mouseY = 0;

        getChildren().add(islandCanvas);
        getChildren().add(placementOverlay);

        Theme.addListener(this::updateTheme);
        updateTheme();

        setOnMouseMoved(this::mouseMoved);
        setOnMousePressed(this::mousePressed);
        setOnMouseClicked(this::mouseClicked);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnMouseExited(this::mouseExited);
        setOnMouseEntered(this::mouseEntered);
        setOnScroll(this::scroll);
    }

    private void updateTheme() {
        setBackground(Theme.getCurrent().getIslandBackground());
        islandCanvas.redraw();
        placementOverlay.redraw();
    }

    private void mouseExited(MouseEvent event) {
        placement.saveMode();
    }

    private void mouseEntered(MouseEvent event) {
        placement.restoreMode();
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

    private void mouseMoved(MouseEvent event) {
        placement.updateMouse(event.getX(), event.getY(), getWidth(), getHeight());
    }

    private void mousePressed(MouseEvent event) {
        mousePressed = true;
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseXBeforeDrag = mouseX = event.getX();
            mouseYBeforeDrag = mouseY = event.getY();
        }
    }

    private void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            placement.cycleMode();
        }
        else if (event.getButton() == MouseButton.SECONDARY) {
            placement.cycleTileOrientationOrBuildingTypeAndColor();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        islandCanvas.setTranslateX(islandCanvas.getTranslateX()
                + event.getX() - mouseX);
        islandCanvas.setTranslateY(islandCanvas.getTranslateY()
                + event.getY() - mouseY);

        placementOverlay.setTranslateX(placementOverlay.getTranslateX()
                + event.getX() - mouseX);
        placementOverlay.setTranslateY(placementOverlay.getTranslateY()
                + event.getY() - mouseY);

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
