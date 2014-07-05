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

import java.util.Set;

import static org.maxur.jj.utils.Arrays.toSet;
import static org.maxur.jj.utils.Strings.left;

/**
 * Represents a class that can be loaded through {@link #load}.
 */
public final class ClassInfo extends ResourceInfo {

    private static final String CLASS_FILE_NAME_EXTENSION = ".class";

    public static final char PATH_SEPARATOR = '/';

    public static final char PACKAGE_SEPARATOR = '.';

    public static final Set<Character> DIGITS = toSet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private final String className;

    ClassInfo(final String resourceName, final ClassLoader loader) {
        super(resourceName, loader);
        this.className = getClassName(resourceName);
    }

    static boolean isApplicable(final String resourceName) {
        return resourceName.endsWith(CLASS_FILE_NAME_EXTENSION);
    }

    static String getClassName(final String filename) {
        int classNameEnd = filename.length() - CLASS_FILE_NAME_EXTENSION.length();
        return filename.substring(0, classNameEnd).replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
    }

    /**
     * Returns the package name of the class, without attempting to load the class.
     * <p>
     * <p>Behaves identically to {@link Package#getName()} but does not require the class (or
     * package) to be loaded.
     */
    public String getPackageName() {
        return left(className, PACKAGE_SEPARATOR);
    }

    /**
     * Returns the simple name of the underlying class as given in the source code.
     * <p>
     * <p>Behaves identically to {@link Class#getSimpleName()} but does not require the class to be
     * loaded.
     */

    public String getSimpleName() {
        int lastDollarSign = className.lastIndexOf('$');
        if (lastDollarSign != -1) {
            final String innerClassName = className.substring(lastDollarSign + 1);
            // local and anonymous classes are prefixed with number (1,2,3...), anonymous classes are
            // entirely numeric whereas local classes have the user supplied name as a suffix
            return trimLeadingFrom(innerClassName);
        }
        final  String packageName = getPackageName();
        if (packageName.isEmpty()) {
            return className;
        }
        // Since this is a top level class, its simple name is always the part after package name.
        return className.substring(packageName.length() + 1);
    }

    public String trimLeadingFrom(final CharSequence sequence) {
        int len = sequence.length();
        for (int first = 0; first < len; first++) {
            if (!DIGITS.contains(sequence.charAt(first))) {
                return sequence.subSequence(first, len).toString();
            }
        }
        return "";
    }

    /**
     * Returns the fully qualified name of the class.
     * <p>
     * <p>Behaves identically to {@link Class#getName()} but does not require the class to be
     * loaded.
     */

    public String getName() {
        return className;
    }

    /**
     * Loads (but doesn't link or initialize) the class.
     *
     * @throws LinkageError when there were errors in loading classes that this class depends on.
     *                      For example, {@link NoClassDefFoundError}.
     */
    public Class<?> load() {
        try {
            return getLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // Shouldn't happen, since the class name is read from the class path.
            throw new IllegalStateException(e);
        }
    }

    public String getId() {
        return className;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    public boolean isTopLevel() {
        return this.className.indexOf('$') == -1;
    }
}
