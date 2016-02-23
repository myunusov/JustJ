package org.maxur.justj.core.cli;

import org.maxur.justj.core.Command;

/**
 *
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
public class CliMenu<C extends Command> {



    protected C makeCommand(String[] args, Class<? extends C> commandClass) throws OptionsProcessingException {
        return new PosixArgumentsMapper<>(commandClass).readValue(args);
    }

}
