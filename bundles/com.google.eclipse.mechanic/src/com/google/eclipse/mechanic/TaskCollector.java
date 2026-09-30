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

/**
 * Interface for collecting tasks from {@link TaskScanner}s.
 *
 * <p>This is the main communications channel between the {@link MechanicService} and the
 * {@link TaskScanner}s.
 */
public interface TaskCollector extends ICollector<Task> {
}
