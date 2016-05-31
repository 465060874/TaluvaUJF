package theme;

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
            CurrentTheme.IslandTHEME = new BasicIslandTheme();
        }
        else if (CurrentTheme.IslandTHEME instanceof BasicIslandTheme){
            CurrentTheme.IslandTHEME = new BasicIslandTheme_withNoLight();
        } else {
            CurrentTheme.IslandTHEME = new ImageIslandTheme();
        }

        CurrentTheme.listeners.forEach(Runnable::run);

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

    Paint getTileBottomPaint(HexStyle style);

    Effect getTileBottomEffect(Grid grid, HexStyle style);

    Paint getTileTopPaint(FieldType type, HexStyle style);

    Effect getTileTopEffect(Grid grid, HexStyle style);

    Paint getBuildingBorderPaint();

    Paint getBuildingFacePaint(Building building, BuildingStyle style);

    Effect getBuildingFaceEffect(Grid grid, Building building, BuildingStyle style);

    Paint getBuildingTopPaint(Building building, BuildingStyle style);

    Effect getBuildingTopEffect(Grid grid, Building building, BuildingStyle style);

    Paint getGradiantEffect(Orientation orientation, double[] hexagonBorderX, double[] hexagonBorderY);

    class CurrentTheme {

        private static IslandTheme IslandTHEME = new BasicIslandTheme();
        private static final List<Runnable> listeners = new ArrayList<>();
    }
}

