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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * See {@link KeyBindingsParser}. This class represents a collection of key
 * bindings to add and another collection to remove, all of which share the
 * same scheme, plaform, contextId and action.
 *
 * @author zorzella@google.com
 */
final class KbaChangeSet {

  @SerializedName(KeyBindingsParser.SCHEME_JSON_KEY)
  private final String schemeId;
  @SerializedName(KeyBindingsParser.PLATFORM_JSON_KEY)
  private final String platform;
  @SerializedName(KeyBindingsParser.CONTEXT_JSON_KEY)
  private final String contextId;
  @SerializedName(KeyBindingsParser.ACTION_JSON_KEY)
  private final String actionLabel;
  @SerializedName(KeyBindingsParser.BINDINGS_JSON_KEY)
  private final KbaBindingList bindingList;

  KbaChangeSet(
      String schemeId,
      String platform,
      String contextId,
      String action,
      KbaBindingList bindings) {
    this.schemeId = Objects.requireNonNull(schemeId);
    this.platform = platform;
    this.contextId = Objects.requireNonNull(contextId);
    // Use this just to assert that action is a good string
    Action.fromLabel(action);
    this.actionLabel = action;
    this.bindingList = Objects.requireNonNull(bindings);
  }

  KbaChangeSet(
      KbaChangeSetQualifier qualifier,
      Iterable<KbaBinding> bindingList) {
    this(qualifier.scheme, qualifier.platform, qualifier.context, qualifier.action, bindingList);
  }
  
  KbaChangeSet(
      String schemeId,
      String platform,
      String contextId,
      String action,
      Iterable<KbaBinding> bindingList) {
    this(schemeId, platform, contextId, action, new KbaBindingList(bindingList));
  }

  public String getSchemeId() {
    return schemeId;
  }

  public String getPlatform() {
    return platform;
  }

  public String getContextId() {
    return contextId;
  }

  public String getActionLabel() {
    return actionLabel;
  }
  
  public Action getAction() {
    return Action.fromLabel(actionLabel);
  }

  public List<KbaBinding> getBindingList() {
    return bindingList.getList();
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemeId, platform, contextId, bindingList);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof KbaChangeSet)) {
      return false;
    }
    KbaChangeSet that = (KbaChangeSet) obj;
    return this.schemeId.equals(that.schemeId)
        && this.platform.equals(that.platform)
        && this.contextId.equals(that.contextId)
        && this.actionLabel.equals(that.actionLabel)
        && this.bindingList.equals(that.bindingList);
  }

  @Override
  public String toString() {
    return String.format(
        "***\nschemeId: %s\nplatform: %s\ncontextId: %s\naction: %s\nbindings: %s\n****\n",
        this.schemeId, this.platform, this.contextId, this.actionLabel, this.bindingList);
  }
  
  enum Action {
    ADD("add"),
    REMOVE("remove"),
    ;

    /**
     * Label as it appears in the .kbd JSON file
     */
    private final String label;

    Action(String label) {
      this.label = label;
    }

    public static Action fromLabel(String someLabel) {
      for (Action a : values()) {
        if (a.label.equals(someLabel))
          return a;
      }
      throw new IllegalArgumentException(String.format(
          "'%s' is not a valid action",
          someLabel));
    }

    static Action forLabel(String label) {
      for (Action action : values()) {
        if (action.label.equals(label)) {
          return action;
        }
      }
      throw new IllegalArgumentException(label);
    }
    
    @Override
    public String toString() {
      return label;
    }
  }

 // TODO: this class seems to add no value.
  static final class KbaBindingList {
    private final List<KbaBinding> list;

    public KbaBindingList(KbaBinding... list) {
      this(Arrays.asList(list));
    }
    
    public KbaBindingList(Iterable<KbaBinding> list) {
      List<KbaBinding> copy = new ArrayList<>();
      list.forEach(copy::add);
      this.list = List.copyOf(copy);
    }

    @Override
    public int hashCode() {
      return Objects.hash(list);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof KbaBindingList)) {
        return false;
      }
      KbaBindingList that = (KbaBindingList) obj;
      return this.list.equals(that.list);
    }

    @Override
    public String toString() {
      return this.list.toString();
    }

    public List<KbaBinding> getList() {
      return list;
    }
  }
}