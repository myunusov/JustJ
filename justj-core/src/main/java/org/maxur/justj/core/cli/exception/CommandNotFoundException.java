package org.maxur.justj.core.cli.exception;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class CommandNotFoundException extends CommandFabricationException {

    private static final String MESSAGE = "Command '%s' is not found";

    public CommandNotFoundException(final String commandName) {
        super(format(MESSAGE, commandName));
    }
}
