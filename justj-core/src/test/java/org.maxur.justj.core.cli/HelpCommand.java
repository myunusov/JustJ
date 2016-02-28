package org.maxur.justj.core.cli;

import org.maxur.justj.core.cli.annotation.Command;
import org.maxur.justj.core.cli.annotation.Flag;
import org.maxur.justj.core.cli.annotation.Key;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/26/2016</pre>
 */

@Key(value = "?")
@Key(value = "h")
@Command
public class HelpCommand extends CliMenuSpec.TestCommand {
    @Flag("all")
    boolean all;
}
