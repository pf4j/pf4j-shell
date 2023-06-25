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
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "plugin")
public class PluginCommand extends AbstractCommand {

    @CommandLine.Parameters(description = "The plugin identifier")
    private String pluginId;

    @Override
    public void run() {
        info();
    }

    @CommandLine.Command(name = "load", description = "Load plugin")
    public void load(@CommandLine.Parameters Path pluginPath) {
        System.out.printf("Load plugin %s", pluginId);
        getPluginManager().loadPlugin(pluginPath);
    }

    @CommandLine.Command(name = "unload", description = "Unload plugin")
    public void unload() {
        System.out.printf("Unload plugin %s", pluginId);
        getPluginManager().unloadPlugin(pluginId);
    }

    @CommandLine.Command(name = "start", description = "Start plugin")
    public void start() {
        System.out.printf("Start plugin %s", pluginId);
        getPluginManager().startPlugin(pluginId);
    }

    @CommandLine.Command(name = "stop", description = "Stop plugin")
    public void stop() {
        System.out.printf("Stop plugin %s", pluginId);
        getPluginManager().stopPlugin(pluginId);
    }

    @CommandLine.Command(name = "info", description = "Display information about plugin")
    public void info() {
        PluginWrapper plugin = getPlugin();
        System.out.printf("Id : %s%n", plugin.getPluginId());
        System.out.printf("Version : %s%n", plugin.getDescriptor().getVersion());
        System.out.printf("State : %s\n", plugin.getPluginState());
        System.out.printf("Path : %s\n", plugin.getPluginPath());
        System.out.printf("PluginClass : %s\n", plugin.getDescriptor().getPluginClass());
        System.out.printf("Description : %s%n", plugin.getDescriptor().getPluginDescription());
        System.out.printf("Provider : %s\n", plugin.getDescriptor().getProvider());
        System.out.printf("License : %s\n", plugin.getDescriptor().getLicense());
        System.out.printf("Requires : %s\n", plugin.getDescriptor().getRequires());
        System.out.printf("Dependencies : %s\n", plugin.getDescriptor().getDependencies());
        System.out.printf("ClassLoader : %s\n", plugin.getPluginClassLoader());
    }

    private PluginWrapper getPlugin() {
        return getPluginManager().getPlugin(pluginId);
    }

}
