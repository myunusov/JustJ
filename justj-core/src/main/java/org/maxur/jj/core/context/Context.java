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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.maxur.jj.core.context.BeanReference.identifier;
import static org.maxur.jj.core.context.BeanWrapper.wrap;

/**
 *
 *
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class Context extends Entity implements Function<BeanReference, BeanWrapper> {

    private final Optional<Context> parent;

    private final Map<BeanReference, BeanWrapper> beans = new HashMap<>();

    Context() {
        this.parent = empty();
    }

    Context(final Context parent) {
        this.parent = Optional.of(parent);
    }

    public <T> T inject(final T bean) {
        return BeanWrapper.inject(this, bean);
    }

    public <T> T bean(final Role role) {
        return bean(identifier(role));
    }

    public <T> T bean(final Class<T> type) {
        return bean(BeanReference.referenceBy(type));
    }

    private <T> T bean(final BeanReference ref) {
        //noinspection unchecked
        final BeanWrapper<T> wrapper = apply(ref);
        if (wrapper == null)  {
            return null;
        }
        try {
            return wrapper.bean(this);
        } catch (Exception e) {
            throw new JustJSystemException(format("%s is not created", ref.toString()), e);
        }
    }

    public <T> void put(final Role<T> role, final Supplier<T> supplier) {
        put(() -> wrap(supplier, role.getSuitableType()), identifier(role));
    }

    public void put(final Role role, final Object bean) {
        put(() -> wrap(bean), identifier(role));
    }

    public void put(final Role role, final Class clazz) {
        put(() -> wrap(clazz), identifier(role));
    }

    public <T> void put(final Class<T> type, final Supplier<T> supplier) {
        put(() -> wrap(supplier, type), BeanReference.referenceBy(type));
    }

    public void put(final Class type, final Object bean) {
        put(() -> wrap(bean), BeanReference.referenceBy(type));
    }

    public void put(final Class type, final Class clazz) {
        put(() -> wrap(clazz), BeanReference.referenceBy(type));
    }

    private void put(final Supplier<BeanWrapper> supplier, final BeanReference ref) {
        checkDuplicate(ref);
        final BeanWrapper wrapper = supplier.get();
        wrapper.checkType(ref);
        beans.put(ref, wrapper);
    }

    public Optional<Context> parent() {
        return parent;
    }

    public Context root() {
        return parent.isPresent() ? parent.get().root() : this;
    }

    private void checkDuplicate(final BeanReference ref) {
        if (apply(ref) != null) {
            throw new JustJSystemException("%s is already exists", ref.toString());
        }
    }

    @Override
    public BeanWrapper apply(final BeanReference ref) {
        final BeanWrapper wrapper = beans.get(ref);
        if (wrapper == null && parent.isPresent()) {
            return parent.get().apply(ref);
        }
        return wrapper;
    }
}
