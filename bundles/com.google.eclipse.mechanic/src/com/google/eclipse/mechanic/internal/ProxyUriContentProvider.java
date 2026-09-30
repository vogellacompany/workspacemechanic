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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

public final class ProxyUriContentProvider implements IUriContentProvider {

  private volatile IUriContentProvider delegate;

  public ProxyUriContentProvider(IUriContentProvider delegate) {
    this.delegate = delegate;
  }

  public void set(IUriContentProvider delegate) {
    this.delegate = Objects.requireNonNull(delegate);
  }

  public InputStream get(URI uri) throws IOException {
    return delegate.get(uri);
  }

  public long lastModifiedTime(URI uri) throws IOException {
    return delegate.lastModifiedTime(uri);
  }

  public void clear() {
    delegate.clear();
  }
}
