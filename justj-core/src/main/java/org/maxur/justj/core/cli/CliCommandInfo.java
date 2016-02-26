package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/25/2016</pre>
 */
public class CliCommandInfo {

    private static final String COMMAND_CLASS_POSTFIX = "Command";

    private static final String CAUSE = " \n" +
        "Check that class has accessible default constructor and that inner class is static";

    private final Class<CliCommand> commandClass;

    private final String commandName;

    private final Set<Character> keys;

    private final boolean isDefault;

    private final Map<String, Field> flagsByName = new HashMap<>();

    private final Map<Character, Field> flagsByKey = new HashMap<>();

    private final Map<String, Field> triggersByName = new HashMap<>();

    private final Map<Character, Field> triggersByKey = new HashMap<>();

    private final Map<String, String> valueByName = new HashMap<>();

    private final Map<Character, String> valueByKey = new HashMap<>();


    protected CliCommandInfo(Class<CliCommand> commandClass) {
        this.commandClass = commandClass;
        commandName = makeName();
        keys = makeKeys();
        isDefault = commandClass.isAnnotationPresent(Default.class);
        findFields(commandClass);
    }

    private Set<Character> makeKeys() {
        if (annotatedWithKey()) {
            return Collections.singleton(keyFromAnnotation());
        } else if (annotatedWithKeys()) {
            return keysFromAnnotation();
        } else {
            return Collections.singleton(keyFromName());
        }
    }

    private String makeName() {
        return annotatedAsCommand() ?
            nameFromAnnotation() :
            nameFromClassName();
    }

    private boolean annotatedWithKeys() {
        return commandClass.isAnnotationPresent(KeyContainer.class);
    }

    private Set<Character> keysFromAnnotation() {
        final Set<Character> result = new HashSet<>();
        final KeyContainer annotation = commandClass.getAnnotation(KeyContainer.class);
        for (Key key : annotation.value()) {
            result.add(key.value().charAt(0));
        }
        return result;
    }

    private boolean annotatedWithKey() {
        return commandClass.isAnnotationPresent(Key.class);
    }

    private Character keyFromAnnotation() {
        final Key annotation = commandClass.getAnnotation(Key.class);
        return annotation.value().charAt(0);
    }

    private Character keyFromName() {
        return commandName == null ? null : commandName.charAt(0);
    }

    private boolean annotatedAsCommand() {
        return commandClass.isAnnotationPresent(Command.class);
    }

    private String nameFromClassName() {
        final String className = commandClass.getSimpleName();
        int index = className.indexOf(COMMAND_CLASS_POSTFIX);
        return index == -1 ?
            className.toLowerCase() :
            className.substring(0, index).toLowerCase();
    }

    private String nameFromAnnotation() {
        final Command annotation = commandClass.getAnnotation(Command.class);
        return annotation.value().isEmpty() ? nameFromClassName() : annotation.value();
    }

    private void findFields(final Class commandClass) {
        if (!CliCommand.class.isAssignableFrom(commandClass)) {
            return;
        }
        for (Field field : commandClass.getDeclaredFields()) {
           // final OptionInfo id = new OptionInfo(field);
            findFlag(field);
            findTriggers(field);
        }
        findFields(commandClass.getSuperclass());
    }

    private void findFlag(final Field field) {
        final String name = extractFlagName(field);
        final Character key = findKey(field, name);

        if (name != null) {
            flagsByName.put(name, field);
        }
        if (key != null) {
            flagsByKey.put(key, field);
        }
    }

    private void findTriggers(final Field field) {
        final Class<?> type = field.getType();
        if (!type.isEnum()) {
            return;
        }
        for (Field f : type.getDeclaredFields()) {
            if (!f.isEnumConstant()) {
                continue;
            }
            final String name = extractTriggerName(f);
            final Character key = findKey(f, name);

            triggersByName.put(name, field);
            valueByName.put(name, f.getName());
            triggersByKey.put(key, field);
            valueByKey.put(key, f.getName());
        }
    }

    private Character findKey(final Field field, final String name) {
        final Character key = extractFlagKey(field);
        if (key == null) {
            return name == null ? null : name.charAt(0);
        } else {
            return key;
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

    private String extractTriggerName(final Field field) {
        final String name;
        if (field.isAnnotationPresent(Flag.class)) {
            final Flag flag = field.getAnnotation(Flag.class);
            name = flag.value().isEmpty() ? field.getName() : flag.value();
        } else {
            name = field.getName();
        }
        return name.toLowerCase();
    }

    private boolean isBoolean(final Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    public void setOptionByKey(final Character key, final CliCommand command) throws InvalidCommandArgumentException {
        setOptionById(command, new OptionId(key));
    }

    public void setOptionByName(final String name, final CliCommand command) throws InvalidCommandArgumentException {
        setOptionById(command, new OptionId(name));
    }

    private void setOptionById(CliCommand command, OptionId id) throws InvalidCommandArgumentException {
        if (isCommandId(id)) {
            return;
        }
        final Field field = flagById(id);
        if (field != null) {
            setOption(id, field, true, command);
            return;
        }
        final Field trigger = triggerById(id);
        if (trigger != null) {
            setEnum(id, trigger, findValue(id), command);
            return;
        }
        throw new InvalidCommandArgumentException(
            commandName,
            id.asString(),
            format("Flag '%s' is not found", id.asString())
        );
    }

    private boolean isCommandId(final OptionId id) {
        return keys.contains(id.key) || commandName.equals(id.name);
    }

    private Field flagById(final OptionId id) {
        return id.isKey() ? flagsByKey.get(id.key) : flagsByName.get(id.name);
    }

    private Field triggerById(final OptionId id) {
        return id.isKey() ? triggersByKey.get(id.key) : triggersByName.get(id.name);
    }

    <T extends CliCommand> T instance() throws CommandInstancingException {
        try {
            //noinspection unchecked
            return (T) commandClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommandInstancingException(this.name(), e.getMessage() + CAUSE, e);
        }
    }

    private String findValue(final OptionId id) throws InvalidCommandArgumentException {
        final String result = id.isKey() ? valueByKey.get(id.key) : valueByName.get(id.name);
        if (result == null) {
            throw new InvalidCommandArgumentException(
                this.commandName,
                id.asString(),
                format("Flag '%s' is not found", id.asString())
            );
        }
        return result;
    }

    private void setEnum(
        final OptionId id,
        final Field field,
        final String value,
        final CliCommand command
    ) throws InvalidCommandArgumentException {
        try {
            Method valueOf = field.getType().getMethod("valueOf", String.class);
            Object v = valueOf.invoke(null, value);
            setOption(id, field, v, command);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setOption(
        final OptionId id,
        final Field field,
        final Object value,
        final CliCommand command
    ) throws InvalidCommandArgumentException {
        field.setAccessible(true);
        try {
            field.set(command, value);
        } catch (IllegalAccessException e) {
            throw new InvalidCommandArgumentException(
                this.commandName,
                id.asString(),
                format("Illegal access to field %s", field.getName())
                , e
            );
        }
    }


    public Set<Character> keys() {
        return keys;
    }

    public String name() {
        return commandName;
    }

    boolean isDefault() {
        return isDefault;
    }

    private static class OptionId {

        private final Character key;

        private final String name;

        private OptionId(final Character key) {
            this.key = key;
            this.name = null;
        }

        private OptionId(final String name) {
            this.key = null;
            this.name = name;
        }

        private boolean isKey() {
            return key != null;
        }

        private String asString() {
            return key != null ? "" + key : name;
        }
    }
}


