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
package org.maxur.jj.core.entity;

import java.util.UUID;

import static java.lang.String.format;

/**
 * Super class for Objects that have a distinct identity that runs through time and different representations.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/15/2014</pre>
 */
public abstract class Entity {

    protected final String id;

    public Entity() {
        id = UUID.randomUUID().toString();
    }

    public Entity(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return format("Entity '%s' {%s}", getClass().getName(), id);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Entity && id.equals(((Entity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
