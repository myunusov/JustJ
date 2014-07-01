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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/27/14</pre>
 */
public final class DirClassScanner extends ClassScanner {


    private final String[] packages;

    DirClassScanner(final String... packages) {
        this.packages = packages;
    }

    @Override
    public List<Class<?>> getAllClassesFrom() throws ClassNotFoundException, IOException {

        if (packages.length == 0)  {
            final Enumeration<URL> resources = this.getClass().getClassLoader().getResources("");
            final List<File> directories = getDirectoriesBy(resources);
            return getClasses("", directories);
        }
        final List<Class<?>> result = new ArrayList<>();
        for (String packageName : packages) {
            final Enumeration<URL> resources = getResourcesFrom(packageName);
            final List<File> directories = getDirectoriesBy(resources);
            result.addAll(getClasses(packageName, directories));
        }
        return result;
    }

    private  Enumeration<URL> getResourcesFrom(final String packageName) throws IOException {
        return this.getClass().getClassLoader().getResources(resourceNameByPackageName(packageName));
    }

    private static List<File> getDirectoriesBy(final Enumeration<URL> resources) {
        final List<File> result = new ArrayList<>();
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            result.add(new File(resource.getFile()));
        }
        return result;
    }

    private List<Class<?>> getClasses(final String packageName, final List<File> dirs) throws ClassNotFoundException {
        final List<Class<?>> result = new ArrayList<>();
        for (final File directory : dirs) {
            result.addAll(findClasses(getAllFilesFromDir(directory), packageName));
        }
        return result;
    }


    private  List<Class<?>> findClasses(final File[] files, final String packageName) throws ClassNotFoundException {

        final List<Class<?>> result = new ArrayList<>();
        for (final File file : files) {
            final String jName = packageName + PACKAGE_SEPARATOR + file.getName();
            if (file.isDirectory()) {
                result.addAll(findClasses(getAllFilesFromDir(file), jName));
            } else if (isClass(file)) {
                result.add(Class.forName(classNameByFileName(jName)));
            }
        }
        return result;
    }

    private File[] getAllFilesFromDir(final File directory) {
        assert !directory.getName().contains(PACKAGE_SEPARATOR)
                : format("Invalid package directory name: '%s'. (It contains a dot)", directory.getName());
        if (!directory.exists()) {
            return new File[0];
        }
        final File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return new File[0];
        }
        return files;
    }


}
