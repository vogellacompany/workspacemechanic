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

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EpfFileModelWriter}.
 */
public class EpfFileModelWriterTest {
  @Test
  public void testSimple() throws IOException {
    EpfFileModel model = new EpfFileModel("TiTlE", "DeScRiPtIoN", TaskType.RECONCILE);
    model.addElement("first", "second");
    String actual = writeToString(model);
    assertBasics(actual);
  }

  @Test

  public void testOnePreference() throws IOException {
    EpfFileModel model = new EpfFileModel("TiTlE", "DeScRiPtIoN", TaskType.RECONCILE);
    model.addElement("first", "second");
    String actual = writeToString(model);
    assertBasics(actual);
    assertLine("first=second", actual);
  }

  @Test

  public void testTwoPreferences() throws IOException {
    EpfFileModel model = new EpfFileModel("TiTlE", "DeScRiPtIoN", TaskType.RECONCILE);
    model.addElement("first", "second");
    model.addElement("third", "fourth");
    String actual = writeToString(model);
    assertBasics(actual);
    assertLine("first=second", actual);
    assertLine("third=fourth", actual);
  }

  @Test

  public void testNewlineInPreferences() throws IOException {
    EpfFileModel model = new EpfFileModel("TiTlE", "DeScRiPtIoN", TaskType.RECONCILE);
    model.addElement("key", "longline\nnewline");
    String actual = writeToString(model);
    assertBasics(actual);
    assertLine("key=longline\\nnewline", actual);
  }

  private String writeToString(EpfFileModel model) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    EpfFileModelWriter.write(model, os);
    String actual = new String(os.toByteArray());
    return actual;
  }

  private void assertLine(String substring, String string) {
    assertTrue(Arrays.asList(string.split("\n", -1)).contains(substring), "Could not find line [" + substring + "] in [" + string + "]");
  }

  private void assertBasics(String actual) {
    assertLine("# @title TiTlE", actual);
    assertLine("# @description DeScRiPtIoN", actual);
    assertLine("# @task_type RECONCILE", actual);
    assertLine("# Created by the Workspace Mechanic Preference Recorder", actual);
    assertLine("file_export_version=3.0", actual);
  }

}
