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

import com.google.eclipse.mechanic.TaskScanner;

/**
 * Code behind the {@code com.google.eclipse.mechanic.scanners} extension point.
 *
 * <p>This class interfaces with the {@link org.eclipse.core.runtime.Platform}, reading all extensions of the
 * {@code scanners} extension point, providing a mechanism for translating their
 * implementations to instances of {@link TaskScanner}.
 */
public class ScannersExtensionPoint {
  private static final String EXTENSION_POINT_NAME = "scanners";
  private static final String TAG_SCANNER = "scanner";
  private static final String ATTR_CLASS = "class";

  // Initialization On Demand Holder Idiom
  // http://crazybob.org/2007/01/lazy-loading-singletons.html
  private static class SingletonHolder {
    static SimpleExtensionPointManager<TaskScanner> instance =
        SimpleExtensionPointManager.newInstance(
            EXTENSION_POINT_NAME,
            TaskScanner.class,
            TAG_SCANNER,
            ATTR_CLASS,
            null);
  }

  private ScannersExtensionPoint() {
  }

  /**
   * Return the {@link TaskScanner} supplier, initializing it if required.
   *
   * <p>The supplier is memoized, so it will return the same instantiated
   * objects upon repeated calls.
   */
  public static Supplier<List<TaskScanner>> getInstance() {
    return MemoizingSupplier.memoize(() -> SingletonHolder.instance.getInstances());
  }

  /**
   * Clears the list of scanners.
   * 
   * <p><em>This should only be called by {@link com.google.eclipse.mechanic.plugin.core.MechanicPlugin#stop}.</em>
   */
  public static void dispose() {
    SingletonHolder.instance = null;
  }
}
