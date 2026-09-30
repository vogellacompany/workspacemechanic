/*******************************************************************************
 * Copyright (C) 2009, Google Inc.
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

import com.google.eclipse.mechanic.CompositeTaskInterface;
import com.google.eclipse.mechanic.TaskCollector;
import com.google.eclipse.mechanic.TaskScanner;

/**
 * Provides support for loading tasks defined in extension points.
 */
public class ExtensionPointScanner implements TaskScanner {

  private Supplier<List<CompositeTaskInterface>> taskSupplier;

  public ExtensionPointScanner() {
    this(TasksExtensionPoint.getInstance());
  }
  ExtensionPointScanner(Supplier<List<CompositeTaskInterface>> taskSupplier) {
    this.taskSupplier = taskSupplier;
  }

  public void scan(TaskCollector collector) {
    for (CompositeTaskInterface task : taskSupplier.get()) {
      collector.collect(task);
    }
  }
}
