package org.maxur.justj.core.cli;

public class CliCommandHandler {

    public static int handle(CliCommand command) {
        return new CliCommandHandler().process(command);
    }

    private int process(CliCommand command) {
        if (command.init().error().isPresent()) {
            return command.errorCode();
        } else {
            return  command.execute().errorCode();
        }
    }

}