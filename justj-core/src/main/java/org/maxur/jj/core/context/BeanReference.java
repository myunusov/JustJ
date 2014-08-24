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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Maxim Yunusov
 * @version 1.0 10.08.2014
 */
public interface BeanReference<T> {

    public static <Z> BeanReference<Z> nullReference() {
        return new BeanReference<Z>() {
            @Override
            public BeanReference<Z> checkType(final BeanIdentifier id) {
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
            public Optional<Z> create(final InnerScope scope) {
                return Optional.empty();
            }

            @Override
            public Z inject(final Optional<Z> bean, final InnerScope scope) {
                return null;
            }

            @Override
            public Z bean(final InnerScope scope) {
                return null;
            }
        };
    }

    public static <Z> BeanReference<Z> reference(final Supplier<? extends Z> supplier, final Class<Z> clazz) {
        if (supplier == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new BaseBeanReference.SupplierBeanReference<>(supplier, clazz);
    }

    public static <T> BeanReference<T> reference(final T bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Been must not be null");
        }
        return new BaseBeanReference.ObjectBeanReference<>(bean);
    }

    public static <T> BeanReference<T> reference(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of been must not be null");
        }
        return new BaseBeanReference.ClassBeanReference<>(clazz);
    }

    T bean(InnerScope scope);

    BeanReference checkType(BeanIdentifier id);

    boolean isPresent();

    Optional<T> create(InnerScope scope);

    T inject(Optional<T> bean, InnerScope scope);
}
