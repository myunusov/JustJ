package org.maxur.justj.core.cli;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class InvalidCommandArgumentException extends CommandFabricationException {

    private static final String MESSAGE = "Command '%s' not supported command line %s: %s";

    public InvalidCommandArgumentException(final String commandName, final String arguments, final String message) {
        super(format(MESSAGE, commandName, arguments, message));
    }
}
