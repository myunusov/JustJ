/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.sample.adm.service;

import org.maxur.jj.service.api.JJContext;
import org.maxur.jj.service.api.JJSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public class MazeApplication extends JJSystem {

    private final static Logger LOGGER = LoggerFactory.getLogger(MazeApplication.class);

    public MazeApplication(final JJContext context) {
        super(context);
    }

    @Override
    protected void onStop() {
        LOGGER.info("Stop Maze Application");
    }

    @Override
    protected void onStart() {
        LOGGER.info("Start Maze Application");
    }

    @Override
    protected void onInvalidCommand(final String token) {
        LOGGER.error("The Command '{}' is invalid", token);
    }

}
