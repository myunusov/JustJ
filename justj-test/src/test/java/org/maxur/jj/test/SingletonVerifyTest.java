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

package org.maxur.jj.test;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SingletonVerifyTest {

    @Test
    public void testCreate() throws Exception {
        assertNotNull(SingletonVerify.forInstance(Singleton::getInstance));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        SingletonVerify.forInstance(null);
    }

    @Test
    public void testVerifySingleton() throws Exception {
        final SingletonVerify verify = SingletonVerify.forInstance(Singleton::getInstance);
        verify.verify();
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testVerifyPrototype() throws Exception {
        final SingletonVerify verify = SingletonVerify.forInstance(Prototype::getInstance);
        verify.verify();
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testVerifyNull() throws Exception {
        final SingletonVerify verify = SingletonVerify.forInstance(() -> null);
        verify.verify();
    }


}

class Singleton {
    static Singleton instance = new Singleton();
    static Singleton getInstance() {
        return instance;
    }
}

class Prototype {
    static Prototype getInstance() {
        return new Prototype();
    }
}