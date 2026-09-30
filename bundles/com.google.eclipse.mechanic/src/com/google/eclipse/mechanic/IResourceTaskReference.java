/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A reference to a resource-based task.
 *
 * <p>About {@link #getPath()}: For the file and URI-based providers, their fundamental
 * use provides enough distinction, but custom providers should return references with
 * information about their source. For instance, the sample
 * {@code com.google.eclipse.mechanic.samples.InMemoryTaskProvider} puts "InMemoryTaskProvider:"
 * in front all its resources paths.
 *
 * <p>Since some tasks can come from disk, and it's expected that most of them will,
 * task scanners should rely on a null-test of {@link #asFile()} to see if that's the case.
 * So when identifying if a resource has changed, the caller might typically rely on
 * {@link #getLastModified()}, which works great if it's for a file, but not for a URL-based
 * resource. In that case, use {@link #computeMD5()}.
 */
public interface IResourceTaskReference {
  /** Return the name of the task reference. This is typically a local name. */
  String getName();

  /** Return the task reference as an input stream. */
  InputStream newInputStream() throws IOException;

  /**
   * Return the time this resource was last modified, in milliseconds since the epoch.
   */
  long getLastModified() throws IOException;

  /** 
   * Return the task reference path. Provide enough metadata to give it some distinction,
   * separate from other providers.
   */
  String getPath();

  /**
   * Return the File representation of this resource. Is {@code null} it's not a File.
   */
  File asFile();

  /**
   * Return the first eight bytes of the MD5 hash of this task, read as a little-endian long.
   */
  default long computeMD5() throws IOException {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IOException(e);
    }
    try (InputStream in = new DigestInputStream(newInputStream(), digest)) {
      in.transferTo(OutputStream.nullOutputStream());
    }
    return ByteBuffer.wrap(digest.digest()).order(ByteOrder.LITTLE_ENDIAN).getLong();
  }
}
