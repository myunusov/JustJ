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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.maxur.jj.utils.ClassScanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.deleteIfExists;
import static org.junit.Assert.assertEquals;
import static org.maxur.jj.reflect.Reflection.loadJarBy;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/28/14</pre>
 */
public class JarClassScannerTest {

    private final static String DIR_NAME = "tmp";
    private final static String FILE_NAME = "fake.jar";
    private final static String PATH_NAME = DIR_NAME + "/" + FILE_NAME;

    @BeforeClass
    public static void setUp() throws Exception {
        final File directory = new File(DIR_NAME);
        if (!directory.exists()) {
            createDirectory(directory.toPath());
        }
        final InputStream jarFileStream = JarClassScannerTest.class.getResourceAsStream("/" + FILE_NAME);
        final File jarFile = new File(PATH_NAME);
        deleteIfExists(jarFile.toPath());
        Files.copy(jarFileStream, jarFile.toPath());
        loadJarBy(PATH_NAME);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            deleteIfExists(new File(PATH_NAME).toPath());
        } catch (IOException e) {
            // Ignore
        }
    }

    @Test
    public void testGetAllClassesFrom() throws Exception {
        final ClassScanner scanner = new JarClassScanner(PATH_NAME);
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(1, classes.size());
        assertEquals("org.maxur.jj.utils.fake.Fake", classes.get(0).getName());
    }

    @Test
    public void testGetAllClassesFromWithPackage() throws Exception {
        final ClassScanner scanner = new JarClassScanner(PATH_NAME, "org.maxur.jj.utils.fake.*");
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(1, classes.size());
        assertEquals("org.maxur.jj.utils.fake.Fake", classes.get(0).getName());
    }

    @Test
    public void testGetAllClassesFromWithParentPackage() throws Exception {
        final ClassScanner scanner = new JarClassScanner(PATH_NAME, "org.maxur.jj.utils.*");
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(1, classes.size());
        assertEquals("org.maxur.jj.utils.fake.Fake", classes.get(0).getName());
    }

    @Test
    public void testGetAllClassesFromWithWrongPackage() throws Exception {
        final ClassScanner scanner = new JarClassScanner(PATH_NAME, "org.maxur.jj.wrong.*");
        final List<Class<?>> classes = scanner.getAllClassesFrom();
        assertEquals(0, classes.size());
    }


}
