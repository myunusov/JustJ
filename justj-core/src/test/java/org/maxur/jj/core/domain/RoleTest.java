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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class RoleTest {

    @Test
    public void testAnyRoleSuitableToAnyClass() throws Exception {
        assertTrue(Role.ANY.suitableTo((new Object() {}).getClass()));
    }

    @Test
    public void testRoleSuitableToChildClass() throws Exception {
        final Role role = new Role(DummyObject.class) {
        };
        assertTrue(role.suitableTo((new DummyObject()).getClass()));
        assertTrue(role.suitableTo((new DummyObject(){}).getClass()));
    }

    @Test
    public void testRoleUnSuitableToDifferentClass() throws Exception {
        final Role role = new Role(DummyObject.class) {
        };
        assertFalse(role.suitableTo((new Object() {}).getClass()));
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Role.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .verify();
    }


    private static class DummyObject {
    }
}