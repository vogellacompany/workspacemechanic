/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Function;

import com.google.gson.Gson;

/**
 * Parser for the list of resource tasks stored as preferences.
 */
public class ResourceTaskProviderParser {

  private static final Gson gson = new Gson();

  private final Function<String, String> variableParser;

  /**
   * Create a new instance.
   *
   * @param variableParser used to perform variable substitution. One such translator is
   * {@link VariableManagerStringParser#INSTANCE}. To get the raw values, pass
   * {@code Function.identity()}
   */
  public ResourceTaskProviderParser(Function<String, String> variableParser) {
    this.variableParser = Objects.requireNonNull(variableParser);
  }

  public final String[] parse(String text) {
    if (!text.startsWith("[")) {
      // I would use Splitter, but I won't use split.
      StringTokenizer st = new StringTokenizer(text, File.pathSeparator);
      List<String> list = new ArrayList<>();
      while (st.hasMoreElements()) {
        String elem = (String) st.nextElement();
        // Historically paths named "null" somehow got added to default prefs
        if (elem == null) {
          continue;
        }
        String substituted = variableParser.apply(elem);
        list.add(substituted);
      }
      return list.toArray(new String[0]);
    } else {
      return gson.fromJson(text, String[].class);
    }
  }

  public final String unparse(String... items) {
    return gson.toJson(items);
  }
}
