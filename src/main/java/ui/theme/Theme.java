package ui.theme;

import data.FieldType;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import map.Building;
import ui.island.Grid;

import java.util.ArrayList;
import java.util.List;

public interface Theme {

    static Theme getCurrent() {
        return CurrentTheme.THEME;
    }

    static Theme change() {
        if (CurrentTheme.THEME instanceof ImageTheme) {
            CurrentTheme.THEME = new BasicTheme();
        }
        else {
            CurrentTheme.THEME = new ImageTheme();
        }

        CurrentTheme.listeners.forEach(Runnable::run);

        return CurrentTheme.THEME;
    }

    static void addListener(Runnable listener) {
        CurrentTheme.listeners.add(listener);
    }

    static void removeListener(Runnable listener) {
        CurrentTheme.listeners.remove(listener);
    }

    Background getIslandBackground();

    Paint getTileBorderPaint(HexStyle style);

    Paint getTileBottomPaint(HexStyle style);

    Effect getTileBottomEffect(Grid grid, HexStyle style);

    Paint getTileTopPaint(FieldType type, HexStyle style);

    Effect getTileTopEffect(Grid grid, HexStyle style);

    Paint getBuildingBorderPaint();

    Paint getBuildingFacePaint(Building building, BuildingStyle style);

    Effect getBuildingFaceEffect(Grid grid, Building building, BuildingStyle style);

    Paint getBuildingTopPaint(Building building, BuildingStyle style);

    Effect getBuildingTopEffect(Grid grid, Building building, BuildingStyle style);

    class CurrentTheme {

        private static Theme THEME = new BasicTheme();
        private static final List<Runnable> listeners = new ArrayList<>();
    }
}

