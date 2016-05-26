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

    Paint getTileBorderPaint(PlacementState placementState);

    Paint getTileBottomPaint(PlacementState placementState);

    Effect getTileBottomEffect(Grid grid, PlacementState placementState);

    Paint getTileTopPaint(FieldType type, PlacementState placementState);

    Effect getTileTopEffect(Grid grid, PlacementState placementState);

    Paint getBuildingBorderPaint();

    Paint getBuildingFacePaint(Building building, PlacementState placementState);

    Effect getBuildingFaceEffect(Grid grid, Building building, PlacementState placementState);

    Paint getBuildingTopPaint(Building building, PlacementState placementState);

    Effect getBuildingTopEffect(Grid grid, Building building, PlacementState placementState);

    class CurrentTheme {

        private static Theme THEME = new BasicTheme();
        private static final List<Runnable> listeners = new ArrayList<>();
    }
}

