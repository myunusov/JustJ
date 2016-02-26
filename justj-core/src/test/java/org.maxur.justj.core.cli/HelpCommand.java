package org.maxur.justj.core.cli;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/26/2016</pre>
 */

@Key(value = "?")
@Key(value = "h")
public class HelpCommand extends CliMenuSpec.TestCommand {
    @Flag("all")
    boolean all;
}
