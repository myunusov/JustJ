package org.maxur.justj.core.cli.exception;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public abstract class CommandFabricationException extends Exception {

    public CommandFabricationException(final String message) {
        super(message);
    }

    public CommandFabricationException(final String message, final Exception cause) {
        super(message, cause);
    }
}
