/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.core.domain;

/**
 * @author Maxim Yunusov
 * @version 1.0 18.07.2014
 */
public class Role<T> extends Entity {

    private static final Role ANYTHING = new Role("Anything", Object.class) {
        @Override
        public boolean suitableTo(Class type) {
            return true;
        }
    };

    private final Class<T> suitableType;

    private final String name;

    public static <Z> Role<Z> any() {
        //noinspection unchecked
        return ANYTHING;
    }

    public static <O> Role<O> role(final String name, final Class<O> suitableType) {
        return new Role<>(name, suitableType);
    }

    private Role(final String name, final Class<T> suitableType) {
        this.suitableType = suitableType;
        this.name = name;
    }

    public boolean suitableTo(final Class type) {
        //noinspection unchecked
        return suitableType.isAssignableFrom(type);
    }

    public Class<T> getSuitableType() {
        return suitableType;
    }

    @Override
    public String toString() {
        return name;
    }
}
