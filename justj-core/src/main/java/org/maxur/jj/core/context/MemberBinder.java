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
import org.maxur.jj.core.reflection.ConstructorDescriptor;
import org.maxur.jj.core.reflection.FieldDescriptor;
import org.maxur.jj.core.reflection.MethodDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanReference.referenceBy;

/**
* @author Maxim Yunusov
* @version 1.0 10.08.2014
*/
public abstract class MemberBinder {

    protected final List<BeanReference> references;

    MemberBinder(final List<BeanReference> references) {
        this.references = references;
    }

    protected Object[] parameters(final Function<BeanReference, BeanWrapper> context) {
        return references
                .stream()
                .map(ref -> getValue(context, ref))
                .collect(toList()).toArray();
    }

    protected Object getValue(final Function<BeanReference, BeanWrapper> context, final BeanReference ref) {
        final BeanWrapper wrapper = context.apply(ref);
        if (isMandatory() && !wrapper.isPresent()) {
            throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                    "It should be added to context.", ref.getType().getName());
        }
        return wrapper.bean(context);
    }

    public boolean isMandatory() {
        return true;
    }

    public Map<BeanReference, BeanWrapper> dependencies(
            final Function<BeanReference, BeanWrapper> context,
            final Map<BeanReference, BeanWrapper> accumulator
    ) {
        for (BeanReference ref : references) {
            if (accumulator.get(ref) != null) {
                continue;
            }
            final BeanWrapper<?> wrapper = context.apply(ref);
            accumulator.put(ref, wrapper);
            accumulator.putAll(wrapper.dependencies(context, accumulator));
        }
        return accumulator;
    }

    public static MethodBinder binder(final MethodDescriptor descriptor) {
        return new MethodBinder(descriptor);
    }

    public static FieldBinder binder(final FieldDescriptor descriptor) {
        return new FieldBinder(descriptor);
    }

    public static ConstructorBinder binder(final ConstructorDescriptor descriptor) {
        return new ConstructorBinder(descriptor);
    }

    public void setValue(final Object bean, final Function<BeanReference, BeanWrapper> context) {
        throw new UnsupportedOperationException();
    }

    public void invoke(final Object bean, final Function<BeanReference, BeanWrapper> context) {
        throw new UnsupportedOperationException();
    }

    public Object newInstance(final Function<BeanReference, BeanWrapper> context) {
        throw new UnsupportedOperationException();
    }


    static class ConstructorBinder extends MemberBinder {

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
            return constructor.newInstance(parameters(context));
        }
    }

    static class FieldBinder extends MemberBinder {

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
            field.setValue(bean, parameters(context)[0]);
        }
    }

    static class MethodBinder extends MemberBinder {

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
            method.invoke(bean, parameters(context));
        }

    }


}
