/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.testing;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.Bundle;

/**
 * no-op log.
 */
public class EmptyLog implements ILog {
  public void removeLogListener(ILogListener listener) {
  }
  public void log(IStatus status) {
  }
  public Bundle getBundle() {
    return null;
  }
  public void addLogListener(ILogListener listener) {
  }
}
