package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.util.*;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu {

    private static final String NAME_PREFIX = "--";

    private static final String KEY_PREFIX = "-";

    private final Map<String, CliCommand> commandsByName = new HashMap<>();

    private final Map<Character, CliCommand> commandsByKey = new HashMap<>();

    private CliCommand defaultCommand = null;

    public void register(final CliCommand... list) {
        for (CliCommand command : list) {
            if (isDefault(command)) {
                defaultCommand = command;
            }
            commandsByName.put(command.name(), command);
            commandsByKey.put(command.key(), command);
        }
    }

    private boolean isDefault(final CliCommand command) {
        return command.getClass().isAnnotationPresent(Default.class);
    }

    public <T extends CliCommand> T makeCommand(final String name) throws CommandFabricationException {
        final CliCommand prototype = commandsByName.get(name);
        if (null == prototype) {
            throw new CommandNotFoundException(name);
        }
        return prototype.copy();

    }

    public <T extends CliCommand> T makeCommand(final String[] args) throws CommandFabricationException {
        List<CliCommand> result = new ArrayList<>();
        for (String arg : args) {
            if (isOptionName(arg)) {
                final CliCommand command = getCommandByName(arg);
                if (command != null) {
                    result.add(command);
                }
            } else if (isOptionKey(arg)) {
                final CliCommand command = getCommandByKey(arg);
                if (command != null) {
                    result.add(command);
                }
            }
        }
        final T command = processResult(args, result);
        return command == null ? null : bind(command).to(args);
    }

    @SuppressWarnings("unchecked")
    private <T extends CliCommand> T processResult(String[] args, List<CliCommand> result) throws InvalidCommandLineException {
        switch (result.size()) {
            case 0:
                return (T) defaultCommand;
            case 1:
                return (T)result.get(0);
            default:
                throw moreThanOneCommandException(args, result);
        }
    }

    private <T extends CliCommand> CommandBinder<T> bind(final T command) {
        return new CommandBinder<>(command);
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

    private boolean isOptionName(final String arg) {
        return arg.startsWith(NAME_PREFIX);
    }

    private boolean isOptionKey(final String arg) {
        return arg.startsWith(KEY_PREFIX) && arg.charAt(1) != '-';
    }

    private CliCommand getCommandByName(final String arg) throws CommandFabricationException {
        final String name = extractOptionName(arg);
        final CliCommand command = commandsByName.get(name);
        return command == null ? null : command.copy();
    }

    private String extractOptionName(final String arg) {
        return arg.substring(NAME_PREFIX.length());
    }

    private CliCommand getCommandByKey(final String arg) throws CommandFabricationException {
        final Character key = extractOptionKey(arg);
        final CliCommand command = commandsByKey.get(key);
        return command == null ? null : command.copy();
    }

    private Character extractOptionKey(final String arg) {
        return arg.charAt(KEY_PREFIX.length());
    }

    private class CommandBinder<T extends CliCommand> {

        private final T command;

        private final Map<String, Field> flagsByName = new HashMap<>();

        private final Map<Character, Field> flagsByKey = new HashMap<>();

        CommandBinder(final T command) {
            this.command = command;
            findFields(command.getClass());
        }

        private void findFields(final Class commandClass) {
            if (!CliCommand.class.isAssignableFrom(commandClass)) {
                return;
            }
            for (Field field : commandClass.getDeclaredFields()) {
                makeFlag(field);
            }
            findFields(commandClass.getSuperclass());
        }

        private void makeFlag(Field field) {
            final String name = extractFlagName(field);
            if (name != null) {
                flagsByName.put(name, field);
            }
            final Character key = extractFlagKey(field);
            if (key != null) {
                flagsByKey.put(key, field);
            } else if (name != null) {
                flagsByKey.put(name.charAt(0), field);
            }
        }

        private Character extractFlagKey(final Field field) {
            if (field.isAnnotationPresent(Key.class)) {
                final Key key = field.getAnnotation(Key.class);
                return key.value().charAt(0);
            }
            return null;
        }

        private String extractFlagName(final Field field) {
            if (field.isAnnotationPresent(Flag.class)) {
                final Flag flag = field.getAnnotation(Flag.class);
                return flag.value().isEmpty() ? field.getName() : flag.value();
            } else {
                if (isBoolean(field)) {
                    return field.getName();
                }
            }
            return null;
        }

        private boolean isBoolean(final Field field) {
            return field.getType() == boolean.class || field.getType() == Boolean.class;
        }

        public T to(final String[] args) throws InvalidCommandArgumentException {
            for (String arg : args) {
                if (isOptionName(arg)) {
                    final String name = extractOptionName(arg);
                    if (!name.equals(command.name())) {
                        this.setFlag(name, this.getFieldBy(name));
                    }
                }  else if (isOptionKey(arg)) {
                    final Character key = extractOptionKey(arg);
                    if (!key.equals(command.key())) {
                        this.setFlag("" + key, this.getFieldBy(key));
                    }
                }
            }
            return command;
        }

        private void setFlag(final String name, final Field field) throws InvalidCommandArgumentException {
            try {
                setOption(field, true);
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
            final Field result = flagsByName.get(name);
            if (result == null) {
                throw new InvalidCommandArgumentException(
                        command.name(),
                        name,
                        format("Flag '%s' is not found", name)
                );
            }
            return result;
        }

        private Field getFieldBy(final Character key) throws InvalidCommandArgumentException {
            final Field result = flagsByKey.get(key);
            if (result == null) {
                throw new InvalidCommandArgumentException(
                        command.name(),
                        "" + key,
                        format("Flag '%s' is not found", key)
                );
            }
            return result;
        }

        private void setOption(final Field field, final Object value) throws IllegalAccessException {
            field.setAccessible(true);
            field.set(command, value);
        }
    }

}
