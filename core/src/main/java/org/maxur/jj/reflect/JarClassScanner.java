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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.net.URLDecoder.decode;
import static org.maxur.jj.utils.Arrays.toSet;
import static org.maxur.jj.utils.Strings.left;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/27/14</pre>
 */
public final class JarClassScanner extends ClassScanner {

    private final String path;

    private final Set<String> packages;

    JarClassScanner(final String path, String... packages) {
        this.path = path;
        this.packages = toSet(packages);
    }

    @Override
    public List<Class<?>> getAllClassesFrom() throws IOException, ClassNotFoundException {
        final JarFile jarFile = new JarFile(decode(path, "UTF-8"));
        final Enumeration<JarEntry> entries = jarFile.entries();
        final List<Class<?>> classes = new ArrayList<>();
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            if (isClass(jarEntry)) {
                final String className = classNameByFileName(jarEntry.getName());
                if (isPackagesMatch(className)) {
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

    private boolean isPackagesMatch(final String className) {
        if (packages.isEmpty()) {
            return true;
        }
        for (String packageName : packages) {
            if (packageName.endsWith(".*")) {
                final String left = left(packageName, '.');
                if (className.startsWith(left)) {
                    return true;
                }
            }
        }
        return false;
    }

}
