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

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for {@link EpfFileModel}.
 */
@RunAsJUnitTest
public class EpfFileModelTest {
  @Test
  public void testTitle() {
    try {
      new EpfFileModel(null, "x", TaskType.LASTMOD);
      fail("exception expected");
    } catch (NullPointerException e) {}

    assertEquals("Y", new EpfFileModel("Y", "x", TaskType.LASTMOD).getTitle());
  }

  @Test

  public void testDescription() {
    try {
      new EpfFileModel("x", null, TaskType.LASTMOD);
      fail("exception expected");
    } catch (NullPointerException e) {}

    assertEquals("Y", new EpfFileModel("x", "Y", TaskType.LASTMOD).getDescription());
  }

  @Test

  public void testTaskType() {
    try {
      new EpfFileModel("x", "y", null);
      fail("exception expected");
    } catch (NullPointerException e) {}

    assertEquals(TaskType.LASTMOD, new EpfFileModel("x", "Y", TaskType.LASTMOD).getTaskType());
  }

  @Test

  public void testElements() {
    EpfFileModel model = new EpfFileModel("x", "y", TaskType.RECONCILE);
    assertEquals(0, model.getPreferences().size());

    model.addElement("first", "second");
    assertEquals(1, model.getPreferences().size());
    assertEquals("first", model.getPreferences().keySet().iterator().next());
    assertEquals("second", model.getPreferences().values().iterator().next());

    model.addElement("third", "fourth");
    assertEquals(2, model.getPreferences().size());
  }
}
