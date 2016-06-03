package theme;

import data.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public enum PlayerTheme {

    WHITE("ivory", "theme/player/whitePlayer.png"),
    RED("salmon", "theme/player/redPlayer.png"),
    YELLOW("gold", "theme/player/yellowPlayer.png"),
    BROWN("peru", "theme/player/brownPlayer.png");

    private final String cssDefinition;
    private final Color color;
    private final Image image;

    PlayerTheme(String colorCssDefinition, String imageUrl) {
        this.cssDefinition = colorCssDefinition;
        this.color = Color.web(colorCssDefinition);
        this.image = new Image(imageUrl);
    }

    public String cssDefinition() {
        return cssDefinition;
    }

    public Color color() {
        return color;
    }

    public Image getImage() {
        return image;
    }

    public static PlayerTheme of(PlayerColor color) {
        return values()[color.ordinal()];
    }

    public static Color ELIMINATED = Color.web("707070");
}
