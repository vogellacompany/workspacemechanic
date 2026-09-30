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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.ILog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.plugin.core.IMechanicPreferences;
import com.google.eclipse.mechanic.plugin.core.MechanicLog;
import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for {@link LastModifiedPreferencesFileTask}.
 */
@RunAsJUnitTest
public class LastModifiedPreferencesFileTaskTest {

  private IResourceTaskReference ref;
  private ILog ilog;
  private IMechanicPreferences prefs;
  private MechanicLog log;


  private static class TestTask extends LastModifiedPreferencesFileTask {
    private final String title;
    private final String description;

    public TestTask(
        IResourceTaskReference taskRef,
        IMechanicPreferences prefs,
        MechanicLog log,
        String title,
        String description) {
      super(taskRef, prefs, log);
      this.title = title;
      this.description = description;
    }

    public String getTitle() {
      return title;
    }

    public String getDescription() {
      return description;
    }
  }

  
  @BeforeEach

  
  public void setUp() throws Exception {
    ref = mock(IResourceTaskReference.class);
    ilog = mock(ILog.class);
    prefs = mock(IMechanicPreferences.class);
    log = new MechanicLog(ilog);
  }

  @AfterEach

  public void tearDown() throws Exception {
//    verify(ref);
//    verify(ilog);
//    verify(prefs);
  }

  @Test

  public void testGetId() {
    when(ref.getPath()).thenReturn("/path/to");

    LastModifiedPreferencesFileTask task = new TestTask(ref, prefs, log, "X", "Y");

    assertEquals(
        "com.google.eclipse.mechanic.LastModifiedPreferencesFileTaskTest$TestTask@/path/to",
        task.getId());
  }

  @Test

  public void testMd5_DoesNotExist() {
    when(ref.getPath()).thenReturn("/path/to");

    String key =
        "com.google.eclipse.mechanic.LastModifiedPreferencesFileTaskTest$TestTask@/path/to_lastmd5";

    when(prefs.contains(key)).thenReturn(false);

    LastModifiedPreferencesFileTask task = new TestTask(ref, prefs, log, "X", "Y");

    assertFalse(task.evaluate());

    verify(prefs).contains(key);
  }

  @Test

  public void testMd5_ExistsButDoesNotMatch() throws Exception {
    when(ref.getPath()).thenReturn("/path/to");

    String key =
        "com.google.eclipse.mechanic.LastModifiedPreferencesFileTaskTest$TestTask@/path/to_lastmd5";

    when(prefs.contains(key)).thenReturn(true);
    when(prefs.getLong(key)).thenReturn(12345L);
    when(ref.computeMD5()).thenReturn(23456L);

    LastModifiedPreferencesFileTask task = new TestTask(ref, prefs, log, "X", "Y");

    assertFalse(task.evaluate());

    verify(prefs).contains(key);
    verify(prefs).getLong(key);
    verify(ref).computeMD5();
  }

  @Test

  public void testMd5_ExistsButMatches() throws Exception {
    when(ref.getPath()).thenReturn("/path/to");

    String key =
        "com.google.eclipse.mechanic.LastModifiedPreferencesFileTaskTest$TestTask@/path/to_lastmd5";

    when(prefs.contains(key)).thenReturn(true);
    when(prefs.getLong(key)).thenReturn(12345L);
    when(ref.computeMD5()).thenReturn(12345L);

    LastModifiedPreferencesFileTask task = new TestTask(ref, prefs, log, "X", "Y");

    assertTrue(task.evaluate());

    verify(prefs).contains(key);
    verify(prefs).getLong(key);
    verify(ref).computeMD5();
  }
}
