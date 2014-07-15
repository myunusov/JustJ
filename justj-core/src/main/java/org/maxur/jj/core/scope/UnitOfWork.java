package org.maxur.jj.core.scope;

import org.maxur.jj.core.entity.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public class UnitOfWork {

    private final List<Event> events = new ArrayList<>();

    public final void add(final Event event) {
        if (isApplicable(event)) {
            events.add(event);
        }
    }

    private boolean isApplicable(final Event event) {
        return false;
    }

    public void commit() {

    }


}
