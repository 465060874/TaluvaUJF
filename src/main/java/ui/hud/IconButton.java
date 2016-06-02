package ui.hud;

import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class IconButton extends Button {

    private static final Lighting LIGHTING_HOVER = new Lighting(new Light.Point(0, 0, 60, Color.WHITE));
    private static final Lighting LIGHTING_PRESSED = new Lighting(new Light.Point(0, 0, 120, Color.WHITE));

    private final ImageView icon;

    public IconButton(String url) {
        this.icon = new ImageView(url);
        icon.setFitWidth(60);
        icon.setFitHeight(60);

        setBackground(Background.EMPTY);
        setGraphic(icon);
        hoverProperty().addListener(this::update);
        pressedProperty().addListener(this::update);
    }

    private void update(Observable observable) {
        if (isPressed()) {
            icon.setEffect(LIGHTING_PRESSED);
        }
        else if (isHover()) {
            icon.setEffect(LIGHTING_HOVER);
        }
        else {
            icon.setEffect(null);
        }
    }
}
