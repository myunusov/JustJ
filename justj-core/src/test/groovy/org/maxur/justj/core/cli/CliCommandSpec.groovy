package org.maxur.justj.core.cli

import spock.lang.Specification


class CliCommandSpec extends Specification {

    def "Should returns name for class with @Command annotation"() {
        given: "new Command with @Command annotation"
        def command = new CliCommandInfo(VersionCommand);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "version";
    }

    def "Should returns name for class without @Command annotation but with Command postfix in name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = new CliCommandInfo(HelpCommand);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "help";

    }

    def "Should returns null for class without @Command annotation and without Command postfix in name"() {
        given: "new Command without @Command annotation and without Command postfix in name"
        def command = new CliCommandInfo(Invalid);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns null"
        assert name == null;
    }

    def "Should returns shortKey for class with @ShortKey annotation"() {
        given: "new Command with @ShortKey annotation"
        def command = new CliCommandInfo(HelpCommand);
        when: "Client requests key"
        def shortKey = command.key()
        then: "Command returns key"
        assert shortKey == "h" as char;
    }

    def "Should returns shortKey for class without @ShortKey annotation but with Command name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = new CliCommandInfo(VersionCommand);
        when: "Client requests key"
        def shortKey = command.key()
        then: "Command returns key"
        assert shortKey == "v" as char;
    }

    def "Should returns null for class without @ShortKey annotation and without Command name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = new CliCommandInfo(Invalid);
        when: "Client requests key"
        def shortKey = command.key()
        then: "Command returns key"
        assert shortKey == null;
    }

    @Command("version")
    static class VersionCommand extends CliCommand {
    }

    @Key("h")
    static class HelpCommand extends CliCommand {
    }

    static class Invalid extends CliCommand {
    }



}
