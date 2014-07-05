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

package org.maxur.jj.utils;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/31/14</pre>
 */
public class Pair<T, S> {

    private final T key;
    private final S value;

    private Pair(final T key, final S value) {
        this.key = key;
        this.value = value;
    }

    public static <T, S> Pair<T, S> of(final T key, final S value) {
        return new Pair<>(key, value);
    }

    public T key() {
        return this.key;
    }

    public S value() {
        return this.value;
    }
}
