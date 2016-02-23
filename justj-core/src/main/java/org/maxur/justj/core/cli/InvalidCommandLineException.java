package org.maxur.justj.core.cli;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class InvalidCommandLineException extends CommandFabricationException {

    private static final String MESSAGE = "Command line '%s' is invalid: %s";

    public InvalidCommandLineException(final String commandName, final String message) {
        super(format(MESSAGE, commandName, message));
    }
}
