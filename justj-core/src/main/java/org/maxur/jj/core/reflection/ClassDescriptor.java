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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/8/2014</pre>
 */
public final class ClassDescriptor<T> {

    private final Class<T> beanClass;

    private ClassDescriptor(final Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public static <T> ClassDescriptor<T> meta(@NonNull final Class<T> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException(
                    "Parameter 'beanClass' of 'ClassMetaData.meta()' function must not be null"
            );
        }
        return new ClassDescriptor<>(beanClass);
    }

    public List<ClassDescriptor> parents()  {
        return collectParents(beanClass);
    }

    public List<MethodDescriptor> methods() {
        final List<MethodDescriptor> allMethods = new ArrayList<>();
        ///CLOVER:OFF
        parents().forEach(d ->
                allMethods.addAll(
                        stream(d.beanClass.getDeclaredMethods())
                                .map(method -> MethodDescriptor.meta(method, this))
                                .collect(toList())
                )
        );
        ///CLOVER:ON
        return allMethods.stream()
                .filter(isNotOverridden(allMethods))
                .collect(toList());
    }

    public List<FieldDescriptor> fields() {
        final List<FieldDescriptor> allFields = new ArrayList<>();
        ///CLOVER:OFF
        parents().forEach(d ->
                        allFields.addAll(
                                stream(d.beanClass.getDeclaredFields())
                                        .map(method -> FieldDescriptor.meta(method, this))
                                        .collect(toList())
                        )
        );
        ///CLOVER:ON
        return allFields;
    }


    private Predicate<MethodDescriptor> isNotOverridden(final List<MethodDescriptor> methods) {
        return method -> {
            for (MethodDescriptor metaData : methods) {
                if (metaData.overridesFor(method)) {
                    return false;
                }
            }
            return true;
        };
    }

    private List<ClassDescriptor> collectParents(final Class beanClass) {
        final Class parent = beanClass.getSuperclass();
        final List<ClassDescriptor> result  = parent == null ? new ArrayList<>() : collectParents(parent);
        result.add(meta(beanClass));
        return result;
    }

    public String getName() {
        return this.beanClass.getName();
    }

    public boolean isAssignable(final Class type) {
        //noinspection unchecked
        return type.isAssignableFrom(this.beanClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassDescriptor)) {
            return false;
        }
        final ClassDescriptor that = (ClassDescriptor) o;
        return beanClass.equals(that.beanClass);
    }

    @Override
    public int hashCode() {
        return beanClass.hashCode();
    }

    public int getHierarchyLevelFor(final Class<? super T> type) {
        final List<ClassDescriptor> parents = parents();
        for (int i = 0; i < parents.size(); i++) {
            //noinspection unchecked
            if (parents.get(i).is(type)) {
                return i;
            }
        }
        return -1;
    }

    private boolean is(final Class<?> otherClass) {
        return beanClass == otherClass;
    }

}
