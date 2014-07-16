package org.maxur.jj.core.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.maxur.jj.core.entity.Command.command;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandTest {

    @Spy
    Command<DummyObject> command1 = new Command<DummyObject>() {
        @Override
        protected void process(DummyObject subject) {
        }
    };

    @Spy
    Command command2 = new Command() {
        @Override
        protected void process(Object subject) {
        }
    };

    @Spy
    Command<TreeNode> command3 = new Command<TreeNode>() {
        @Override
        protected void process(TreeNode subject) {
        }
    };

    @Spy
    private DummyObject subject = new DummyObject();


    @Test
    public void testRunWithTypeLikeToGenericType() throws Exception {
        final DummyObject subject = new DummyObject();
        command1.execute(subject);
        verify(command1).process(subject);
    }

    @Test
    public void testRunWithTypeInheritedGenericType() throws Exception {
        final ChildDummyObject subject = new ChildDummyObject();
        command1.execute(subject);
        verify(command1).process(subject);
    }

    @Test
    public void testNotRunWithTypeUnlikeToGenericType() throws Exception {
        final OtherDummyObject subject = new OtherDummyObject();
        command1.execute(subject);
        verify(command1, times(0)).process(any());
    }

    @Test
    public void testRunWithoutGenericType() throws Exception {
        final Object subject = new Object();
        command2.execute(subject);
        //noinspection unchecked
        verify(command2, times(1)).process(subject);
    }

    @Test
    public void testNotRunWithNotApplicableSubject() throws Exception {
        final DummyObject subject = new DummyObject();
        when(command1.isApplicableTo(subject)).thenReturn(false);
        command1.execute(subject);
        verify(command1, times(0)).process(any());
    }

    @Test
    public void testMakeCommandWithFunction() throws Exception {
        Command<DummyObject> command = command(DummyObject::method);
        command.execute(subject);
        verify(subject).method();
    }

    @Test
    public void testBatchCommand() throws Exception {
        Command<DummyObject> command = command(DummyObject::method);
        Command<DummyObject> batch = Command.<DummyObject>batch()
                .add(DummyObject::method)
                .add(command);
        batch.execute(subject);
        verify(subject, times(2)).method();
    }


    @Test
    public void testVisitTreeNode() throws Exception {
        final TreeNode treeNode = new TreeNode();
        treeNode.accept(command3);
        verify(command3, times(1)).process(treeNode);
    }

    @Test
    public void testBatchVisitTreeNode() throws Exception {
        // TODO
    }


    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Command.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .verify();
    }


    private static class DummyObject {
        public void method() {

        }
    }
    private static class ChildDummyObject extends DummyObject {
    }
    private static class OtherDummyObject {
    }

}