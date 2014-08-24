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

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanIdentifier.identifier;


/**
* @author Maxim Yunusov
* @version 1.0 10.08.2014
*/
public abstract class MemberReference {

    protected final List<BeanIdentifier<?>> identifiers;

    MemberReference(final Class<?>... types) {
        this.identifiers = stream(types)
                .map(type -> {
                    return identifier(type);
                })
                .collect(toList());
    }

    protected Object[] parameters(final InnerScope scope) {
        return identifiers
                .stream()
                .map(id -> getValue(scope, id))
                .collect(toList()).toArray();
    }

    protected Object getValue(final InnerScope scope, final BeanIdentifier id) {
        final BeanReference ref = scope.apply(id);
        if (isMandatory() && !ref.isPresent()) {
            throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                    "It should be added to scope.", id.getType().getName());
        }
        return ref.bean(scope);
    }

    public boolean isMandatory() {
        return true;
    }

    public static MethodReference binder(final MethodDescriptor descriptor) {
        return new MethodReference(descriptor);
    }

    public static FieldReference binder(final FieldDescriptor descriptor) {
        return new FieldReference(descriptor);
    }

    public static ConstructorReference binder(final ConstructorDescriptor descriptor) {
        return new ConstructorReference(descriptor);
    }

    public void setValue(final Object bean, final InnerScope scope) {
        throw new UnsupportedOperationException();
    }

    public void invoke(final Object bean, final InnerScope scope) {
        throw new UnsupportedOperationException();
    }

    public Object newInstance(final InnerScope scope) {
        throw new UnsupportedOperationException();
    }


    static class ConstructorReference extends MemberReference {

        private ConstructorDescriptor constructor;

        private ConstructorReference(final ConstructorDescriptor constructor) {
            //noinspection unchecked
            super(constructor.getParameterTypes());
            this.constructor = constructor;
        }

        public Object newInstance(final InnerScope scope) {
            return constructor.newInstance(parameters(scope));
        }
    }

    static class FieldReference extends MemberReference {

        private final FieldDescriptor field;

        private FieldReference(final FieldDescriptor field) {
            super(field.getType());
            this.field = field;
        }

        @Override
        public boolean isMandatory() {
            return !field.isAnnotationPresent(Optional.class);
        }

        public void setValue(final Object bean, final InnerScope scope) {
            field.setValue(bean, parameters(scope)[0]);
        }
    }

    static class MethodReference extends MemberReference {

        private final MethodDescriptor method;

        private MethodReference(final MethodDescriptor method) {
            //noinspection unchecked
            super(method.getParameterTypes());
            this.method = method;
        }

        public void invoke(final Object bean, final InnerScope scope) {
            method.invoke(bean, parameters(scope));
        }

    }


}
