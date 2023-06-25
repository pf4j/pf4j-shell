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
import org.pf4j.shell.util.TextTable;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "plugins")
public class PluginsCommand extends AbstractCommand {

    @Override
    public void run() {
        info();
    }

    @CommandLine.Command(name = "load", description = "Load plugins")
    public void load() {
        System.out.println("Load plugins ...");
        getPluginManager().loadPlugins();
    }

    @CommandLine.Command(name = "reload", description = "Reload plugins")
    public void reload() {
        System.out.println("Reload plugins ...");
        getPluginManager().unloadPlugins();
        getPluginManager().loadPlugins();
        getPluginManager().startPlugins();
    }

    @CommandLine.Command(name = "unload", description = "Unload plugins")
    public void unload() {
        System.out.println("Unload plugins ...");
        getPluginManager().unloadPlugins();
    }

    @CommandLine.Command(name = "start", description = "Start plugins")
    public void start() {
        System.out.println("Start plugins ...");
        getPluginManager().startPlugins();
    }

    @CommandLine.Command(name = "stop", description = "Stop plugins")
    public void stop() {
        System.out.println("Stop plugins ..");
        getPluginManager().stopPlugins();
    }

    @CommandLine.Command(name = "info", description = "Display information about plugins")
    public void info() {
        List<PluginWrapper> plugins = getPluginManager().getPlugins();
        TextTable<PluginWrapper> table = new TextTable<>();
        table.addColumn("Id", PluginWrapper::getPluginId);
        table.addColumn("Version", PluginsCommand::getVersion);
        table.addColumn("State", PluginWrapper::getPluginState);
        System.out.println(table.createString(plugins));
    }

    private static String getVersion(PluginWrapper pluginWrapper) {
        return pluginWrapper.getDescriptor().getVersion();
    }

}
