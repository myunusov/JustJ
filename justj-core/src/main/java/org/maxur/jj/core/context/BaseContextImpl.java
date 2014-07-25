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

import org.maxur.jj.core.domain.JustJSystemException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
public class BaseContextImpl implements ContextImpl {

    private final BaseContextImpl parent;

    private final Map<BeanIdentifier, BeanWrapper> beans = new HashMap<>();

    public BaseContextImpl() {
        this.parent = null;
    }

    public BaseContextImpl(final BaseContextImpl parent) {
        this.parent = parent;
    }

    @Override
    public BeanWrapper wrapper(final BeanIdentifier id) {
        final BeanWrapper wrapper = findBeanWrapper(id);
        if (wrapper == null) {
            return null;
        }
        return wrapper;
    }

    @Override
    public void put(final Supplier<BeanWrapper> supplier, final BeanIdentifier id) {
        checkDuplicate(id);
        final BeanWrapper wrap = supplier.get();
        if (wrap.suitableTo(id.getType())) {
            beans.put(id, wrap);
            return;
        }
        final String message = format(
                "The type '%s' is not suitable to %s",
                wrap.type().getName(),
                id.getName()
        );
        throw new IllegalArgumentException(message);
    }

    protected BeanWrapper findBeanWrapper(final BeanIdentifier id) {
        final BeanWrapper wrapper = beans.get(id);
        if (wrapper == null)  {
            if (parent != null) {
                return parent.findBeanWrapper(id);
            }
        }
        return wrapper;
    }

    private void checkDuplicate(final BeanIdentifier id) {
        if (findBeanWrapper(id) != null) {
            throw new JustJSystemException("Bean with %s is exist already", id.getName());
        }
    }


}
