package org.maxur.justj.service;

import org.maxur.justj.core.Logger;
import org.maxur.justj.core.cli.CliMenu;
import org.maxur.justj.core.cli.CliCommand;
import org.maxur.justj.core.cli.Option;
import org.maxur.justj.core.cli.OptionsProcessingException;
import org.maxur.justj.core.cli.PosixArgumentsMapper;

import static org.maxur.justj.core.cli.Slf4jAndLogbackLoggerAdapter.loggerFor;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
@SuppressWarnings("unused")
public class JustJMenu extends CliMenu<CliCommand> {

    private static final Logger LOGGER = loggerFor(JustJCommand.class);

    @Option("q")
    private boolean quiet;

    @Option("h")
    private boolean help;

    @Option("v")
    private boolean version;

    @Option("x")
    private boolean debug;

    private static final PosixArgumentsMapper<JustJMenu> MAPPER = new PosixArgumentsMapper<>(JustJMenu.class);

    public static CliCommand commandBy(final String[] args) {
        try {
            final JustJMenu menu = MAPPER.readValue(args);
            return menu.buildCommandBy(args);
        } catch (OptionsProcessingException e) {
            LOGGER.error(e.getMessage());
            return new ShowHelpCommand();
        }
    }

    private CliCommand buildCommandBy(String[] args) throws OptionsProcessingException {
        if (version) {
            return makeCommand(args, ShowVersionCommand.class);
        } else if (help) {
            return makeCommand(args, ShowHelpCommand.class);
        } else {
            return makeCommand(args, BuildCommand.class);
        }
    }

}
