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

import java.io.File;
import java.io.FileFilter;

/**
 * Simple suffix FileFilter. Matches the value supplied to the constructor at
 * the end of a file name (inclusive of the extension).
 *
 * <br/><br/>
 *
 * For example:
 * <pre>
 * FileFilter filter = new SuffixFileFilter(".class");
 * for (File file : dir.listFiles(filter)) {
 *   System.out.pringln("Matched file: " + file.getName());
 * }
 * </pre>
 * Would print out all files in a directory that ended with ".class".
 *
 * @author smckay@google.com (Steve McKay)
 */
public final class SuffixFileFilter implements FileFilter {

  private final String suffix;

  public SuffixFileFilter(String suffix) {
    this.suffix = suffix;
  }

  public boolean accept(File file) {
    return file.getName().endsWith(suffix);
  }
}
