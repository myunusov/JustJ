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
public abstract class BeanIdentifier {

    public static BeanIdentifier identifier(final Role role) {
        return new RoleIdentifier(role);
    }

    public static BeanIdentifier identifier(final Class type) {
        return new TypeIdentifier(type);
    }

    public abstract String getName();

    public abstract Class getType();

    private static class RoleIdentifier extends BeanIdentifier {

        private final Role role;

        public RoleIdentifier(final Role role) {
            this.role = role;
        }

        @Override
        public Class getType() {
            return role.getSuitableType();
        }

        @Override
        public String getName() {
            return format("Role '%s'", role);
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

    public static class TypeIdentifier extends BeanIdentifier {

        private final Class type;

        public TypeIdentifier(final Class type) {
            this.type = type;
        }

        @Override
        public Class getType() {
            return type;
        }

        @Override
        public String getName() {
            return format("Type '%s'", type.getName());
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

