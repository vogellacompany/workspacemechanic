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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;

import com.google.eclipse.mechanic.RepairDecisionProvider;
import com.google.eclipse.mechanic.Task;

/**
 * Adapts MechanicDialog to the RepairDecisionProvider interface as needed by
 * {@link com.google.eclipse.mechanic.RepairManager}.
 *
 * @author smckay@google.com (Steve McKay)
 */
class UserChoiceDecisionProvider implements RepairDecisionProvider {

  private final IWorkbenchWindow window;

  private Map<Task, RepairDecisionProvider.Decision> decisions
      = Collections.emptyMap();

  public UserChoiceDecisionProvider(IWorkbenchWindow window) {
    this.window = window;
  }

  public ResponseStatus initialize(List<Task> failing) {

    ResponseStatus rs = ResponseStatus.CANCEL;

    MechanicDialog dialog = new MechanicDialog(window.getShell(), failing);

    dialog.open(); // blocks

    if (dialog.isOkay()) {
      rs = ResponseStatus.OK;
      decisions = dialog.getUserChoices();
    }

    // finally, return the status
    return rs;
  }

  public Map<Task, Decision> getDecisions() {
    return decisions;
  }
}