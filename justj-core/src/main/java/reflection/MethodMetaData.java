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

package reflection;

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
public class MethodMetaData<T> extends MemberMetaData<T> {

    private final int hierarchyLevel;

    private MethodMetaData(final Method method, final int hierarchyLevel) {
        super(method);
        this.hierarchyLevel = hierarchyLevel;
    }

    public static <T> MethodMetaData<T> meta(final Method method, final ClassMetaData<? extends T> classMetaData) {
        //noinspection unchecked
        final Class<T> declaringClass = (Class<T>) method.getDeclaringClass();
        final int level = classMetaData.getHierarchyLevelFor(declaringClass);
        if (level == -1) {
            throw new IllegalArgumentException(format("Class %s has not method %s",
                    classMetaData.getName(),
                    method.getName())
            );
        }
        return new MethodMetaData<>(method, level);
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean overridesFor(final MethodMetaData method) {
        return method != null &&
                isAccessible(method) &&
                sameName(method) &&
                sameParams(method);
    }

    private boolean isAccessible(final MethodMetaData method) {
        return getHierarchyLevel() > method.getHierarchyLevel() &&
                !(method.isDefault() && !getPackage().equals(method.getPackage()));
    }

    private boolean sameParams(final MethodMetaData method) {
        final Class[] types1 = getParameterTypes();
        final Class[] types2 = method.getParameterTypes();
        if (types1.length != types2.length) {
            return false;
        }
        for (int i = 0; i < types1.length; i++) {
            //noinspection unchecked
            if (!types2[i].isAssignableFrom(types1[i])) {
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

    public void invoke(final Object bean, final Object[] parameters) {
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
