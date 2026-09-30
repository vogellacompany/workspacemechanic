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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for {@link UriTaskProviderModelParser}
 */
@RunAsJUnitTest
public class UriTaskProviderModelParserTest {
  private static final String GOOD_METADATA_TEXT =
      "metadata : {" +
      "  name: 'green hornet'," +
      "  description: 'i wear green and i fight crime'," +
      "  contact: 'greenhornet.com'" +
      "}";

  private static final UriTaskProviderModel.Metadata GOOD_METADATA =
      new UriTaskProviderModel.Metadata(
      "green hornet", "i wear green and i fight crime", "greenhornet.com");

  private static final String BAD_METADATA_TEXT =
    "metadata : {" +
    "}";

  private static final String MINIMAL_METADATA_TEXT =
    "metadata : {" +
    "  description: 'yo ho ho'" +
    "}";

  private static final UriTaskProviderModel.Metadata MINIMAL_METADATA =
      new UriTaskProviderModel.Metadata(
      null, "yo ho ho", null);

  private static final String TASKS_TEXT = 
      "tasks: [" +
      "  'http://www.google.com/path'," +
      "  'http://www.google.com/path2'" +
      "]";

  private static final List<URI> TASKS = toList("http://www.google.com/path", "http://www.google.com/path2");

  private static final String RELATIVE_TASKS_TEXT = 
      "tasks: [" +
      "  'path'," +
      "  'path2'" +
      "]";

  private static final List<URI> RELATIVE_TASKS = toList("path", "path2");


  private static final String BAD_TASKS_TEXT = 
    "tasks: [" +
    "  'stetsons are cool'" +
    "]";

  @Test

  public void testNoSections_invalid() {
    try {
      UriTaskProviderModelParser.readForTests("{ }");
      fail("Exception expected");
    } catch(RuntimeException e) {
    }
  }

  @Test

  public void testNoTasks_invalid() {
    try {
      UriTaskProviderModelParser.readForTests("{ metadata : { } }");
      fail("Exception expected");
    } catch(RuntimeException e) {
    }
  }

  @Test

  public void testNoType_invalid() {
    try {
      UriTaskProviderModelParser.readForTests("{ tasks : [], metadata : { } }");
      fail("Exception expected");
    } catch(RuntimeException e) {
    }
  }

  @Test

  public void testBadType_invalid() {
    try {
      UriTaskProviderModelParser.readForTests("{ type : 'com.blah', tasks : [], metadata : { } }");
      fail("Exception expected");
    } catch(RuntimeException e) {
    }
  }

  @Test

  public void testNoMetadata_invalid() {
    try {
      UriTaskProviderModelParser.readForTests("{ tasks : [ ] }");
      fail("Exception expected");
    } catch(RuntimeException e) {
    }
  }

  @Test

  public void testCombination_1() {
    testCombination(GOOD_METADATA_TEXT, TASKS_TEXT, new UriTaskProviderModel(GOOD_METADATA, TASKS));
  }

  @Test

  public void testCombination_2() {
    testCombination(MINIMAL_METADATA_TEXT, TASKS_TEXT, new UriTaskProviderModel(MINIMAL_METADATA, TASKS));
  }

  @Test

  public void testBadCombination_tasks() {
    try {
      parse(GOOD_METADATA_TEXT, BAD_TASKS_TEXT);
      fail("exception expected");
    } catch(RuntimeException e) {
      assertEquals(URISyntaxException.class, e.getCause().getClass());
      assertTrue(e.getMessage().contains("stetsons are cool"));
    }
  }

  @Test

  public void testBadCombination_metadata() {
    try {
      parse(BAD_METADATA_TEXT, TASKS_TEXT);
      fail("exception expected");
    } catch(RuntimeException e) {
      assertEquals("com.google.gson.JsonParseException", e.getClass().getName());
      assertEquals("description is missing", e.getMessage());
    }
  }

  @Test

  public void testRelativeResources() {
    UriTaskProviderModel expected = new UriTaskProviderModel(GOOD_METADATA, RELATIVE_TASKS);
    UriTaskProviderModel actual = parse(GOOD_METADATA_TEXT, RELATIVE_TASKS_TEXT);
    assertEquals(expected, actual);
  }

  private void testCombination(String metadataText, String tasksText,
      UriTaskProviderModel expected) {
    UriTaskProviderModel actual = parse(metadataText, tasksText);
    assertEquals(expected.getMetadata(), actual.getMetadata());
    assertEquals(expected.getTasks(), actual.getTasks());
    assertEquals(expected, actual);
  }

  private UriTaskProviderModel parse(String metadataText, String tasksText) {
    String text = "{ type : 'com.google.eclipse.mechanic.UriTaskProviderModel', " +
      metadataText + ", " + tasksText + "}";
    UriTaskProviderModel actual = UriTaskProviderModelParser.readForTests(text);
    return actual;
  }

  private static List<URI> toList(String... raw) {
    ArrayList<URI> list = new ArrayList<>();
    for (String string : raw) {
      list.add(URI.create(string));
    }
    return list;
  }
}
