package ui.theme;

import data.FieldType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import map.FieldBuilding;

public interface Theme {

    static ObjectProperty<Theme> getCurrent() {
        return CurrentThemeHolder.CURRENT_THEME;
    }

    Background getIslandBackground();

    Paint getTileBorderPaint(PlacementState placementState);

    Paint getTileBottomPaint(PlacementState placementState);

    Paint getTileTopPaint(FieldType type, PlacementState placementState);

    Paint getBuildingBorderPaint();

    Paint getBuildingFacePaint(FieldBuilding building, PlacementState placementState);

    Paint getBuildingTopPaint(FieldBuilding building, PlacementState placementState);

    class CurrentThemeHolder {

        private static ObjectProperty<Theme> CURRENT_THEME = new SimpleObjectProperty<>(new BasicTheme());
    }
}

