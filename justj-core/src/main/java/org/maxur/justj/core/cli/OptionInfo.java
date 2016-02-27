package org.maxur.justj.core.cli;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
class OptionInfo {

    private final Field field;

    private final String name;

    private final Character key;

    private final OptionType type;

    private final Set<OptionInfo> children = new HashSet<>();

    private OptionInfo(final Field field, final OptionType type) {
        this.field = field;
        this.type = type;
        this.name = findName();
        this.key = findKey();
    }

    OptionInfo(final Field field) {
        this.field = field;
        this.type = detectType(field);
        this.name = findName();
        this.key = findKey();
    }


    private OptionType detectType(final Field field) {
        if (isBoolean(field)) {
            return OptionType.FLAG;
        }
        if (field.getType().isEnum()) {
            children.addAll(findChildren());
            return OptionType.TRIGGER;
        }
        if (field.isAnnotationPresent(Option.class)) {
            return OptionType.OPTION;
        }
        return OptionType.NONE;
    }

    private Set<OptionInfo> findChildren() {
        final Set<OptionInfo> result = new HashSet<>();
        for (Field f : field.getType().getDeclaredFields()) {
            if (f.isEnumConstant()) {
                result.add(new OptionInfo(f, OptionType.NONE));
            }
        }
        return result;
    }

    private boolean isBoolean(final Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    private String findName() {
        if (field.isAnnotationPresent(Flag.class)) {
            final Flag flag = field.getAnnotation(Flag.class);
            return flag.value().isEmpty() ? field.getName() : flag.value();
        }
        if (field.isAnnotationPresent(Option.class)) {
            final Option option = field.getAnnotation(Option.class);
            return option.value().isEmpty() ? field.getName() : option.value();
        }
        return field.getName().toLowerCase();
    }

    private Character findKey() {
        if (field.isAnnotationPresent(Key.class)) {
            final String result = field.getAnnotation(Key.class).value();
            if (!result.isEmpty()) {
                return result.charAt(0);
            }
        }
        return name == null ? null : name.charAt(0);
    }

    boolean apply(final Argument argument, final Object command) throws InvalidCommandArgumentException {
        switch (type) {
            case FLAG:
                return applyToFlag(argument, command);
            case TRIGGER:
                return applyToTrigger(argument, command);
            case OPTION:
                return applyToOption(argument, command);
            default:
                return false;
        }
    }

    private boolean applyToOption(
            final Argument argument,
            final Object command
    ) throws InvalidCommandArgumentException {
        final boolean result = this.applicable(argument);
        if (result) {
            if (field.getType() == String.class) {
                setOption(argument, argument.optionArgument(), command);
            } else {
                makeValueAndSetIt(argument, argument.optionArgument(), command);
            }
            return true;
        }
        return false;
    }

    private boolean applyToFlag(
            final Argument argument,
            final Object command
    ) throws InvalidCommandArgumentException {
        final boolean result = this.applicable(argument);
        if (result) {
            setOption(argument, true, command);
            return true;
        }
        return false;
    }

    private boolean applyToTrigger(
            final Argument argument,
            final Object command
    ) throws InvalidCommandArgumentException {
        for (OptionInfo child : children) {
            final boolean result = child.applicable(argument);
            if (result) {
                makeValueAndSetIt(argument, child.field.getName(), command);
                return true;
            }
        }
        return false;
    }

    private boolean applicable(final Argument argument) {
        return argument.isKey() ?
                Objects.equals(argument.key(), this.key) :
                Objects.equals(argument.name(), this.name);
    }


    private void makeValueAndSetIt(
            final Argument argument,
            final String value,
            final Object command
    ) throws InvalidCommandArgumentException {
        try {
            final Method valueOf = this.field.getType().getMethod("valueOf", String.class);
            final Object v = valueOf.invoke(null, value);
            setOption(argument, v, command);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }


    private void setOption(
            final Argument argument,
            final Object value,
            final Object command
    ) throws InvalidCommandArgumentException {
        this.field.setAccessible(true);
        try {
            this.field.set(command, value);
        } catch (IllegalAccessException e) {
            throw new InvalidCommandArgumentException(
                    this.name,
                    argument.asString(),
                    format("Illegal access to field %s", this.field.getName())
                    , e
            );
        }
    }

    private enum OptionType {
        NONE,
        FLAG,
        TRIGGER,
        OPTION
    }
}
