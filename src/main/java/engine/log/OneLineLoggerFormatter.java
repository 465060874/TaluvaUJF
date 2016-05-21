package engine.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Implementation d'un formatteur pour l'API standard Logger de Java
 * affichant tout les éléments principaux de manière compact sur une seule
 * ligne. (A l'exception des eventuelles stacktrace d'{@link Exception})
 */
class OneLineLoggerFormatter extends Formatter {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("[dd/MM-hh:mm:ss]");

    private final boolean displaySource;

    OneLineLoggerFormatter(boolean displaySource) {
        this.displaySource = displaySource;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(DATE_FORMAT.format(new Date(record.getMillis())));
        builder.append(" ");
        builder.append(record.getLevel().getName().toUpperCase());
        builder.append(": ");
        builder.append(formatMessage(record));
        if (displaySource) {
            builder.append(" (@");
            builder.append(record.getSourceClassName());
            builder.append("#");
            builder.append(record.getSourceMethodName());
            builder.append(")");
        }
        builder.append("\n");

        Throwable thrown = record.getThrown();
        if (thrown != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                thrown.printStackTrace(pw);
                pw.close();
                builder.append(sw.toString());
            }
            catch (Exception ex) {
                // Difficile de lancer une exeption quand on est en train
                // d'en loggé une autre.
            }
        }

        return builder.toString();
    }
}