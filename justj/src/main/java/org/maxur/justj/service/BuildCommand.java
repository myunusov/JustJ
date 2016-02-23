package org.maxur.justj.service;

import org.maxur.justj.domain.InitPhase;
import org.maxur.justj.domain.JarConfig;
import org.maxur.justj.domain.PackagePhase;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
public class BuildCommand extends JustJCommand {

    @Override
    public JustJCommand execute() {
        new InitPhase().execute();
        new PackagePhase(new JarConfig()).execute();
        return this;
    }
}
