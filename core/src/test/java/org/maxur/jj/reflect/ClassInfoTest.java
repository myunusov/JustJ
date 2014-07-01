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

package org.maxur.jj.reflect;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/31/14</pre>
 */
public class ClassInfoTest {

    @Test
    public void testEquals() {
        assertEquals(classInfo(ClassPathTest.class), classInfo(ClassPathTest.class));
        assertEquals(classInfo(Test.class), classInfo(Test.class, getClass().getClassLoader()));
    }

    private static ClassInfo classInfo(final Class<?> cls) {
        return classInfo(cls, cls.getClassLoader());
    }

    private static ClassInfo classInfo(final Class<?> cls, final ClassLoader classLoader) {
        return new ClassInfo(cls.getName().replace('.', '/') + ".class", classLoader);
    }


}
