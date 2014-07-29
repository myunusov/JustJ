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

package org.maxur.jj.core.domain;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/18/2014</pre>
 */
public class JustJSystemException extends RuntimeException {

    private static final long serialVersionUID = 5376585918814932276L;

    public JustJSystemException(final String message, final Exception cause) {
        super(
                cause.getMessage() == null ?
                        format("%s. The causes is '%s'", message, cause.getClass().getSimpleName()) :
                        format("%s. The causes is '%s'", message, cause.getMessage()),
                cause
        );
    }

    public JustJSystemException(final String message, final Object... args) {
        super(format(message, args));
    }
}
