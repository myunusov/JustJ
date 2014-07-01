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
import org.maxur.jj.utils.ClassScanner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/27/14</pre>
 */
public class DirClassScannerTest {

    @Test
    public void testGetAllClassesFrom() throws Exception {
        final ClassScanner scanner = new DirClassScanner("org.maxur.jj.reflect.fake.sub");
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(
                0,
                classes.stream().filter(c -> "org.maxur.jj.reflect.fake.FakeInDir".equals(c.getName())).count()
        );
        assertEquals(
                1,
                classes.stream().filter(c -> "org.maxur.jj.reflect.fake.sub.ClassInSubPackage".equals(c.getName())).count()
        );

    }

    @Test
    public void testGetAllClassesFromRootPackage() throws Exception {
        final ClassScanner scanner = new DirClassScanner();
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(
                1,
                classes.stream().filter(c -> "org.maxur.jj.reflect.fake.FakeInDir".equals(c.getName())).count()
        );
        assertEquals(
                1,
                classes.stream().filter(c -> "org.maxur.jj.reflect.fake.sub.ClassInSubPackage".equals(c.getName())).count()
        );
    }

}
