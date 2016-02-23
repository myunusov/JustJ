/*
 * Copyright (c) 2016 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.justj.domain;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>19.02.2016</pre>
 */
public class JarConfig {

    private String version = "1.0";

    private String name = "./target/output";

    private String folder = "./target/classes";

    private String libs = "./lib";

    private String mainClass = "org.maxur.justj.Launcher";

    public String version() {
        return version;
    }

    public String name() {
        return name;
    }

    public String folder() {
        return folder;
    }

    public String libs() {
        return libs;
    }

    public String mainClass() {
        return mainClass;
    }

    public Manifest manifest() {
        Manifest manifest = new Manifest();
        final Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, version());
        attributes.put(Attributes.Name.MAIN_CLASS, mainClass());
        return manifest;
    }
}
