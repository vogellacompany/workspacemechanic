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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.PreferenceReconcilerTask.CompositeReconciler;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.ContainsMatcher;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.EqualsMatcher;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.ImmutablePreference;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.ListAppendResolver;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.Matcher;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.PathAppendResolver;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.Preference;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.Reconciler;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.Resolver;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.SimpleResolver;
import com.google.eclipse.mechanic.PreferenceReconcilerTask.StringReplaceResolver;
import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for PreferenceReconcilerTask.
 *
 * @author smckay@google.com (Steve McKay)
 */
@RunAsJUnitTest
public class PreferenceReconcilerTaskTest {

  private static final Preference WEE = new ImmutablePreference(
      "/instance/foo", "bar", "wee");

  private static final Preference HAA = new ImmutablePreference(
      "/instance/foo", "bar", "haa");

  @Test

  public void testEqualsMatcher() {
    Matcher m = new EqualsMatcher(HAA);
    assertTrue(m.matches("haa"));
    assertFalse(m.matches("foo:bar:wee:haa"));
    assertFalse(m.matches("wee"));
  }

  @Test

  public void testContainsMatcher() {
    Matcher m = new ContainsMatcher("haa");
    assertTrue(m.matches("haa"));
    assertTrue(m.matches("foo:bar:wee:haa"));
    assertFalse(m.matches("wee:sna"));
  }

  @Test

  public void testSimpleResolver() {
    Resolver r = new SimpleResolver(HAA);
    assertEquals("haa", r.resolve("wee"));
  }

  @Test

  public void testListAppendResolver() {
    Resolver r = new ListAppendResolver(",", HAA);
    assertEquals("wee,haa", r.resolve("wee"));
  }

  @Test

  public void testListAppendResolverNull() {
    Resolver r = new ListAppendResolver(",", HAA);
    assertEquals("haa", r.resolve(null));
  }

  @Test

  public void testPathAppendResolver() {
    Resolver r = new PathAppendResolver(HAA);
    assertEquals("wee" + File.pathSeparator + "haa",
        r.resolve("wee"));
  }

  @Test

  public void testStringReplaceResolver() {
    String test = "grrammygrr";
    assertEquals("Fizzyammygrr",
        new StringReplaceResolver("^grr", "Fizzy").resolve(test));
    assertEquals("grrammyFizzy",
        new StringReplaceResolver("grr$", "Fizzy").resolve(test));
    assertEquals("FizzyammyFizzy",
        new StringReplaceResolver("grr", "Fizzy").resolve(test));
  }

  @Test

  public void testCompositeReconcilerDoesNothing() {

    IEclipsePreferences root = mock(IEclipsePreferences.class);
    IEclipsePreferences wee = mock(IEclipsePreferences.class);

    when(root.node(WEE.getPath())).thenReturn(wee);

    // should return the correct value
    when(wee.get(WEE.getKey(), null)).thenReturn(WEE.getValue());

    // doesn't need reconciliation
    Reconciler reconciler = new CompositeReconciler(root, WEE,
        new EqualsMatcher(WEE), new SimpleResolver(WEE));

    assertTrue(reconciler.isReconciled());
  }

  @Test

  public void testCompositeReconcilerReconciles() throws Exception {

    IEclipsePreferences root = mock(IEclipsePreferences.class);
    IEclipsePreferences wee = mock(IEclipsePreferences.class);

    when(root.node(WEE.getPath()))
        .thenReturn(wee);

    // should return an incorrect value
    when(wee.get(WEE.getKey(), null))
        .thenReturn(HAA.getValue());

    // It's a weird idiom, but we have to call this method before the
    // replay so that easy-mock will be aware of the fact that we will
    // be calling it after the replay. Else we get an exception.
    wee.put(WEE.getKey(), WEE.getValue());

    // needs reconciliation
    Reconciler reconciler = new CompositeReconciler(root, HAA,
        new EqualsMatcher(WEE), new SimpleResolver(WEE));

    assertFalse(reconciler.isReconciled());
    reconciler.reconcile(); // causes wee.put to be called.

    verify(wee).flush();
  }
}
