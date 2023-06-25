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
import com.sun.tools.attach.VirtualMachine;
import org.pf4j.shell.util.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class InstrumentationAgentLoader {

    public void loadAgent() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        String pid = ProcessUtils.getProcessId();
        System.out.println("pid = " + pid);
        VirtualMachine virtualMachine = VirtualMachine.attach(pid);
        Properties properties = virtualMachine.getAgentProperties();
        System.out.println("properties = " + properties);

        File agentFile = getAgentFile();
        System.out.println("Loading agent from " + agentFile.getCanonicalPath());
        virtualMachine.loadAgent(agentFile.getCanonicalPath(), null);
    }

    protected File getAgentFile() throws IOException {
        return new InstrumentationAgentFactory().createAgentFile();
    }

}
