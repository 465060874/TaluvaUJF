package theme;

import data.FieldType;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import map.Building;
import map.Orientation;
import ui.island.Grid;

public class DefaultTheme implements IslandTheme {

    private final Color backgroundColor = Color.web("5E81A2");
    private final Color tileBorderColor = Color.web("303030");
    private final Color tileBottomColor = Color.web("707070");
    private final Color darkerTileBottomColor = tileBottomColor.darker();

    private final Color tileVolcanoColor = Color.web("E97B33");
    private final Color redInnerStrokeColor = Color.web("EA3434");
    private final Color volcanoDarkRed = Color.web("730C0C");

    private final Color tileJungleColor = Color.web("A681B6");
    private final Color tileClearingColor = Color.web("8DC435");
    private final Color tileSandColor = Color.web("EFDD6F");
    private final Color tileRockColor = Color.web("C2D0D1");
    private final Color tileLakeColor = Color.web("8BE1EB");

    private final Color selectedTransparent = Color.rgb(234, 52, 52, .5);
    private final Color translucent = Color.rgb(255, 255, 255, .5);

    private final Lighting lighting = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingHigh = new Lighting(new Light.Point(0, 0, 0, Color.WHITE));
    private final Lighting lightingFaded = new Lighting(new Light.Point(0, 0, 0, Color.GRAY));

    private final Stop[] stops = new Stop[]{new Stop(0, volcanoDarkRed), new Stop(0.4, redInnerStrokeColor), new Stop(1, tileVolcanoColor)};
    private final LinearGradient NORTH_LinearGradiant = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
    private final LinearGradient SOUTH_LinearGradiant = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
    private final LinearGradient SOUTH_WEST_LinearGradiant = new LinearGradient(0, 1, 1, 0, true, CycleMethod.NO_CYCLE, stops);
    private final LinearGradient NORTH_EAST_LinearGradiant = new LinearGradient(1, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
    private final LinearGradient NORTH_WEST_LinearGradiant = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
    private final LinearGradient SOUTH_EAST_LinearGradiant = new LinearGradient(1, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);

    private final Stop[] stopsBuildings = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, volcanoDarkRed)};
    private final LinearGradient buildingLinearGradiant = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stopsBuildings);

    private final Stop[] stopsBottom = new Stop[]{new Stop(0, tileBottomColor.brighter()), new Stop(0.5, tileBottomColor)};
    private final LinearGradient bottomLinearGradiant = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stopsBottom);


    @Override
    public Paint getBackgroundPaint() {
        return backgroundColor;
    }

    @Override
    public Paint getTileBorderPaint(HexStyle style) {
        return tileBorderColor;

    }
    @Override
    public Paint getInnerBorderPaint(HexStyle style) {
        return redInnerStrokeColor;
    }

    @Override
    public Paint getTileBottomPaint(HexStyle style, int level) {
        return darkerTileBottomColor;
    }

    @Override
    public Effect getTileBottomEffect(Grid grid, HexStyle style) {
        return null;
    }

    @Override
    public Paint getTileTopPaint(FieldType type, HexStyle style, Orientation orientation) {
        switch (type) {
            case VOLCANO:
                switch(orientation) {
                    case NORTH: return NORTH_LinearGradiant;
                    case SOUTH: return SOUTH_LinearGradiant;
                    case NORTH_WEST: return NORTH_WEST_LinearGradiant;
                    case NORTH_EAST: return NORTH_EAST_LinearGradiant;
                    case SOUTH_WEST: return SOUTH_WEST_LinearGradiant;
                    case SOUTH_EAST: return SOUTH_EAST_LinearGradiant;
                }
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
            case FADED:       return lighting;
            case TRULYFADED:  return lightingFaded;
            case HIGHLIGHTED: return lightingHigh;
            case LASTPLAYED: return lightingHigh;
            case FADEDLASTPLAYED: return lightingHigh;
        }

        throw new IllegalStateException();
    }

    @Override
    public Paint getBuildingBorderPaint(BuildingStyle style) {
        switch (style) {
            case EXPAND:
                return Color.TRANSPARENT;
            case INVALID:
                return Color.RED;
            case LASTPLACED:
                return buildingLinearGradiant;
            default:
            return tileBorderColor;
        }
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
        return getBuildingTopColor(building, style);
    }

    private Color getBuildingTopColor(Building building, BuildingStyle style) {
        switch(style) {
            case FLOATING:
                return Color.TRANSPARENT;
            case EXPAND:
                return Color.TRANSPARENT;
            default:
            switch (building.getColor()) {
                case RED:    return PlayerTheme.RED.color();
                case WHITE:  return PlayerTheme.WHITE.color();
                case BROWN:  return PlayerTheme.BROWN.color();
                case YELLOW: return PlayerTheme.YELLOW.color();
            }

            throw new IllegalStateException();
        }
    }

    @Override
    public Effect getBuildingTopEffect(Grid grid, Building building, BuildingStyle style) {
        return null;
    }
}
