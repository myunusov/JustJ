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

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.maxur.jj.utils.Strings.isBlank;

/**
* @author Maxim Yunusov
* @version 1.0
* @since <pre>1/31/14</pre>
*/
final class Scanner {

    private static final Logger logger = Logger.getLogger(ClassPath.class.getName());

    private final Comparator<ResourceInfo> RESOURCE_COMPARATOR = (o1, o2) -> o1.compareTo(o2);

    private final SortedSet<ResourceInfo> resources = new TreeSet<>(RESOURCE_COMPARATOR);

    private final Set<URI> scannedUris = new HashSet<>();

    SortedSet<ResourceInfo> getResources() {
        return resources;
    }

    void scan(final URI uri, final ClassLoader classloader) throws IOException {
        if (uri.getScheme().equals("file") && scannedUris.add(uri)) {
            scanFrom(new File(uri), classloader);
        }
    }

    void scanFrom(final File file, final ClassLoader classloader) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            scanDirectory(file, classloader);
        } else {
            scanJar(file, classloader);
        }
    }

    private void scanDirectory(File directory, ClassLoader classloader) throws IOException {
        scanDirectory(directory, classloader, "", new HashSet<>());
    }

    private void scanDirectory(
            final File directory,
            final ClassLoader classloader,
            final String packagePrefix,
            final Set<File> ancestors
    ) throws IOException {
        final File canonical = directory.getCanonicalFile();
        if (ancestors.contains(canonical)) {
            // A cycle in the filesystem, for example due to a symbolic link.
            return;
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            logger.warning("Cannot read directory " + directory);
            // IO error, just skip the directory
            return;
        }
        final Set<File> newAncestors = new HashSet<>(ancestors);
        newAncestors.add(canonical);
        for (File file : files) {
            final String resourceName = packagePrefix + file.getName();
            if (file.isDirectory()) {
                scanDirectory(file, classloader, resourceName + "/", newAncestors);
            } else if (!isManifest(resourceName)) {
                resources.add(ResourceInfo.of(resourceName, classloader));
            }
        }
    }

    private void scanJar(final File file, final ClassLoader classloader) throws IOException {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            // Not a jar file
            return;
        }
        try {
            for (URI uri : getClassPathFromManifest(file, jarFile.getManifest())) {
                scan(uri, classloader);
            }
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || isManifest(entry.getName())) {
                    continue;
                }
                resources.add(ResourceInfo.of(entry.getName(), classloader));
            }
        } finally {
            try {
                jarFile.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean isManifest(final String resourceName) {
        return resourceName.equals(JarFile.MANIFEST_NAME);
    }

    /**
     * Returns the class path URIs specified by the {@code Class-Path} manifest attribute, according
     * to <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes">
     * JAR File Specification</a>. If {@code manifest} is null, it means the jar file has no
     * manifest, and an empty set will be returned.
     */
    static Set<URI> getClassPathFromManifest(final File jarFile, final @Nullable Manifest manifest) {
        if (manifest == null) {
            return emptySet();
        }
        final Set<URI> result = new HashSet<>();
        final String classpathAttribute = manifest.getMainAttributes()
                .getValue(Attributes.Name.CLASS_PATH.toString());
        for (String path : splitAttributes(classpathAttribute)) {
            if (isBlank(path)) {
                continue;
            }
            try {
                result.add(getClassPathEntry(jarFile, path.trim()));
            } catch (URISyntaxException e) {
                // Ignore bad entry
                logger.warning("Invalid Class-Path entry: " + path);
            }
        }
        return result;
    }

    private static List<String> splitAttributes(final String attributes) {
        if (attributes == null) {
            return emptyList();
        }
        return stream(attributes.split(" "))
                .filter(attribute -> !isBlank(attribute))
                .collect(Collectors.toList());
    }

    /**
     * Returns the absolute uri of the Class-Path entry value as specified in
     * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes">
     * JAR File Specification</a>. Even though the specification only talks about relative urls,
     * absolute urls are actually supported too (for example, in Maven surefire plugin).
     */
    static URI getClassPathEntry(final File jarFile, final String path) throws URISyntaxException {
        final URI uri = new URI(path);
        if (uri.isAbsolute()) {
            return uri;
        } else {
            return new File(jarFile.getParentFile(), path.replace('/', File.separatorChar)).toURI();
        }
    }
}
