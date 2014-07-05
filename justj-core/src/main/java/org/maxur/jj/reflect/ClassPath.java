/*
 *    Copyright (c) 2014 Maxim Yunusov
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


import java.io.IOException;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.maxur.jj.utils.Reflection.getClassPathEntries;
import static org.maxur.jj.utils.Contracts.notNull;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/29/14</pre>
 */
public final class ClassPath {

    private final Set<ResourceInfo> resources;

    private ClassPath(final Set<ResourceInfo> resources) {
        this.resources = resources;
    }

    /**
     * Returns a {@code ClassPath} representing all classes and resources loadable from {@code
     * classloader} and its parent class loaders.
     * <p>
     * <p>Currently only {@link URLClassLoader} and only {@code file://} urls are supported.
     *
     * @throws IOException if the attempt to read class path resources (jar files or directories)
     *                     failed.
     */
    public static ClassPath from(final ClassLoader classloader) throws IOException {
        final Scanner scanner = new Scanner();
        for (Map.Entry<URI, ClassLoader> entry : getClassPathEntries(classloader).entrySet()) {
            scanner.scan(entry.getKey(), entry.getValue());
        }
        return new ClassPath(scanner.getResources());
    }

    /**
     * Returns all resources loadable from the current class path, including the class files of all
     * loadable classes but excluding the "META-INF/MANIFEST.MF" file.
     */
    public Set<ResourceInfo> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    /**
     * Returns all classes loadable from the current class path.
     */
    public Set<ClassInfo> getAllClasses() {
        return resources.stream()
                .filter(ResourceInfo::isClass)
                .map(resourceInfo -> (ClassInfo) resourceInfo)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all top level classes loadable from the current class path.
     */
    public Set<ClassInfo> getTopLevelClasses() {
        return resources.stream()
                .filter(ResourceInfo::isClass)
                .map(resourceInfo -> (ClassInfo) resourceInfo)
                .filter(ClassInfo::isTopLevel)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all top level classes whose package name is {@code packageName}.
     * Returns all top level classes whose package name starts with
     * {@code packageName} if {@code packageName} ends with '*'.
     */
    public Set<ClassInfo> getTopLevelClasses(final String packageName) {
        notNull(packageName);
        return resources.stream()
                .filter(ResourceInfo::isClass)
                .map(resourceInfo -> (ClassInfo) resourceInfo)
                .filter(ClassInfo::isTopLevel)
                .filter(makePredicate(packageName))
                .collect(Collectors.toSet());
    }

    private Predicate<? super ClassInfo> makePredicate(String packageName) {
        final Predicate<? super ClassInfo> result;
        if ('*' == packageName.charAt(packageName.length() - 1)) {
            final String packagePrefix = packageName.substring(0, packageName.length() - 1);
            result = classInfo -> classInfo.getName().startsWith(packagePrefix);
        } else {
            result = classInfo -> classInfo.getPackageName().equals(packageName);
        }
        return result;
    }




}
