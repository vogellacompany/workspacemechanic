/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.plugin.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.Evaluator;
import com.google.eclipse.mechanic.RepairAction;
import com.google.eclipse.mechanic.Task;
import com.google.eclipse.mechanic.internal.MechanicPreferences;
import com.google.eclipse.mechanic.tests.internal.RunAsPluginTest;

/**
 * Tests for {@link MechanicPreferences}
 */
@RunAsPluginTest
public class MechanicPreferencesTest {

  private final IMechanicPreferences mechanicPreferences = new MechanicPreferences();

  @Test

  public void testGetHelpUrl() {
    setToDefault(IMechanicPreferences.HELP_URL_PREF);
    assertEquals(
        "http://code.google.com/a/eclipselabs.org/p/workspacemechanic/wiki/GettingStarted",
        mechanicPreferences.getHelpUrl());
  }

  @BeforeEach

  public void setUp() throws Exception {
    mechanicPreferences.setBlockedTaskIds(Collections.<String>emptySet());
    assertEquals(0, mechanicPreferences.getBlockedTaskIds().size());
  }

  @Test

  public void testSetBlockedTaskIds() {
    mechanicPreferences.setBlockedTaskIds(Collections.singleton("asdf"));
    assertContentsAnyOrder(mechanicPreferences.getBlockedTaskIds(), "asdf");

    mechanicPreferences.setBlockedTaskIds(Collections.<String>emptySet());
    assertEquals(0, mechanicPreferences.getBlockedTaskIds().size());

    Set<String> set = new HashSet<String>();
    set.add("123");
    set.add("234");
    mechanicPreferences.setBlockedTaskIds(set);
    assertContentsAnyOrder(mechanicPreferences.getBlockedTaskIds(), "123", "234");
  }


  @Test


  public void testBlockItem() {
    mechanicPreferences.blockItem(createTask("abcdefg"));
    assertContentsAnyOrder(mechanicPreferences.getBlockedTaskIds(), "abcdefg");

    mechanicPreferences.blockItem(createTask("hijklmnop"));
    assertContentsAnyOrder(mechanicPreferences.getBlockedTaskIds(), "abcdefg", "hijklmnop");

    mechanicPreferences.blockItem(createTask("qrstuv"));
    assertContentsAnyOrder(
        mechanicPreferences.getBlockedTaskIds(), "abcdefg", "hijklmnop", "qrstuv");

    mechanicPreferences.blockItem(createTask("wxyz"));
    assertContentsAnyOrder(
        mechanicPreferences.getBlockedTaskIds(), "abcdefg", "hijklmnop", "qrstuv", "wxyz");
  }

  @Test

  public void testShowPopup() {
    mechanicPreferences.showPopup();
    assertTrue(mechanicPreferences.isShowPopup());
    mechanicPreferences.doNotShowPopup();
    assertFalse(mechanicPreferences.isShowPopup());
    mechanicPreferences.showPopup();
    assertTrue(mechanicPreferences.isShowPopup());
  }

  @Test

  public void testCleanSleepSeconds() {
    assertEquals(10, mechanicPreferences.cleanSleepSeconds(8));
    assertEquals(11, mechanicPreferences.cleanSleepSeconds(11));
  }

  @Test

  public void testSetLongGetLong() {
    String pref = "TEST_PREF";
    assertEquals(0, mechanicPreferences.getLong(pref));
    mechanicPreferences.setLong(pref, 1);
    assertEquals(1, mechanicPreferences.getLong(pref));
    mechanicPreferences.setLong(pref, Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE, mechanicPreferences.getLong(pref));
  }

  @Test

  public void testWithFunnyKey() {
    String pref =
        "com.google.eclipse.mechanic.internal.PreferenceFileTaskScanner$" +
        "LastmodEpfTask@" + 
        "/Users/kberg/.eclipse/mechanic/showlinenumbers.epf_lastmod";

    assertEquals(0, mechanicPreferences.getLong(pref));
    mechanicPreferences.setLong(pref, 1);
    assertEquals(1, mechanicPreferences.getLong(pref));
    mechanicPreferences.setLong(pref, Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE, mechanicPreferences.getLong(pref));
  }

  @Test

  public void testGetTaskDirectories() {
    setToDefault(IMechanicPreferences.DIRS_PREF);

    String taskDirs = mechanicPreferences.getString(IMechanicPreferences.DIRS_PREF);
    assertEquals("${user_homedir}/.eclipse/mechanic:${mechanic_configuration_path}/mechanic", taskDirs);
  }

  @Test

  public void testContains() {
    String pref = "XYZPDQ";
    assertFalse(mechanicPreferences.contains(pref));
    mechanicPreferences.setLong(pref, 1L);
    assertTrue(mechanicPreferences.contains(pref));
    setToDefault(pref);
    assertFalse(mechanicPreferences.contains(pref));
  }

  //public static int getThreadSleepSeconds() {
  //public static String doVariableSubstitution(String input) {
  //public static IStatus validatePreferencesFile(IPath path) {

  /**
   * Create a mock task with the given id.
   */
  private Task createTask(final String id) {
    return new Task() {
      public String getId() {
        return id;
      }

      public String getTitle() {
        throw new AssertionError();
      }

      public String getDescription() {
        throw new AssertionError();
      }

      public Evaluator getEvaluator() {
        throw new AssertionError();
      }

      public RepairAction getRepairAction() {
        throw new AssertionError();
      }
    };
  }

  private void assertContentsAnyOrder(Collection<?> stuff, Object... elements) {
    assertEquals(elements.length, stuff.size());
    for (Object object : elements) {
      assertTrue(stuff.contains(object), String.valueOf(object));
    }
  }

  private void setToDefault(String pref) {
    MechanicPlugin.getDefault().getPluginPreferences().setToDefault(pref);
  }
}
