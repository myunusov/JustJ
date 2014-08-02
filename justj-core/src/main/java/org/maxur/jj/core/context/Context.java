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

import java.util.function.Supplier;

import static java.lang.String.format;
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

    private final MetaData metaData;

    Context() {
        this.parent = null;
        metaData = new BaseMetaData();
    }

    Context(final Context parent) {
        this.parent = parent;
        metaData = new BaseMetaData((BaseMetaData) parent.metaData);
    }

    public <T> T inject(final T bean) {
        return BeanWrapper.inject(this, bean);
    }

    public <T> T bean(final Role role) {
        return bean(identifier(role));
    }

    public <T> T bean(final Class<T> type) {
        return bean(identifier(type));
    }

    private <T> T bean(final BeanIdentifier id) {
        final BeanWrapper wrapper = metaData.wrapper(id);
        if (wrapper == null)  {
            return null;
        }
        try {
            return wrapper.bean(this);
        } catch (Exception e) {
            throw new JustJSystemException(format("Bean '%s' is not created", id.getName()), e);
        }
    }

    public void put(final Role role, final Supplier<?> supplier) {
        metaData.put(() -> wrap(supplier), identifier(role));
    }

    public void put(final Role role, final Object bean) {
        metaData.put(() -> wrap(bean), identifier(role));
    }

    public void put(final Role role, final Class clazz) {
        metaData.put(() -> wrap(clazz), identifier(role));
    }

    public void put(final Class type, final Supplier<?> supplier) {
        metaData.put(() -> wrap(supplier), identifier(type));
    }

    public void put(final Class type, final Object bean) {
        metaData.put(() -> wrap(bean), identifier(type));
    }

    public void put(final Class type, final Class clazz) {
        metaData.put(() -> wrap(clazz), identifier(type));
    }

    public Context parent() {
        return parent;
    }

    public Context root() {
        return parent == null ?  this : parent.root();
    }

}
