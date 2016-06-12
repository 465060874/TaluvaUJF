package menu.data;

import javafx.scene.image.Image;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavedGame {

    static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM HH:mm");

    private final Date date;
    private final File file;
    private final Image image;

    public SavedGame(Date date, File file, Image image) {
        this.date = date;
        this.file = file;
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        return DATE_FORMATTER.format(date);
    }

    public File getFile() {
        return file;
    }

    public Image getImage() {
        return image;
    }
}
