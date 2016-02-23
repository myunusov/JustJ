package org.maxur.justj.service;

import org.maxur.justj.core.Logger;
import org.maxur.justj.core.cli.Option;
import org.maxur.justj.core.cli.Slf4jAndLogbackLoggerAdapter;

import java.io.*;

/**
 * TODO see maven
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
public class ShowHelpCommand extends JustJCommand {

    private static final Logger LOGGER = Slf4jAndLogbackLoggerAdapter.loggerFor(ShowHelpCommand.class);

    @Option("h")
    private boolean help;

    @Override
    public JustJCommand execute() {
        try(
                InputStream stream = ShowHelpCommand.class.getResourceAsStream("/help.txt");
                Reader reader = new InputStreamReader(stream);
                BufferedReader breader = new BufferedReader(reader)
        ) {
            String line = breader.readLine();
            while (line != null) {
                logger().info(line);
                line = breader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return this;
    }


}
