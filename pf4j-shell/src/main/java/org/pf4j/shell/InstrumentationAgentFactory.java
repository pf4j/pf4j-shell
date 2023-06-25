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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class InstrumentationAgentFactory {

    private File agentFile;

    public File createAgentFile() throws IOException {
        if (agentFile == null) {
            // Compute classpath
            String classPath = getClasspath();
            // Create manifest
            Manifest agentManifest = createAgentManifest(classPath);
            // Create jar file
            agentFile = File.createTempFile("agent", ".jar");
            agentFile.deleteOnExit();
            // inject agent manifest in agent file
            new JarOutputStream(Files.newOutputStream(agentFile.toPath()), agentManifest).close();

            System.out.println("Created agent jar " + agentFile.getCanonicalPath());
        }

        return agentFile;
    }

    private Manifest createAgentManifest(String classpath) {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Agent-Class", InstrumentationAgent.class.getName());
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.CLASS_PATH, classpath);

        return manifest;
    }

    private String getClasspath() throws IOException {
        String classpath = System.getProperty("java.class.path");
        String pathSeparator = System.getProperty("path.separator");

        StringBuilder builder = new StringBuilder();
        for (String path : classpath.split(Pattern.quote(pathSeparator))) {
            File file = new File(path);
            if (file.exists()) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                String fileName = file.getCanonicalPath();
                if (fileName.charAt(0) != '/' && fileName.charAt(1) == ':') {
                    // On window must be like "/C:/path/lib/abc.jar"
                    fileName = fileName.replace(File.separatorChar, '/');
                    builder.append("/").append(fileName);
                } else {
                    builder.append(file.getCanonicalPath());
                }
            }
        }

        return builder.toString();
    }

}
