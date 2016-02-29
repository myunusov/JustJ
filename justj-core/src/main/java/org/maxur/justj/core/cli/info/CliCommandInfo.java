package org.maxur.justj.core.cli.info;

import org.maxur.justj.core.cli.annotation.Command;
import org.maxur.justj.core.cli.annotation.Default;
import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.exception.CommandInstancingException;
import org.maxur.justj.core.cli.exception.InvalidCommandArgumentException;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/25/2016</pre>
 */
public class CliCommandInfo extends CliItemInfo {

    private static final String COMMAND_CLASS_POSTFIX = "Command";

    private static final String CAUSE = " \n" +
            "Check that class has accessible default constructor and that inner class is static";

    private final boolean isDefault;

    private final Set<CliOptionInfo> options = new HashSet<>();

    private CliCommandInfo(final Class<Object> commandClass) {
        super(commandClass);
        this.isDefault = commandClass.isAnnotationPresent(Default.class);
        findFields(commandClass);
    }

    public static CliCommandInfo commandInfo(final Class<Object> commandClass) {
        if (!annotatedAsCommand(commandClass)) {
            throw new IllegalArgumentException(
                    format("Class '%s' must be annotated as Command", commandClass.getName())
            );
        }
        return new CliCommandInfo(commandClass);
    }

    private static boolean annotatedAsCommand(final Class commandClass) {
        return commandClass.isAnnotationPresent(Command.class);
    }


    @Override
    protected String findName() {
        return isAnnotationPresent(Command.class) ?
                nameFromAnnotation() :
                nameFromClassName();
    }

    private String nameFromClassName() {
        final String className = commandClass().getSimpleName();
        int index = className.indexOf(COMMAND_CLASS_POSTFIX);
        return index == -1 ?
                className.toLowerCase() :
                className.substring(0, index).toLowerCase();
    }

    private String nameFromAnnotation() {
        final Command annotation = getAnnotation(Command.class);
        return annotation.value().isEmpty() ? nameFromClassName() : annotation.value();
    }

    private void findFields(final Class commandClass) {
        if (!annotatedAsCommand(commandClass)) {
            return;
        }
        for (Field field : commandClass.getDeclaredFields()) {
            options.add(new CliOptionInfo(field));
        }
        findFields(commandClass.getSuperclass());
    }

    public <T> T instance() throws CommandInstancingException {
        try {
            //noinspection unchecked
            return (T) commandClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommandInstancingException(this.name(), e.getMessage() + CAUSE, e);
        }
    }

    public void bind(
            final Object command, final Argument argument
    ) throws InvalidCommandArgumentException {
        if (applicable(argument)) {
            return;
        }
        boolean result = false;
        for (CliOptionInfo option : options) {
            result |= option.apply(argument, command);
        }

        if (!result) {
            throw new InvalidCommandArgumentException(
                    name(),
                    argument.asString(),
                    format("Flag '%s' is not found", argument.asString())
            );
        }
    }

    public boolean isDefault() {
        return isDefault;
    }


    public Set<CliOptionInfo> options() {
        return this.options;
    }
}


