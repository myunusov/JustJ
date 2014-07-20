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

import java.util.function.Supplier;

/**
* @author Maxim Yunusov
* @version 1.0 20.07.2014
*/
abstract class BeanWrapper {

    public static BeanWrapper wrap(final Supplier<?> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new SupplierBeanWrapper(supplier);
    }

    public static BeanWrapper wrap(final Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new ObjectBeanWrapper(bean);
    }

    public abstract Class type();

    public abstract <T> T bean();

    protected abstract boolean suitableTo(final Class type);

    private static class SupplierBeanWrapper extends BeanWrapper {

        private final Supplier<?> supplier;

        public SupplierBeanWrapper(final Supplier<?> supplier) {
            this.supplier = supplier;
        }

        public Class type() {
            return Object.class;  // TODO
        }

        @SuppressWarnings("unchecked")
        public <T> T bean() {
            return (T) supplier.get();
        }

        @Override
        protected boolean suitableTo(Class type) {
            return true;    // TODO
        }
    }

    private static class ObjectBeanWrapper extends BeanWrapper {

        private final Object bean;

        public ObjectBeanWrapper(Object bean) {
            this.bean = bean;
        }

        public Class type() {
            return bean.getClass();
        }

        @SuppressWarnings("unchecked")
        public <T> T bean() {
            return (T) bean;  // TODO must be catch exception
        }

        @Override
        protected boolean suitableTo(Class type) {
            //noinspection unchecked
            return type.isAssignableFrom(type());
        }
    }

}
