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
import java.lang.reflect.Modifier;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/8/2014</pre>
 */
public class MethodMetaData<T> extends MetaData {

    private final Method method;

    private final Class<T> declaringClass;

    private final int hierarchyLevel;

    private final boolean isInheritable;

    public static <T> MethodMetaData<T> meta(final Method method, final ClassMetaData<? extends T> classMetaData) {
        //noinspection unchecked
        Class<T> declaringClass = (Class<T>) method.getDeclaringClass();
        final int level = classMetaData.getHierarchyLevelFor(declaringClass);
        if (level == -1) {
            throw new IllegalArgumentException(format("Class %s has not method %s",
                    classMetaData.getName(),
                    method.getName())
            );
        }
        return new MethodMetaData<>(method, declaringClass, level);
    }

    private MethodMetaData(final Method method, final Class<T> declaringClass, final int hierarchyLevel) {
        this.method = method;
        this.declaringClass = declaringClass;
        this.hierarchyLevel = hierarchyLevel;
        this.isInheritable = isInheritable(method);
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean isInheritable() {
        return isInheritable;
    }

    public boolean overridesFor(final MethodMetaData method) {
        return method != null &&
                isAccessible(method) &&
                sameName(method) &&
                sameParams(method);
    }

    private boolean sameName(MethodMetaData method) {
        return getName().equals(method.getName());
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
        return this.method.getParameterTypes();
    }

    public Package getPackage() {
        return declaringClass.getPackage();
    }

    public String getName() {
        return method.getName();
    }

    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    public boolean isPublic() {
        return Modifier.isPublic(method.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(method.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(method.getModifiers());
    }

    public boolean isDefault() {
        final int modifiers = method.getModifiers();
        return !Modifier.isPublic(modifiers) &&
                !Modifier.isProtected(modifiers) &&
                !Modifier.isPrivate(modifiers);
    }

    public void invoke(final Object bean, final Object[] parameters) {
        try {
            method.setAccessible(true);
            method.invoke(bean, parameters);
        } catch (IllegalAccessException ignore) {
            throw new IllegalStateException("Unreachable operation", ignore);
        } catch (InvocationTargetException | IllegalArgumentException e) {
            throw new JustJSystemException(format(
                    "Unable to execute method '%s.%s'",
                    declaringClass.getName(),
                    method.getName()
            ), e);
        }
    }

}
