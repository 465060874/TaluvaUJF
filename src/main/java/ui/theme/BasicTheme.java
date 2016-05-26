package ui.theme;

import data.ChoosenColors;
import data.FieldType;
import javafx.geometry.Insets;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import map.Building;
import ui.island.Grid;

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
    private final Lighting lighting = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingValid = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingInvalid = new Lighting(new Light.Point(0, 0, 0, Color.GRAY));

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
        return tileBottomColor;
    }

    @Override
    public Effect getTileBottomEffect(Grid grid, PlacementState placementState) {
        return null;
    }

    @Override
    public Paint getTileTopPaint(FieldType type, PlacementState placementState) {
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
    public Effect getTileTopEffect(Grid grid, PlacementState placementState) {
        ((Light.Point) lighting.getLight()).setZ(grid.getScale() * 150);
        ((Light.Point) lightingValid.getLight()).setZ(grid.getScale() * 1000);
        ((Light.Point) lightingInvalid.getLight()).setZ(grid.getScale() * 150);
        switch (placementState) {
            case NONE:     return lighting;
            case FLOATING: return lighting;
            case INVALID:  return lightingInvalid;
            case VALID:    return lightingValid;
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getBuildingBorderPaint() {
        return tileBorderColor;
    }

    @Override
    public Paint getBuildingFacePaint(Building building, PlacementState placementState) {
        return getBuildingTopPaint(building, placementState);
    }

    @Override
    public Effect getBuildingFaceEffect(Grid grid, Building building, PlacementState placementState) {
        return getBuildingTopEffect(grid, building, placementState);
    }

    @Override
    public Paint getBuildingTopPaint(Building building, PlacementState placementState) {
        Color color = getBuildingTopColor(building);
        if (placementState == PlacementState.INVALID) {
            color = color.darker();
        }
        return color;
    }

    private Color getBuildingTopColor(Building building) {
        switch (building.getColor()) {
            case RED:    return ChoosenColors.RED.color();
            case WHITE:  return ChoosenColors.WHITE.color();
            case BROWN:  return ChoosenColors.BROWN.color();
            case YELLOW: return ChoosenColors.YELLOW.color();
        }

        throw new IllegalStateException();
    }

    @Override
    public Effect getBuildingTopEffect(Grid grid, Building building, PlacementState placementState) {
        return null;
    }
}
