/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.TaskCollector;
import com.google.eclipse.mechanic.TaskScanner;
import com.google.eclipse.mechanic.plugin.core.MechanicLog;
import com.google.eclipse.mechanic.testing.EmptyLog;

/**
 * Tests for {@link RootTaskScanner}
 */
public class RootTaskScannerTest {

  @Test
  public void testThatAThrowingTaskScannerDoesNotKillTheMechanic() {
    // TODO(zorzella): where are we supposed to put test infra, like fakes
    // for ScannersExtensionPointInterface, TaskScanner and TaskCollector?
    Supplier<List<TaskScanner> > scannerPoint = () -> {
      List<TaskScanner> result = new ArrayList<TaskScanner>();
      result.add(new TaskScanner() {
        public void scan(TaskCollector collector) {
          throw new RuntimeException();
        }
      });
      return result;
    };
    MechanicLog log = new MechanicLog(new EmptyLog());
    RootTaskScanner scanner = new RootTaskScanner(log, scannerPoint);
    TaskCollector collector = _ -> {};
    scanner.scan(collector);
    // We just want to be sure that this does not throw
  }
}
