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

import static java.util.Objects.requireNonNull;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/11/2014</pre>
 */
public class SimpleReadWriteProperty<T> implements Property<T> {

    private T value;

    public SimpleReadWriteProperty(T value) {
        this.value = requireNonNull(value);
    }

    public SimpleReadWriteProperty() {
        this.value = null;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return check(value);
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

}
