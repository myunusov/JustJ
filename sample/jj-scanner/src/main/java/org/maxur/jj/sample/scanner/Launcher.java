package org.maxur.jj.sample.scanner;

import org.maxur.jj.core.entity.JJCommand;
import org.maxur.jj.core.system.JJSystem;
import org.slf4j.Logger;

import static org.maxur.jj.core.system.JJSystem.configBy;
import static org.maxur.jj.core.system.JJSystem.system;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 05.07.2014
 */
public final class Launcher {

    public static void main(final String[] args) {
        final JJSystem system = system(configBy(MazeConfig.class));
        system
                .tell(new Start(args))    // TODO start Application context
           //     .tell(new Login())      // TODO start Session context
           //     .tell(new GoTo(MazeMainView.class))  // TODO start View context
        ;

        while (system.isActive()) {           // TODO move to system ?
            final JJCommand command = getCommand();
            if (command != null) {
                command.execute(system);               // TODO start Request context
            }
        }
    }

    private static JJCommand getCommand() {
        return new ExitCommand();  // TODO
    }

    private static class Start extends JJCommand {
        private static final Logger LOGGER = getLogger(Start.class);


        private final String[] args;

        public Start(final String[] args) {
            this.args = args;
        }

        @Override
        protected void process(final Object subject) {
        }

        @Override
        public boolean isApplicableTo(final Object subject) {
            return subject instanceof JJSystem;
        }
    }

    private static class ExitCommand extends JJCommand {
        private static final Logger LOGGER = getLogger(ExitCommand.class);

        @Override
        protected void process(final Object subject) {
            final JJSystem system = (JJSystem) subject;
            system.passive();
        }

        @Override
        public boolean isApplicableTo(final Object subject) {
            return subject instanceof JJSystem;
        }
    }

}