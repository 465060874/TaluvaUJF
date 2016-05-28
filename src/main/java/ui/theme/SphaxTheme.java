package ui.theme;

import data.FieldType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class SphaxTheme extends BasicTheme {

    private static final ImagePattern loadImagePattern(String resourceName) {
        String resource = SphaxTheme.class.getResource(resourceName).toString();
        return new ImagePattern(new Image(resource));
    }

    private final ImagePattern volcano = loadImagePattern("fields/sphax/lava.png");
    private final ImagePattern jungle = loadImagePattern("fields/sphax/leaves_oak.png");
    private final ImagePattern clearing = loadImagePattern("fields/sphax/grass_top.png");
    private final ImagePattern sand = loadImagePattern("fields/sphax/sand.png");
    private final ImagePattern rock = loadImagePattern("fields/sphax/cobblestone.png");
    private final ImagePattern lake = loadImagePattern("fields/sphax/water.png");

    @Override
    public Paint getTileTopPaint(FieldType type, HexStyle style) {
        switch (type) {
            case VOLCANO:  return volcano;
            case JUNGLE:   return jungle;
            case CLEARING: return clearing;
            case SAND:     return sand;
            case ROCK:     return rock;
            case LAKE:     return lake;
        }

        throw new IllegalStateException();
    }
}
