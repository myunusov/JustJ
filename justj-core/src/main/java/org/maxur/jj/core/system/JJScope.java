package org.maxur.jj.core.system;

import org.maxur.jj.core.config.Context;
import org.maxur.jj.core.entity.JJCommand;
import org.maxur.jj.core.entity.JJEvent;
import org.maxur.jj.core.entity.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public class JJScope<T extends Context> extends TreeNode<JJScope> {

    private final List<UnitOfWork> works = new ArrayList<>();

    private T context;

    private boolean isActive;

    public JJScope() {
        active();
    }

    protected void setContext(T context) {
        this.context = context;
    }

    public final void add(final JJEvent event) {
        for (UnitOfWork work : works) {
            work.add(event);
        }
    }

    public final void add(final JJScope scope) {
        super.add(scope);
        scope.setParent(this);
    }

    private void setParent(JJScope scope) {
        scope.context.setParent(this.context);
    }

    private void commit() {
        for (UnitOfWork work : works) {
            work.commit();
        }
    }

    protected void beforeStop() {
    }


    protected void afterStart() {
    }

    protected T context() {
        return context;
    }

    public final void active() {
        isActive = true;
        afterStart();
    }

    public final void passive() {
        beforeStop();
        commit();
        context = null;
        isActive = false;
    }

    public final boolean isActive() {
        return isActive;
    }

    public void tell(final JJCommand command) {
        command.execute(this);
    }

    public static JJCommand exitCmd() {
        return new ExitCommand();
    }

    private static class ExitCommand extends JJCommand<JJScope> {
        @Override
        protected void process(final JJScope scope ) {
            scope.passive();
        }
    }
}
