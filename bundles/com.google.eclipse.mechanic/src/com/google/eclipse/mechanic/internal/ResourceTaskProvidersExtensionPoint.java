/*******************************************************************************
 * Copyright (C) 2014, Google Inc.
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

import com.google.eclipse.mechanic.IResourceTaskProvider;

/**
 * Code behind the {@code com.google.eclipse.mechanic.resourcetaskproviders} extension point.
 *
 * <p>This class interfaces with the {@link org.eclipse.core.runtime.Platform}, reading all extensions of the
 * {@code tasks} extension point, providing a mechanism for translating their
 * implementations to instances of {@link IResourceTaskProvider}.
 */
public class ResourceTaskProvidersExtensionPoint {
  private static final String EXTENSION_POINT_NAME = "resourcetaskproviders";
  private static final String TAG_TASK = "provider";
  private static final String ATTR_CLASS = "class";

  // Initialization On Demand Holder Idiom
  // http://crazybob.org/2007/01/lazy-loading-singletons.html
  private static class SingletonHolder {
    static SimpleExtensionPointManager<IResourceTaskProvider> instance =
        SimpleExtensionPointManager.newInstance(
            EXTENSION_POINT_NAME,
            IResourceTaskProvider.class,
            TAG_TASK,
            ATTR_CLASS,
            null);
  }

  private ResourceTaskProvidersExtensionPoint() {
  }

  /**
   * Return the {@link IResourceTaskProvider} supplier, initializing it if required.
   *
   * <p>The supplier is memoized, so it will return the same instantiated
   * objects upon repeated calls.
   */
  public static Supplier<List<IResourceTaskProvider>> getInstance() {
    return MemoizingSupplier.memoize(() -> SingletonHolder.instance.getInstances());
  }

  /**
   * Clears the list of task providers.
   * 
   * <p><em>This should only be called by {@link com.google.eclipse.mechanic.plugin.core.MechanicPlugin#stop}.</em>
   */
  public static void dispose() {
    SingletonHolder.instance = null;
  }
}
