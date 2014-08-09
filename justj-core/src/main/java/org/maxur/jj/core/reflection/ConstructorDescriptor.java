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

package org.maxur.jj.core.reflection;

import checkers.nullness.quals.NonNull;
import org.maxur.jj.core.domain.JustJSystemException;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Maxim Yunusov
 * @version 1.0 09.08.2014
 */
public class ConstructorDescriptor<T> extends MemberDescriptor<T> {

    private ConstructorDescriptor(final Constructor method) {
        super(method);
    }

    public static <T> ConstructorDescriptor<T> meta(
            @NonNull final Constructor method,
            final @NonNull ClassDescriptor<? extends T> classDescriptor
    ) {
        if (classDescriptor == null) {
            throw new IllegalArgumentException(
                    "Parameter 'classDescriptor' of 'ConstructorDescriptor.meta()' function must not be null"
            );
        }
        if (method == null) {
            throw new IllegalArgumentException(
                    "Parameter 'method' of 'ConstructorDescriptor.meta()' function must not be null"
            );
        }
        return new ConstructorDescriptor<>(method);
    }

    public Class[] getParameterTypes() {
        return this.getConstructor().getParameterTypes();
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return getConstructor().isAnnotationPresent(annotationClass);
    }

    /**
     * Injectable constructors are annotated with @Inject and accept zero or more dependencies as arguments.
     * Inject can apply to at most one constructor per class.
     */
    @Override
    public boolean isInjectable() {
        return isAnnotationPresent(Inject.class);
    }

    public Constructor getConstructor() {
        return (Constructor) getMember();
    }

    @Override
    public Class getType() {
        return getConstructor().getDeclaringClass();
    }

    public Object newInstance(final Object... parameters) {
        try {
            return getConstructor().newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JustJSystemException("Error instantiating", e);
        }
    }
}