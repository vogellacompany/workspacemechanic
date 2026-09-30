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

import org.eclipse.core.variables.IValueVariable;

/**
 * Fake instance of {@link IValueVariable} for testing.
 */
public class FakeValueVariable implements  IValueVariable {
  private String value;

  public String getName() { return null; }
  public String getDescription() { return null; }
  public void setDescription(String description) { }
  public boolean isReadOnly() { return false; }
  public boolean isContributed() { return false; }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
