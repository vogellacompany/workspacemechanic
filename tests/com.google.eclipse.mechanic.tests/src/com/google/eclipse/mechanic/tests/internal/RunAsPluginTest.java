/*******************************************************************************
 * Copyright (C) 2010, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.tests.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate a test as one that must be run in a plug-in test.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RunAsPluginTest {
}
