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

import org.maxur.jj.utils.Strings;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/8/2014</pre>
 */
public abstract class JJCommand<T extends JJEntity, O extends JJEntity> extends JJEntity implements Cloneable {

    private final String hotKey;

    public JJCommand(final String name) {
        super(Strings.extract(name, '&'));
        final int i = name.indexOf('&');
        hotKey = i == -1 ? null : ("" + name.charAt(i + 1)).toUpperCase();
    }

    public void params(final String params) {
        // TODO
    }

    public abstract O execute(T sender);

    @Override
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public JJCommand clone()  {
        try {
            return (JJCommand) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getHotKey() {
        return hotKey;
    }
}
