package org.maxur.justj.core.cli.strategy;

import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.argument.ArgumentCursor;
import org.maxur.justj.core.cli.exception.CommandFabricationException;
import org.maxur.justj.core.cli.info.CliCommandInfo;

import java.util.HashSet;
import java.util.Set;

// TODO remove it class. Strategy move to cursor. Loop to Menu (with Check option type and bind).
public class CLiMenuPosixStrategy implements CLiMenuStrategy {

    @Override
    public Set<CliCommandInfo> selectCommands(
            final String[] args,
            final Set<CliCommandInfo> commands
    ) throws CommandFabricationException {
        final Set<CliCommandInfo> result = new HashSet<>();
        final ArgumentCursor cursor = ArgumentCursor.cursor(args);
        while (cursor.hasNext()) {
            final Argument argument = cursor.nextOption();
            for (CliCommandInfo command : commands) {
                if (command.applicable(argument)) {
                    result.add(command);
                }
            }
        }
        return result;
    }

    @Override
    public <T> T bind(
        final CliCommandInfo info,
        final String[] args
    ) throws CommandFabricationException {
        T command = info.instance();
        final ArgumentCursor cursor = ArgumentCursor.cursor(args);
        while (cursor.hasNext()) {
            info.bind(command, cursor.nextOption());
        }
        return command;
    }

}