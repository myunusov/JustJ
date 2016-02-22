package org.maxur.justj.core.cli;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class OptionsProcessingException extends Exception {

    public OptionsProcessingException(final String message, final Exception cause) {
        super(message, cause);
    }

    public OptionsProcessingException(final String message) {
        super(message);
    }
}
