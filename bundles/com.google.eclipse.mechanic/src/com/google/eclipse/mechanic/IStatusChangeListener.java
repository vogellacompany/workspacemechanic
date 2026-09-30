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
 * Listener for changes to the TaskService status. Register one of these
 * with the TaskService to get notifications of status change.
 *
 * @author smckay@google.com (Steve McKay)
 */
public interface IStatusChangeListener {

  void statusChanged(StatusChangedEvent event);

}
