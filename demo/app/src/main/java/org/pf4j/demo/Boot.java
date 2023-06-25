/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pf4j.demo;

import org.apache.commons.lang.StringUtils;
import org.pf4j.PluginManager;
import org.pf4j.shell.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A boot class that start the demo.
 *
 * @author Decebal Suiu
 */
public class Boot {

    private static final Logger log = LoggerFactory.getLogger(Boot.class);

    public static void main(String[] args) {
        // print logo
        printLogo();

        // create the plugin manager
        PluginManager pluginManager = new DemoPluginManager();

        // create shell
        Shell shell = new Shell(pluginManager);

        // start shell
        shell.start();
    }

    private static void printLogo() {
        log.info(StringUtils.repeat("#", 40));
        log.info(StringUtils.center("PF4J-DEMO", 40));
        log.info(StringUtils.repeat("#", 40));
    }

}
