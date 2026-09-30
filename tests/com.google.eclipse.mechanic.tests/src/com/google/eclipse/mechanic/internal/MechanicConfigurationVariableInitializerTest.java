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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.eclipse.core.variables.IValueVariable;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsPluginTest;

/**
 * Tests for {@link MechanicConfigurationVariableInitializer}.
 */
@RunAsPluginTest
public class MechanicConfigurationVariableInitializerTest {
  @Test
  public void testSanity() {
    IValueVariable variable = new FakeValueVariable();
    final Properties properties = new Properties();
    properties.put("file.separator", "!");
    properties.put("osgi.install.area", "file:/path/to/eclipse/");

    new MechanicConfigurationVariableInitializer() {
      @Override
      protected Properties getProperties() {
        return properties;
      }
    }.initialize(variable);
    assertEquals("/path/to/eclipse!configuration!com.google.eclipse.mechanic",
        variable.getValue());
  }
}
