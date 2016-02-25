package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CLiMenuPosixStrategy implements CLiMenuStrategy {

    private static final String NAME_PREFIX = "--";

    private static final String KEY_PREFIX = "-";

    @Override
    public boolean isOptionName(final String arg) {
        return arg.startsWith(NAME_PREFIX);
    }

    @Override
    public boolean isOptionKey(final String arg) {
        return arg.startsWith(KEY_PREFIX) && !isOptionName(arg);
    }

    @Override
    public String extractOptionName(final String arg) {
        return arg.substring(NAME_PREFIX.length());
    }

    @Override
    public Collection<Character> extractOptionKeys(final String arg) {
        final Set<Character> result = new HashSet<>();
        for (int i = KEY_PREFIX.length(); i < arg.length(); i++) {
            result.add(arg.charAt(i));
        }
        return result;
    }

    @Override
    public <T extends CliCommand> T bind(
        final CliCommandInfo info,
        final String[] args,
        final T command
    ) throws InvalidCommandArgumentException {
        return new CommandBinder<T>(info, command).to(args);
    }

    private class CommandBinder<T extends CliCommand> {

        private final CliCommandInfo info;

        private final T command;

        private final Map<String, Field> flagsByName = new HashMap<>();

        private final Map<Character, Field> flagsByKey = new HashMap<>();

        CommandBinder(final CliCommandInfo info, final T command) {
            this.info = info;
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

        private T to(final String[] args) throws InvalidCommandArgumentException {
            for (String arg : args) {
                if (isOptionName(arg)) {
                    final String name = extractOptionName(arg);
                    if (!name.equals(info.name())) {
                        setFlag(name, getFieldBy(name));
                    }
                } else if (isOptionKey(arg)) {
                    final Collection<Character> keys = extractOptionKeys(arg);
                    for (Character key : keys) {
                        if (!key.equals(info.key())) {
                            setFlag("" + key, getFieldBy(key));
                        }
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
                    info.name(),
                    name,
                    String.format("Illegal access to field %s", field.getName())
                    , e
                );
            }
        }

        private Field getFieldBy(final String name) throws InvalidCommandArgumentException {
            final Field result = flagsByName.get(name);
            if (result == null) {
                throw new InvalidCommandArgumentException(
                    info.name(),
                    name,
                    String.format("Flag '%s' is not found", name)
                );
            }
            return result;
        }

        private Field getFieldBy(final Character key) throws InvalidCommandArgumentException {
            final Field result = flagsByKey.get(key);
            if (result == null) {
                throw new InvalidCommandArgumentException(
                    info.name(),
                    "" + key,
                    String.format("Flag '%s' is not found", key)
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