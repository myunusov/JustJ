package org.maxur.justj.core.cli;

import org.maxur.justj.core.cli.strategy.CLiMenuStrategy;
import org.maxur.justj.core.cli.exception.CommandFabricationException;
import org.maxur.justj.core.cli.exception.InvalidCommandLineError;
import org.maxur.justj.core.cli.info.CliCommandInfo;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu {

    private final Set<CliCommandInfo> commands = new HashSet<>();

    private CliCommandInfo defaultCommand = null;

    private final CLiMenuStrategy strategy;

    public CliMenu(final CLiMenuStrategy strategy) {
        this.strategy = strategy;
    }

    @SafeVarargs
    public final void register(final Class<Object>... classes) {
        stream(classes)
                .map(CliCommandInfo::commandInfo)
                .forEach(commands::add);

        defaultCommand = findDefaultCommand();
    }

    private CliCommandInfo findDefaultCommand() {
        final List<CliCommandInfo> defaultCommands = commands.stream()
                .filter(CliCommandInfo::isDefault)
                .collect(Collectors.toList());

        if (defaultCommand != null) {
            defaultCommands.add(defaultCommand);
        }

        switch (defaultCommands.size()) {
            case 0:
                return null;
            case 1:
                return defaultCommands.get(0);
            default:
                throw moreThanOneDefaultCommandsError(defaultCommands);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T makeCommand(final String[] args) throws CommandFabricationException {
        final Collection<CliCommandInfo> result = strategy.selectCommands(args, commands);
        CliCommandInfo command;
        switch (result.size()) {
            case 0:
                if (defaultCommand == null) {
                    return null;
                }
                command = defaultCommand;
                break;
            case 1:
                command = result.iterator().next();
                break;
            default:
                throw moreThanOneCommandException(args, result);
        }
        return strategy.bind(command, args);
    }

    private InvalidCommandLineError moreThanOneCommandException(String[] args, Collection<CliCommandInfo> commands) {
        return new InvalidCommandLineError(
                Arrays.toString(args),
                format("You try to call commands %s simultaneously", getCommandsAsString(commands))
        );
    }

    private IllegalStateException moreThanOneDefaultCommandsError(List<CliCommandInfo> defaultCommands) {
        return new IllegalStateException(
                format("You try to register few commands (%s) as default", getCommandsAsString(defaultCommands))
        );
    }

    private String getCommandsAsString(Collection<CliCommandInfo> commands) {
        final Iterator<CliCommandInfo> iterator = commands.iterator();
        String result = "'" + iterator.next().name() + "'";
        while (iterator.hasNext()) {
            CliCommandInfo command = iterator.next();
            String separator = iterator.hasNext() ? ", " : " and ";
            result += separator + "'" + command.name() + "'";
        }
        return result;
    }


}
