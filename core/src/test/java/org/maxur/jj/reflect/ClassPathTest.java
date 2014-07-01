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
import org.maxur.jj.reflect.fake.sub.ClassInSubPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/31/14</pre>
 */
public class ClassPathTest {

    @SuppressWarnings("UnusedDeclaration")
    private static class Nested {
    }

    @Test
    public void testGetResources() throws Exception {
        final ClassPath classpath = ClassPath.from(getClass().getClassLoader());
        final Set<ResourceInfo> resources = classpath.getResources();

        final Map<String, ResourceInfo> byName = new HashMap<>();
        final Map<String, ResourceInfo> byId = new HashMap<>();
        for (ResourceInfo resource : resources) {
            assertFalse(resource.getResourceName().equals(JarFile.MANIFEST_NAME));
            assertFalse(resource.toString().equals(JarFile.MANIFEST_NAME));
            byName.put(resource.getResourceName(), resource);
            byId.put(resource.getId(), resource);
//            assertNotNull(resource.url());    TODO ?
        }
        final String testResourceName = "org/maxur/jj/reflect/test.properties";
        assertTrue(byName.keySet().contains("org/maxur/jj/reflect/ClassPath.class"));
        assertTrue(byName.keySet().contains("org/maxur/jj/reflect/ClassPathTest.class"));
        assertTrue(byName.keySet().contains("org/maxur/jj/reflect/ClassPathTest$Nested.class"));
        assertTrue(byName.keySet().contains(testResourceName));

        assertTrue(byId.keySet().contains("org.maxur.jj.reflect.ClassPath"));
        assertTrue(byId.keySet().contains("org.maxur.jj.reflect.ClassPathTest"));
        assertTrue(byId.keySet().contains("org.maxur.jj.reflect.ClassPathTest$Nested"));
        assertTrue(byId.keySet().contains(testResourceName));

        assertEquals(
                getClass().getClassLoader().getResource(testResourceName),
                byName.get(testResourceName).url()
        );
    }

    @Test
    public void testGetAllClasses() throws Exception {
        final ClassPath classpath = ClassPath.from(getClass().getClassLoader());
        final Set<ClassInfo> allClasses = classpath.getAllClasses();

        final Set<String> names = new HashSet<>();
        final Set<String> ids = new HashSet<>();
        final Set<Class<?>> classes = new HashSet<>();
        final Set<String> packageNames = new HashSet<>();
        final Set<String> simpleNames = new HashSet<>();
        for (ClassInfo classInfo : allClasses) {
            if (!classInfo.getPackageName().equals(ClassPathTest.class.getPackage().getName())) {
                continue;
            }
            names.add(classInfo.getName());
            ids.add(classInfo.getId());
            classes.add(classInfo.load());
            packageNames.add(classInfo.getPackageName());
            simpleNames.add(classInfo.getSimpleName());
        }
        class LocalClass {
        }
        Class<?> anonymousClass = new Object() {
        }.getClass();

        assertTrue(names.contains(anonymousClass.getName()));
        assertTrue(names.contains(LocalClass.class.getName()));
        assertTrue(names.contains(ClassPath.class.getName()));
        assertTrue(names.contains(ClassPathTest.class.getName()));

        assertTrue(ids.contains(anonymousClass.getName()));
        assertTrue(ids.contains(LocalClass.class.getName()));
        assertTrue(ids.contains(ClassPath.class.getName()));
        assertTrue(ids.contains(ClassPathTest.class.getName()));

        assertTrue(classes.contains(anonymousClass));
        assertTrue(classes.contains(LocalClass.class));
        assertTrue(classes.contains(ClassPath.class));
        assertTrue(classes.contains(ClassPathTest.class));

        assertArrayEquals(new String[]{ClassPath.class.getPackage().getName()}, packageNames.toArray());

        assertTrue(simpleNames.contains(""));
//        assertTrue(simpleNames.contains("Local"));
        assertTrue(simpleNames.contains("ClassPathTest"));


    }

