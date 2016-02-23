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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>19.02.2016</pre>
 */
public class PackagePhase implements Phase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackagePhase.class);

    public static final String JAR = ".jar";

    public static final int BUFFER_SIZE = 1024;

    private final JarConfig jarConfig;

    private final File baseFolder;

    private final File libFolder;

    public PackagePhase(JarConfig jarConfig) {
        this.jarConfig = jarConfig;
        this.baseFolder = new File(jarConfig.folder());
        this.libFolder = new File(jarConfig.libs());
    }

    @Override
    public void execute() {
      // TODO  Manifest manifest = jarConfig.manifest();
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(jarConfig.name() + JAR))) {
            add(baseFolder, target, baseFolder);
            add(libFolder, target, new File("./"));
        } catch (IOException e) {
            LOGGER.error("Jar file cannot be created", e);
        }
    }

    private void add(File source, JarOutputStream target, File base) throws IOException {
        insert(source, target, base);
        if (source.isDirectory()) {
            for (File nestedFile : source.listFiles())
                add(nestedFile, target, base);
        }
    }

    private void insert(File source, JarOutputStream target, File base) throws IOException {
        String name = makeNameFor(source, base);
        if (!name.isEmpty() || source.isFile()) {
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            write(source, target);
            target.closeEntry();
        }
    }

    private void write(File source, JarOutputStream target) throws IOException {
        if (source.isDirectory()) {
            return;
        }
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
        }
    }

    private String makeNameFor(File source, File base) {
        String relative = base.toURI().relativize(source.toURI()).getPath();
        String name = relative.replace("\\", "/");
        if (source.isDirectory() && !name.isEmpty() && !name.endsWith("/")) {
            name += "/";
        }
        return name;
    }
}
