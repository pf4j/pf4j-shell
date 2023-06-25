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

import org.fusesource.jansi.AnsiConsole;
import org.jline.builtins.ConfigurationPath;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;
import org.pf4j.PluginManager;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class Shell {

    private final PluginManager pluginManager;

    private Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
    private String prompt = "pf4j-shell> ";
    private String rightPrompt;

    public Shell(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public Supplier<Path> getWorkDir() {
        return workDir;
    }

    public Shell setWorkDir(Supplier<Path> workDir) {
        this.workDir = workDir;
        return this;
    }

    public String getPrompt() {
        return prompt;
    }

    public Shell setPrompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    public String getRightPrompt() {
        return rightPrompt;
    }

    public Shell setRightPrompt(String rightPrompt) {
        this.rightPrompt = rightPrompt;
        return this;
    }

    public void start() {
        AnsiConsole.systemInstall();

        try {
            // set up JLine built-in commands
            Builtins builtins = createBuiltins();

            ParentCommand parentCommand = new ParentCommand(pluginManager);

            // set up picocli commands
            PicocliCommands.PicocliCommandsFactory factory = createCommandsFactory();
            CommandLine commandLine = createCommandLine(parentCommand, factory);
            PicocliCommands picocliCommands = new PicocliCommands(commandLine);

            Parser parser = new DefaultParser();
            try (Terminal terminal = TerminalBuilder.builder().build()) {
                SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, getWorkDir(), null);
                systemRegistry.setCommandRegistries(builtins, picocliCommands);
                systemRegistry.register("help", picocliCommands);

                LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(systemRegistry.completer())
                    .parser(parser)
                    .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
                    .build();
                builtins.setLineReader(reader);
                parentCommand.setReader(reader);
                factory.setTerminal(terminal);
                TailTipWidgets widgets = new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER);
                widgets.enable();
                KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
                keyMap.bind(new Reference("tailtip-toggle"), KeyMap.alt("s"));

                processInput(systemRegistry, reader);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    protected CommandLine createCommandLine(ParentCommand parentCommand, PicocliCommands.PicocliCommandsFactory factory ) {
        CommandLine commandLine = new CommandLine(parentCommand, factory);
        CommandsProvider commandsProvider = new DefaultCommandsProvider();
        commandsProvider.getCommands().forEach(commandLine::addSubcommand);

        return commandLine;
    }

    protected PicocliCommands.PicocliCommandsFactory createCommandsFactory() {
        // Or, if you have your own factory, you can chain them like this:
        // MyCustomFactory customFactory = createCustomFactory(); // your application custom factory
        // PicocliCommandsFactory factory = new PicocliCommandsFactory(customFactory); // chain the factories
        return new PicocliCommands.PicocliCommandsFactory();
    }

    protected Builtins createBuiltins() {
        ConfigurationPath configurationPath = new ConfigurationPath(null, null);
        Builtins builtins = new Builtins(getWorkDir(), configurationPath, null);
        builtins.rename(Builtins.Command.TTOP, "top");
        builtins.alias("zle", "widget");
        builtins.alias("bindkey", "keymap");

        return builtins;
    }

    private void processInput(SystemRegistry systemRegistry, LineReader reader) {
        // start the shell and process input until the user quits with Ctrl-D
        String line;
        while (true) {
            try {
                systemRegistry.cleanUp();
                line = reader.readLine(getPrompt(), getRightPrompt(), (MaskingCallback) null, null);
                systemRegistry.execute(line);
            } catch (UserInterruptException e) {
                // ignore
            } catch (EndOfFileException e) {
                return;
            } catch (Exception e) {
                systemRegistry.trace(e);
            }
        }
    }

}
