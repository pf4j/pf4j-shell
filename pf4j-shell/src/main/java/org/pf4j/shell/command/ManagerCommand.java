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
import org.pf4j.shell.util.TextTable;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "manager")
public class ManagerCommand extends AbstractCommand {

    @Override
    public void run() {
        info();
    }

    @CommandLine.Command(name = "info", description = "Display information about plugin manager")
    public void info() {
        System.out.printf("Plugins roots: %s%n", getPluginManager().getPluginsRoots());
        System.out.printf("System version: %s%n", getPluginManager().getSystemVersion());
        System.out.printf("Runtime mode: %s%n", getPluginManager().getRuntimeMode());
    }

    @CommandLine.Command(name = "unresolved", description = "Display unresolved")
    public void unresolved() {
        List<PluginWrapper> plugins = getPluginManager().getUnresolvedPlugins();;
        TextTable<PluginWrapper> table = new TextTable<>();
        table.addColumn("Id", PluginWrapper::getPluginId);
        table.addColumn("Version", ManagerCommand::getVersion);
        table.addColumn("State", PluginWrapper::getPluginState);
        System.out.println(table.createString(plugins));
    }

    @CommandLine.Command(name = "extensions", description = "Display extensions for an extension point")
    public void extensions() {
        List<PluginWrapper> plugins = getPluginManager().getPlugins();
        for (PluginWrapper plugin : plugins) {
            System.out.printf("Plugin '%s': %s%n", plugin.getPluginId(), getPluginManager().getExtensionClassNames(plugin.getPluginId()));
        }
    }

    @CommandLine.Command(name = "which", description = "Display the plugin that loaded the given class")
    public void which(@CommandLine.Parameters String className) {
        Class<?> clazz = ClassLoaderUtils.getClass(className, getInstrumentation());
        if (clazz == null) {
            System.out.println("Class '" + className + "' not found");
            return;
        }

        PluginWrapper plugin = getPluginManager().whichPlugin(clazz);
        System.out.printf("Plugin %s%n", plugin.getPluginId());
    }

    private static String getVersion(PluginWrapper pluginWrapper) {
        return pluginWrapper.getDescriptor().getVersion();
    }

}
