package org.maxur.justj.core.cli.info;

import org.maxur.justj.core.cli.annotation.Flag;
import org.maxur.justj.core.cli.annotation.Option;
import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.exception.InvalidCommandArgumentException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
class CliOptionInfo extends CliItemInfo {

    private final Set<CliOptionInfo> children = new HashSet<>();

    private CliOptionInfo(final Field field, final OptionType type) {
        super(field, type);
        if (type == OptionType.TRIGGER) {
            this.children.addAll(findChildren(field));
        }
    }

    CliOptionInfo(final Field field) {
        this(field, detectType(field));
    }

    private static OptionType detectType(final Field field) {
        if (isBoolean(field)) {
            return OptionType.FLAG;
        }
        if (field.getType().isEnum()) {
            return OptionType.TRIGGER;
        }
        if (field.isAnnotationPresent(Option.class)) {
            return OptionType.OPTION;
        }
        return OptionType.NONE;
    }

    private static boolean isBoolean(final Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    private static Set<CliOptionInfo> findChildren(final Field field) {
        final Set<CliOptionInfo> result = new HashSet<>();
        for (Field f : field.getType().getDeclaredFields()) {
            if (f.isEnumConstant()) {
                result.add(new CliOptionInfo(f, OptionType.NONE));
            }
        }
        return result;
    }

    @Override
    protected String findName() {
        if (field().isAnnotationPresent(Flag.class)) {
            final Flag flag = field().getAnnotation(Flag.class);
            return flag.value().isEmpty() ? field().getName() : flag.value();
        }
        if (field().isAnnotationPresent(Option.class)) {
            final Option option = field().getAnnotation(Option.class);
            return option.value().isEmpty() ? field().getName() : option.value();
        }
        return field().getName().toLowerCase();
    }

    boolean apply(final Argument argument, final Object command) throws InvalidCommandArgumentException {
        switch (type()) {
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
            if (field().getType() == String.class) {
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
        for (CliOptionInfo child : children) {
            final boolean result = child.applicable(argument);
            if (result) {
                makeValueAndSetIt(argument, child.field().getName(), command);
                return true;
            }
        }
        return false;
    }


    private void makeValueAndSetIt(
            final Argument argument,
            final String value,
            final Object command
    ) throws InvalidCommandArgumentException {
        try {
            final Method valueOf = this.field().getType().getMethod("valueOf", String.class);
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
        this.field().setAccessible(true);
        try {
            this.field().set(command, value);
        } catch (IllegalAccessException e) {
            throw new InvalidCommandArgumentException(
                    this.name(),
                    argument.asString(),
                    format("Illegal access to field %s", this.field().getName())
                    , e
            );
        }
    }

}
