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
package org.pf4j.shell.util;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ClassLoaderUtils {

    private ClassLoaderUtils() {
        // prevent instantiation
    }

    public static Set<ClassLoader> getAllClassLoader(Instrumentation instrumentation) {
        Set<ClassLoader> classLoaders = new HashSet<>();

        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                classLoaders.add(classLoader);
            }
        }

        return classLoaders;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getLoadedClasses(ClassLoader classLoader, Instrumentation instrumentation) {
        Set<String> loadedClasses = new HashSet<>();

        try {
            Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);

            loadedClasses.addAll((Vector<String>) field.get(classLoader));
        } catch (Exception e) {
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (classLoader == clazz.getClassLoader()) {
                    loadedClasses.add(clazz.getName());
                }
            }
        }

        return loadedClasses;
    }

    public static URL[] getUrls(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).getURLs();
        }

        return new URL[0];
    }

    public static Class<?> getClass(String className, Instrumentation instrumentation) {
        Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }

        return null;
    }

}
