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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.IMechanicService;
import com.google.eclipse.mechanic.IStatusChangeListener;
import com.google.eclipse.mechanic.MechanicStatus;
import com.google.eclipse.mechanic.RepairDecisionProvider;
import com.google.eclipse.mechanic.RepairManager;
import com.google.eclipse.mechanic.StatusChangedEvent;
import com.google.eclipse.mechanic.Task;
import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for {@link StateSensitiveCache}.
 */
@RunAsJUnitTest
public class StateSensitiveCacheTest {

  private static final URI WWW_GOOGLE_COM = TestUriContentProvider.WWW_GOOGLE_COM;

  private final TestUriContentProvider delegate = new TestUriContentProvider();

  static class TestService implements IMechanicService {
    public IStatusChangeListener listener;

    public void start() {
    }

    public void stop() {
    }

    public boolean isStopped() {
      return false;
    }

    public void addTaskStatusChangeListener(
        IStatusChangeListener statusChangeListener) {
      this.listener = statusChangeListener;
    }

    public void removeTaskStatusChangeListener(
        IStatusChangeListener statusChangeListener) {
    }

    public RepairManager getRepairManager(RepairDecisionProvider rdp) {
      return null;
    }

    public Collection<Task> getAllKnownTasks() {
      return null;
    }

    public int getFailingItemCount() {
      return 0;
    }
  }

  @Test

  public void testNPE() {
    try {
      new StateSensitiveCache(null, null);
      fail("npe expected");
    } catch(NullPointerException e) {
    }
    try {
      new StateSensitiveCache(mock(IMechanicService.class), null);
      fail("npe expected");
    } catch(NullPointerException e) {
    }
    try {
      new StateSensitiveCache(null, delegate);
      fail("npe expected");
    } catch(NullPointerException e) {
    }
  }

  @Test

  public void testInitialize() {
    IMechanicService service = mock(IMechanicService.class);

    StateSensitiveCache cache = new StateSensitiveCache(service, delegate);
    cache.initialize();
  }


  @Test


  public void testDispose() {
    IMechanicService service = mock(IMechanicService.class);
    StateSensitiveCache cache = new StateSensitiveCache(service, delegate);
    cache.initialize();
    cache.dispose();
  }

  @Test

  public void testGet_clear() throws Exception {
    TestService service = new TestService();
    StateSensitiveCache cache = new StateSensitiveCache(service, delegate);

    cache.initialize();

    assertEquals(0, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    assertEquals("asdf", read(cache.get(WWW_GOOGLE_COM)));
    assertEquals(1, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    // This status doesn't clear the cache.
    service.listener.statusChanged(new StatusChangedEvent(MechanicStatus.FAILED));

    assertEquals("asdf", read(cache.get(WWW_GOOGLE_COM)));
    assertEquals(2, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    // This call will cause the cache to clear, and so, the
    // inner raw reading count will increase.
    service.listener.statusChanged(new StatusChangedEvent(MechanicStatus.UPDATING));

    assertEquals("asdf", read(cache.get(WWW_GOOGLE_COM)));
    assertEquals(3, delegate.fetchCount());
    assertEquals(1, delegate.clearCount());
  }

  @Test

  public void testLastmod_clear() throws Exception {
    TestService service = new TestService();
    StateSensitiveCache cache = new StateSensitiveCache(service, delegate);

    cache.initialize();

    assertEquals(0, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    assertEquals(1L, cache.lastModifiedTime(WWW_GOOGLE_COM));
    assertEquals(1, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    // This status doesn't clear the cache.
    service.listener.statusChanged(new StatusChangedEvent(MechanicStatus.FAILED));

    assertEquals(1L, cache.lastModifiedTime(WWW_GOOGLE_COM));
    assertEquals(2, delegate.fetchCount());
    assertEquals(0, delegate.clearCount());

    // This call will cause the cache to clear, and so, the
    // inner raw reading count will increase.
    service.listener.statusChanged(new StatusChangedEvent(MechanicStatus.UPDATING));

    assertEquals(1L, cache.lastModifiedTime(WWW_GOOGLE_COM));
    assertEquals(3, delegate.fetchCount());
    assertEquals(1, delegate.clearCount());
  }

  private String read(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    return reader.readLine();
  }
}
