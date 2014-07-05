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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import static java.io.File.createTempFile;
import static java.nio.file.Files.createDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.maxur.jj.reflect.Scanner.getClassPathEntry;
import static org.maxur.jj.utils.Contracts.notNull;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/31/14</pre>
 */
public class ScannerTest {

    @Test
    public void testScan_classPathCycle() throws IOException {
        final File directory = new File("tmp");
        if (!directory.exists()) {
            createDirectory(directory.toPath()) ;
        }
        final File jarFile = createTempFile("with_circular_class_path", ".jar", directory);
        try {
            writeSelfReferencingJarFile(jarFile, "org/maxur/jj/reflect/test.properties");
            final Scanner scanner = new Scanner();
            scanner.scan(jarFile.toURI(), ScannerTest.class.getClassLoader());
            assertEquals(1, scanner.getResources().size());
        } finally {
            //noinspection ResultOfMethodCallIgnored
            jarFile.delete();
        }
    }

    @Test
    public void testScanFromFile_fileNotExists() throws IOException {
        final ClassLoader classLoader = ClassPathTest.class.getClassLoader();
        final Scanner scanner = new Scanner();
        scanner.scanFrom(new File("no/such/file/anywhere"), classLoader);
        assertTrue(scanner.getResources().isEmpty());
    }

    @Test
    public void testScanFromFile_notJarFile() throws IOException {
        final ClassLoader classLoader = ClassPathTest.class.getClassLoader();
        final File notJar = File.createTempFile("not_a_jar", "txt");
        final Scanner scanner = new Scanner();
        try {
            scanner.scanFrom(notJar, classLoader);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            notJar.delete();
        }
        assertTrue(scanner.getResources().isEmpty());
    }

    @Test
    public void testGetClassPathEntryForAbsolutePath() throws Exception {
        assertEquals(URI.create(("file:/usr/test/dep.jar")),
                getClassPathEntry(new File("/home/build/outer.jar"), "file:/usr/test/dep.jar"));
    }

    @Test
    public void testGetClassPathEntryForRelativePath() throws Exception {
        final File jarFile = new File("/home/build/outer.jar");
        // file:/home/build/a.jar = Linux
        // file:/D:/home/build/a.jar - Windows
        assertEquals(new File("/home/build/a.jar").toURI(),     getClassPathEntry(jarFile, "a.jar"));
        assertEquals(new File("/home/build/x/y/z").toURI(),     getClassPathEntry(jarFile, "x/y/z"));
        assertEquals(new File("/home/build/x/y/z.jar").toURI(), getClassPathEntry(jarFile, "x/y/z.jar"));
    }

    private static URI url(final String str) throws IOException {
        return URI.create(new File(str).getCanonicalPath().replace("\\", "/"));
    }

    @Test
    public void testGetClassPathFromManifest_nullManifest() {
        assertTrue(Scanner.getClassPathFromManifest(new File("some.jar"), null).isEmpty());
    }

    @Test
    public void testGetClassPathFromManifest_noClassPath() throws IOException {
        File jarFile = new File("base.jar");
        assertTrue(Scanner.getClassPathFromManifest(jarFile, manifest("")).isEmpty());
    }

    @Test
    public void testGetClassPathFromManifest_emptyClassPath() throws IOException {
        final File jarFile = new File("base.jar");
        assertTrue(Scanner.getClassPathFromManifest(jarFile, manifestClasspath("")).isEmpty());
    }

    @Test
    public void testGetClassPathFromManifest_badClassPath() throws IOException {
        final File jarFile = new File("base.jar");
        final Manifest manifest = manifestClasspath("an_invalid^path");
        assertTrue(Scanner.getClassPathFromManifest(jarFile, manifest).isEmpty());
    }

    @Test
    public void testGetClassPathFromManifest_relativeDirectory() throws IOException {
        final File jarFile = new File("base/some.jar");
        // with/relative/directory is the Class-Path value in the mf file.
        final Manifest manifest = manifestClasspath("with/relative/dir");
        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new File("base/with/relative/dir").toURI(), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_relativeJar() throws IOException {
        final File jarFile = new File("base/some.jar");
        // with/relative/directory is the Class-Path value in the mf file.
        final Manifest manifest = manifestClasspath("with/relative.jar");
        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
                assertEquals(1, classPaths.size());
        assertEquals(new File("base/with/relative.jar").toURI(), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_jarInCurrentDirectory() throws IOException {
        final File jarFile = new File("base/some.jar");
        // with/relative/directory is the Class-Path value in the mf file.
        final Manifest manifest = manifestClasspath("current.jar");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new File("base/current.jar").toURI(), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_absoluteDirectory() throws IOException, URISyntaxException {
        final File jarFile = new File("base/some.jar");
        final Manifest manifest = manifestClasspath("file:/with/absolute/dir");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new URI("file:/with/absolute/dir"), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_absoluteJar() throws IOException, URISyntaxException {
        final File jarFile = new File("base/some.jar");
        final Manifest manifest = manifestClasspath("file:/with/absolute.jar");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new URI("file:/with/absolute.jar"), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_multiplePaths() throws IOException, URISyntaxException {
        final File jarFile = new File("base/some.jar");
        final Manifest manifest = manifestClasspath("file:/with/absolute.jar relative.jar  relative/dir");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(3, classPaths.size());
        classPaths.contains(new URI("file:/with/absolute.jar"));
        classPaths.contains(new URI("file:base/relative.jar"));
        classPaths.contains(new URI("file:base/relative/dir"));
    }

    @Test
    public void testGetClassPathFromManifest_leadingBlanks() throws IOException {
        final File jarFile = new File("base/some.jar");
        final Manifest manifest = manifestClasspath(" relative.jar");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new File("base/relative.jar").toURI(), classPaths.iterator().next());
    }

    @Test
    public void testGetClassPathFromManifest_trailingBlanks() throws IOException {
        final File jarFile = new File("base/some.jar");
        final Manifest manifest = manifestClasspath("relative.jar ");

        final Set<URI> classPaths = Scanner.getClassPathFromManifest(jarFile, manifest);
        assertEquals(1, classPaths.size());
        assertEquals(new File("base/relative.jar").toURI(), classPaths.iterator().next());
    }


    private static void writeSelfReferencingJarFile(final File jarFile, final String... entries) throws IOException {
        final Manifest manifest = new Manifest();
        // Without version, the manifest is silently ignored. Ugh!
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, jarFile.getName());
        try (
                FileOutputStream fileOut = new FileOutputStream(jarFile);
                JarOutputStream jarOut = new JarOutputStream(fileOut)
        ) {
            for (String entry : entries) {
                final URL resource = ScannerTest.class.getClassLoader().getResource(entry);
                if (resource == null) {
                    continue;
                }
                jarOut.putNextEntry(new ZipEntry(entry));
                copy(resource, jarOut);
                jarOut.closeEntry();
            }
        }
    }

    private static void copy(final URL fromUrl, final OutputStream to) throws IOException {
        final URL url = notNull(fromUrl);
        final InputStream from = url.openStream();
        final byte[] buffer = new byte[1024];
        int len;
        while ((len = from.read(buffer)) != -1) {
            to.write(buffer, 0, len);
        }
    }

    private static Manifest manifestClasspath(String classpath) throws IOException {
        return manifest("Class-Path: " + classpath + "\n");
    }

    private static Manifest manifest(String content) throws IOException {
        final InputStream in = new ByteArrayInputStream(content.getBytes(Charset.forName("US-ASCII")));
        final Manifest manifest = new Manifest();
        manifest.read(in);
        return manifest;
    }

}
