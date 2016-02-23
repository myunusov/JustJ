package org.maxur.justj.service;

import org.maxur.justj.core.Logger;
import org.maxur.justj.core.cli.Option;
import org.maxur.justj.core.cli.Slf4jAndLogbackLoggerAdapter;

import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;

/**
 * TODO see maven
 * @author myunusov
 * @version 1.0
 * @since <pre>22.02.2016</pre>
 */
@SuppressWarnings("WeakerAccess")
public class ShowVersionCommand extends JustJCommand {

    private static final Logger LOGGER = Slf4jAndLogbackLoggerAdapter.loggerFor(ShowVersionCommand.class);

    @Option("v")
    private boolean version;

    @Override
    public JustJCommand execute() {
        logger().info(format("Maxur JusJ Builder. Version %s", version()));
        return this;
    }

    private String version() {
        String result  = getClass().getPackage().getImplementationVersion();
        if (result == null) {
            Properties prop = new Properties();
            try {
                prop.load(ShowVersionCommand.class.getResourceAsStream("/META-INF/MANIFEST.MF"));
                result = prop.getProperty("Implementation-Version");
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return result;
    }

}
