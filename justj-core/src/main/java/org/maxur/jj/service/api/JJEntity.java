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

import org.maxur.jj.view.api.JJView;

import java.util.UUID;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/8/2014</pre>
 */
public class JJEntity {

    private final String id;

    private final String name;

    public JJEntity(final String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public JJEntity(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JJEntity)) {
            return false;
        }
        final JJView jjView = (JJView) o;
        return name.equals(jjView.getName()) && id.equals(jjView.getId());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
