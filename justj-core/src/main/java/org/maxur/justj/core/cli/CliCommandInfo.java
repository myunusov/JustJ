package org.maxur.justj.core.cli;

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

    private final String name;

    private final Character key;

    private final boolean isDefault;


    protected CliCommandInfo(Class<CliCommand> commandClass) {
        this.commandClass = commandClass;
        name = makeName();
        key = makeKey();
        isDefault = commandClass.isAnnotationPresent(Default.class);
    }

    <T extends CliCommand> T copy() throws CommandInstancingException {
        try {
            //noinspection unchecked
            return (T) commandClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommandInstancingException(this.name(), e.getMessage() + CAUSE, e);
        }
    }


    private Character makeKey() {
        return annotatedWithKey() ?
            keyFromAnnotation() :
            keyFromName();
    }

    private String makeName() {
        return annotatedAsCommand() ?
            nameFromAnnotation() :
            nameFromClassName();
    }

    private boolean annotatedWithKey() {
        return commandClass.isAnnotationPresent(Key.class);
    }

    private Character keyFromAnnotation() {
        final Key annotation = commandClass.getAnnotation(Key.class);
        return annotation.value().charAt(0);
    }

    private Character keyFromName() {
        return name == null ? null : name.charAt(0);
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

    public Character key() {
        return key;
    }

    public String name() {
        return name;
    }

    boolean isDefault() {
        return isDefault;
    }
}
