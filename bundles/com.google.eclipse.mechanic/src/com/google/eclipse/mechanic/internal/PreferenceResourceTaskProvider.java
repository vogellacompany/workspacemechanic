/*******************************************************************************
 * Copyright (C) 2010, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.google.eclipse.mechanic.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.eclipse.mechanic.ICollector;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.plugin.core.MechanicPlugin;

/**
 * This provider is loaded by the extension mechanism only, and should not
 * be instantiated by anything else.
 */
public class PreferenceResourceTaskProvider implements IResourceTaskProvider {
  // TODO(konigsberg): install a preference listener to reduce reading
  // preferences all the time.
  private IResourceTaskProvider get() {
    // This removes duplicates, but ensures insertion order.
     Set<IResourceTaskProvider> providers = new LinkedHashSet<>();
     for (IResourceTaskProvider provider : MechanicPlugin.getDefault().getMechanicPreferences().getTaskProviders()) {
       providers.add(provider);
     }

     return new CompositeResourceTaskProvider(providers);
   }

  public void collectTaskReferences(String extFilter,
      ICollector<IResourceTaskReference> collector) {
    get().collectTaskReferences(extFilter, collector);
  }

  public void collectTaskReferences(String localPath, String extFilter,
      ICollector<IResourceTaskReference> collector) {
    get().collectTaskReferences(localPath, extFilter, collector);
  }
}
