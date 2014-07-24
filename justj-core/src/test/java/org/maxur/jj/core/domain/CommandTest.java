package org.maxur.jj.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.context.Context;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommandTest {

    @Test
    public void testExecute() throws Exception {
        new Command() {
            protected void run() {

            }
            public final void execute() {
                final Context context = Context.current().branch();
                context.inject(this);
                run();
                context.stop();
            }
        }.execute();

    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Command.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .verify();
    }

}