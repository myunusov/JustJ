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

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 09.08.2014
 */
public class FieldDescriptor<T> extends MemberDescriptor<T> {

    private FieldDescriptor(final Field field, final Class<T> declaringClass) {
        super(field);
    }

    public static <T> FieldDescriptor<T> meta(
            @NonNull final Field field,
            @NonNull final ClassDescriptor<? extends T> classDescriptor
    ) {
        if (classDescriptor == null) {
            throw new IllegalArgumentException(
                    "Parameter 'classDescriptor' of 'FieldDescriptor.meta()' function must not be null"
            );
        }
        if (field == null) {
            throw new IllegalArgumentException(
                    "Parameter 'field' of 'FieldDescriptor.meta()' function must not be null"
            );
        }
        //noinspection unchecked
        Class<T> declaringClass = (Class<T>) field.getDeclaringClass();
        final int level = classDescriptor.getHierarchyLevelFor(declaringClass);
        if (level == -1) {
            throw new IllegalArgumentException(format("Class %s has not field %s",
                    classDescriptor.getName(),
                    field.getName())
            );
        }
        return new FieldDescriptor<>(field, declaringClass);
    }

    public Field getField() {
        return (Field) getMember();
    }

    @Override
    public Class getType() {
        return getField().getType();
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return getField().isAnnotationPresent(annotationClass);
    }

    /**
     * Injectable fields:
     * are annotated with @Inject.
     * are not final.
     * may have any otherwise valid name.
     *
     * @return true if field is injectable
     */
    @Override
    public boolean isInjectable() {
        return !isFinal() && isAnnotationPresent(Inject.class);
    }

    public void setValue(Object bean, Object value) {
        getField().setAccessible(true);
        try {
            getField().set(bean, value);
        } catch (IllegalAccessException ignore) {
            throw new IllegalStateException("Unreachable operation", ignore);
        }
    }

}
