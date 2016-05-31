package engine.log;

import engine.Engine;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Wrapper personnalisé de la classe Logger de Java qui fournit un jeu de
 * méthode plus claires et consistant.
 * <p>
 * Chaque méthode utilise un argument `varargs` pour les paramètres du message.
 * <pre>
 * {@code logger.level("Message, {0} & {1}", "arg1", "arg2"); }
 * </pre>
 */
public class EngineLogger {

    static final boolean LOG_IN_FILE = false;

    static {
        // Force initialisation of base logger
        EngineLoggerSetup.BASE_LOGGER.getLevel();
    }

    final Logger javaLogger;

    public static EngineLogger create(Level level) {
        return new EngineLogger(level);
    }

    EngineLogger(Level level) {
        this.javaLogger = Logger.getLogger(Engine.class.getCanonicalName() + "." + Long.toString(System.nanoTime()));
        javaLogger.setLevel(level);
    }

    public void log(Level level, Throwable thrown, String message, Object... parameters) {
        LogRecord record = new LogRecord(level, message);
        record.setParameters(parameters);
        record.setThrown(thrown);
        javaLogger.log(record);
    }

    public void finest(String message, Object... arguments) {
        finest(null, message, arguments);
    }

    public void finest(Throwable thrown, String message, Object... arguments) {
        log(Level.FINEST, thrown, message, arguments);
    }

    public void finer(String message, Object... arguments) {
        finer(null, message, arguments);
    }

    public void finer(Throwable thrown, String message, Object... arguments) {
        log(Level.FINER, thrown, message, arguments);
    }

    public void fine(String message, Object... arguments) {
        fine(null, message, arguments);
    }

    public void fine(Throwable thrown, String message, Object... arguments) {
        log(Level.FINE, thrown, message, arguments);
    }

    public void config(String message, Object... arguments) {
        config(null, message, arguments);
    }

    public void config(Throwable thrown, String message, Object... arguments) {
        log(Level.CONFIG, thrown, message, arguments);
    }

    public void info(String message, Object... arguments) {
        info(null, message, arguments);
    }

    public void info(Throwable thrown, String message, Object... arguments) {
        log(Level.INFO, thrown, message, arguments);
    }

    public void warning(String message, Object... arguments) {
        warning(null, message, arguments);
    }

    public void warning(Throwable thrown, String message, Object... arguments) {
        log(Level.WARNING, thrown, message, arguments);
    }

    public void severe(String message, Object... arguments) {
        severe(null, message, arguments);
    }

    public void severe(Throwable thrown, String message, Object... arguments) {
        log(Level.SEVERE, thrown, message, arguments);
    }
}