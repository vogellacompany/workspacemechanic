/*******************************************************************************
 * Copyright (C) 2026, Lars Vogel and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.plugin.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.Task;

/**
 * Tests for {@link MechanicDialog}.
 */
public class MechanicDialogTest {

  @Test
  public void testDescriptionsAreNotClipped() {
    Display display = Display.getDefault();
    Shell parent = new Shell(display);
    List<Task> tasks = new ArrayList<>();
    tasks.add(task("Short", "Fits on one line."));
    tasks.add(task("Borderline",
        "Lets m2e download Javadoc for dependencies (sources are downloaded by default)."));
    tasks.add(task("Long", "A description that is far too long for a single line of the dialog "
        + "and therefore has to wrap onto a second or even a third line to be readable."));
    MechanicDialog dialog = new MechanicDialog(parent, tasks);
    dialog.setBlockOnOpen(false);
    dialog.open();
    try {
      List<Label> wrapping = new ArrayList<>();
      collectWrappingLabels(dialog.getShell(), wrapping);
      assertTrue(wrapping.size() >= tasks.size());
      for (Label label : wrapping) {
        int needed = label.computeSize(label.getSize().x, SWT.DEFAULT).y;
        assertTrue(label.getSize().y >= needed, "Clipped: " + label.getText());
      }
    } finally {
      dialog.close();
      parent.dispose();
    }
  }

  private static Task task(String title, String description) {
    Task task = mock(Task.class);
    when(task.getId()).thenReturn(title);
    when(task.getTitle()).thenReturn(title);
    when(task.getDescription()).thenReturn(description);
    return task;
  }

  private static void collectWrappingLabels(Control control, List<Label> result) {
    if (control instanceof Label label && (label.getStyle() & SWT.WRAP) != 0 && !label.getText().isEmpty()) {
      result.add(label);
    }
    if (control instanceof Composite composite) {
      for (Control child : composite.getChildren()) {
        collectWrappingLabels(child, result);
      }
    }
  }
}
