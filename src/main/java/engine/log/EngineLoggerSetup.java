package engine.log;

import engine.Engine;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class EngineLoggerSetup {

    private static final DateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyMMdd_HHmmss_SSS");

    public static EngineLogger setup() {
        Class<?> clazz = Engine.class;
        EngineLogger logger = new EngineLogger(clazz.getCanonicalName());
        logger.javaLogger.setUseParentHandlers(false);
        logger.javaLogger.addHandler(new SystemLoggerHandler(new OneLineLoggerFormatter(false)));

        try {
            File sourceFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            File logDir = new File(sourceFile.getParent(), "../logs");
            if (!logDir.mkdirs() && !logDir.exists()) {
                logger.severe("Can't initialize log file (Creating logs dir failed)");
                return logger;
            }

            String logFilename = new File(logDir, LOG_FILE_DATE_FORMAT.format(new Date()) + ".log").toString();
            FileHandler fileHandler = new FileHandler(logFilename, true);
            fileHandler.setFormatter(new OneLineLoggerFormatter(false));
            fileHandler.setLevel(Level.ALL);
            logger.javaLogger.addHandler(fileHandler);
        }
        catch (URISyntaxException | IOException exc) {
            logger.severe(exc, "Can't initialize log file");
        }

        return logger;

    }
}
