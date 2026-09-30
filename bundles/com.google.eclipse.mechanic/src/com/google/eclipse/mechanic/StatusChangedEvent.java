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
 * Event supplied to StatusChangeListeners.
 *
 * @author smckay@google.com (Steve McKay)
 */
public class StatusChangedEvent {

  private final MechanicStatus status;

  public StatusChangedEvent(MechanicStatus status) {
    this.status = status;
  }

  public MechanicStatus getStatus() {
    return status;
  }
}
