package ui.island;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import map.Island;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class IslandSnapshot {

    private static final double WIDTH = 170;
    private static final double HEIGHT = 170;

    public static void take(Island island, File outputDir, String basename) throws IOException {
        Grid grid = new Grid();
        grid.scale(0.2);

        IslandCanvas canvas = new IslandCanvas(island, grid, new Placement(null, grid), false);
        canvas.setWidth(WIDTH);
        canvas.setHeight(HEIGHT);

        SnapshotParameters parameters = new SnapshotParameters();
        WritableImage image = new WritableImage(170, 170);
        WritableImage snapshot = canvas.snapshot(parameters, image);

        File output = new File(outputDir, basename + ".png");
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", output);
    }
}
