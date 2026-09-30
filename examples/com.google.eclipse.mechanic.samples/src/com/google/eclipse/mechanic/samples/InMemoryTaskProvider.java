/*******************************************************************************
 * Copyright (C) 2014, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.samples;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.eclipse.mechanic.ICollector;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.IResourceTaskReference;

/**
 * Task provider that uses an in-memory map to provide task data.
 */
public class InMemoryTaskProvider implements IResourceTaskProvider {

  private static final Map<String, String> RESOURCES = new LinkedHashMap<>();
  private static final String SHOW_LINE_NUMBERS = String.join("\n",
      "# @title Show Line Numbers",
      "# @description Show line numbers in text editors.",
      "# @audit_type LASTMOD",
      "",
      "file_export_version=3.0",
      "/instance/org.eclipse.ui.editors/lineNumberRuler=true");

  static {
    RESOURCES.put("foo.message", "HELLO");
    RESOURCES.put("showlinenumbers.epf", SHOW_LINE_NUMBERS);
    RESOURCES.put("breakpoint.showview", "org.eclipse.debug.ui.BreakpointView");
    RESOURCES.put("junit.showview", "org.eclipse.jdt.junit.ResultView");
    RESOURCES.put("packageexplorer.showview", "org.eclipse.jdt.ui.PackageExplorer");
  }

  public void collectTaskReferences(String extFilter,
      ICollector<IResourceTaskReference> collector) {
    for (String key : RESOURCES.keySet()) {
      if (key.endsWith(extFilter)) {
        collector.collect(new TaskReference(key));
      }
    }
  }

  public void collectTaskReferences(String localPath, String extFilter,
      ICollector<IResourceTaskReference> collector) {
    // Not supporting this for the example.
  }

  private class TaskReference implements IResourceTaskReference {
    private final String key;

    public TaskReference(String key) {
      this.key = key;
    }

    public String getName() {
      return key;
    }

    public InputStream newInputStream() throws IOException {
      return new ByteArrayInputStream(RESOURCES.get(key).getBytes());
    }

    public long getLastModified() throws IOException {
      return 0;
    }

    public String getPath() {
      return "InMemoryTaskProvider:" + key;
    }

    public File asFile() {
      return null;
    }


    @Override
    public String toString() {
      return getClass().toString() + ": " + key;
    }
  }
}
