package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

        private final Map<String, Field> triggersByName = new HashMap<>();

        private final Map<Character, Field> triggersByKey = new HashMap<>();


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
                makeTriggers(field);
            }
            findFields(commandClass.getSuperclass());
        }

        private void makeTriggers(final Field field) {
            final Class<?> type = field.getType();
            if (!type.isEnum()) {
                return;
            }
            for (Field f : type.getDeclaredFields()) {
                if (!f.isEnumConstant()) {
                    continue;
                }
                final String name = extractEnumFlagName(f);
                triggersByName.put(name, field);
                final Character key = extractFlagKey(f);
                if (key != null) {
                    triggersByKey.put(key, field);
                } else {
                    triggersByKey.put(name.charAt(0), field);
                }
            }
        }

        private void makeFlag(final Field field) {
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
                    setOptionByName(extractOptionName(arg));
                } else if (isOptionKey(arg)) {
                    final Collection<Character> keys = extractOptionKeys(arg);
                    for (Character key : keys) {
                        setOptionByKey(key);
                    }
                }
            }
            return command;
        }

        private void setOptionByKey(final Character key) throws InvalidCommandArgumentException {
            if (key.equals(info.key())) {
                return;
            }
            final Field field = flagsByKey.get(key);
            if (field != null) {
                setOption("" + key, field, true);
                return;
            }
            final Field trigger = triggersByKey.get(key);
            if (trigger != null) {
                setEnum("" + key, trigger, findValue(key, trigger));
                return;
            }
            throw new InvalidCommandArgumentException(
                    info.name(),
                    "" + key,
                    String.format("Flag '%s' is not found", key)
            );
        }

        private void setOptionByName(final String name) throws InvalidCommandArgumentException {
            if (name.equals(info.name())) {
                return;
            }
            final Field field = flagsByName.get(name);
            if (field != null) {
                setOption(name, field, true);
                return;
            }
            final Field trigger = triggersByName.get(name);
            if (trigger != null) {
                setEnum(name, trigger, findValue(name, trigger));
                return;
            }
            throw new InvalidCommandArgumentException(
                    info.name(),
                    name,
                    String.format("Flag '%s' is not found", name)
            );
        }

        private String findValue(final String name, final Field trigger) throws InvalidCommandArgumentException {
            final Class<?> type = trigger.getType();
            for (Field f : type.getDeclaredFields()) {
                if (!f.isEnumConstant()) {
                    continue;
                }
                if (extractEnumFlagName(f).equals(name)) {
                    return f.getName();
                }
            }
            throw new InvalidCommandArgumentException(
                    info.name(),
                    name,
                    String.format("Flag '%s' is not found", name)
            );
        }

        private String findValue(final Character key, final Field trigger) throws InvalidCommandArgumentException {
            final Class<?> type = trigger.getType();
            for (Field f : type.getDeclaredFields()) {
                if (!f.isEnumConstant()) {
                    continue;
                }
                final Character k = extractFlagKey(f);
                if (k != null && k == key) {
                    return f.getName();
                } else if (extractEnumFlagName(f).charAt(0) == key) {
                    return f.getName();
                }
            }
            throw new InvalidCommandArgumentException(
                    info.name(),
                    "" + key,
                    String.format("Flag '%s' is not found", key)
            );
        }

        private String extractEnumFlagName(Field f) {
            final String name;
            if (f.isAnnotationPresent(Flag.class)) {
                final Flag flag = f.getAnnotation(Flag.class);
                name = flag.value().isEmpty() ? f.getName() : flag.value();
            } else {
                name = f.getName();
            }
            return name.toLowerCase();
        }

        private void setEnum(
                final String name,
                final Field field,
                final String value
        ) throws InvalidCommandArgumentException {
            try {
                Method valueOf = field.getType().getMethod("valueOf", String.class);
                Object v = valueOf.invoke(null, value);
                setOption(name, field, v);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }


        private void setOption(
                final String name,
                final Field field,
                final Object value
        ) throws InvalidCommandArgumentException {
            field.setAccessible(true);
            try {
                field.set(command, value);
            } catch (IllegalAccessException e) {
                throw new InvalidCommandArgumentException(
                        info.name(),
                        name,
                        String.format("Illegal access to field %s", field.getName())
                        , e
                );
            }
        }
    }
}