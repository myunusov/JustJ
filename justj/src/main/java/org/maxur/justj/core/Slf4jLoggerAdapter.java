package org.maxur.justj.core;

import org.slf4j.LoggerFactory;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class Slf4jLoggerAdapter implements Logger {

    private final org.slf4j.Logger logger;

    private Slf4jLoggerAdapter(final org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public static Slf4jLoggerAdapter loggerFor(final Class<?> clazz) {
        return new Slf4jLoggerAdapter(LoggerFactory.getLogger(clazz));
    }

    @Override
    public void error(final Exception e) {
        this.logger.error(e.getMessage(), e);
    }
}
