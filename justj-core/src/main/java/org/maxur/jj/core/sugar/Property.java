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

package org.maxur.jj.core.sugar;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/11/2014</pre>
 */
public interface Property<T> extends FinalProperty<T> {


    public static <O> FinalProperty<O> readOnly(O initialValue) {
        return new SimpleReadOnlyProperty<>(initialValue);
    }

    public static <O> FinalProperty<O> readOnly(final Supplier<O> getter) {
        return new ReadOnlyProperty<>(getter);
    }

    public static <O> Property<O> readWrite() {
        return new SimpleReadWriteProperty<>();
    }

    public static <O> Property<O> readWrite(O initialValue) {
        return new SimpleReadWriteProperty<>(initialValue);
    }

    public static <O> Property<O> readWrite(final Supplier<O> getter, final Consumer<O> setter) {
        return new ReadWriteProperty<>(getter, setter);
    }


    public static<O> Property<O> empty() {
        return new SimpleReadWriteProperty<>();
    }

    public static <O> Property<O> of(O value) {
        return new SimpleReadWriteProperty<>(value);
    }

    void set(T value);

    T get();

}
