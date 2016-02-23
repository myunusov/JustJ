package org.maxur.justj.core.cli;

import org.maxur.justj.core.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class Slf4jAndLogbackLoggerAdapter implements Logger {

    private final org.slf4j.Logger logger;

    private Slf4jAndLogbackLoggerAdapter(final org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public static Slf4jAndLogbackLoggerAdapter loggerFor(final Class<?> clazz) {
        return new Slf4jAndLogbackLoggerAdapter(LoggerFactory.getLogger(clazz));
    }

    public static Logger loggerFor(final String name) {
        return new Slf4jAndLogbackLoggerAdapter(LoggerFactory.getLogger(name));
    }

    @Override
    public void error(final Exception cause) {
        this.logger.error(cause.getMessage(), cause);
    }

    @Override
    public void error(final String message) {
        this.logger.error(message);
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void setLevel(final Level level) {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            final ch.qos.logback.classic.Logger l = (ch.qos.logback.classic.Logger) this.logger;
            l.setLevel(translateLevelFrom(level));
        } else {
            throw new IllegalStateException("Set log level is'not supported for " + logger.getClass());
        }
    }

    private ch.qos.logback.classic.Level translateLevelFrom(final Level level) {
        switch (level) {
            case TRACE: return ch.qos.logback.classic.Level.ALL;
            case DEBUG: return ch.qos.logback.classic.Level.DEBUG;
            case INFO: return ch.qos.logback.classic.Level.INFO;
            case WARNING: return ch.qos.logback.classic.Level.WARN ;
            case ERROR: return ch.qos.logback.classic.Level.ERROR;
            default:
                return ch.qos.logback.classic.Level.OFF;
        }
    }

}
