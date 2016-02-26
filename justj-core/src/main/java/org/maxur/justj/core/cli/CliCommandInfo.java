package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
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

    private final Set<OptionInfo> options = new HashSet<>();

    protected CliCommandInfo(Class<CliCommand> commandClass) {
        this.commandClass = commandClass;
        commandName = makeName();
        keys = makeKeys();
        isDefault = commandClass.isAnnotationPresent(Default.class);
        findFields(commandClass);
    }

    private String makeName() {
        return annotatedAsCommand() ?
                nameFromAnnotation() :
                nameFromClassName();
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
            options.add(new OptionInfo(field));
        }
        findFields(commandClass.getSuperclass());
    }

    <T extends CliCommand> T instance() throws CommandInstancingException {
        try {
            //noinspection unchecked
            return (T) commandClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommandInstancingException(this.name(), e.getMessage() + CAUSE, e);
        }
    }

    public void bind(
            final Object command, final Argument argument
    ) throws InvalidCommandArgumentException {
        if (isCommandId(argument)) {
            return;
        }
        boolean result = false;
        for (OptionInfo option : options) {
            result |= option.apply(argument, command);
        }

        if (!result) {
            throw new InvalidCommandArgumentException(
                    commandName,
                    argument.asString(),
                    format("Flag '%s' is not found", argument.asString())
            );
        }
    }

    private boolean isCommandId(final Argument argument) {
        return keys.contains(argument.key()) || commandName.equals(argument.name());
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


}


