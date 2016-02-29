package org.maxur.justj.core.cli.info;

import org.maxur.justj.core.cli.annotation.Key;
import org.maxur.justj.core.cli.annotation.KeyContainer;
import org.maxur.justj.core.cli.argument.Argument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>28.02.2016</pre>
 */
public abstract class CliItemInfo {

    private String name;

    private Set<Character> keys;

    private final Field field;

    private final Class<Object> commandClass;

    private final OptionType type;

    private CliItemInfo(final Field field, final Class<Object> commandClass, final OptionType type) {
        this.field = field;
        this.commandClass = commandClass;
        this.type = type;
        this.name = findName();
        this.keys = findKeys();

    }

    CliItemInfo(final Class<Object> commandClass) {
        this(null, commandClass, OptionType.COMMAND);
    }

    CliItemInfo(final Field field, final OptionType type) {
        this(field, null, type);
    }

    public boolean applicable(final Argument argument) {
        return argument.isKey() ?
                this.keys.contains(argument.key()) :
                Objects.equals(argument.name(), this.name);
    }

    protected abstract String findName();

    private Set<Character> findKeys() {
        if (hasOneKey()) {
            return Collections.singleton(keyFromAnnotation());
        } else if (hasManyKeys()) {
            return keysFromAnnotation();
        } else {
            return Collections.singleton(keyFromName());
        }
    }

    private boolean hasOneKey() {
        return isAnnotationPresent(Key.class);
    }

    private Set<Character> keysFromAnnotation() {
        final Set<Character> result = new HashSet<>();
        for (Key key : getAnnotation(KeyContainer.class).value()) {
            result.add(key.value().charAt(0));
        }
        return result;
    }

    private Character keyFromAnnotation() {
        return getAnnotation(Key.class).value().charAt(0);
    }

    private boolean hasManyKeys() {
        return isAnnotationPresent(KeyContainer.class);
    }

    boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return field != null ?
                field.isAnnotationPresent(annotationClass) :
                commandClass.isAnnotationPresent(annotationClass);
    }

    <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return field != null ?
                field.getAnnotation(annotationClass) :
                commandClass.getAnnotation(annotationClass);
    }

    private Character keyFromName() {
        return name == null ? null : name.charAt(0);
    }
    
    public Set<Character> keys() {
        return keys;
    }

    public String name() {
        return name;
    }

    public OptionType type() {
        return type;
    }

    Field field() {
        return field;
    }

    Class<?> commandClass() {
        return commandClass;
    }

}
