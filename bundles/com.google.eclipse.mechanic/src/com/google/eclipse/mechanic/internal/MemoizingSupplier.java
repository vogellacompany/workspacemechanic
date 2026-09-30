/*******************************************************************************
 * Copyright (C) 2026, Lars Vogel and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A thread-safe {@link Supplier} that calls its delegate once and returns the cached value afterwards.
 */
final class MemoizingSupplier<T> implements Supplier<T> {
  private final Supplier<T> delegate;
  private volatile boolean initialized;
  private T value;

  private MemoizingSupplier(Supplier<T> delegate) {
    this.delegate = Objects.requireNonNull(delegate);
  }

  static <T> Supplier<T> memoize(Supplier<T> delegate) {
    return new MemoizingSupplier<>(delegate);
  }

  @Override
  public T get() {
    if (!initialized) {
      synchronized (this) {
        if (!initialized) {
          value = delegate.get();
          initialized = true;
        }
      }
    }
    return value;
  }
}
