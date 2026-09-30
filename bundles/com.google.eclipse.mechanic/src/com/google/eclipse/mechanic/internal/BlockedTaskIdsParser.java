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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.Gson;

/**
 * Parser for the list of blocked task IDs stored as preferences.
 */
public class BlockedTaskIdsParser {
  private static final Gson gson = new Gson();

  public final List<String> parse(String text) {
    if (text == null) {
      return Collections.emptyList();
    }

    List<String> list = new ArrayList<>();
    if (!text.startsWith("[")) {
      // I would use Splitter, but I won't use split.
      StringTokenizer st = new StringTokenizer(text, File.pathSeparator);
      while (st.hasMoreElements()) {
        list.add((String) st.nextElement());
      }
    } else {
      // TODO(konigsberg): There's got to be a way to get GSON to 
      // parse the string as a list, I'm just not interested right now.
      for (String entry : gson.fromJson(text, String[].class)) {
        list.add(entry);
      }
    }
    return list;
  }

  public final String unparse(Collection<String> items) {
    return gson.toJson(items);
  }
}
