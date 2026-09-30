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

import org.eclipse.jface.notifications.NotificationPopup;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.IMechanicService;
import com.google.eclipse.mechanic.plugin.core.IMechanicPreferences;

/**
 * Tests for {@link PopupNotifier}.
 */
public class PopupNotifierTest {

  @Test
  public void testPopupOpens() {
    Display display = Display.getDefault();
    PopupNotifier notifier =
        new PopupNotifier(mock(IMechanicService.class), mock(IMechanicPreferences.class));
    NotificationPopup popup = notifier.createPopup(display);
    try {
      popup.open();
      Shell shell = popup.getShell();
      assertNotNull(shell);
      assertTrue(shell.isVisible());
    } finally {
      popup.close();
    }
  }
}
