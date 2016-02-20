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

package org.maxur.justj;

import com.jcabi.aether.Aether;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>19.02.2016</pre>
 */
public final class Launcher {

    private final static Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    private Launcher() {
    }

    public static void main(String[] args) throws DependencyResolutionException {
        File local = new File("./tmp/local-repository");
        Collection<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository(
                        "maven-central",
                        "default",
                        "http://repo1.maven.org/maven2/"
                )
        );
        Collection<Artifact> deps = new Aether(remotes, local).resolve(
                new DefaultArtifact("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "", "jar", "2.7.1"),
                "runtime"
        );
        deps.stream().map(Artifact::getFile).forEach(f -> LOGGER.info(f.getAbsolutePath()));
        new PackagePhase(new JarConfig()).execute();
    }

}
