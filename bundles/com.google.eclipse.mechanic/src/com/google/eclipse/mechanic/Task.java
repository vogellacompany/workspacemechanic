/*******************************************************************************
 * Copyright (C) 2007, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic;

/**
 * Contains a single piece of work for the mechanic to perform, including
 * the test to verify environment compliance and the repair action to
 * bring the environment into compliance if needed.
 * <p/>
 * The implementor should guarantee that each instance with an
 * identical id as returned from getId(), should also return true from
 * equals, and a sensible hashcode.
 *
 * @author smckay@google.com (Steve McKay)
 */
public interface Task {

  /**
   * Return an id for this task. The id should be unique to any instance that
   * behaves differently than another instance. Typically the id would be the
   * fully qualified class name unless two task instances somehow manage to 
   * behave differently. In this case they should each return a unique id.
   */
  public String getId();

  /**
   * Returns a string suitable for display to the user providing a title for
   * this Task. This is used in contexts where a short
   * description is needed. Capitalization should follow the same rules as
   * applied to book titles.
   */
  public String getTitle();

  /**
   * Returns a string suitable for display to the user describing what action
   * will be taken when the associated {@link RepairAction} is executed.
   * <p/>
   * The description should start with a 3rd person descriptive verb such as:
   * <p/>
   * "Customizes your java code templates with user information."
   * "Configures a local extension location."
   */
  public String getDescription();

  /**
   * Returns an Evaluator for this task. The Evaluator should return false
   * if the associated RepairAction needs to be executed.
   */
  public Evaluator getEvaluator();

  /**
   * Returns a {@link RepairAction} capable of bringing this {@link Task}
   * into compliance.
   */
  public RepairAction getRepairAction();
}
