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
import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.maxur.jj.core.context.BeanIdentifier.identifier;
import static org.maxur.jj.core.context.BeanWrapper.wrap;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public final class Context extends Entity {

    private static final ThreadLocal<Context> contextHolder = new ThreadLocal<>();

    private final Context parent;

    private final BeansHolder beansHolder;

    public static Context current() {
        return contextHolder.get();
    }

    public static Context root() {
        Context result = current();
        if (result != null)  {
            while (result.parent != null) {
                result = result.parent;
            }
        } else {
            result = new Context();
            contextHolder.set(result);
        }
        return result;
    }

    public Context branch() {
        final Context result = new Context(this);
        contextHolder.set(result);
        return result;
    }

    Context() {
        this(null);
        contextHolder.set(this);
    }

    Context(final Context parent) {
        this.parent = parent;
        if (parent == null) {
            beansHolder = new BeansHolderBaseImpl(null);
        } else {
            beansHolder = new BeansHolderBaseImpl((BeansHolderBaseImpl) parent.beansHolder);
        }
    }

    public void close() {
        contextHolder.set(parent);
    }

    public <T> T inject(final T bean) {
        return inject(bean, findInjectedFields(bean.getClass()));
    }

    <T> T inject(final T bean, final Collection<Field> fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                field.set(bean, bean(field.getType()));
            } catch (IllegalAccessException ignore) {
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
        final BeanWrapper wrapper = beansHolder.wrapper(id);
        final T bean = wrapper.bean(this);
        if (bean == null) {
            throw new JustJSystemException("Bean of %s is not created.\n" +
                    "Check it supplier.", id.getName());
        }
        return bean;
    }


    public void put(final Role role, final Supplier<?> supplier) {
        beansHolder.put(() -> wrap(supplier), identifier(role));
    }

    public void put(final Role role, final Object bean) {
        beansHolder.put(() -> wrap(bean), identifier(role));
    }

    public void put(final Role role, final Class clazz) {
        beansHolder.put(() -> wrap(clazz), identifier(role));
    }

    public void put(final Class type, final Supplier<?> supplier) {
        beansHolder.put(() -> wrap(supplier), identifier(type));
    }

    public void put(final Class type, final Object bean) {
        beansHolder.put(() -> wrap(bean), identifier(type));
    }

    public void put(final Class type, final Class clazz) {
        beansHolder.put(() -> wrap(clazz), identifier(type));
    }



}
