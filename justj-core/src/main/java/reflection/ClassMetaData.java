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
public final class ClassMetaData<T> {

    private final Class<T> beanClass;

    private ClassMetaData(final Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public static <T> ClassMetaData<T> meta(final Class<T> beanClass) {
        return new ClassMetaData<>(beanClass);
    }

    public List<ClassMetaData> parents()  {
        return collectParents(beanClass);
    }

    public List<MethodMetaData> methods() {
        final List<MethodMetaData> allMethods = new ArrayList<>();
        ///CLOVER:OFF
        parents().forEach(d ->
                allMethods.addAll(
                        stream(d.beanClass.getDeclaredMethods())
                                .map((method) -> MethodMetaData.meta(method, this))
                                .collect(toList())
                )
        );
        ///CLOVER:ON
        return allMethods.stream()
                .filter(isNotOverridden(allMethods))
                .collect(toList());
    }

    public List<FieldMetaData> fields() {
        final List<FieldMetaData> allFields = new ArrayList<>();
        ///CLOVER:OFF
        parents().forEach(d ->
                        allFields.addAll(
                                stream(d.beanClass.getDeclaredFields())
                                        .map((method) -> FieldMetaData.meta(method, this))
                                        .collect(toList())
                        )
        );
        ///CLOVER:ON
        return allFields;
    }


    private Predicate<? super MethodMetaData> isNotOverridden(final List<MethodMetaData> methods) {
        return method -> {
            if (!method.isInheritable()) {
                return true;
            }
            for (MethodMetaData metaData : methods) {
                if (metaData.overridesFor(method)) {
                    return false;
                }
            }
            return true;
        };
    }


    private List<ClassMetaData> collectParents(final Class beanClass) {
        final Class parent = beanClass.getSuperclass();
        final List<ClassMetaData> result  = parent == null ? new ArrayList<>() : collectParents(parent);
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
        if (!(o instanceof ClassMetaData)) {
            return false;
        }

        final ClassMetaData that = (ClassMetaData) o;

        return !(beanClass != null ? !beanClass.equals(that.beanClass) : that.beanClass != null);

    }

    @Override
    public int hashCode() {
        return beanClass != null ? beanClass.hashCode() : 0;
    }

    public int getHierarchyLevelFor(final Class<? super T> type) {
        final List<ClassMetaData> parents = parents();
        for (int i = 0; i < parents.size(); i++) {
            //noinspection unchecked
            if (parents.get(i).is(type)) {
                return i;
            }
        }
        return -1;
    }

    private boolean is(final Class<?> declaringClass) {
        return beanClass == declaringClass;
    }

}
