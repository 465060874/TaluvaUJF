package ui.theme;

import data.ChoosenColors;
import data.FieldType;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import map.FieldBuilding;

public class BasicTheme implements Theme {

    private final Background islandBackground = new Background(new BackgroundFill(
            Color.web("5E81A2"),
            CornerRadii.EMPTY,
            Insets.EMPTY));
    private final Color tileBorderColor = Color.web("303030");
    private final Color tileBottomColor = Color.web("707070");
    private final Color tileVolcanoColor = Color.web("E97B33");
    private final Color tileJungleColor = Color.web("A681B6");
    private final Color tileClearingColor = Color.web("8DC435");
    private final Color tileSandColor = Color.web("EFDD6F");
    private final Color tileRockColor = Color.web("C2D0D1");
    private final Color tileLakeColor = Color.web("8BE1EB");

    private Paint deriveColor(PlacementState placementState, Color color) {
        switch (placementState) {
            case NONE: return color;
            case INVALID: return color.deriveColor(1, 1, 1, 0.5);
            case VALID: return color.deriveColor(1, 1, 1, 0.75);
        }

        throw new IllegalStateException();
    }

    @Override
    public Background getIslandBackground() {
        return islandBackground;
    }

    @Override
    public Paint getTileBorderPaint(PlacementState placementState) {
        return tileBorderColor;
    }

    @Override
    public Paint getTileBottomPaint(PlacementState placementState) {
        return deriveColor(placementState, tileBottomColor);
    }

    private Color doGetTileTopPaint(FieldType type) {
        switch (type) {
            case VOLCANO:  return tileVolcanoColor;
            case JUNGLE:   return tileJungleColor;
            case CLEARING: return tileClearingColor;
            case SAND:     return tileSandColor;
            case ROCK:     return tileRockColor;
            case LAKE:     return tileLakeColor;
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getTileTopPaint(FieldType type, PlacementState placementState) {
        return deriveColor(placementState, doGetTileTopPaint(type));
    }

    @Override
    public Paint getBuildingBorderPaint() {
        return tileBorderColor;
    }

    private Color getBuildingFaceColor(FieldBuilding building) {
        switch (building.getColor()) {
            case RED: return ChoosenColors.RED.color().darker();
            case WHITE: return ChoosenColors.WHITE.color().darker();
            case BROWN: return ChoosenColors.BROWN.color().darker();
            case YELLOW: return ChoosenColors.YELLOW.color().darker();
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getBuildingFacePaint(FieldBuilding building, PlacementState placementState) {
        return deriveColor(placementState, getBuildingFaceColor(building));
    }

    private Color getBuildingTopColor(FieldBuilding building) {
        switch (building.getColor()) {
            case RED:    return ChoosenColors.RED.color();
            case WHITE:  return ChoosenColors.WHITE.color();
            case BROWN:  return ChoosenColors.BROWN.color();
            case YELLOW: return ChoosenColors.YELLOW.color();
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getBuildingTopPaint(FieldBuilding building, PlacementState placementState) {
        return deriveColor(placementState, getBuildingTopColor(building));
    }
}
