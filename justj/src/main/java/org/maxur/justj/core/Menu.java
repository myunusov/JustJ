package org.maxur.justj.core;

import org.maxur.justj.core.cli.OptionsProcessingException;
import org.maxur.justj.core.cli.PosixArgumentsMapper;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
public class Menu<C extends Command> {

    protected C makeCommand(String[] args, Class<? extends C> commandClass) throws OptionsProcessingException {
        return new PosixArgumentsMapper<>(commandClass).readValue(args);
    }

}
