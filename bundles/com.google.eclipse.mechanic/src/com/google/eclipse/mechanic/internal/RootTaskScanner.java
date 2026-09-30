/*******************************************************************************
 * Copyright (C) 2007, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import java.util.List;
import java.util.function.Supplier;

import com.google.eclipse.mechanic.TaskCollector;
import com.google.eclipse.mechanic.TaskScanner;
import com.google.eclipse.mechanic.plugin.core.MechanicLog;

/**
 * A {@link TaskScanner} that loads and runs all other {@link TaskScanner}s.
 */
public class RootTaskScanner implements TaskScanner {

  private static RootTaskScanner instance;

  private final MechanicLog log;
  private final Supplier<List<TaskScanner>> scannersExtensionPoint;

  public RootTaskScanner() {
    this(MechanicLog.getDefault(), ScannersExtensionPoint.getInstance());
  }
  RootTaskScanner(MechanicLog log, Supplier<List<TaskScanner>> scannersExtensionPoint) {
    this.log = log;
    this.scannersExtensionPoint = scannersExtensionPoint;
  }

  public synchronized static RootTaskScanner getInstance() {
    if (instance == null) {
      instance = new RootTaskScanner();
    }
    return instance;
  }

  public void scan(TaskCollector collector) {
    for (TaskScanner scanner : scannersExtensionPoint.get()) {
      try {
        scanner.scan(collector);
      } catch (RuntimeException e) {
        log.logError(e, "Exception scanning '%s', class '%s'",
            scanner, scanner.getClass().getName());
      }
    }
  }
}
