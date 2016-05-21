package engine.log;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class SystemLoggerHandler extends Handler {

    private final PrintStream out;
    private final PrintStream err;

    SystemLoggerHandler(Formatter formatter) {
        this.out = System.out;
        this.err = System.err;
        setFormatter(formatter);
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            err.print(getFormatter().format(record));
        }
        else {
            out.print(getFormatter().format(record));
        }
    }

    @Override
    public void flush() {
        out.flush();
        err.flush();
    }

    @Override
    public void close() throws SecurityException {
        flush();
    }
}