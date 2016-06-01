package theme;

import data.FieldType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import map.Orientation;

public class ImageIslandTheme extends DefaultTheme {

    private static final ImagePattern loadImagePattern(String resourceName) {
        String resource = ImageIslandTheme.class.getResource(resourceName).toString();
        return new ImagePattern(new Image(resource));
    }

    private final ImagePattern volcano = loadImagePattern("fields/volcano.png");
    private final ImagePattern jungle = loadImagePattern("fields/jungle.png");
    private final ImagePattern clearing = loadImagePattern("fields/clearing.png");
    private final ImagePattern sand = loadImagePattern("fields/sand.png");
    private final ImagePattern rock = loadImagePattern("fields/rock.png");
    private final ImagePattern lake = loadImagePattern("fields/lake.png");

    @Override
    public Paint getTileTopPaint(FieldType type, HexStyle style, Orientation orientation) {
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
