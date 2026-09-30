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

/**
 * An enum that describes a task's policy for overwriting existing 
 * preferences.
 * 
 * @author brianchin@google.com (Brian Chin)
 */
public enum TaskType {
  /**
   * Indicates the task should only overwrite preferences if the file's 
   * modification date is earlier than the changes.
   */
  LASTMOD,
  
  /**
   * Indicates the task should overwrite any preferences that are different 
   * from the values listed the one written here.
   */
  RECONCILE
}