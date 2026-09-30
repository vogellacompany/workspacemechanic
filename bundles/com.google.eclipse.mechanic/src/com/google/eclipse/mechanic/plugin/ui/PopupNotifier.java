/*******************************************************************************
 * Copyright (C) 2009, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.plugin.ui;

import java.util.concurrent.TimeUnit;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.notifications.NotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;

import com.google.eclipse.mechanic.IMechanicService;
import com.google.eclipse.mechanic.IStatusChangeListener;
import com.google.eclipse.mechanic.RepairDecisionProvider;
import com.google.eclipse.mechanic.plugin.core.IMechanicPreferences;

/**
 * A {@link IStatusChangeListener} that appears when tasks fail.
 *
 * <p>Use cases and conditions for when a popup will appear.
 *
 * <p>The popup appears when the mechanic announces failure. But if we showed the
 * popup every time the mechanic returned failure, it would pop up way too often,
 * and when that happens, people will turn it off, and its value will be lost.
 *
 * <p>Case 1: when the workbench starts, the first failure will cause the popup
 * to appear.
 *
 * <p>Case 1a: (Specialization) Restarting the mechanic: When the user has stopped
 * the mechanic service (right-click on the trim widget, select Stop Service),
 * and then restarts it, the popup will appear on first-failure.
 *
 * <p>Case 1b: (Specialization) Reenabling showing the popup: if the popup
 * notifier was turned off, and subsequently turned on, the poup will appear
 * the first time the mechanic returns a failure notification.
 *
 * <p>Case 2: popup is already shown: If the mechanic announces failure, and the
 * popup is already shown, then don't show a second popup a second time.
 *
 * <p>Case 3: subsequent failures: If the mechanic has announced failure, then the
 * popup will not appear a second time, at least, not until the mechanic has returned
 * a status of passed. That addresses this flow of statuses: (the "#" means that
 * the popup appears):
 *
 * failed # failed failed (user fixes) passed failed #
 *
 * <p>Case 3a: (Specialization) Bug in a repair results in perpetual failure:
 * Even though the user tries to fix failed tasks, if one of them, no matter
 * what, continues to fail, the popup will not appear.
 */
public class PopupNotifier {

  // Popup appears for two minutes.
  private static final long POPUP_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(2);

  private static final int PADDING = 10;

  private final IStatusChangeListener statusChangeListener;

  private final IMechanicService service;
  private final IMechanicPreferences mechanicPreferences;

  /**
   * controls whether the next failure from the mechanic should result in showing the popup.
   */
  private volatile boolean showOnFailure = true;

  /**
   * Identifies whether the popup is currently visible. We don't want to open a second
   * instance while one is open.
   */
  private volatile boolean visible = false;

  public PopupNotifier(IMechanicService mechanicService,
      final IMechanicPreferences mechanicPreferences) {
    this.service = mechanicService;
    this.mechanicPreferences =  mechanicPreferences;
    this.statusChangeListener = event -> {
      switch (event.getStatus()) {
        case FAILED:
          if (mechanicPreferences.isShowPopup()) {
            if (showOnFailure) {
              showPopup();
            }
            showOnFailure = false;
          } else {
            // By setting showOnFailure here, the popup will appear once the preference is unset
            showOnFailure = true;
          }
          break;

        case PASSED:
        case STOPPED:
          /*
           * Once the mechanic has passed all tasks, or has stopped analyzing, the
           * first subsequent failure should show the popup.
           */
          showOnFailure = true;
          break;

        case UPDATING:
          // Do nothing
          break;

        default:
          throw new IllegalArgumentException("Unknown status: " + event.getStatus());
      }
    };
  }

  public void initialize() {
    service.addTaskStatusChangeListener(statusChangeListener);
  }

  public void dispose() {
    service.removeTaskStatusChangeListener(statusChangeListener);
  }

  private void showPopup() {
    if (visible) {
      return;
    }
    Display display = Display.getCurrent() != null ? Display.getCurrent() : Display.getDefault();
    NotificationPopup popup = NotificationPopup.forDisplay(display)
        .title(PopupNotifier::createTitle, true)
        .content(this::createContent)
        .delay(POPUP_TIMEOUT_MILLIS)
        .build();
    visible = true;
    popup.open();
    popup.getShell().addDisposeListener(_ -> visible = false);
  }

  private static Control createTitle(Composite parent) {
    // NotificationPopup lays out custom title and content without margins.
    ((GridLayout) parent.getLayout()).marginWidth = PADDING;
    Label title = new Label(parent, SWT.NONE);
    title.setText("Workspace Mechanic");
    return title;
  }

  private Control createContent(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayoutFactory.fillDefaults().margins(PADDING, PADDING).applyTo(composite);
    Label label = new Label(composite, SWT.WRAP);
    label.setText("The Workspace Mechanic found issues that need your attention.");
    // Without a width hint the label asks for its full text width and is cut off.
    GridDataFactory.fillDefaults().grab(true, false).hint(340, SWT.DEFAULT).applyTo(label);
    createLink(composite, "View and correct configuration issues", this::correctConfigurationIssues);
    createLink(composite, "Disable this popup", mechanicPreferences::doNotShowPopup);
    return composite;
  }

  private static void createLink(Composite parent, String text, Runnable action) {
    Link link = new Link(parent, SWT.NONE);
    link.setText("<a>" + text + "</a>");
    link.addSelectionListener(SelectionListener.widgetSelectedAdapter(_ -> {
      parent.getShell().close();
      action.run();
    }));
  }

  private void correctConfigurationIssues() {
    RepairDecisionProvider rdp = new UserChoiceDecisionProvider(
        PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    service.getRepairManager(rdp).run();
  }
}
