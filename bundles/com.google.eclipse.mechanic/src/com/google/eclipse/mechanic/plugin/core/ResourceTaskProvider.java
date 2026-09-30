/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.plugin.core;

import com.google.eclipse.mechanic.IResourceTaskProvider;

/**
 * Provides resource-based tasks.
 *
 * <b>Note: this API needs some rework, and is very likely to change.
 */
public abstract class ResourceTaskProvider implements IResourceTaskProvider {

  /**
   * Throws exception, ensures subclasses implement equals method.
   */
  @Override
  public boolean equals(Object obj) {
    throw new RuntimeException(this.getClass().getName() + "doesn't implement equals");
  }

  /**
   * Throws exception, ensures subclasses implement hashCode method.
   */
  @Override
  public int hashCode() {
    throw new RuntimeException(this.getClass().getName() + "doesn't implement hashCode");
  }
}
