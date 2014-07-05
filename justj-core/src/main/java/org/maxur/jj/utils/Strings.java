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
 * @since <pre>1/24/14</pre>
 */
public final class Strings {

    private Strings() {
        //empty - prevent construction
    }

    public static boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }


    public static String left(final String value, final char separator) {
        if (value == null) {
            return null;
        }
        return value.lastIndexOf(separator) < 0 ? value : value.substring(0, value.lastIndexOf(separator));
    }

    public static String right(final String value, final char separator) {
        if (value == null) {
            return null;
        }
        return value.lastIndexOf(separator) < 0 ? value : value.substring(value.lastIndexOf(separator) + 1);
    }


    /**
     * Return empty.
     *
     * @return the empty string
     */
    public static String empty() {
        return "";
    }

    /**
     * Quote string.
     *
     * @param value the value
     * @return the quoted text
     */
    public static String quote(final String value) {
        return "\"" + value + "\"";
    }


}
