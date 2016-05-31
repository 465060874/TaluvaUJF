package theme;

import data.FieldType;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import map.Building;
import map.Orientation;
import ui.island.Grid;

public class BasicIslandTheme implements IslandTheme {

    private final Color backgroundColor = Color.web("5E81A2");
    private final Color tileBorderColor = Color.web("303030");
    private final Color tileBottomColor = Color.web("707070");
    private final Color tileVolcanoColor = Color.web("E97B33");
    private final Color tileJungleColor = Color.web("A681B6");
    private final Color tileClearingColor = Color.web("8DC435");
    private final Color tileSandColor = Color.web("EFDD6F");
    private final Color tileRockColor = Color.web("C2D0D1");
    private final Color tileLakeColor = Color.web("8BE1EB");
    private final Lighting lighting = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingHigh = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingFaded = new Lighting(new Light.Point(0, 0, 0, Color.GRAY));

    @Override
    public Paint getBackgroundPaint() {
        return backgroundColor;
    }

    @Override
    public Paint getTileBorderPaint(HexStyle style) {
        return tileBorderColor;
    }

    @Override
    public Paint getTileBottomPaint(HexStyle style) {
        return tileBottomColor;
    }

    @Override
    public Effect getTileBottomEffect(Grid grid, HexStyle style) {
        return null;
    }

    @Override
    public Paint getTileTopPaint(FieldType type, HexStyle style) {
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
    public Effect getTileTopEffect(Grid grid, HexStyle style) {
        ((Light.Point) lighting.getLight()).setZ(grid.getScale() * 150);
        ((Light.Point) lightingHigh.getLight()).setZ(grid.getScale() * 1000);
        ((Light.Point) lightingFaded.getLight()).setZ(grid.getScale() * 150);
        switch (style) {
            case NORMAL:      return lighting;
            case FLOATING:    return lighting;
            case FADED:       return lightingFaded;
            case HIGHLIGHTED: return lightingHigh;
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getBuildingBorderPaint() {
        return tileBorderColor;
    }

    @Override
    public Paint getBuildingFacePaint(Building building, BuildingStyle style) {
        return getBuildingTopPaint(building, style);
    }

    @Override
    public Effect getBuildingFaceEffect(Grid grid, Building building, BuildingStyle style) {
        return getBuildingTopEffect(grid, building, style);
    }

    @Override
    public Paint getBuildingTopPaint(Building building, BuildingStyle style) {
        return getBuildingTopColor(building);
    }

    private Color getBuildingTopColor(Building building) {
        switch (building.getColor()) {
            case RED:    return PlayerTheme.RED.color();
            case WHITE:  return PlayerTheme.WHITE.color();
            case BROWN:  return PlayerTheme.BROWN.color();
            case YELLOW: return PlayerTheme.YELLOW.color();
        }

        throw new IllegalStateException();
    }

    @Override
    public Effect getBuildingTopEffect(Grid grid, Building building, BuildingStyle style) {
        return null;
    }

    @Override
    public Paint getGradiantEffect(Orientation orientation, double[] hexagonBorderX, double[] hexagonBorderY) {
        return null;
    }

    @Override
    public Paint getGradiantEffectOthers(Orientation orientation, double[] hexagonX, double[] hexagonY, FieldType baseColor) {
        return null;
    }
}