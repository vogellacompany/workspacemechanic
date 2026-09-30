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

import java.util.Arrays;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for {@link ResourceTaskProviderParser}.
 */
@RunAsJUnitTest
public class ResourceTaskProviderParserTest {
  private ResourceTaskProviderParser parser =
      new ResourceTaskProviderParser(Function.<String>identity());

  @Test

  public void testParse() {
    assertResults(parser.parse(""));
    assertResults(parser.parse("x"), "x");
    assertResults(parser.parse("x" + System.getProperty("path.separator") + "y"), "x", "y");
    assertResults(parser.parse(
        "['/home/user/path/.eclipse','http://www.google.com/directory'," +
            "'https://www.yahoo.com/directory?param\\u003dvalue%amp;term']"),
        "/home/user/path/.eclipse", "http://www.google.com/directory",
            "https://www.yahoo.com/directory?param=value%amp;term");
  }

  @Test

  public void testUnparse() {
    assertEquals("[]", parser.unparse());
    assertEquals("[\"x\"]", parser.unparse("x"));
    assertEquals("[\"x\",\"x\"]", parser.unparse("x", "x"));
    assertEquals(
        "[\"/home/user/path/.eclipse\",\"http://www.google.com/directory\"," +
            "\"https://www.yahoo.com/directory?param\\u003dvalue%amp;term\"]",
            parser.unparse("/home/user/path/.eclipse", "http://www.google.com/directory",
            "https://www.yahoo.com/directory?param=value%amp;term"));
  }

  @Test

  public void testRoundTrip() {
    testRoundTripFromJson(
        "[\"/home/user/path/.eclipse\",\"http://www.google.com/directory\"," +
            "\"https://www.yahoo.com/directory?param\\u003dvalue%amp;term\"]");
    testRoundTripFromList("/home/user/path/.eclipse", "http://www.google.com/directory",
            "https://www.yahoo.com/directory?param=value%amp;term");
  }

  private void testRoundTripFromList(String... items) {
    assertTrue(Arrays.deepEquals(items, parser.parse(parser.unparse(items))));
  }

  private void testRoundTripFromJson(String string) {
    assertEquals(string, parser.unparse(parser.parse(string)));
  }

  // TODO(konigsberg): Move to common area, BlockedTaskIdsParserTest also uses this.
  private void assertResults(String[] actual, String... expected) {
    if (!Arrays.deepEquals(actual, expected)) {
      fail("Expected " + Arrays.deepToString(expected) + " but got " +
          Arrays.deepToString(actual));
    }
  }
}
