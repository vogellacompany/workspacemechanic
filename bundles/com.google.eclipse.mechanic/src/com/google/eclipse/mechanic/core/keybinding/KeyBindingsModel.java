/*******************************************************************************
 * Copyright (C) 2010, Google Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.core.keybinding;

import java.util.List;
import java.util.Objects;

import com.google.eclipse.mechanic.core.keybinding.KbaChangeSet.Action;
import com.google.gson.annotations.SerializedName;

/**
 * A java representation of a .kbd task, abbreviated as KBA.
 *
 * <p>This, in disk, is represented as a JSON string.
 * 
 * <p>This class operates with primitive types (Strings, ints, lists), not with
 * Eclipse constructs.
 *
 * @author zorzella@google.com
 */
class KeyBindingsModel {

  @SerializedName(KeyBindingsParser.METADATA_JSON_KEY)
  private final KbaMetaData kbaMetadata;
  @SerializedName(KeyBindingsParser.CHANGE_SETS_JSON_KEY)
  private final List<KbaChangeSet> kbaChangeSetList;

  public KeyBindingsModel(List<KbaChangeSet> changeSetList, KbaMetaData metadata) {
    this.kbaChangeSetList = filteredChangeSetList(changeSetList);
    this.kbaMetadata = metadata;
  }

  private static List<KbaChangeSet> filteredChangeSetList(
      List<KbaChangeSet> changeSetList) {
    // TODO: support remove
    return changeSetList.stream()
        .filter(source -> KeyboardBindingsTask.ENABLE_EXP_REM() || source.getAction() == Action.ADD)
        .toList();
  }

  public List<KbaChangeSet> getKeyBindingsChangeSetsWith(Action action) {
    return kbaChangeSetList.stream().filter(toTest -> toTest.getAction() == action).toList();
  }

  public Iterable<KbaChangeSet> getKeyBindingsChangeSets() {
    return kbaChangeSetList;
  }

  public KbaMetaData getMetadata() {
    return kbaMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.kbaChangeSetList, this.kbaMetadata);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof KeyBindingsModel)) {
      return false;
    }
    KeyBindingsModel that = (KeyBindingsModel)obj;
    return
      this.kbaChangeSetList.equals(that.kbaChangeSetList)
        &&
      this.kbaMetadata.equals(that.kbaMetadata);
  }

  @Override
  public String toString() {
    return String.format(
        "metadata: %s, keyBindingsChangeSets: %s",
        this.kbaMetadata, this.kbaChangeSetList);
  }

  public static final class KbaMetaData {

    private final String description;

    public KbaMetaData(String description) {
      this.description = Objects.requireNonNull(description);
    }

    @Override
    public int hashCode() {
      return Objects.hash(description);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof KbaMetaData)) {
        return false;
      }
      KbaMetaData that = (KbaMetaData)obj;
      return
        this.description.equals(that.description);
    }

    @Override
    public String toString() {
      return String.format(
          "description: %s",
          this.description);
    }

    public String getDescription() {
      return description;
    }
  }
}
