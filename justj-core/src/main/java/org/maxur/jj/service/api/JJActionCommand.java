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

package org.maxur.jj.service.api;

import java.util.function.Function;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/8/2014</pre>
 */
public class JJActionCommand<T extends JJEntity, O extends JJEntity> extends JJCommand<T, O>  {

    private final Function<T, O> action;

    public JJActionCommand(final String name, final Function<T, O> action) {
        super(name);
        this.action = action;
    }

    @Override
    public O execute(final T sender) {
        return action.apply(sender);
    }


}
