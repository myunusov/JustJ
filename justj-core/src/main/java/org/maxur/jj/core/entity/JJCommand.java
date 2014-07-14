package org.maxur.jj.core.entity;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public abstract class JJCommand {

    // TODO  toString, Id, GashCode, Equals

    private State state = State.CONTINUE_TRAVERSAL;

    public final void execute(final Object subject) {
        if (isApplicableTo(subject) && !State.STOP_TRAVERSAL.equals(state)) {
            process(subject);
        }
        if (!State.CONTINUE_TRAVERSAL.equals(state)) {
            return;
        }
        if (subject instanceof TreeNode) {
            final TreeNode node = (TreeNode) subject;
            for (Object child : node) {
                this.execute(child);
            }
        }
    }

    protected void stop() {
        state = State.STOP_TRAVERSAL;
    }

    protected void dontGoDeeper() {
        state = State.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
    }

    protected abstract void process(final Object subject);

    public boolean isApplicableTo(final Object subject) {
        return true;
    }

    private static enum State {
        CONTINUE_TRAVERSAL,
        CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER,
        STOP_TRAVERSAL
    }
}

