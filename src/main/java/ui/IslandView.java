package ui;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import map.Island;

class IslandView extends StackPane {

    private Grid grid;
    private Placement placement;

    final IslandCanvas islandCanvas;
    private final PlacementOverlay placementOverlay;

    private double mouseXBeforeDrag;
    private double mouseYBeforeDrag;
    private double mouseX;
    private double mouseY;

    IslandView(Island island, boolean debug) {
        this.grid = new Grid(0.0, 0.0, 1);
        this.placement = new Placement(island, grid);
        this.placementOverlay = new PlacementOverlay(island, grid, placement);
        this.islandCanvas = new IslandCanvas(island, grid, placement, debug);
        placement.placementOverlay = placementOverlay;
        placement.islandCanvas = islandCanvas;
        this.mouseX = 0;
        this.mouseY = 0;

        getChildren().add(islandCanvas);
        getChildren().add(placementOverlay);

        if (false && HexShape.IMG_FIELD_TYPE) {
            BackgroundImage bgImage = new BackgroundImage(
                    new Image(IslandView.class.getResource("sea.jpg").toString()),
                    BackgroundRepeat.REPEAT,
                    BackgroundRepeat.REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            setBackground(new Background(bgImage));
        }
        else {
            BackgroundFill backgroundFill = new BackgroundFill(
                    IslandCanvas.BG_COLOR,
                    CornerRadii.EMPTY,
                    Insets.EMPTY);
            setBackground(new Background(backgroundFill));
        }

        setOnMouseMoved(this::mouseMoved);
        setOnMousePressed(this::mousePressed);
        setOnMouseClicked(this::mouseClicked);
        setOnMouseDragged(this::mouseDragged);
        setOnMouseReleased(this::mouseReleased);
        setOnMouseExited(this::mouseExited);
        setOnMouseEntered(this::mouseEntered);
        setOnScroll(this::scroll);
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
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseXBeforeDrag = mouseX = event.getX();
            mouseYBeforeDrag = mouseY = event.getY();
        }
    }

    private void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            placement.cycleMode();
        }
        else if (event.getButton() == MouseButton.SECONDARY){
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
        placementOverlay.redraw();
    }
}
