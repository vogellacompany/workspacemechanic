/*******************************************************************************
 * Copyright (C) 2012, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.internal;

import java.util.function.Function;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * Perform variable substitution on a string. Used for translating the task directories,
 * which can contain variables.
 *
 * <p>Comes from the default {@link IStringVariableManager} from the {@link VariablesPlugin}.
 */
public class VariableManagerStringParser implements Function<String, String> {
  public static final VariableManagerStringParser INSTANCE = new VariableManagerStringParser();

  private VariableManagerStringParser() { }

  public String apply(String input) {
    try {
      IStringVariableManager stringManager =
          VariablesPlugin.getDefault().getStringVariableManager();
      return stringManager.performStringSubstitution(input);
    } catch (CoreException e) {
      return "";
    }
  }
}
