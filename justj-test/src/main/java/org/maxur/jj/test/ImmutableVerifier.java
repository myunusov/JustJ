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

import checkers.nullness.quals.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import static java.lang.String.format;
import static java.util.Collections.addAll;
import static org.maxur.jj.test.Warning.IMMUTABLE_FIELDS;
import static org.maxur.jj.test.Warning.NON_FINAL_CLASS;

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

    private Class superClass;

    private Class<T> testableClass;

    /**
     * Factory method. For general use.
     *
     * @param type The class for which the immutable should be tested.
     * @param <T> The type of instance for which the immutable should be tested.
     *
     * @return  The instance of ImmutableVerifier.
     */
    public static <T> ImmutableVerifier<T> forClass(@NonNull Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Instance must not be null");
        }
        return new ImmutableVerifier<>(type);
    }

    /**
     * Factory method. For general use.
     *
     * @param instance The instance for which the immutable should be tested.
     * @param <T> The type of instance for which the immutable should be tested.
     *
     * @return  The instance of ImmutableVerifier.
     */
    public static <T> ImmutableVerifier<T> forInstance(@NonNull T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Instance must not be null");
        }
        //noinspection unchecked
        return new ImmutableVerifier<>((Class<T>) instance.getClass());
    }


    private ImmutableVerifier(Class<T> type) {
        testableClass = type;
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
        if (!superClass.isAssignableFrom(testableClass)) {
            throw new IllegalArgumentException(format("Class '%s' must be superclass for '%s'",
                    superClass.getName(),
                    testableClass.getName()
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
        checkFields(testableClass, new HashSet<>());
    }

    private void checkFields(final Class type, final HashSet<Class> accumulator) {
        accumulator.add(type);
        for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isFinal(field.getModifiers())) {
                throw new AssertionError(format("All fields defined in the class '%s' must be final", type.getName()));
            } else {
                final Class<?> fieldType = field.getType();
                if (!warningsToSuppress.contains(IMMUTABLE_FIELDS) && !isValidFieldType(fieldType)) {
                    if (accumulator.contains(fieldType)) {
                        break;
                    }
                    try {
                        checkFields(fieldType, accumulator);
                    } catch (AssertionError e) {
                        throw new AssertionError("All fields defined in the class must be immutable", e);
                    }
                }
            }
        }
    }

    private void checkIsFinal() {
        if (warningsToSuppress.contains(NON_FINAL_CLASS)) {
            return;
        }
        if (!Modifier.isFinal(testableClass.getModifiers())) {
            throw new AssertionError("Class must be final");
        }
    }

    private void checkDirectInherited() {
        if (superClass != null && !superClass.equals(testableClass.getSuperclass())) {
            throw new AssertionError("Class of the object must be a direct child class of the required class");
        }
    }

    static boolean isValidFieldType(Class<?> type) {
        return type.isPrimitive() || String.class.equals(type);
    }


}
