package org.maxur.justj.domain;

import com.jcabi.aether.Aether;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class InitPhase {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitPhase.class);

    public void execute() {
        File local = new File("./tmp/local-repository");
        Collection<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository(
                        "maven-central",
                        "default",
                        "http://repo1.maven.org/maven2/"
                )
        );

        try {
            Collection<Artifact> deps = new Aether(remotes, local).resolve(
                    new DefaultArtifact(
                            "com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "", "jar", "2.7.1"
                    ),
                    "runtime"
            );
            deps.stream().map(Artifact::getFile).forEach(this::copyToLib);
        } catch (DependencyResolutionException e) {
            LOGGER.error("Dependency cannot be loaded", e);
        }
    }

    private void copyToLib(final File file) {
        LOGGER.info(file.getAbsolutePath());
        final String libPath = "./tmp/lib";
        final File dir = new File(libPath);
        copyFileToDir(file, dir);

    }

    private static void copyFileToDir(File file, File dir) {
        if (!(dir.exists() || dir.mkdir())) {
            LOGGER.error(format("Directory '%s' cannot be make", dir.getName()));
            return;
        }
        try (
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(dir.getCanonicalPath() + "/" + file.getName())
        ) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            LOGGER.error(format("File '%s' cannot be copy to lib dir", file.getName()), e);
        }
    }

}
