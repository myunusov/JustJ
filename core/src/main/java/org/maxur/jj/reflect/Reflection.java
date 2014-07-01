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

import org.maxur.jj.utils.ClassScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.String.format;
import static java.net.URLDecoder.decode;
import static org.maxur.jj.utils.Arrays.contains;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/27/14</pre>
 */
public final class Reflection {

    private Reflection() {
        //empty - prevent construction
    }

    public static List<Class<?>> getAllClassesFrom(final String path, final String... packages)
            throws ClassNotFoundException, IOException {
        final File classPathFile = new File(decode(path, "UTF-8"));
        final ClassScanner scanner;
        if (classPathFile.isDirectory()) {
            scanner = new DirClassScanner(packages);
        } else if (classPathFile.isFile()) {
            scanner = new JarClassScanner(path, packages);
        } else {
            throw new IllegalArgumentException(format("Can not find classes. The Path %s is invalid", path));
        }
        return scanner.getAllClassesFrom();
    }


    public static void loadJarBy(final String pathName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, MalformedURLException {
        final File file = new File(pathName);
        final URLClassLoader systemClassLoader = (URLClassLoader) getSystemClassLoader();
        final URL url = file.toURI().toURL();
        if (contains(systemClassLoader.getURLs(), url)) {
            return;
        }
        //noinspection unchecked
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);

        method.invoke(systemClassLoader, url);
    }


    public static Map<URI, ClassLoader> getClassPathEntries(final ClassLoader classloader) {
        final Map<URI, ClassLoader> result = new LinkedHashMap<>();
        // Search parent first, since it's the order ClassLoader#loadClass() uses.
        final ClassLoader parent = classloader.getParent();
        if (parent != null) {
            result.putAll(getClassPathEntries(parent));
        }
        if (classloader instanceof URLClassLoader) {
            for (URL entry : ((URLClassLoader) classloader).getURLs()) {
                final URI uri;
                try {
                    uri = entry.toURI();
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
                if (!result.containsKey(uri)) {
                    result.put(uri, classloader);
                }
            }
        }
        return result;
    }


}
