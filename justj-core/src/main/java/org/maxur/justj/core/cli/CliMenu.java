package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu {

    private static final String FULL_NAME_PREFIX = "--";

    // private static final String SHORT_NAME_PREFIX = "-";

    private final Map<String, CliCommand> commands = new HashMap<>();

    public void register(final CliCommand... list) {
        for (CliCommand command : list) {
            commands.put(command.name(), command);
        }
    }

    public CliCommand makeCommand(final String name) throws CommandFabricationException {
        final CliCommand prototype = commands.get(name);
        if (null == prototype) {
            throw new CommandNotFoundException(name);
        }
        return prototype.copy();

    }

    public CliCommand makeCommand(final String[] args) throws CommandFabricationException {
        List<CliCommand> result = new ArrayList<>();
        for (String arg : args) {
            if (isOption(arg)) {
                final CliCommand command = processFlag(arg);
                if (command != null) {
                    result.add(command);
                }
            }
        }
        switch (result.size()) {
            case 0: return null;
            case 1: return bind(result.get(0)).to(args);
            default:
                throw moreThanOneCommandException(args, result);
        }
    }

    private CommandBinder bind(final CliCommand command) {
        return new CommandBinder(command);
    }

    private InvalidCommandLineException moreThanOneCommandException(String[] args, List<CliCommand> result) {
        return new InvalidCommandLineException(
            Arrays.toString(args),
            format(
                "You try to call commands '%s' and '%s' simultaneously",
                result.get(0).name(),
                result.get(1).name()
            )
        );
    }

    private boolean isOption(final String arg) {
        return arg.startsWith(FULL_NAME_PREFIX);
    }

    private CliCommand processFlag(final String arg) throws CommandFabricationException {
        final String name = arg.substring(FULL_NAME_PREFIX.length());
        final CliCommand command = commands.get(name);
        return command == null ? null : command.copy();
    }

    private class CommandBinder {

        private final CliCommand command;

        private final Map<String, Field> flags = new HashMap<>();

        public CommandBinder(final CliCommand command) {
            this.command = command;
            findFields(command.getClass());
        }

        private void findFields(final Class commandClass) {
            if (commandClass == null) {
                return;
            }
            for (Field field : commandClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Flag.class)) {
                    final Flag option = field.getAnnotation(Flag.class);
                    flags.put(option.value(), field);
                }
                if (field.isAnnotationPresent(Operands.class)) {
                    //operands.add(field);
                }

            }
            findFields(commandClass.getSuperclass());
        }

        public CliCommand to(final String[] args) throws InvalidCommandArgumentException {
            for (String arg : args) {
                if (isOption(arg)) {
                    this.processFlag(arg.substring(FULL_NAME_PREFIX.length()));
                }
            }
            return command;
        }

        private void processFlag(final String name) throws InvalidCommandArgumentException {
            try {
                setOption(getFieldBy(name), true);
            } catch (IllegalAccessException e) {
                throw new InvalidCommandArgumentException(
                    command.name(),
                    name,
                    format("Illegal access to field %s", field.getName())
                    , e
                );
            }
        }

        private Field getFieldBy(final String name) throws InvalidCommandArgumentException {
            for (Field field : flags.values()) {
                if (field.getName().equalsIgnoreCase(name)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            throw new InvalidCommandArgumentException(
                command.name(),
                name,
                format("Flag '%s' is not found", name)
            );
        }

        private void setOption(final Field field, final Object value) throws IllegalAccessException {
            field.set(command, value);
        }
    }

}
