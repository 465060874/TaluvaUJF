package data;

import javafx.scene.paint.Color;

public enum ChoosenColors {

    RED("salmon"),
    WHITE("ivory"),
    BROWN("peru"),
    YELLOW("gold");

    private final String cssDefinition;
    private final Color color;

    ChoosenColors(String cssDefinition) {
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
