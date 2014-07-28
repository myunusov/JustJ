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

package org.maxur.jj.core.context;

import org.maxur.jj.core.domain.Entity;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanIdentifier.identifier;
import static org.maxur.jj.core.context.BeanWrapper.wrap;

/**
 *
 *
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class Context extends Entity {

    private final Context parent;

    /**
     * Bridge Context -> ContextImpl
     */
    private final ContextImpl contextImpl;

    /**
     *
     */
    private ChangeManager changeManager;

    Context() {
        this.parent = null;
        contextImpl = new BaseContextImpl();
    }

    Context(final Context parent) {
        this.parent = parent;
        contextImpl = new BaseContextImpl((BaseContextImpl) parent.contextImpl);
    }

    public <T> T inject(final T bean) {
        return inject(bean, findInjectedFields(bean.getClass()));
    }

    <T> T inject(final T bean, final Collection<Field> fields) {
        for (Field field : fields) {
            final Object injectedBean = bean(field.getType());
            if (injectedBean == null) {     // TODO optional case
                throw new JustJSystemException("Bean of type '%s' is not found.\n" +
                        "It must be added to context.", field.getType().getName());
            }
            field.setAccessible(true);
            try {
                field.set(bean, injectedBean);
            } catch (IllegalAccessException ignore) {
                assert false : "Unreachable operation";
            }
        }
        return bean;
    }

    private Collection<Field> findInjectedFields(final Class<?> beanClass) {
        return stream(beanClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(toList());
    }

    public <T> T bean(final Role role) {
        return bean(identifier(role));
    }

    public <T> T bean(final Class<T> type) {
        return bean(identifier(type));
    }

    private <T> T bean(final BeanIdentifier id) {
        final BeanWrapper wrapper = contextImpl.wrapper(id);
        if (wrapper == null)  {
            return null;
        }
        try {
            return wrapper.bean(this);
        } catch (Exception e) {
            throw new JustJSystemException(format("Bean '%s' is not created. Cause: %s",
                    id.getName(),
                    e.getMessage()), e
            );
        }
    }

    public void put(final Role role, final Supplier<?> supplier) {
        contextImpl.put(() -> wrap(supplier), identifier(role));
    }

    public void put(final Role role, final Object bean) {
        contextImpl.put(() -> wrap(bean), identifier(role));
    }

    public void put(final Role role, final Class clazz) {
        contextImpl.put(() -> wrap(clazz), identifier(role));
    }

    public void put(final Class type, final Supplier<?> supplier) {
        contextImpl.put(() -> wrap(supplier), identifier(type));
    }

    public void put(final Class type, final Object bean) {
        contextImpl.put(() -> wrap(bean), identifier(type));
    }

    public void put(final Class type, final Class clazz) {
        contextImpl.put(() -> wrap(clazz), identifier(type));
    }

    public Context parent() {
        return parent;
    }

    public Context root() {
        return parent == null ?  this : parent.root();
    }

}
