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

package org.maxur.jj.core.context;

import org.maxur.jj.core.annotation.Optional;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.reflection.ClassDescriptor;
import org.maxur.jj.core.reflection.ConstructorDescriptor;
import org.maxur.jj.core.reflection.FieldDescriptor;
import org.maxur.jj.core.reflection.MethodDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanReference.referenceBy;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
abstract class BeanWrapper<T> {

    private final List<FieldBinder> injectableFields;

    private final List<MethodBinder> injectableMethods;

    private final List<ConstructorBinder> injectableConstructor;

    private final ClassDescriptor<T> metaData;

    protected BeanWrapper(final Class<T> clazz) {
        injectableConstructor = findInjectableConstructor(clazz);
        injectableFields = findInjectableFields(clazz);
        injectableMethods = findInjectableMethods(clazz);
        metaData = meta(clazz);
    }

    public static <T> T inject(final Function<BeanReference, BeanWrapper> context, final T bean) {
        wrap(bean).injectFields(context, bean).injectMethods(context, bean);
        return bean;
    }

    public static <T> BeanWrapper wrap(final Supplier<T> supplier, final Class<T> clazz) {
        if (supplier == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new SupplierBeanWrapper<>(supplier, clazz);
    }

    public static <O> BeanWrapper wrap(final O bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new ObjectBeanWrapper<>(bean);
    }

    public static <T> BeanWrapper wrap(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new ClassBeanWrapper<>(clazz);
    }

    public final T bean(final Function<BeanReference, BeanWrapper> context) {

        // TODO
        //  1. Get Dependencies
        //       Stream.of(this).flatMap(b -> b.dependencies(context).stream());
        //  2. Create all instances (with proxy if required)
        //  3. Inject all fields
        //  4. Inject all methods
        final T bean = create(context);
        injectFields(context, bean);
        injectMethods(context, bean);
        return bean;
    }

/*    private Map<BeanWrapper, Object> dependencies(final Function<BeanReference, BeanWrapper> context) {
        return this.injectableFields.stream().map(FieldBinder::getReference)
                .map(context::apply)
                .collect(toMap(wrapper -> wrapper, wrapper -> wrapper.create(context)));
    }*/


    protected  List<ConstructorBinder> findInjectableConstructor(final Class<T> clazz) {
        return Collections.emptyList();
    }

    protected final List<MethodBinder> findInjectableMethods(final Class<?> beanClass) {
        ///CLOVER:OFF
        return meta(beanClass).methods().stream()
                .filter(MethodDescriptor::isInjectable)
                .map(MethodBinder::new)
                .collect(toList());
        ///CLOVER:ON
    }

    protected final List<FieldBinder> findInjectableFields(final Class<?> beanClass) {
        ///CLOVER:OFF
        return meta(beanClass).fields().stream()
                .filter(FieldDescriptor::isInjectable)
                .map(FieldBinder::new)
                .collect(toList());
        ///CLOVER:ON
    }

    /**
     * Inject is optional for public, no-argument constructors when no other constructors are present.
     * This enables injectors to invoke default constructors.
     *
     * @param context IoC context function
     * @return Instance of Bean
     */
    @SuppressWarnings("unchecked")
    protected T create(final Function<BeanReference, BeanWrapper> context) {
        // XXX check and field value set should be separated
        if (injectableConstructor.isEmpty()) {
            return getMetaData().newInstance();
        } else {
            return (T) injectableConstructor.get(0).newInstance(context);
        }
    }

    private BeanWrapper injectFields(final Function<BeanReference, BeanWrapper> context, final Object bean) {
        if (bean == null) {
            return this;
        }
        // XXX check and field value set should be separated
        for (FieldBinder data : injectableFields) {
            data.setValue(bean, context);
        }
        return this;
    }

    private BeanWrapper injectMethods(final Function<BeanReference, BeanWrapper> context, final Object bean) {
        if (bean == null) {
            return this;
        }
        for (MethodBinder data : injectableMethods) {
            // XXX check and field value set should be separated
            data.invoke(bean, context);
        }
        return this;
    }

    void checkType(final BeanReference id) {
        //noinspection unchecked
        if (!metaData.isAssignable(id.getType())) {
            throw new IllegalArgumentException(format(
                    "The type '%s' is not suitable to %s",
                    this.metaData.getName(),
                    id.toString()
            ));
        }
    }

    @Override
    public String toString() {
        return format("Bean '%s'", metaData.getName());
    }

    public ClassDescriptor<T> getMetaData() {
        return metaData;
    }

    private static class SupplierBeanWrapper<T> extends BeanWrapper<T> {

        private final Supplier<T> supplier;

        public SupplierBeanWrapper(final Supplier<T> supplier, final Class<T> clazz) {
            super(clazz);
            this.supplier = supplier;
        }


        protected T create(final Function<BeanReference, BeanWrapper> context) {
            return supplier.get();
        }
    }

    private static class ObjectBeanWrapper<T> extends BeanWrapper<T> {

        private final T bean;

        public ObjectBeanWrapper(final T bean) {
            //noinspection unchecked
            super((Class<T>) bean.getClass());
            this.bean = bean;
        }
        protected T create(final Function<BeanReference, BeanWrapper> context) {
            return bean;
        }
    }

    private static class ClassBeanWrapper<T> extends BeanWrapper<T> {

        public ClassBeanWrapper(final Class<T> clazz) {
            super(clazz);
        }

        protected List<ConstructorBinder> findInjectableConstructor(Class<T> beanClass) {
            ///CLOVER:OFF
            final List<ConstructorBinder> result = meta(beanClass).constructors().stream()
                    .filter(ConstructorDescriptor::isInjectable)
                    .map(ConstructorBinder::new)
                    .collect(toList());
            ///CLOVER:ON
            if (result.size() > 1) {
                throw new JustJSystemException("Class %s has %d Injectable constructors," +
                        " but according to JSR-330 @Inject can apply to at most one constructor per class.",
                        beanClass.getName(), result.size());
            }
            return result;
        }

    }

    private static abstract class MemberBinder {

        protected final List<BeanReference> references;

        protected MemberBinder(final List<BeanReference> references) {
            this.references = references;
        }

        protected Object[] getParameters(final Function<BeanReference, BeanWrapper> context) {
            return references.stream()
                    .map(ref -> getValue(context, ref))
                    .collect(toList()).toArray();
        }

        protected Object getValue(final Function<BeanReference, BeanWrapper> context, final BeanReference ref) {
            final BeanWrapper wrapper = context.apply(ref);
            if (isMandatory() && wrapper == null) {
                throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                        "It should be added to context.", ref.getType().getName());
            }
            return wrapper == null ? null : wrapper.bean(context);
        }

        public boolean isMandatory() {
            return true;
        }
    }

