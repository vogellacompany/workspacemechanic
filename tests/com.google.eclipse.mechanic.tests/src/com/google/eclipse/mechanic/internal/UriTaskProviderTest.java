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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.ListCollector;
import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for (@link {@link UriTaskProvider}.
 *
 */
@RunAsJUnitTest
public class UriTaskProviderTest {
  private static final String CONTENT =
    "{ type : 'com.google.eclipse.mechanic.UriTaskProviderModel', " +
    "metadata : {" +
    "  name: 'green hornet'," +
    "  description: 'i wear green and i fight crime'," +
    "  contact: 'greenhornet.com'" +
    "}, " + 
    "tasks: [" +
    "  'http://www.google.com/foo/bar/baz', " +
    "  'path'," +
    "  'path2'" +
    "]" +
    "} ";

  @Test

  public void testRelativize() throws Exception {
    URI uri = URI.create("http://www.testuri.com/mechanic/tasks/schema");

    IUriContentProvider cache = mock(IUriContentProvider.class);
    when(cache.get(uri)).thenReturn(asInputStream(CONTENT));

    UriTaskProvider provider = UriTaskProvider.newInstance(uri, cache, cache);
    ListCollector<IResourceTaskReference> collector = ListCollector.create();
    provider.collectTaskReferences("", collector);

    List<IResourceTaskReference> taskReferences = collector.get();
    assertEquals(3, taskReferences.size());
    assertEquals("http://www.google.com/foo/bar/baz", taskReferences.get(0).getPath());
    assertEquals("http://www.testuri.com/mechanic/tasks/path", taskReferences.get(1).getPath());
    assertEquals("http://www.testuri.com/mechanic/tasks/path2", taskReferences.get(2).getPath());
  }

	private InputStream asInputStream(String content) {
    return new ByteArrayInputStream(content.getBytes());
  }
}
