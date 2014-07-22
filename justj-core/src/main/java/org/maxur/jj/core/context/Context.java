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

import org.maxur.jj.core.domain.Command;
import org.maxur.jj.core.domain.Entity;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.util.function.Supplier;

import static org.maxur.jj.core.context.BeanIdentifier.identifier;
import static org.maxur.jj.core.context.BeanWrapper.wrap;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class Context extends Entity {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();

    private final Context parent;

    private final BeansHolder beansHolder;

    public static Context current() {
        return context.get();
    }

    protected Context() {
        this(null);
        context.set(this);
    }

    Context(final Context parent) {
        this.parent = parent;
        if (parent == null) {
            beansHolder = new BeansHolderBaseImpl(null);
        } else {
            beansHolder = new BeansHolderBaseImpl((BeansHolderBaseImpl) parent.beansHolder);
        }
    }

    public Command command(final Runnable consumer) {   // TODO
        return new Command() {
            @Override
            protected void run() {
                consumer.run();
            }
        };
    }

    public Context branch() {
        final Context result = new Context(this);
        context.set(result);
        return result;
    }

    public void close() {
        context.set(parent);
    }

    public void inject(final Object bean) {
        // TODO
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


    void put(final Role role, final Supplier<?> supplier) {
        beansHolder.put(() -> wrap(supplier), identifier(role));
    }

    void put(final Role role, final Object bean) {
        beansHolder.put(() -> wrap(bean), identifier(role));
    }

    void put(final Role role, final Class clazz) {
        beansHolder.put(() -> wrap(clazz), identifier(role));
    }

    void put(final Class type, final Supplier<?> supplier) {
        beansHolder.put(() -> wrap(supplier), identifier(type));
    }

    void put(final Class type, final Object bean) {
        beansHolder.put(() -> wrap(bean), identifier(type));
    }

    void put(final Class type, final Class clazz) {
        beansHolder.put(() -> wrap(clazz), identifier(type));
    }


}
