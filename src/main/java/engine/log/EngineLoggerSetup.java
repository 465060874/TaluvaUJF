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
import java.util.logging.Logger;

class EngineLoggerSetup {

    private static final DateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    static final Logger BASE_LOGGER;



    static {
        BASE_LOGGER = Logger.getLogger(Engine.class.getCanonicalName());
        BASE_LOGGER.setUseParentHandlers(false);
        BASE_LOGGER.addHandler(new SystemLoggerHandler(new OneLineLoggerFormatter(false)));

        if (EngineLogger.LOG_IN_FILE) {
            try {
                File sourceFile = new File(Engine.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File logDir = new File(sourceFile.getParent(), "../logs");
                if (!logDir.mkdirs() && !logDir.exists()) {
                    BASE_LOGGER.severe("Can't initialize log file (Creating logs dir failed)");
                }

                String logFilename = new File(logDir, LOG_FILE_DATE_FORMAT.format(new Date()) + ".log").toString();
                FileHandler fileHandler = new FileHandler(logFilename, true);
                fileHandler.setFormatter(new OneLineLoggerFormatter(false));
                fileHandler.setLevel(Level.ALL);
                BASE_LOGGER.addHandler(fileHandler);
            }
            catch (URISyntaxException | IOException exc) {
                BASE_LOGGER.log(Level.SEVERE, "Can't initialize log file", exc);
            }
        }
    }
}
