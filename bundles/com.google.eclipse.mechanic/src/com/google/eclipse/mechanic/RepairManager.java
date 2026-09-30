/*******************************************************************************
 * Copyright (C) 2007, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.eclipse.mechanic.RepairDecisionProvider.Decision;
import com.google.eclipse.mechanic.RepairDecisionProvider.ResponseStatus;
import com.google.eclipse.mechanic.plugin.core.MechanicLog;
import com.google.eclipse.mechanic.plugin.core.MechanicPlugin;

/**
 * Provides a controller for the process of selecting tasks to be repaired,
 * then repairing them.
 *
 * @author smckay@google.com (Steve McKay)
 */
public class RepairManager implements Runnable {

  private final MechanicLog log = MechanicLog.getDefault();

  private final IMechanicService service;
  private final List<Task> failing;
  private final RepairDecisionProvider choiceProvider;

  /**
   * @param service
   * @param failing list of failing {@link Task}s. Doesn't modify the list.
   * @param choiceProvider a mechanism for determining how to react to failed tasks.
   */
  public RepairManager(IMechanicService service, List<Task> failing,
      RepairDecisionProvider choiceProvider) {

    if (failing.isEmpty()) {
      throw new IllegalArgumentException("'failing' cannot be empty");
    }
    this.service = Objects.requireNonNull(service);
    this.failing = Objects.requireNonNull(failing);
    this.choiceProvider = Objects.requireNonNull(choiceProvider);
  }

  /**
   * Collects list of tasks to be repaired, then repairs them.
   *
   * @throws IllegalStateException if the current status is UPDATING.
   */
  public void run() {

    ResponseStatus userStatus = choiceProvider.initialize(failing);

    // the user can cancel or approve the operation. When the operation is
    // canceled, we should not do anything.
    if (userStatus == ResponseStatus.OK) {

      // Repair and block individual actions as specified by the user.
      for (Map.Entry<Task, Decision> entry
          : choiceProvider.getDecisions().entrySet()) {

        switch (entry.getValue()) {
          case YES:
            repairItem(entry.getKey());
            break;
          case NO:
            // don't do anything this time
            break;
          case NEVER:
            MechanicPlugin.getDefault().getMechanicPreferences().blockItem(entry.getKey());
            break;
          default:
            throw new RuntimeException("Unhandled Decision value.");
        }
      }

      // attempt to re-run the Mechanic immediately, to verify our work
      service.start();
    }
  }

  /**
   * Execute action associated with the supplied Task.
   */
  private void repairItem(Task item) {
    log.logInfo("Repairing item: %s", item.getId());
    try {
      item.getRepairAction().run();
    } catch (RuntimeException e) {
      log.logError(e, "RepairAction failed for %s, %s", item.getId(), e.getMessage());
    }
  }
}
