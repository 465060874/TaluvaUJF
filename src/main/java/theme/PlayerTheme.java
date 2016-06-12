package theme;

import data.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public enum PlayerTheme {

    WHITE("ivory", "theme/player/whitePlayer.png",
            "theme/buildings/mb1.png",
            "theme/buildings/mbso1.png",
            "theme/buildings/mb1inter.png",
            "theme/buildings/temb.png",
            "theme/buildings/tbso.png",
            "theme/buildings/tbinter.png",
            "theme/buildings/tb.png",
            "theme/buildings/tourbso.png",
            "theme/buildings/tourbinter.png"),
    RED("salmon", "theme/player/redPlayer.png",
            "theme/buildings/mr1.png",
            "theme/buildings/mrso1.png",
            "theme/buildings/mr1inter.png",
            "theme/buildings/temr.png",
            "theme/buildings/trso.png",
            "theme/buildings/trinter.png",
            "theme/buildings/tr.png",
            "theme/buildings/tourrso.png",
            "theme/buildings/tourrinter.png"),
    YELLOW("gold", "theme/player/yellowPlayer.png",
            "theme/buildings/mj1.png",
            "theme/buildings/mjso1.png",
            "theme/buildings/mj1inter.png",
            "theme/buildings/temj.png",
            "theme/buildings/tjso.png",
            "theme/buildings/tjinter.png",
            "theme/buildings/tj.png",
            "theme/buildings/tourjso.png",
            "theme/buildings/tourjinter.png"),
    BROWN("peru", "theme/player/brownPlayer.png",
            "theme/buildings/mm1.png",
            "theme/buildings/mmso1.png",
            "theme/buildings/mm1inter.png",
            "theme/buildings/temm.png",
            "theme/buildings/tmso.png",
            "theme/buildings/tminter.png",
            "theme/buildings/tm.png",
            "theme/buildings/tourmso.png",
            "theme/buildings/tourminter.png");

    private final String cssDefinition;
    private final Color color;
    private final Image image;
    private final Image hutImage;
    private final Image hutSansOmbreImage;
    private final Image hutImageInterdit;
    private final Image templeImage;
    private final Image templeSansOmbreImage;
    private final Image templeImageInterdit;
    private final Image towerImage;
    private final Image towerSansOmbreImage;
    private final Image towerImageInterdit;

    PlayerTheme(String colorCssDefinition, String imageUrl,
                String hutImageUrl,
                String hutImageSansOmbreUrl,
                String hutImageInterditUrl,
                String templeImageUrl,
                String templeImageSansOmbreUrl,
                String templeImageInterditUrl,
                String towerImageUrl,
                String towerImageSansOmbreUrl,
                String towerImageInterditUrl) {
        this.cssDefinition = colorCssDefinition;
        this.color = Color.web(colorCssDefinition);
        this.image = new Image(imageUrl);
        this.hutImage = new Image(hutImageUrl);
        this.hutSansOmbreImage = new Image(hutImageSansOmbreUrl);
        this.hutImageInterdit = new Image(hutImageInterditUrl);
        this.templeImage = new Image(templeImageUrl);
        this.templeSansOmbreImage = new Image(templeImageSansOmbreUrl);
        this.templeImageInterdit = new Image(templeImageInterditUrl);
        this.towerImage = new Image(towerImageUrl);
        this.towerSansOmbreImage = new Image(towerImageSansOmbreUrl);
        this.towerImageInterdit = new Image(towerImageInterditUrl);
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

    public Image getHutSansOmbreImage() {
        return hutSansOmbreImage;
    }

    public Image getHutImageInterdit() {
        return hutImageInterdit;
    }

    public Image getTempleImage() {
        return templeImage;
    }

    public Image getTempleSansOmbreImage() {
        return templeSansOmbreImage;
    }

    public Image getTempleImageInterdit() {
        return templeImageInterdit;
    }

    public Image getTowerImage() {
        return towerImage;
    }

    public Image getTowerSansOmbreImage() {
        return towerSansOmbreImage;
    }

    public Image getTowerImageInterdit() {
        return towerImageInterdit;
    }

    public static PlayerTheme of(PlayerColor color) {
        return values()[color.ordinal()];
    }

    public static Color ELIMINATED = Color.web("707070");
}
