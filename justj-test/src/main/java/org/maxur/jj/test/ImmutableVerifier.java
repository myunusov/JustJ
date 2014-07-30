/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import static java.lang.String.format;
import static java.util.Collections.addAll;
import static org.maxur.jj.test.Warning.IMMUTABLE_FIELDS;

/**
 * {@code ImmutableVerifier} can be used in unit tests to verify whether the
 * contract for the {@code Service} classes.
 *
 * The contracts are Service must be immutable.
 *
 * @author Maxim Yunusov
 * @version 1.0 19.07.2014
 */
public class ImmutableVerifier<T> {

    private final Collection<Warning> warningsToSuppress = new HashSet<>();

    private final T instance;

    private Class superClass;

    /**
     * Factory method. For general use.
     *
     * @param instance The instance for which the immutable should be tested.
     * @param <T> The type of instance for which the immutable should be tested.
     *
     * @return  The instance of ImmutableVerifier.
     */
    public static <T> ImmutableVerifier<T> forInstance(T instance) {
        return new ImmutableVerifier<>(instance);
    }

    private ImmutableVerifier(T instance) {
        this.instance = instance;
    }

    /**
     * Adds superClass values.
     *
     * @param superClass The superclass of testable instance.
     *
     * @return  The instance of ImmutableVerifier {@code this}, for easy method chaining.
     *
     * @throws IllegalArgumentException If superClass is not superclass of tested instance.
     */
    public ImmutableVerifier<T> withSuperclass(final Class<? super T> superClass) {
        this.superClass = superClass;
        if (!superClass.isAssignableFrom(instance.getClass())) {
            throw new IllegalArgumentException(format("Class '%s' must be superclass for '%s'",
                    superClass.getName(),
                    instance.getClass().getName()
            ));
        }
        return this;
    }

    /**
     * Suppresses warnings given by {@code ImmutableVerifier}.
     * See {@link Warning} to see what warnings can be suppressed.
     *
     * @param warnings A list of warnings to suppress in {@code ImmutableVerifier}.
     *
     * @return The instance of ImmutableVerifier {@code this}, for easy method chaining.
     */
    public ImmutableVerifier<T> suppress(final Warning... warnings) {
        addAll(this.warningsToSuppress, warnings);
        return this;
    }

    /**
     * Performs the verification of the contracts for service classes.
     *
     * @throws AssertionError If the contract is not met
     */
    public void verify() {
        checkDirectInherited();
        checkIsFinal();
        checkFields();
    }

    private void checkFields() {
        final Class<?> objClass = instance.getClass();
        for (Field objField : objClass.getDeclaredFields()) {
            if (!Modifier.isFinal(objField.getModifiers())) {
                throw new AssertionError("All fields defined in the class must be final");
            } else if (warningsToSuppress.contains(IMMUTABLE_FIELDS) && !isValidFieldType(objField.getType())) {
                throw new AssertionError("All fields defined in the class must be primitive types or String");
            }
        }
    }

    private void checkIsFinal() {
        final Class<?> objClass = instance.getClass();
        if (!Modifier.isFinal(objClass.getModifiers())) {
            throw new AssertionError("Class must be final");
        }
    }

    private void checkDirectInherited() {
        final Class<?> objClass = instance.getClass();
        if (superClass != null && !superClass.equals(objClass.getSuperclass())) {
            throw new AssertionError("Class of the object must be a direct child class of the required class");
        }
    }

    static boolean isValidFieldType(Class<?> type) {
        return type.isPrimitive() || String.class.equals(type);
    }


}
