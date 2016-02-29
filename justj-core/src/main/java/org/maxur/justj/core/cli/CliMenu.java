package org.maxur.justj.core.cli;

import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.exception.CommandFabricationException;
import org.maxur.justj.core.cli.exception.InvalidCommandLineError;
import org.maxur.justj.core.cli.info.CliCommandInfo;
import org.maxur.justj.core.cli.info.OptionType;
import org.maxur.justj.core.cli.strategy.ArgumentCursor;
import org.maxur.justj.core.cli.strategy.CommandBuilder;
import org.maxur.justj.core.cli.strategy.OptionDetector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu implements OptionDetector {

    private final Set<CliCommandInfo> commands = new HashSet<>();

    private CliCommandInfo defaultCommand = null;

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
        final ArgumentCursor cursor = ArgumentCursor.cursor(args, this);
        final CommandBuilder builder = new CommandBuilder(this);
        while (cursor.hasNext()) {
            final Argument argument = cursor.next();
            builder.add(argument);
        }
        final Collection<CliCommandInfo> result = builder.commandsCandidates();
        switch (result.size()) {
            case 0:
                builder.add(defaultCommand);
            case 1:
                return builder.build();
            default:
                throw moreThanOneCommandException(args, result);
        }
    }

    @Override
    public boolean findInfoBy(final Argument argument, final OptionType type) {
        return commands.stream()
            .flatMap(c -> c.options().stream())
            .filter(o -> o.applicable(argument))
            .anyMatch(o -> o.type() == type);
    }

    @Override
    public Collection<CliCommandInfo> findCommandBy(final Argument argument, final OptionType type) {
        return commands.stream()
            .filter(o -> o.applicable(argument))
            .filter(o -> o.type() == OptionType.COMMAND)
            .collect(toSet());
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
