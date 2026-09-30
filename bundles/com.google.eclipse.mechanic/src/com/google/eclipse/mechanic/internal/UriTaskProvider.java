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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import com.google.eclipse.mechanic.ICollector;
import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.plugin.core.ResourceTaskProvider;
import com.google.gson.JsonSyntaxException;

/**
 * Provides information about tasks that come from URIs.
 */
public final class UriTaskProvider extends ResourceTaskProvider {
  private final URI uri;

  private UriTaskProviderModel model;

  private final IUriContentProvider stateSensitiveCache;
  private final IUriContentProvider longTermCache;

  private final class TaskReference implements IResourceTaskReference {
    private final URI uri;

    public TaskReference(URI uri) {
      this.uri = uri;
    }

    public InputStream newInputStream() throws IOException {
      return longTermCache.get(uri);
    }

    public String getName() {
      return uri.getPath();
    }

    public String getPath() {
      return uri.toString();
    }

    @Override
    public String toString() {
      return uri.toString();
    }

    public File asFile() {
      return null;
    }

    public long getLastModified() throws IOException {
      return longTermCache.lastModifiedTime(uri);
    }

  }

  private UriTaskProvider(URI uri, IUriContentProvider stateSensitiveCache,
      IUriContentProvider longTermCache) {
    this.uri = Objects.requireNonNull(uri);
    this.stateSensitiveCache = Objects.requireNonNull(stateSensitiveCache);
    this.longTermCache = Objects.requireNonNull(longTermCache);
  }

  /**
   * Create a new instance.
   *
   * <p>The constructor takes two caches. One has a shorter lifetime and so is more frequently
   * polled, to get the list of tasks to process. The other has a longer lifetime (12 hours
   * ATM) and is used to cache actual tasks (e.g. .epf files) which are much less likely to change.
   *
   * @param uri The URI that contains information about tasks.
   * @param stateSensitiveCache short term cache.
   * @param longTermCache long term cache.
   */
  public static UriTaskProvider newInstance(
      URI uri,
      IUriContentProvider stateSensitiveCache,
      IUriContentProvider longTermCache) throws IOException {

    UriTaskProvider instance = new UriTaskProvider(
        uri, stateSensitiveCache, longTermCache);
    instance.setModel();
    return instance;
  }

  private void setModel() throws IOException, JsonSyntaxException {
    InputStream inputStream = stateSensitiveCache.get(uri);
    try {
      model = UriTaskProviderModelParser.read(inputStream);
    } finally {
      inputStream.close();
    }
  }

  public void collectTaskReferences(String localPath, String filter, ICollector<IResourceTaskReference> collector) {
    // This is for class files, and we don't need to implement this. The function should be
    // removed anyway.
  }

  public void collectTaskReferences(String filterText, ICollector<IResourceTaskReference> collector) {
    for (URI uri : model.getTasks()) {
      if (uri.getPath().endsWith(filterText)) {
        if (!uri.isAbsolute()) {
          // resolve 
          uri = UriTaskProvider.this.uri.resolve(uri);
        }
        collector.collect(new TaskReference(uri));
      }
    }
  }

  @Override
  public String toString() {
    return "URI Provider: " + uri.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof UriTaskProvider)) {
      return false;
    }

    return ((UriTaskProvider) obj).uri.equals(uri);
  }

  @Override
  public int hashCode() {
    return uri.hashCode();
  }
}
