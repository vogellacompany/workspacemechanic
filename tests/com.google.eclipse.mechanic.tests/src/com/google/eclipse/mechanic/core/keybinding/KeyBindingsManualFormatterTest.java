/*******************************************************************************
 * Copyright (C) 2011, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.core.keybinding;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.core.keybinding.KbaChangeSet.Action;
import com.google.eclipse.mechanic.core.keybinding.KbaChangeSet.KbaBindingList;
import com.google.eclipse.mechanic.core.keybinding.KeyBindingsManualFormatter.BindingType;

/**
 * Tests for {@link KeyBindingsManualFormatter}
 */
public class KeyBindingsManualFormatterTest {

  private static final KbaChangeSetQualifier QUALIFIER = new KbaChangeSetQualifier(
      "org.eclipse.ui.defaultAcceleratorConfiguration",
      null, // platform
      "org.eclipse.ui.contexts.window",
      Action.ADD.toString());
  
  @Test
  public void testARoundTripCommandWithParams() {
    
    KbaBinding expectedKbaBinding = kbaBindingCommandWithParams();
    Map<KbaChangeSetQualifier, KbaChangeSet> map = kbaMap(new KbaBindingList(
        expectedKbaBinding));
    
    String json = KeyBindingsManualFormatter.getBindingsPrintout(BindingType.USER, map, "");
    KeyBindingsModel kbaFromJson = KeyBindingsParser.deSerialize(new StringReader(json));
    List<KbaChangeSet> keyBindingsChangeSetsToAdd =
        kbaFromJson.getKeyBindingsChangeSetsWith(Action.ADD);
    assertEquals(1, keyBindingsChangeSetsToAdd.size());
    assertEquals(1, keyBindingsChangeSetsToAdd.get(0).getBindingList().size());
    
    KbaBinding actualKbaBinding = keyBindingsChangeSetsToAdd.get(0).getBindingList().get(0);
    
    assertEquals(expectedKbaBinding, actualKbaBinding);
  }

  @Test
  public void testARoundTripCommandWithNoParams() {
    
    KbaBinding expectedKbaBinding = kbaBindingCommandWithNoParams();
    Map<KbaChangeSetQualifier, KbaChangeSet> map = kbaMap(new KbaBindingList(
        expectedKbaBinding));
    
    String json = KeyBindingsManualFormatter.getBindingsPrintout(BindingType.USER, map, "");
    KeyBindingsModel kbaFromJson = KeyBindingsParser.deSerialize(new StringReader(json));

    List<KbaChangeSet> keyBindingsChangeSetsToAdd =
        kbaFromJson.getKeyBindingsChangeSetsWith(Action.ADD);
    assertEquals(1, keyBindingsChangeSetsToAdd.size());
    assertEquals(1, keyBindingsChangeSetsToAdd.get(0).getBindingList().size());
    
    KbaBinding actualKbaBinding = keyBindingsChangeSetsToAdd.get(0).getBindingList().get(0);
    
    assertEquals(expectedKbaBinding, actualKbaBinding);
  }

  @Test
  public void testEscapingDescription() {
    testEscapingDescription("x");
    testEscapingDescription("Joe's Apartment");
    testEscapingDescription("Thank you, \"The Management\"");
    testEscapingDescription("Found on my c:\\ drive");
  }

  private void testEscapingDescription(String description) {
    try {
      String json = KeyBindingsManualFormatter.getBindingsPrintout(
          BindingType.USER,
          Map.<KbaChangeSetQualifier, KbaChangeSet>of(),
          description);
  
      KeyBindingsModel kbaFromJson = KeyBindingsParser.deSerialize(new StringReader(json));
      assertEquals(description, kbaFromJson.getMetadata().getDescription());
    } catch(RuntimeException e) {
      throw new RuntimeException("For description: " + description, e);
    }
  }

  private static Map<KbaChangeSetQualifier, KbaChangeSet> kbaMap(
      KbaBindingList kbaBindingList) {
    return Map.of(QUALIFIER, kbaChangeSetFor(kbaBindingList));
  }

  private static KbaChangeSet kbaChangeSetFor(KbaBindingList kbaBindingList) {
    return new KbaChangeSet(QUALIFIER.scheme, QUALIFIER.platform, QUALIFIER.context, QUALIFIER.action, kbaBindingList);
  }

  private static KbaBinding kbaBindingCommandWithNoParams() {
    KbaBinding kbaBinding = new KbaBinding(
      "Ctrl+/",
      "org.eclipse.jdt.ui.edit.text.java.toggle.comment",
      new HashMap<String, String>());
    return kbaBinding;
  }

  private static KbaBinding kbaBindingCommandWithParams() {
    Map<String,String> params =
        Map.of("org.eclipse.ui.views.showView.viewId", "org.eclipse.jdt.debug.ui.DisplayView");
    
    KbaBinding kbaBinding = new KbaBinding(
      "Shift+Alt+Q I",
      "org.eclipse.ui.views.showView",
      params);
    return kbaBinding;
  }
}
