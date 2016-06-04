package util;

import engine.Engine;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

public class FxUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String HEADER = "The game ran into a problem";
    private static final String[] ERROR_CODES = new String[] {
            "(Error 42)",
            "(Error 404)",
            "(Error " + Integer.toBinaryString(42) + ")",
            "(Error 49.3)",
            "(Error " + Integer.toString(Integer.MAX_VALUE) + ")"
    };

    private static final String[] FOOTER = new String[] {
            "Checking if the weather has anything to do with it ... hmm, apparently not",
            "We're all doomed",
            "The error occured because of code written by Daniel Blevin",
            "Occurred because P != NP",
            "This is an heisenbug !",
            "Marwa Daoud is to blame.",
    };

    public static void install(Engine engine) {
        install(Thread.currentThread(), engine);
    }

    public static void install(Thread thread, Engine engine) {
        FxUncaughtExceptionHandler handler = new FxUncaughtExceptionHandler(engine);
        handler.doInstall(thread);
    }

    private final Engine engine;

    private FxUncaughtExceptionHandler(Engine engine) {
        this.engine = engine;
    }

    private void doInstall(Thread thread) {
        thread.setUncaughtExceptionHandler(new FxUncaughtExceptionHandler(engine));
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            engine.logger().severe(e, "An error occured");
            Platform.runLater(() -> fxUncaughtException(e));
        }
        catch (Exception exc) {
            // Er, let's hope we do not get there
        }
    }

    private void fxUncaughtException(Throwable e) {
        Random random = new Random();
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(HEADER + " " + ERROR_CODES[random.nextInt(ERROR_CODES.length)]);
        alert.setContentText(e.getMessage()
                + "\n\n" + FOOTER[random.nextInt(FOOTER.length)]
                + "\n\n" + stringWriter.toString());

        alert.showAndWait();
        Platform.exit();
    }
}
