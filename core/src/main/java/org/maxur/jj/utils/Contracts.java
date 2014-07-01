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

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/29/14</pre>
 */
public class Contracts {

    private Contracts() {
        //empty - prevent construction
    }

    /**
     * If <code>value</code> is null, then throw a <code>IllegalArgumentException</code>.
     * @param value the value
     * @return value
     */
    public static <T> T notNull(final T value) {
        return notNull(value, "Value must not be null");
    }

    /**
     * If <code>value</code> is null, then throw a <code>IllegalArgumentException</code>.
     * @param value the value
     * @param message Error message template (see <code>String.format()</code> description)
     * @param args    Error message argument
     * @return value
     */
    public static <T> T notNull(final T value, final String message, final Object... args) {
        if (value == null) {
            throw new IllegalArgumentException(format(message, args));
        }
        return value;
    }


    /**
     * If <code>array</code> contains null, then throw a <code>IllegalArgumentException</code>.
     * @param array the value
     * @return value if it's valid
     */
    public static <T> T[] notContainsNull(final T[] array) {
        return notContainsNull(array, "Value must not contain null");
    }

    /**
     * If <code>array</code> contains null, then throw a <code>IllegalArgumentException</code>.
     * @param array the value
     * @param message Error message template (see <code>String.format()</code> description)
     * @param args    Error message argument
     * @return value if it's valid
     */
    public static <T> T[] notContainsNull(final T[] array, final String message, final Object... args) {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null");
        }
        for (Object object : array) {
            if (object == null) {
                throw new IllegalArgumentException(format(message, args));
            }
        }
        return array;
    }






}