    private static class ConstructorBinder extends MemberBinder {

        private ConstructorDescriptor constructor;

        private ConstructorBinder(final ConstructorDescriptor constructor) {
            ///CLOVER:OFF
            super(stream(constructor.getParameterTypes())
                    .map(BeanReference::referenceBy)
                    .collect(toList()));
            ///CLOVER:ON
            this.constructor = constructor;
        }

        public Object newInstance(final Function<BeanReference, BeanWrapper> context) {
            return constructor.newInstance(getParameters(context));
        }
    }

    private static class FieldBinder extends MemberBinder {

        private final FieldDescriptor field;

        private FieldBinder(final FieldDescriptor field) {
            super(Collections.singletonList(referenceBy(field.getType())));
            this.field = field;
        }

        @Override
        public boolean isMandatory() {
            return !field.isAnnotationPresent(Optional.class);
        }

        public void setValue(final Object bean, final Function<BeanReference, BeanWrapper> context) {
            field.setValue(bean, getParameters(context)[0]);
        }
    }

    private static class MethodBinder extends MemberBinder {

        private final MethodDescriptor method;

        private MethodBinder(final MethodDescriptor method) {
            ///CLOVER:OFF
            super(stream(method.getParameterTypes())
                    .map(BeanReference::referenceBy)
                    .collect(toList()));
            ///CLOVER:ON
            this.method = method;
        }

        public void invoke(final Object bean, final Function<BeanReference, BeanWrapper> context) {
            method.invoke(bean, getParameters(context));
        }

    }
}
