package org.maxur.justj.core.cli;

public class CLiMenuPosixStrategy implements CLiMenuStrategy {

    @Override
    public <T extends CliCommand> T bind(
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