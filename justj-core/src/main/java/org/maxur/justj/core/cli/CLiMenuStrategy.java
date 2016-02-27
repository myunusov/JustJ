package org.maxur.justj.core.cli;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/25/2016</pre>
 */
public interface CLiMenuStrategy {

    <T extends CliCommand> T bind(CliCommandInfo info, String[] args) throws CommandFabricationException;
}
