/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.plugin.ui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Validates a resource.as either a viable absolute directory on the local filesystem,
 * or, if configured, an https: or file: URL.
 */
class TaskResourceValidator implements IInputValidator {
  static final String PATH_DOES_NOT_EXIST = "The specified path does not exist.";
  static final String NOT_A_DIRECTORY = "Not a directory";
  static final String CANNOT_READ = "Cannot read";
  static final String UNACCEPTABLE_PROTOCOL = "This protocol is not supported.";
  static final String RELATIVE_PATH = "Local directories must be absolute paths.";
  static final String INVALID_URL = "Invalid URL";

  private final boolean allowUrls;

  public TaskResourceValidator(boolean allowUrls) {
    this.allowUrls = allowUrls;
  }

  public String isValid(String newText) {
    if (allowUrls) {
      return validateUri(newText);
    } else {
      return validateFile(newText);
    }
  }

  private String validateUri(String newText) {
    try {
      URI uri = new URI(newText);
      String scheme = uri.getScheme();
      if (!(scheme == null || "file".equals(scheme) || "https".equals(scheme))) {
        return UNACCEPTABLE_PROTOCOL;
      }
      if (scheme == null) {
        return validateFile(newText);
      }
      return null;
    } catch (URISyntaxException e) {
      return validateFile(newText);
    }
  }

  private String validateFile(String newText) {
    File file = new File(newText);
    if (!file.isAbsolute()) {
      return RELATIVE_PATH;
    }
    if (!file.exists()) {
      return PATH_DOES_NOT_EXIST;
    }
    if (!file.isDirectory()) {
      return NOT_A_DIRECTORY;
    }
    if (!file.canRead()) {
      return CANNOT_READ;
    }
    return null;
  }

  @Override
  public String toString() {
    return "TaskResourceValidator: " + allowUrls;
  }
}
