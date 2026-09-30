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
 * All possible Mechanic states.
 *
 * @author smckay@google.com (Steve McKay)
 */
public enum MechanicStatus {

  /* there are no failing tasks */
  PASSED,

  /* there are failing tasks */
  FAILED,

  /* mechanic service is updating its set of tasks */
  UPDATING,

  /* mechanic service is not running */
  STOPPED

}
