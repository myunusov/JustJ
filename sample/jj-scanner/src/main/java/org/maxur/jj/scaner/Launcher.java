package org.maxur.jj.scaner;/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.maxur.jj.utils.Reflection.getAllClassesFrom;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/24/14</pre>
 */
public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(final String[] args) {

        // TODO Extract Path from arg Args

        final String path = ".";
        try {
            LOGGER.info("Start Application");
            final List<Class<?>> classes = getAllClassesFrom(path);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }







}
