/*
 * Copyright (C) 2023-present the original author or authors.
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
package org.pf4j.shell.command;

import org.pf4j.PluginWrapper;
import org.pf4j.shell.util.ClassLoaderUtils;
import picocli.CommandLine;

import java.net.URL;
import java.util.List;
import java.util.Set;

@CommandLine.Command(name = "classloader")
public class ClassLoaderCommand extends AbstractCommand {

    @Override
    public void run() {
        info();
    }

    @CommandLine.Command(name = "info", description = "Display information about classloaders")
    public void info() {
        ClassLoaderUtils.getAllClassLoader(getInstrumentation()).forEach(System.out::println);
    }

    @CommandLine.Command(name = "plugins", description = "Display information about plugins classloaders")
    public void plugins() {
        List<PluginWrapper> plugins = getPluginManager().getPlugins();
        for (PluginWrapper plugin : plugins) {
            System.out.printf("%s -> %s%n", plugin.getPluginId(), getPluginManager().getPluginClassLoader(plugin.getPluginId()));
        }
    }

    @CommandLine.Command(name = "urls", description = "Display classloader urls")
    public void urls(@CommandLine.Parameters String pluginId) {
        ClassLoader classLoader = getPluginManager().getPluginClassLoader(pluginId);
        URL[] urls = ClassLoaderUtils.getUrls(classLoader);
        for (URL url : urls) {
            System.out.printf("%s%n", url);
        }
    }

    @CommandLine.Command
    public void loadedClasses(@CommandLine.Parameters String pluginId) {
        ClassLoader classLoader = getPluginManager().getPluginClassLoader(pluginId);
        Set<String> loadedClasses = ClassLoaderUtils.getLoadedClasses(classLoader, getInstrumentation());
        System.out.println(loadedClasses);
    }

}
