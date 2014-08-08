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
import reflection.Dependency;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
public abstract class BeanReference {

    public static BeanReference identifier(final Role role) {
        return new RoleReference(role);
    }

    public static BeanReference referenceBy(final Class type) {
        return new TypeReference(type);
    }

    public abstract Class getType();

    static final class RoleReference extends BeanReference {

        private final Role role;

        public RoleReference(final Role role) {
            this.role = role;
        }

        @Override
        public Class getType() {
            return role.getSuitableType();
        }

        @Override
        public String toString() {
            return format("Bean of '%s' role", role);
        }

        @Override
        public boolean equals(Object o) {
            return this == o ||
                    o instanceof RoleReference &&
                            role.equals(((RoleReference) o).role);
        }

        @Override
        public int hashCode() {
            return role.hashCode();
        }
    }

    static final class TypeReference extends BeanReference implements Dependency {

        private final Class type;

        public TypeReference(final Class type) {
            this.type = type;
        }

        @Override
        public Class getType() {
            return type;
        }

        @Override
        public String toString() {
            return format("Bean of '%s' type", type.getName());
        }

        @Override
        public boolean equals(Object o) {
            return this == o ||
                    o instanceof TypeReference &&
                            type.equals(((TypeReference) o).type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }
    }

}

