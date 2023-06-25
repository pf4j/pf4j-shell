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
package org.pf4j.shell;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import org.jline.reader.LineReader;
import org.pf4j.PluginManager;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;

@CommandLine.Command(name = "",
        description = {
                "Example interactive shell with completion and autosuggestions. " +
                        "Hit @|magenta <TAB>|@ to see available commands.",
                "Hit @|magenta ALT-S|@ to toggle tail-tips.",
                ""},
        footer = {"", "Press Ctrl-D to exit."},
        subcommands = { PicocliCommands.ClearScreen.class, CommandLine.HelpCommand.class }
)
public class ParentCommand implements Runnable {

    private final PluginManager pluginManager;

    private Instrumentation instrumentation;

    PrintWriter out;

    public ParentCommand(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setReader(LineReader reader){
        out = reader.getTerminal().writer();
    }

    public void run() {
        out.println(new CommandLine(this).getUsageMessage());
    }

    public Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            try {
                new InstrumentationAgentLoader().loadAgent();
            } catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
                throw new IllegalStateException("Cannot load the instrumentation agent", e);
            }
            instrumentation = InstrumentationAgent.getInstrumentation();
            System.out.println("instrumentation = " + instrumentation);
        }

        return instrumentation;
    }

}
