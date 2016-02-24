package org.maxur.justj.core.cli;

import java.util.Objects;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public abstract class CliCommand {

    private static final String COMMAND_CLASS_POSTFIX = "Command";

    private static final String CAUSE = " \n" +
        "Check that class has accessible default constructor and that inner class is static";

    private final String name;

    private Character key;

    protected CliCommand() {
        name = makeName();
        key = makeKey();
    }

    public <T extends CliCommand> T copy() throws CommandInstancingException {
        @SuppressWarnings("unchecked") final Class<T> aClass = (Class<T>) this.getClass();
        try {
            return aClass.newInstance();
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
        return this.getClass().isAnnotationPresent(Key.class);
    }

    private Character keyFromAnnotation() {
        final Key annotation = this.getClass().getAnnotation(Key.class);
        return annotation.value().charAt(0);
    }

    private Character keyFromName() {
        return name == null ? null : name.charAt(0);
    }

    private boolean annotatedAsCommand() {
        return this.getClass().isAnnotationPresent(Command.class);
    }

    private String nameFromClassName() {
        final String className = this.getClass().getSimpleName();
        int index = className.indexOf(COMMAND_CLASS_POSTFIX);
        if (index == -1) {
            return null;
        }
        return className.substring(0, index).toLowerCase();
    }

    private String nameFromAnnotation() {
        final Command annotation = this.getClass().getAnnotation(Command.class);
        return annotation.value().isEmpty() ? nameFromClassName() : annotation.value();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getClass());
    }

    public Character key() {
        return key;
    }

    public String name() {
        return name;
    }
}
