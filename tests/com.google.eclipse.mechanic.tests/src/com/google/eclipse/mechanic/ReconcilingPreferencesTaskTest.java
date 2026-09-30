/*******************************************************************************
 * Copyright (C) 2012, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsPluginTest;

/**
 * Tests for {@link ReconcilingPreferencesTask}.
 */
@RunAsPluginTest
public class ReconcilingPreferencesTaskTest {

  /**
   * This ensures that it's reading the unicode-formatted reconcile task correctly.
   * The preferences, as we've stored them, will match, and so, there is no call
   * to prefsMock.put.
   */
  @Test
  public void testSanity_Unicode() throws IOException {
    URL url = URI.create("platform:/plugin/com.google.eclipse.mechanic.tests/testdata/reconcile_unicode.epf").toURL();
    InputStream inputStream = url.openConnection().getInputStream();

    assertNotNull(inputStream);

    IResourceTaskReference mockReference = mock(IResourceTaskReference.class);
    when(mockReference.getPath()).thenReturn("/path");
    when(mockReference.newInputStream()).thenReturn(inputStream);

    IEclipsePreferences prefsRootMock = mock(IEclipsePreferences.class);
    IEclipsePreferences prefsMock = mock(IEclipsePreferences.class);
    when(prefsRootMock.node("/instance/org.eclipse.jdt.debug.ui")).thenReturn(prefsMock);
    when(prefsMock.get("org.eclipse.jdt.debug.ui.detail_formatters", null)).thenReturn(
        "java.lang.Exception,String.format(\"Exception : %s\",this),1");

    ReconcilingPreferencesTask task = new ReconcilingPreferencesTask(mockReference, prefsRootMock) {
      public String getTitle() {
        return "title";
      }
      
      public String getDescription() {
        return "desc";
      }
    };

    task.run();
  }
}
