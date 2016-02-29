package org.maxur.justj.core.cli.strategy;

import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.info.CliCommandInfo;
import org.maxur.justj.core.cli.info.OptionType;

import java.util.Collection;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/29/2016</pre>
 */
public interface OptionDetector {

    boolean findInfoBy(Argument argument, OptionType type);

    Collection<CliCommandInfo> findCommandBy(Argument argument, OptionType type);
}
