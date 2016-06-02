package menu.data;

import javafx.scene.image.Image;

import java.io.File;

public class SavedGame {

    private final String date;
    private final File file;
    private final Image image;

    public SavedGame(String date, File file, Image image) {
        this.date = date;
        this.file = file;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public File getFile() {
        return file;
    }

    public Image getImage() {
        return image;
    }
}
