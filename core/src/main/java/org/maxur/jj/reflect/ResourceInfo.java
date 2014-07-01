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

import java.net.URL;

import static org.maxur.jj.utils.Contracts.notNull;

/**
 * Represents a class path resource that can be either a class file or any other resource file
 * loadable from the class path.
 */
public class ResourceInfo implements Comparable<ResourceInfo> {

    private final String resourceName;

    private final ClassLoader loader;


    static ResourceInfo of(final String resourceName, final ClassLoader loader) {
        if (ClassInfo.isApplicable(resourceName)) {
            return new ClassInfo(resourceName, loader);
        } else {
            return new ResourceInfo(resourceName, loader);
        }
    }

    ResourceInfo(final String resourceName, final ClassLoader loader) {
        this.resourceName = notNull(resourceName);
        this.loader = notNull(loader);
    }

    /**
     * Returns the url identifying the resource.
     */
    public final URL url() {
        return notNull(loader.getResource(resourceName), "Failed to load resource: %s", resourceName);
    }

    /**
     * Returns the fully qualified name of the resource. Such as "com/mycomp/foo/bar.txt".
     */
    public final String getResourceName() {
        return resourceName;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    @Override
    public int hashCode() {
        return resourceName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ResourceInfo) {
            final ResourceInfo that = (ResourceInfo) obj;
            return resourceName.equals(that.resourceName) && loader == that.loader;
        }
        return false;
    }

    public String getId() {
        return resourceName;
    }

    public int compareTo(final ResourceInfo o) {   // TODO
        if (o == null) {
            return -1;
        }
        return getId().compareTo(o.getId());
    }

    public boolean isClass() {
        return false;
    }

}
