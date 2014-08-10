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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

/**
 * @author Maxim Yunusov
 * @version 1.0 10.08.2014
 */
public interface BeanWrapper<T> {

    static final BeanWrapper NULL_WRAPPER = new BeanWrapper<Object>() {

        @Override
        public BeanWrapper<Object> checkType(final BeanReference id) {
            return this;
        }

        @Override
        public String toString() {
            return "Bean \"Null Object\"";
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Map<? extends BeanReference, ? extends BeanWrapper> dependencies(
                final Function<BeanReference, BeanWrapper> context,
                final Map<BeanReference, BeanWrapper> accumulator
        ) {
            return emptyMap();
        }

        @Override
        public Object bean(final Function<BeanReference, BeanWrapper> context) {
            return null;
        }
    };

    public static BeanWrapper nullWrapper() {
        return NULL_WRAPPER;
    }

    public static <T> BeanWrapper wrap(final Supplier<T> supplier, final Class<T> clazz) {
        if (supplier == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new BaseBeanWrapper.SupplierBeanWrapper<>(supplier, clazz);
    }

    public static <O> BaseBeanWrapper<O> wrap(final O bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new BaseBeanWrapper.ObjectBeanWrapper<>(bean);
    }

    public static <T> BeanWrapper wrap(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new BaseBeanWrapper.ClassBeanWrapper<>(clazz);
    }

    public static <T> T inject(final Function<BeanReference, BeanWrapper> context, final T bean) {
        return wrap(bean).inject(bean, context);
    }

    T bean(Function<BeanReference, BeanWrapper> context);

    BeanWrapper checkType(BeanReference ref);

    boolean isPresent();

    Map<? extends BeanReference,? extends BeanWrapper> dependencies(
            Function<BeanReference, BeanWrapper> context,
            Map<BeanReference, BeanWrapper> accumulator
    );
}
