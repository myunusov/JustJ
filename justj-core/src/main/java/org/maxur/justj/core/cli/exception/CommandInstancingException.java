package org.maxur.justj.core.cli.exception;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class CommandInstancingException extends CommandFabricationException {

    private static final String MESSAGE = "Command '%s' cannot be instanced: %s";

    public CommandInstancingException(final String commandName, final String message, final Exception cause) {
        super(format(MESSAGE, commandName, message), cause);
    }
}
