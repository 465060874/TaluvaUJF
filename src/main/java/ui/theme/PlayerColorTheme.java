package ui.theme;

import javafx.scene.paint.Color;

public enum PlayerColorTheme {

    WHITE("ivory"),
    RED("salmon"),
    YELLOW("gold"),
    BROWN("peru");

    private final String cssDefinition;
    private final Color color;

    PlayerColorTheme(String cssDefinition) {
        this.cssDefinition = cssDefinition;
        this.color = Color.web(cssDefinition);
    }

    public String cssDefinition() {
        return cssDefinition;
    }

    public Color color() {
        return color;
    }
}
