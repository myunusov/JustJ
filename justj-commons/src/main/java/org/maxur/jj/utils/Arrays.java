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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.maxur.jj.utils.Contracts.notContainsNull;
import static org.maxur.jj.utils.Contracts.notNull;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/28/14</pre>
 */
public final class Arrays {

    private Arrays() {
        //empty - prevent construction
    }

    @SafeVarargs
    public static <T> Set<T> toSet(final T... array) {
        notNull(array);
        notContainsNull(array);
        return stream(array).collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <T, S> Map<T, S> toMap(final Pair<T, S>... array) {
        notNull(array);
        notContainsNull(array);
        return stream(array).collect(Collectors.toMap(Pair::key, Pair::value));
    }


    public static boolean contains(final Object[] arrays, final Object object) {
        if (object == null) {
            return false;
        }
        for (Object otherObject : arrays) {
            if (object.equals(otherObject)) {
                return true;
            }
        }
        return false;
    }
}
