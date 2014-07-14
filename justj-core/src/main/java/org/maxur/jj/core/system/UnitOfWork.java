package org.maxur.jj.core.system;

import org.maxur.jj.core.entity.JJEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public class UnitOfWork {

    private final List<JJEvent> events = new ArrayList<>();

    public final void add(final JJEvent event) {
        if (isApplicable(event)) {
            events.add(event);
        }
    }

    private boolean isApplicable(final JJEvent event) {
        return false;
    }

    public void commit() {

    }


}
