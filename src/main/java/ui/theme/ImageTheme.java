package ui.theme;

import data.FieldType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

public class ImageTheme extends BasicTheme {

    private final ImagePattern volcano = new ImagePattern(new Image(ImageTheme.class.getResource("volcano.png").toString()));
    private final ImagePattern jungle = new ImagePattern(new Image(ImageTheme.class.getResource("jungle.png").toString()));
    private final ImagePattern clearing = new ImagePattern(new Image(ImageTheme.class.getResource("clearing.png").toString()));
    private final ImagePattern sand = new ImagePattern(new Image(ImageTheme.class.getResource("sand.png").toString()));
    private final ImagePattern rock = new ImagePattern(new Image(ImageTheme.class.getResource("rock.png").toString()));
    private final ImagePattern lake = new ImagePattern(new Image(ImageTheme.class.getResource("lake.png").toString()));

    @Override
    public Paint getTileTopPaint(FieldType type, PlacementState placementState) {
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
