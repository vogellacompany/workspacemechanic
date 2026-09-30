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
 * Describes a single test for verifying that the environment is compliant
 * with the author's notion of correctness. A Evaluator should be written
 * in conjunction with a {@link RepairAction} capable of bringing the
 * environment into compliance.
 *
 * @author smckay@google.com (Steve McKay)
 */
public interface Evaluator {

  /**
   * @return true if the environment adheres to this test.
   */
  public boolean evaluate();

}
