package menu.data;

import javafx.scene.image.Image;

public class SavedGame {

    private final String date;
    private final Image image;

    public SavedGame(String date, Image image) {
        this.date = date;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public Image getImage() {
        return image;
    }
}
