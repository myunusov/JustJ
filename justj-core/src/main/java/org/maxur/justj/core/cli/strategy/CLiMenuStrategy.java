package org.maxur.justj.core.cli.strategy;

import org.maxur.justj.core.cli.exception.CommandFabricationException;
import org.maxur.justj.core.cli.info.CliCommandInfo;

import java.util.Set;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/25/2016</pre>
 */
public interface CLiMenuStrategy {

    Set<CliCommandInfo> selectCommands(String[] args, Set<CliCommandInfo> commands) throws CommandFabricationException;

    <T> T bind(CliCommandInfo info, String[] args, Set<CliCommandInfo> commands) throws CommandFabricationException;
}
