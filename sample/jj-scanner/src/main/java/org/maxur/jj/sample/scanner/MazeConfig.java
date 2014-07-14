package org.maxur.jj.sample.scanner;

import org.maxur.jj.core.config.Configuration;

import static org.maxur.jj.core.entity.Role.HOME_VIEW;
import static org.maxur.jj.core.entity.Role.SYSTEM;

/**
 * @author Maxim Yunusov
 * @version 1.0 11.07.2014
 */
public final class MazeConfig extends Configuration {

    @Override
    public void config() {
        bind(SYSTEM).to(MazeApplication.class);
        bind(HOME_VIEW).to(MazeMainView.class);
    }

}
