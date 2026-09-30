/*******************************************************************************
 * Copyright (C) 2012, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;

import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.Task;
import com.google.eclipse.mechanic.plugin.core.IMechanicPreferences;
import com.google.eclipse.mechanic.plugin.core.OldMechanicPreferences;

/**
 * Implementation of {@link IMechanicPreferences}, relies on {@link OldMechanicPreferences}.
 */
@SuppressWarnings("deprecation") // Uses the old-style API.
public class MechanicPreferences implements IMechanicPreferences {

  public void addListener(IPropertyChangeListener listener) {
    OldMechanicPreferences.addListener(listener);
  }

  public void removeListener(IPropertyChangeListener listener) {
    OldMechanicPreferences.removeListener(listener);
  }

  public List<IResourceTaskProvider> getTaskProviders() {
    return OldMechanicPreferences.getTaskProviders();
  }

  public int getThreadSleepSeconds() {
    return OldMechanicPreferences.getThreadSleepSeconds();
  }

  public int cleanSleepSeconds(int seconds) {
    return OldMechanicPreferences.cleanSleepSeconds(seconds);
  }

  public Set<String> getBlockedTaskIds() {
    return OldMechanicPreferences.getBlockedTaskIds();
  }

  public void setBlockedTaskIds(Set<String> ids) {
    OldMechanicPreferences.setBlockedTaskIds(ids);
  }

  public void blockItem(Task item) {
    OldMechanicPreferences.blockItem(item);
  }

  public String getHelpUrl() {
    return OldMechanicPreferences.getHelpUrl();
  }

  public boolean contains(String key) {
    return OldMechanicPreferences.contains(key);
  }

  public int getInt(String key) {
    return OldMechanicPreferences.getInt(key);
  }

  public long getLong(String key) {
    return OldMechanicPreferences.getLong(key);
  }

  public void setLong(String key, long value) {
    OldMechanicPreferences.setLong(key, value);
  }

  public String getString(String key) {
    return OldMechanicPreferences.getString(key);
  }

  public void setString(String key, String value) {
    OldMechanicPreferences.setString(key, value);
  }

  public boolean isShowPopup() {
    return OldMechanicPreferences.isShowPopup();
  }

  public void doNotShowPopup() {
    OldMechanicPreferences.doNotShowPopup();
  }

  public void showPopup() {
    OldMechanicPreferences.showPopup();
  }

  public IStatus validatePreferencesFile(IPath path) {
    return OldMechanicPreferences.validatePreferencesFile(path);
  }
}
