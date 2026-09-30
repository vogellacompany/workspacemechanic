/*******************************************************************************
 * Copyright (C) 2009, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.eclipse.mechanic.internal.ResourceTaskProvidersExtensionPoint;

/**
 * Scanner that looks in the registered {@link IResourceTaskProvider}s
 * for tasks.
 */
public abstract class ResourceTaskScanner implements TaskScanner {

  private final Supplier<List<IResourceTaskProvider>> supplier;

  public ResourceTaskScanner() {
    this(ResourceTaskProvidersExtensionPoint.getInstance());
  }
  ResourceTaskScanner(Supplier<List<IResourceTaskProvider>> supplier) {
    this.supplier = supplier;
  }

  public void scan(TaskCollector collector) {
    Objects.requireNonNull(collector, "'collector' cannot be null.");

    for (IResourceTaskProvider source : supplier.get()) {
      scan(source, collector);
    }
  }

  /**
   * Scan the source for tasks.
   *
   * @param source the source to scan. The source should already be considered valid.
   * @param collector the collector of tasks. Guaranteed to be not null.
   */
  protected abstract void scan(IResourceTaskProvider source, TaskCollector collector);
}
