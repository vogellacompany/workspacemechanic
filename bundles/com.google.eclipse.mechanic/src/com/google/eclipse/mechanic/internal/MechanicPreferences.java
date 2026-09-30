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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.Task;
import com.google.eclipse.mechanic.plugin.core.IMechanicPreferences;
import com.google.eclipse.mechanic.plugin.core.MechanicLog;
import com.google.eclipse.mechanic.plugin.core.MechanicPlugin;
import com.google.eclipse.mechanic.plugin.core.ResourceTaskProvider;

/**
 * Implementation of {@link IMechanicPreferences} backed by the plug-in's preference store.
 */
public class MechanicPreferences implements IMechanicPreferences {

  private final MechanicLog log = MechanicLog.getDefault();

  // Sources whose initialization failed, so each failure is only logged once.
  private final Set<String> sourcesFailingInitialization = ConcurrentHashMap.newKeySet();

  private static IPreferenceStore getStore() {
    return MechanicPlugin.getDefault().getPreferenceStore();
  }

  public void addListener(IPropertyChangeListener listener) {
    getStore().addPropertyChangeListener(listener);
  }

  public void removeListener(IPropertyChangeListener listener) {
    getStore().removePropertyChangeListener(listener);
  }

  public List<IResourceTaskProvider> getTaskProviders() {
    String paths = getString(DIRS_PREF);

    ResourceTaskProviderParser parser =
        new ResourceTaskProviderParser(VariableManagerStringParser.INSTANCE);
    List<IResourceTaskProvider> providers = new ArrayList<>();
    for (String source : parser.parse(paths)) {
      try {
        providers.add(toProvider(source));
        sourcesFailingInitialization.remove(source);
      } catch (FileNotFoundException e) {
        // The default task directories usually do not exist, so a missing directory is not an error.
      } catch (IOException e) {
        if (sourcesFailingInitialization.add(source)) {
          log.logError(e);
        }
      }
    }
    return providers;
  }

  private static ResourceTaskProvider toProvider(String source) throws IOException {
    try {
      URI uri = new URI(source);
      if (uri.getScheme() != null) {
        return UriTaskProvider.newInstance(uri, UriCaches.getStateSensitiveCache(),
            UriCaches.getStateSensitiveCache());
      }
    } catch (URISyntaxException e) {
      // Falls through for paths like C:\path\to\file.
    }
    return FileTaskProvider.newInstance(new File(source));
  }

  public int getThreadSleepSeconds() {
    return cleanSleepSeconds(getInt(SLEEPAGE_PREF));
  }

  public int cleanSleepSeconds(int seconds) {
    return Math.max(seconds, MINIMUM_SLEEP_SECONDS);
  }

  public Set<String> getBlockedTaskIds() {
    return new HashSet<>(new BlockedTaskIdsParser().parse(getString(BLOCKED_PREF)));
  }

  public void setBlockedTaskIds(Set<String> ids) {
    getStore().setValue(BLOCKED_PREF, new BlockedTaskIdsParser().unparse(ids));
  }

  public void blockItem(Task item) {
    Set<String> ids = getBlockedTaskIds();
    ids.add(item.getId());
    setBlockedTaskIds(ids);
  }

  public String getHelpUrl() {
    return getString(HELP_URL_PREF);
  }

  public boolean contains(String key) {
    return getStore().contains(key);
  }

  public int getInt(String key) {
    return getStore().getInt(key);
  }

  public long getLong(String key) {
    return getStore().getLong(key);
  }

  public void setLong(String key, long value) {
    getStore().setValue(key, value);
  }

  public String getString(String key) {
    return getStore().getString(key);
  }

  public void setString(String key, String value) {
    getStore().setValue(key, value);
  }

  public boolean isShowPopup() {
    return getStore().getBoolean(SHOW_POPUP_PREF);
  }

  public void doNotShowPopup() {
    getStore().setValue(SHOW_POPUP_PREF, false);
  }

  public void showPopup() {
    getStore().setValue(SHOW_POPUP_PREF, true);
  }

  // Eclipse offers no public replacement for this version check.
  @SuppressWarnings("deprecation")
  public IStatus validatePreferencesFile(IPath path) {
    return org.eclipse.core.runtime.Preferences.validatePreferenceVersions(path);
  }
}
