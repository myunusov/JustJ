package org.maxur.justj.core.cli.strategy;

import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.exception.CommandFabricationException;
import org.maxur.justj.core.cli.info.CliCommandInfo;
import org.maxur.justj.core.cli.info.OptionType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/29/2016</pre>
 */
public class CommandBuilder {

    private final OptionDetector detector;

    private final Set<Argument> arguments = new HashSet<>();

    private final Set<CliCommandInfo> commands = new HashSet<>();

    public CommandBuilder(final OptionDetector detector) {
        this.detector = detector;
    }

    public CommandBuilder add(final Argument argument) {
        this.arguments.add(argument);
        commands.addAll(detector.findCommandBy(argument, OptionType.COMMAND));
        return this;
    }

    public CommandBuilder add(final CliCommandInfo command) {
        commands.add(command);
        return this;
    }

    public Set<CliCommandInfo> commandsCandidates() {
        return commands;
    }

    public <T>  T build() throws CommandFabricationException {
        final CliCommandInfo info = commands.iterator().next();
        if (info == null) {
            return null;
        }
        T command = info.instance();
        for (Argument argument : arguments) {
            if (argument.isOperator()) {
                info.bind(command, argument);
            }
        }
        return command;
    }


}
