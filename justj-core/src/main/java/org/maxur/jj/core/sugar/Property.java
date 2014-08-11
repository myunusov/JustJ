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

package org.maxur.jj.core.sugar;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Maxim Yunusov
 * @version 1.0 11.08.2014
 */
public interface Property {

    public static <O> RW<O> empty() {
        return new VarReadWriteProperty<>();
    }

    public static <O> RO<O> ro(final O initialValue) {
        return new VarReadOnlyProperty<>(initialValue);
    }

    public static <O> RW<O> rw(final O initialValue) {
        return new VarReadWriteProperty<>(initialValue);
    }

    public static <O> ROBuilder<O> getter(final Supplier<O> getter) {
        return new ROBuilder<>(getter);
    }

    class ROBuilder<O> {

        private final Supplier<O> getter;

        public ROBuilder(final Supplier<O> getter) {
            this.getter = getter;
        }

        public RWBuilder<O> setter(final Consumer<O> setter) {
            return new RWBuilder<O>(getter, setter);
        }

        RO<O> build() {
            return new ReadOnlyProperty<>(getter);
        }

    }

    static class RWBuilder<O> {

        private final ReadWriteProperty<O> property;

        public RWBuilder(final Supplier<O> getter, final Consumer<O> setter) {
            property = new ReadWriteProperty<>(getter, setter);
        }

        RW<O> build() {
            return  property;
        }

    }
}
