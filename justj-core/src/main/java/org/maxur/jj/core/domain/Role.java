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
public abstract class Role extends Entity {

    private final Class<?> suitableType;

    public static final Role ANY = new Role(Object.class) {
        @Override
        public boolean suitableTo(Class<?> type) {
            return true;
        }
        @Override
        public String toString() {
            return "Anything";
        }
    };

    protected Role(final Class<?> suitableType) {
        this.suitableType = suitableType;
    }

    public boolean suitableTo(final Class<?> type) {
        return suitableType.isAssignableFrom(type);
    }

    public Class<?> getSuitableType() {
        return suitableType;
    }
}
