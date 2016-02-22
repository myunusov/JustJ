package org.maxur.justj.service;

import org.maxur.justj.core.cli.CliCommand;
import org.maxur.justj.domain.InitPhase;
import org.maxur.justj.domain.JarConfig;
import org.maxur.justj.domain.PackagePhase;

import static org.maxur.justj.core.Slf4jLoggerAdapter.loggerFor;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class JustJCommand extends CliCommand<JustJOptions> {

    public JustJCommand(final String[] args) {
        super(args, JustJOptions.class, loggerFor(JustJCommand.class));
    }

    @Override
    public JustJCommand execute() {
        new InitPhase().execute();
        new PackagePhase(new JarConfig()).execute();
        return this;
    }

}
