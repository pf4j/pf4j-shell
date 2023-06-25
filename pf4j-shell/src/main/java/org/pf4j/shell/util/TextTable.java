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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextTable<T> {

    private final List<String> columnNames;
    private final List<Function<? super T, String>> stringFunctions;

    public TextTable() {
        columnNames = new ArrayList<>();
        stringFunctions = new ArrayList<>();
    }

    public void addColumn(String columnName, Function<? super T, ?> fieldFunction) {
        columnNames.add(columnName);
        stringFunctions.add(p -> (String.valueOf(fieldFunction.apply(p))));
    }

    public String createString(Iterable<? extends T> elements) {
        List<Integer> columnWidths = computeColumnWidths(elements);

        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < columnNames.size(); c++) {
            if (c > 0) {
                sb.append("|");
            }
            String format = "%" + columnWidths.get(c) + "s";
            sb.append(String.format(format, columnNames.get(c)));
        }
        sb.append("\n");
        for (int c = 0; c < columnNames.size(); c++) {
            if (c > 0) {
                sb.append("+");
            }
            sb.append(padLeft("", '-', columnWidths.get(c)));
        }
        sb.append("\n");

        for (T element : elements) {
            for (int c = 0; c < columnNames.size(); c++) {
                if (c > 0) {
                    sb.append("|");
                }
                String format = "%" + columnWidths.get(c) + "s";
                Function<? super T, String> f = stringFunctions.get(c);
                String s = f.apply(element);
                sb.append(String.format(format, s));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private int computeMaxWidth(int column, Iterable<? extends T> elements) {
        int n = columnNames.get(column).length();
        Function<? super T, String> f = stringFunctions.get(column);
        for (T element : elements) {
            String s = f.apply(element);
            n = Math.max(n, s.length());
        }

        return n;
    }

    private static String padLeft(String s, char c, int length) {
        while (s.length() < length) {
            s = c + s;
        }

        return s;
    }

    private List<Integer> computeColumnWidths(Iterable<? extends T> elements) {
        List<Integer> columnWidths = new ArrayList<>();
        for (int c = 0; c < columnNames.size(); c++) {
            columnWidths.add(computeMaxWidth(c, elements));
        }

        return columnWidths;
    }

}
