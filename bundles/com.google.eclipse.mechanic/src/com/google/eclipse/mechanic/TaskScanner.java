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
 * Simple interface for collecting tasks.
 *
 * @author smckay@google.com (Steve McKay)
 */
public interface TaskScanner {

  /**
   * Adds Tasks to the supplied collector.
   *
   * @param collector the collector of {@link Task}s.
   */
  void scan(TaskCollector collector);

}
