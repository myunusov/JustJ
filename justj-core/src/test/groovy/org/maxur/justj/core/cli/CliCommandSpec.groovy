package org.maxur.justj.core.cli

import spock.lang.Specification


class CliCommandSpec extends Specification {

    def "Should returns name for class with @Command annotation"() {
        given: "new Command with @Command annotation"
        def command = new VersionCommand();
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "version";
    }

    def "Should returns name for class without @Command annotation but with Command postfix in name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = new HelpCommand();
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "help";

    }

    def "Should returns null for class without @Command annotation and without Command postfix in name"() {
        given: "new Command without @Command annotation and without Command postfix in name"
        def command = new Invalid();
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns null"
        assert name == null;
    }

    @Command("version")
    static class VersionCommand extends CliCommand {
    }

    static class HelpCommand extends CliCommand {
    }

    static class Invalid extends CliCommand {
    }



}
