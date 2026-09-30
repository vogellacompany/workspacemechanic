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

import java.util.Objects;

import com.google.eclipse.mechanic.core.keybinding.KbaChangeSet.Action;

/**
 * Qualifies a binding by scheme/platform/context/action
 * 
 * <p>If platform is {@code null}, this applies to all platforms.
 */
// TODO: leverage this class in KbaChangeSet
final class KbaChangeSetQualifier {
  final String scheme;
  final String platform;
  final String context;
  final String action;

  public KbaChangeSetQualifier(
      final String scheme,
      final String platform,
      final String context,
      final String actionLabel) {
    this(scheme,
        platform,
        context,
        Action.fromLabel(actionLabel));
  }

  public KbaChangeSetQualifier(
      final String scheme,
      final String platform,
      final String context,
      final Action action) {
    this.scheme = Objects.requireNonNull(scheme);
    this.platform = platform;
    this.context = Objects.requireNonNull(context);
    this.action = action.toString();
  }

  public Action getAction() {
    return Action.fromLabel(action);
  }
 
  @Override
  public String toString() {
    return String.format(
        "scheme: '%s', platform: '%s', context: '%s', action: '%s'",
        scheme, platform, context, action);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof KbaChangeSetQualifier)) {
      return false;
    }
    KbaChangeSetQualifier that = (KbaChangeSetQualifier)obj;
    return Objects.equals(this.scheme,  that.scheme) 
        && Objects.equals(this.platform,  that.platform)
        && Objects.equals(this.context,  that.context)
        && Objects.equals(this.action,  that.action);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(this.scheme, this.platform, this.context, this.action );
  }
}