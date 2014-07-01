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

package org.maxur.jj.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;

import static org.maxur.jj.utils.Strings.isBlank;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/27/14</pre>
 */
public abstract class ClassScanner {

    protected static final String CLASS_EXT = ".class";

    public static final String PACKAGE_SEPARATOR = ".";

    public static final char SEPARATOR_CHAR = '/';

    public static final String SEPARATOR = "" + SEPARATOR_CHAR;

    public abstract List<Class<?>> getAllClassesFrom() throws IOException, ClassNotFoundException;

    /**
     * Convert all periods (.) to slashes (/)
     * see docs.oracle.com/javase/7/docs/technotes/guides/lang/resources.html
     *
     * @param packageName fully qualified name of the package
     * @return name of a resource
     */
    protected String resourceNameByPackageName(final String packageName) {
        return isBlank(packageName) ? SEPARATOR : packageName.replace('.', SEPARATOR_CHAR);
    }

    protected String classNameByFileName(final String className) {
        final String result = className.replace('/', '.').replace(CLASS_EXT, "");
        return '.' == result.charAt(0) ? result.substring(1) : result;
    }

    protected boolean isClass(final File file) {
        return file.isFile() && file.getName().endsWith(CLASS_EXT);
    }

    protected boolean isClass(final JarEntry jarEntry) {
        return !jarEntry.isDirectory() && jarEntry.getName().endsWith(CLASS_EXT);
    }
}
