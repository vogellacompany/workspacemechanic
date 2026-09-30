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

/**
 * Abstract Task for simple implementations of composite
 * (Task/Evaluator/RepairAction)s
 *
 * @author smckay@google.com (Steve McKay)
 */
public abstract class CompositeTask implements CompositeTaskInterface {

  public Evaluator getEvaluator() {
    return this;
  }

  public RepairAction getRepairAction() {
    return this;
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public String toString() {
    return getId();
  }

  /**
   * Returns true if the supplied Object is an Task with the same
   * id.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof Task) {
      return getId().equals(((Task) obj).getId());
    }
    return false;
  }
}
