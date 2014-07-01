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
import org.maxur.jj.utils.Pair;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.maxur.jj.reflect.Reflection.getClassPathEntries;
import static org.maxur.jj.utils.Arrays.toMap;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/28/14</pre>
 */
public class ReflectionTest {

/*    @Test
    public void testLoadJarBy() throws Exception {
        final String pathname = "test-resources/fake-1.jar";
        loadJarBy(pathname);
        final Class clazz = getSystemClassLoader().loadClass("org.maxur.jj.utils.fake.Fake");
        assertNotNull(clazz.newInstance());
    }

    @Test
    public void testIdempotenceOfLoadJarBy() throws Exception {
        final String pathname = "test-resources/fake-1.jar";
        loadJarBy(pathname);
        loadJarBy(pathname);
        final Class clazz = getSystemClassLoader().loadClass("org.maxur.jj.utils.fake.Fake");
        assertNotNull(clazz.newInstance());
    }*/


    @Test
    public void testClassPathEntries_emptyURLClassLoader_noParent() {
        assertTrue(getClassPathEntries(new URLClassLoader(new URL[0], null)).keySet().isEmpty());
    }

    @Test
    public void testClassPathEntries_URLClassLoader_noParent() throws Exception {
        final URL url1 = new URL("file:/a");
        final URL url2 = new URL("file:/b");
        final ClassLoader classloader = new URLClassLoader(new URL[]{url1, url2}, null);
        assertEquals(
                toMap(Pair.of(url1.toURI(), classloader), Pair.of(url2.toURI(), classloader)),
                getClassPathEntries(classloader));
    }

    @Test
    public void testClassPathEntries_URLClassLoader_withParent() throws Exception {
        final URL url1 = new URL("file:/a");
        final URL url2 = new URL("file:/b");
        final ClassLoader parent = new URLClassLoader(new URL[]{url1}, null);
        final ClassLoader child = new URLClassLoader(new URL[]{url2}, parent) {
        };
        final Map<URI, ClassLoader> classPathEntries = getClassPathEntries(child);

        assertEquals(toMap(
                Pair.of(url1.toURI(), parent),
                Pair.of(url2.toURI(), child)), classPathEntries);

        assertEquals(2, classPathEntries.keySet().size());
        final Iterator<URI> iterator = classPathEntries.keySet().iterator();
        assertEquals(url1.toURI(), iterator.next());
        assertEquals(url2.toURI(), iterator.next());
    }

    @Test
    public void testClassPathEntries_duplicateUri_parentWins() throws Exception {
        final URL url = new URL("file:/a");
        final ClassLoader parent = new URLClassLoader(new URL[]{url}, null);
        final ClassLoader child = new URLClassLoader(new URL[]{url}, parent) {
        };
        assertEquals(toMap(Pair.of(url.toURI(), parent)), getClassPathEntries(child));
    }

    @Test
    public void testClassPathEntries_notURLClassLoader_noParent() {
        assertTrue(getClassPathEntries(new ClassLoader(null) {
        }).keySet().isEmpty());
    }

    @Test
    public void testClassPathEntries_notURLClassLoader_withParent() throws Exception {
        final URL url = new URL("file:/a");
        final ClassLoader parent = new URLClassLoader(new URL[]{url}, null);
        assertEquals(
                toMap(Pair.of(url.toURI(), parent)),
                getClassPathEntries(new ClassLoader(parent) {
                }));
    }

    @Test
    public void testClassPathEntries_notURLClassLoader_withParentAndGrandParent() throws Exception {
        final URL url1 = new URL("file:/a");
        final URL url2 = new URL("file:/b");
        final ClassLoader grandParent = new URLClassLoader(new URL[]{url1}, null);
        final ClassLoader parent = new URLClassLoader(new URL[]{url2}, grandParent);
        assertEquals(
                toMap(Pair.of(url1.toURI(), grandParent), Pair.of(url2.toURI(), parent)),
                getClassPathEntries(new ClassLoader(parent) {
                }));
    }

    @Test
    public void testClassPathEntries_notURLClassLoader_withGrandParent() throws Exception {
        final URL url = new URL("file:/a");
        final ClassLoader grandParent = new URLClassLoader(new URL[]{url}, null);
        final ClassLoader parent = new ClassLoader(grandParent) {
        };
        assertEquals(
                toMap(Pair.of(url.toURI(), grandParent)),
                getClassPathEntries(new ClassLoader(parent) {
                }));
    }


}
