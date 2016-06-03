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

    private ImageView icon;

    public IconButton(String url) {
        this(url, 1);
    }

    public IconButton(String url, double ratio) {
        updateImage(url, ratio);

        setBackground(Background.EMPTY);
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

    public void updateImage(String url, double ratio) {
        this.icon = new ImageView(url);
        icon.setFitWidth(60 * ratio);
        icon.setFitHeight(60 * ratio);
        setGraphic(icon);
        update(null);
    }
}
