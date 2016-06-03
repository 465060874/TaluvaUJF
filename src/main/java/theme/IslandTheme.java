package theme;

import com.google.common.collect.Lists;
import data.FieldType;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import map.Building;
import map.Orientation;
import ui.island.Grid;

import java.util.ArrayList;
import java.util.List;

public interface IslandTheme {

    static IslandTheme getCurrent() {
        return CurrentTheme.IslandTHEME;
    }

    static IslandTheme change() {
        if (CurrentTheme.IslandTHEME instanceof ImageIslandTheme) {
            CurrentTheme.IslandTHEME = new SphaxIslandTheme();
        }
        else if (CurrentTheme.IslandTHEME instanceof SphaxIslandTheme) {
            CurrentTheme.IslandTHEME = new DefaultTheme();
        } else {
            CurrentTheme.IslandTHEME = new ImageIslandTheme();
        }

        Lists.reverse(CurrentTheme.listeners).forEach(Runnable::run);

        return CurrentTheme.IslandTHEME;
    }

    static void addListener(Runnable listener) {
        CurrentTheme.listeners.add(listener);
    }

    static void removeListener(Runnable listener) {
        CurrentTheme.listeners.remove(listener);
    }

    Paint getBackgroundPaint();

    Paint getTileBorderPaint(HexStyle style);

    Paint getInnerBorderPaint(HexStyle style);

    Paint getTileBottomPaint(HexStyle style, int level);

    Effect getTileBottomEffect(Grid grid, HexStyle style);

    Paint getTileTopPaint(FieldType type, HexStyle style, Orientation orientation);

    Effect getTileTopEffect(Grid grid, HexStyle style);

    Paint getBuildingBorderPaint(BuildingStyle style);

    Paint getBuildingFacePaint(Building building, BuildingStyle style);

    Effect getBuildingFaceEffect(Grid grid, Building building, BuildingStyle style);

    Paint getBuildingTopPaint(Building building, BuildingStyle style);

    Effect getBuildingTopEffect(Grid grid, Building building, BuildingStyle style);

    class CurrentTheme {

        private static IslandTheme IslandTHEME = new DefaultTheme();
        private static final List<Runnable> listeners = new ArrayList<>();
    }
}