    @Test
    public void testGetTopLevelClasses() throws Exception {
        final ClassPath classpath = ClassPath.from(getClass().getClassLoader());
        final Set<ClassInfo> topLevelClasses = classpath.getTopLevelClasses(ClassPathTest.class.getPackage().getName());

        final Set<String> names = new HashSet<>();
        final Set<String> ids = new HashSet<>();
        final Set<Class<?>> classes = new HashSet<>();
        final Set<String> packageNames = new HashSet<>();
        final Set<String> simpleNames = new HashSet<>();
        for (ClassInfo classInfo : topLevelClasses) {
            names.add(classInfo.getName());
            ids.add(classInfo.getId());
            classes.add(classInfo.load());
            packageNames.add(classInfo.getPackageName());
            simpleNames.add(classInfo.getSimpleName());
        }

        assertTrue(names.contains(ClassPath.class.getName()));
        assertTrue(names.contains(ClassPathTest.class.getName()));

        assertTrue(ids.contains(ClassPath.class.getName()));
        assertTrue(ids.contains(ClassPathTest.class.getName()));

        assertTrue(classes.contains(ClassPath.class));
        assertTrue(classes.contains(ClassPathTest.class));

        assertArrayEquals(new String[]{ClassPath.class.getPackage().getName()}, packageNames.toArray());

        assertTrue(simpleNames.contains("ClassPathTest"));
        assertTrue(simpleNames.contains("ClassPath"));


        assertFalse(classes.contains(ClassInSubPackage.class));
    }

    @Test
    public void testGetTopLevelClassesRecursive() throws Exception {
        final ClassPath classpath = ClassPath.from(ClassPathTest.class.getClassLoader());
        final Set<ClassInfo> topLevelClassesRecursive =
                classpath.getTopLevelClasses(ClassPathTest.class.getPackage().getName() + ".*");

        final Set<Class<?>> classes = new HashSet<>();
        for (ClassInfo classInfo : topLevelClassesRecursive) {
            if (classInfo.getName().contains("ClassPathTest")) {
                System.err.println("");
            }
            classes.add(classInfo.load());
        }

        assertTrue(classes.contains(ClassPathTest.class));
        assertTrue(classes.contains(ClassInSubPackage.class));
    }

    @Test
    public void testGetTopLevelClasses_diamond() throws Exception {
        final ClassLoader parent = ClassPathTest.class.getClassLoader();
        final ClassLoader sub1 = new ClassLoader(parent) {
        };
        final ClassLoader sub2 = new ClassLoader(parent) {
        };
        assertEquals(findClass(ClassPath.from(sub1).getTopLevelClasses(), ClassPathTest.class),
                findClass(ClassPath.from(sub2).getTopLevelClasses(), ClassPathTest.class));
    }



/*

    public void testGetClassName() {
        assertEquals("abc.d.Abc", ClassPath.getClassName("abc/d/Abc.class"));
    }

    public void testResourceInfo_of() {
        assertEquals(ClassInfo.class, resourceInfo(ClassPathTest.class).getClass());
        assertEquals(ClassInfo.class, resourceInfo(ClassPath.class).getClass());
        assertEquals(ClassInfo.class, resourceInfo(Nested.class).getClass());
    }

    public void testGetSimpleName() {
        assertEquals("Foo",
                new ClassInfo("Foo.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("Foo",
                new ClassInfo("a/b/Foo.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("Foo",
                new ClassInfo("a/b/Bar$Foo.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("",
                new ClassInfo("a/b/Bar$1.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("Foo",
                new ClassInfo("a/b/Bar$Foo.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("",
                new ClassInfo("a/b/Bar$1.class", getClass().getClassLoader()).getSimpleName());
        assertEquals("Local",
                new ClassInfo("a/b/Bar$1Local.class", getClass().getClassLoader()).getSimpleName());

    }

    public void testGetPackageName() {
        assertEquals("",
                new ClassInfo("Foo.class", getClass().getClassLoader()).getPackageName());
        assertEquals("a.b",
                new ClassInfo("a/b/Foo.class", getClass().getClassLoader()).getPackageName());
    }



    public void testNulls() throws IOException {
        new NullPointerTester().testAllPublicStaticMethods(ClassPath.class);
        new NullPointerTester()
                .testAllPublicInstanceMethods(ClassPath.from(getClass().getClassLoader()));
    }
    */

    private static ClassInfo findClass(final Iterable<ClassInfo> classes, final Class<?> cls) {
        for (ClassInfo classInfo : classes) {
            if (classInfo.getName().equals(cls.getName())) {
                return classInfo;
            }
        }
        throw new AssertionError("failed to find " + cls);
    }


    private static ResourceInfo resourceInfo(Class<?> cls) {
        return ResourceInfo.of(cls.getName().replace('.', '/') + ".class", cls.getClassLoader());
    }


}



