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
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.maxur.jj.core.context.BeanIdentifier.identifierBy;
import static org.maxur.jj.core.context.BeanReference.nullReference;
import static org.maxur.jj.core.context.BeanReference.reference;

/**
 *
 *
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class BaseScope extends Entity implements Scope, InnerScope, Container {

    private final Optional<BaseScope> parent;

    private final Map<BeanIdentifier, BeanReference> beans = new HashMap<>();

    BaseScope() {
        this.parent = empty();
    }

    BaseScope(final BaseScope parent) {
        this.parent = Optional.of(parent);
    }

    @Override
    public Scope parent() {
        return parent.isPresent() ? parent.get() : null;
    }

    @Override
    public Scope branch() {
        return new BaseScope(this);
    }

    public <T> T inject(final T bean) {
        return BeanReference.inject(this, bean);
    }

    public <T> T bean(final Role<T> role) {
        return bean(identifierBy(role));
    }

    public <T> T bean(final Class<T> type) {
        return bean(BeanIdentifier.identifierBy(type));
    }

    @Override
    public void accept(final Config config) {
        config.applyTo(this);
    }

    private <T> T bean(final BeanIdentifier id) {
        final BeanReference<T> ref = apply(id);
        if (!ref.isPresent()) {
            return null;
        }
        try {
            return ref.bean(this);
        } catch (Exception e) {
            throw new JustJSystemException(format("%s is not created", id.toString()), e);
        }
    }

    @Override
    public <T> BeanReference<T> apply(final BeanIdentifier id) {
        //noinspection unchecked
        final BeanReference<T> ref = beans.get(id);
        if (ref != null) {
            return ref;
        }
        if (parent.isPresent()) {
            return parent.get().apply(id);
        }
        return nullReference();
    }

    @Override
    public <T> void addSupplier(final Role<T> role, final Supplier<? extends T> supplier) {
        put(() -> BeanReference.reference(supplier, role.getSuitableType()), identifierBy(role));
    }

    @Override
    public <T> void addBean(final Role<T> role, final T bean) {
        put(() -> BeanReference.reference(bean), identifierBy(role));
    }

    @Override
    public <T> void addType(final Role<T> role, final Class<? extends T> type) {
        put(() -> reference(type), identifierBy(role));
    }

    @Override
    public <T> void addSupplier(final Class<T> type, final Supplier<? extends T> supplier) {
        put(() -> BeanReference.reference(supplier, type), BeanIdentifier.identifierBy(type));
    }

    @Override
    public <T> void addBean(final Class<T> type, final T bean) {
        put(() -> BeanReference.reference(bean), BeanIdentifier.identifierBy(type));
    }

    @Override
    public <T> void addType(final Class<T> type, final Class<? extends T> clazz) {
        put(() -> reference(clazz), BeanIdentifier.identifierBy(type));
    }

    private void put(final Supplier<BeanReference> supplier, final BeanIdentifier id) {
        checkDuplicate(id);
        final BeanReference ref = supplier.get().checkType(id);
        beans.put(id, ref);
    }

    public Scope root() {
        return parent.isPresent() ? parent.get().root() : this;
    }

    private void checkDuplicate(final BeanIdentifier id) {
        if (apply(id).isPresent()) {
            throw new JustJSystemException("%s is already exists", id.toString());
        }
    }



}
