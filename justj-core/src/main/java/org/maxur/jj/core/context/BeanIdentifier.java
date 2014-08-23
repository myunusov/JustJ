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

package org.maxur.jj.core.context;

import org.maxur.jj.core.domain.Role;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
public abstract class BeanIdentifier<T> {

    public static <T> BeanIdentifier<T> identifierBy(final Role<T> role) {
        return new RoleIdentifier<>(role);
    }

    public static <T> BeanIdentifier<T> identifierBy(final Class<T> type) {
        return new TypeIdentifier<>(type);
    }

    public abstract Class<T> getType();

    static final class RoleIdentifier<T> extends BeanIdentifier<T> {

        private final Role<T> role;

        public RoleIdentifier(final Role<T> role) {
            this.role = role;
        }

        @Override
        public Class<T> getType() {
            return role.getSuitableType();
        }

        @Override
        public String toString() {
            return format("Bean of '%s' role", role);
        }

        @Override
        public boolean equals(Object o) {
            return this == o ||
                    o instanceof RoleIdentifier &&
                            role.equals(((RoleIdentifier) o).role);
        }

        @Override
        public int hashCode() {
            return role.hashCode();
        }
    }

    static final class TypeIdentifier<T> extends BeanIdentifier<T> {

        private final Class<T> type;

        public TypeIdentifier(final Class<T> type) {
            this.type = type;
        }

        @Override
        public Class<T> getType() {
            return type;
        }

        @Override
        public String toString() {
            return format("Bean of '%s' type", type.getName());
        }

        @Override
        public boolean equals(Object o) {
            return this == o ||
                    o instanceof TypeIdentifier &&
                            type.equals(((TypeIdentifier) o).type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }
    }

}

