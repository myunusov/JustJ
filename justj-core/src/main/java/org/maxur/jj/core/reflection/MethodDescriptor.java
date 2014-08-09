/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.maxur.jj.core.reflection;

import checkers.nullness.quals.NonNull;
import org.maxur.jj.core.domain.JustJSystemException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/8/2014</pre>
 */
public class MethodDescriptor<T> extends MemberDescriptor<T> {

    private final int hierarchyLevel;

    private MethodDescriptor(final Method method, final int hierarchyLevel) {
        super(method);
        this.hierarchyLevel = hierarchyLevel;
    }

    public static <T> MethodDescriptor<T> meta(
            @NonNull final Method method,
            final @NonNull ClassDescriptor<? extends T> classDescriptor
    ) {
        if (classDescriptor == null) {
            throw new IllegalArgumentException(
                    "Parameter 'classDescriptor' of 'MethodDescriptor.meta()' function must not be null"
            );
        }
        if (method == null) {
            throw new IllegalArgumentException(
                    "Parameter 'method' of 'MethodDescriptor.meta()' function must not be null"
            );
        }
        //noinspection unchecked
        final Class<T> declaringClass = (Class<T>) method.getDeclaringClass();
        final int level = classDescriptor.getHierarchyLevelFor(declaringClass);
        if (level == -1) {
            throw new IllegalArgumentException(format("Class %s has not method %s",
                    classDescriptor.getName(),
                    method.getName())
            );
        }
        return new MethodDescriptor<>(method, level);
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean overridesFor(final MethodDescriptor method) {
        if (method == null) {
            throw new IllegalArgumentException(
                    "Parameter 'method' of 'MethodDescriptor.overridesFor()' function must not be null"
            );
        }
        return method.isInheritable() && isAccessible(method) && sameName(method) && sameParams(method);
    }

    private boolean isAccessible(final MethodDescriptor method) {
        return getHierarchyLevel() > method.getHierarchyLevel() &&
                !(method.isDefault() && !getPackage().equals(method.getPackage()));
    }

    private boolean sameParams(final MethodDescriptor method) {
        final Class[] types1 = getParameterTypes();
        final Class[] types2 = method.getParameterTypes();
        if (types1.length != types2.length) {
            return false;
        }
        for (int i = 0; i < types1.length; i++) {
            //noinspection unchecked
            if (types2[i] != types1[i]) {
                 return false;
            }
        }
        return true;
    }

    public Class[] getParameterTypes() {
        return this.getMethod().getParameterTypes();
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return getMethod().isAnnotationPresent(annotationClass);
    }

    public void invoke(final Object bean, final Object... parameters) {
        try {
            getMethod().setAccessible(true);
            getMethod().invoke(bean, parameters);
        } catch (IllegalAccessException ignore) {
            throw new IllegalStateException("Unreachable operation", ignore);
        } catch (InvocationTargetException | IllegalArgumentException e) {
            throw new JustJSystemException(format(
                    "Unable to execute method '%s.%s'",
                    getDeclaringClass().getName(),
                    getMethod().getName()
            ), e);
        }
    }

    public Method getMethod() {
        return (Method) getMember();
    }

    @Override
    public Class getType() {
        return getMethod().getReturnType();
    }
}
