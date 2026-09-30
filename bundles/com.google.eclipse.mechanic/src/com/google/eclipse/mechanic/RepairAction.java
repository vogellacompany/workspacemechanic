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
 * A Runnable by any other name is still a Runnable. Implement a RepairAction
 * to bring the environment into compliance with an {@link Evaluator}'s notion
 * of correctness.
 *
 * @author smckay@google.com (Steve McKay)
 */
public interface RepairAction extends Runnable {

}
