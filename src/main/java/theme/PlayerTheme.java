package theme;

import data.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public enum PlayerTheme {

    WHITE("ivory", "theme/player/whitePlayer.png",
            "theme/buildings/mb1.png",
            "theme/buildings/temb.png",
            "theme/buildings/tb.png"),
    RED("salmon", "theme/player/redPlayer.png",
            "theme/buildings/mr1.png",
            "theme/buildings/temr.png",
            "theme/buildings/tr.png"),
    YELLOW("gold", "theme/player/yellowPlayer.png",
            "theme/buildings/mj1.png",
            "theme/buildings/temj.png",
            "theme/buildings/tj.png"),
    BROWN("peru", "theme/player/brownPlayer.png",
            "theme/buildings/mm1.png",
            "theme/buildings/temm.png",
            "theme/buildings/tm.png");

    private final String cssDefinition;
    private final Color color;
    private final Image image;
    private final Image hutImage;
    private final Image templeImage;
    private final Image towerImage;

    PlayerTheme(String colorCssDefinition, String imageUrl,
                String hutImageUrl, String templeImageUrl, String towerImageUrl) {
        this.cssDefinition = colorCssDefinition;
        this.color = Color.web(colorCssDefinition);
        this.image = new Image(imageUrl);
        this.hutImage = new Image(hutImageUrl);
        this.templeImage = new Image(templeImageUrl);
        this.towerImage = new Image(towerImageUrl);
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

    public Image getHutImage() {
        return hutImage;
    }

    public Image getTempleImage() {
        return templeImage;
    }

    public Image getTowerImage() {
        return towerImage;
    }

    public static PlayerTheme of(PlayerColor color) {
        return values()[color.ordinal()];
    }

    public static Color ELIMINATED = Color.web("707070");
}
