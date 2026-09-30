/*******************************************************************************
 * Copyright (C) 2010, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

/**
 * Variable initializer that sets the initial value to the home directory of the user hosting
 * this environment. 
 */
public class UserHomeVariableInitializer implements IValueVariableInitializer {
  public void initialize(IValueVariable variable) {
    variable.setValue(System.getProperty("user.home"));
  }
}
