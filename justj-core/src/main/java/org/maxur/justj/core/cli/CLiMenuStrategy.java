package org.maxur.justj.core.cli;

import java.util.Collection;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/25/2016</pre>
 */
public interface CLiMenuStrategy {

    boolean isOptionName(String arg);

    boolean isOptionKey(String arg);

    String extractOptionName(String arg);

    Collection<Character> extractOptionKeys(String arg);

    <T extends CliCommand> T bind(CliCommandInfo info, String[] args, T command) throws InvalidCommandArgumentException;
}
