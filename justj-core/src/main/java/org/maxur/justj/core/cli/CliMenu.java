package org.maxur.justj.core.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu {

    private static final String COMMAND_CLASS_POSTFIX = "Command";

    private final Map<String, CliCommand> commands = new HashMap<>();

    public void register(final CliCommand... list) {
        for (CliCommand command : list) {
            commands.put(nameFor(command).toUpperCase(), command);
        }
    }

    private String nameFor(final CliCommand command) {
        final Class<? extends CliCommand> commandClass = command.getClass();
        if (commandClass.isAnnotationPresent(Command.class)) {
            final Command annotation = commandClass.getAnnotation(Command.class);
            return annotation.value();
        } else {
            final String className = commandClass.getSimpleName();
            int index = className.indexOf(COMMAND_CLASS_POSTFIX);
            return  className.substring(0, index);
        }
    }

    public CliCommand makeCommand(final String name) throws CommandNotFoundException {
        return copyFromPrototype(name);
    }

    public CliCommand makeCommand(final String[] args) throws CommandFabricationException {
        if (args.length == 0) {
            return null;
        }
        CliCommand result = null;
        String commandName = null;
        for (String arg : args) {
            final String name;
            name = arg.substring(2);
            CliCommand command = copyFromPrototype(name);
            if (commands.get(name.toUpperCase()) == null) {
                continue;
            }
            if (result != null) {
                throw new InvalidCommandLineException(
                        Arrays.toString(args),
                        format("You try to call commands '%s' and '%s' simultaneously", commandName, name)
                );
            } else {
                result = command;
                commandName = name;
            }
        }
        return result;
    }

    private CliCommand copyFromPrototype(String name) throws CommandNotFoundException {
        final CliCommand prototype = commands.get(name.toUpperCase());
        if (null == prototype) {
            throw new CommandNotFoundException(name);
        }
        return prototype.copy();

    }
}
