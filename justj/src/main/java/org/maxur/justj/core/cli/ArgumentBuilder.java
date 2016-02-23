package org.maxur.justj.core.cli;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static java.lang.String.format;

class ArgumentBuilder<O> {

    private static final String FULL_NAME_PREFIX = "--";

    private static final String SHORT_NAME_PREFIX = "-";

    private final Map<String, Field> flags = new HashMap<>();

    private final Set<Field> operands = new HashSet<>();

    private String[] args;

    private Class<O> argumentsClass;

    private O result;
    private int cursor;

    O build() throws OptionsProcessingException {
        findFields(argumentsClass);
        result = makeInstance();
        cursor = 0;
        makeNextArgument();
        validate();
        return result;
    }

    private void validate() throws OptionsProcessingException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<O>> constraintViolations = validator.validate(result);
        if (!constraintViolations.isEmpty()) {
            String message = "Arguments cannot be instantiated. Constraint violations:";
            for (ConstraintViolation<O> violation : constraintViolations) {
                message += format("%n%s %s", violation.getPropertyPath(), violation.getMessage());
            }
            throw new OptionsProcessingException(message);
        }
    }

    private O makeInstance() throws OptionsProcessingException {
        try {
            return argumentsClass.newInstance();
        } catch (InstantiationException e) {
            throw new OptionsProcessingException("Arguments cannot be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new OptionsProcessingException("Arguments cannot be instantiated. Illegal access", e);
        }
    }

    private void findFields(final Class argumentsClass) {
        if (argumentsClass == null) {
            return;
        }
        for (Field field : argumentsClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Option.class)) {
                final Option option = field.getAnnotation(Option.class);
                flags.put(option.value(), field);
            }
            if (field.isAnnotationPresent(Operands.class)) {
                operands.add(field);
            }

        }
        findFields(argumentsClass.getSuperclass());
    }


    private void makeNextArgument() throws OptionsProcessingException {
        if (cursor == args.length) {
            return;
        }
        parseArgument(args[cursor++]);
        makeNextArgument();
    }

    private void parseArgument(final String arg) throws OptionsProcessingException {
        if (arg.startsWith(FULL_NAME_PREFIX)) {
            setOption(getFieldBy(arg.substring(2)), true);
        } else if (arg.startsWith(SHORT_NAME_PREFIX)) {
            final Field field = getFieldBy(symbol(arg, 1));
            if (isBoolean(field)) {
                setFlags(arg);
            } else {
                setOption(field, getValueFor(field));
            }
        } else {
            addOperand(arg);
        }
    }

    private Field getFieldBy(final String flag) throws OptionsProcessingException {
        for (Field field : flags.values()) {
            if (field.getName().equalsIgnoreCase(flag)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new OptionsProcessingException(
                format("Arguments cannot be instantiated. Option '%s' is not found", flag)
        );
    }

    private Field getFieldBy(final Character flag) throws OptionsProcessingException {
        final Field field = flags.get(flag.toString());
        if (field == null) {
            throw new OptionsProcessingException(
                    format("Arguments cannot be instantiated. Option '%s' is not found", flag)
            );
        }
        field.setAccessible(true);
        return field;
    }

    private void setFlags(final String arg) throws OptionsProcessingException {
        for (int j = 1; j < arg.length(); j++) {
            setOption(getFieldBy(symbol(arg, j)), true);
        }
    }

    private void addOperand(String operand) throws OptionsProcessingException {
        if (operands.isEmpty()) {
            throw new OptionsProcessingException(
                    "Arguments cannot be instantiated. Must be at least one Operands field"
            );
        }
        for (Field field : operands) {
            field.setAccessible(true);
            try {
                final Object list = field.get(result);
                if (!(list instanceof Collection)) {
                    throw new OptionsProcessingException(
                            format(
                                    "Arguments cannot be instantiated. Field %s must be collection",
                                    field.getName()
                            )
                    );
                }
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> itemType = (Class<?>) stringListType.getActualTypeArguments()[0];
                final Object value = itemType == String.class ?
                        operand :
                        makeValueByValueOf(itemType, operand);
                //noinspection unchecked
                ((Collection<Object>) list).add(value);
            } catch (IllegalAccessException e) {
                throw new OptionsProcessingException(
                        format("Arguments cannot be instantiated. Illegal access to field %s", field.getName()),
                        e
                );
            }
        }
    }

    private Object getValueFor(Field field) throws OptionsProcessingException {
        if (cursor == args.length) {
            throw new OptionsProcessingException(
                    format("Arguments cannot be instantiated. Option '%s' has not arguments", args[cursor - 1])
            );
        }
        return field.getType() == String.class ?
                args[cursor++] :
                makeValueByValueOf(field.getType(), args[cursor++]);
    }

    private Object makeValueByValueOf(final Class<?> type, final String arg) throws OptionsProcessingException {
        try {
            return valueOfMethod(type).invoke(null, arg);
        } catch (InvocationTargetException e) {
            throw new OptionsProcessingException(
                    format("Arguments cannot be instantiated. Illegal value '%s' of %s", arg, type),
                    e
            );
        } catch (IllegalAccessException e) {
            throw new OptionsProcessingException(
                    format("Arguments cannot be instantiated. Illegal access to type %s", type),
                    e
            );
        }
    }

    private Method valueOfMethod(Class<?> type) throws OptionsProcessingException {
        try {
            return type.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            throw new OptionsProcessingException(
                    format("Arguments cannot be instantiated. Option '%s' has unsupported type", args[cursor]),
                    e
            );
        }
    }

    private void setOption(final Field field, final Object value) throws OptionsProcessingException {
        try {
            field.set(result, value);
        } catch (IllegalAccessException e) {
            throw new OptionsProcessingException(
                    "Arguments cannot be instantiated. Illegal access to field " + field.getName(),
                    e
            );
        }
    }

    private boolean isBoolean(Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    private Character symbol(String arg, int index) {
        return arg.charAt(index);
    }

    ArgumentBuilder<O> withArgs(final String[] args) {
        this.args = args;
        return this;
    }

    ArgumentBuilder<O> withClass(final Class<O> argumentsClass) {
        this.argumentsClass = argumentsClass;
        return this;
    }
}