/*******************************************************************************
 * Copyright (C) 2011, Alex Blewitt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

/**
 * Tests for FileTaskProvider.
 */
@RunAsJUnitTest
public class FileTaskProviderTest {
  private static final String TMP_ROOT = System.getProperty("java.io.tmpdir");

  private String testDirString = TMP_ROOT + File.separator + "test" + Math.random();
  private File testDir;

  private Properties properties;

  @BeforeEach

  public void setUp() throws Exception {
    testDir = new File(testDirString);
    mkdir(testDir);
    properties = System.getProperties();
  }

  
  @AfterEach

  
  public void tearDown() throws Exception {
    System.setProperties(properties);
  }


  @Test


  public void testDir() throws Exception {
    FileTaskProvider provider = FileTaskProvider.newInstance(testDir);
    assertEquals(testDir, provider.getFile());
  }

  @Test

  public void testRelative() throws IOException {
    System.setProperty("user.dir", testDirString);

    String tmp = "./foo";
    File f = new File(testDir, tmp);
    mkdir(f);

    FileTaskProvider provider = FileTaskProvider.newInstance(new File(tmp), properties);
    assertEquals(f.getCanonicalPath(), provider.getFile().getCanonicalPath());
  }

  @Test

  public void testParentRelative() throws IOException {
    mkdir(new File(testDir, "userdir"));
    mkdir(new File(testDir, "foo"));

    System.setProperty("user.dir", testDirString + File.separator + "userdir");

    String tmp = "../foo";
    File f = new File(testDir, "foo");

    FileTaskProvider provider = FileTaskProvider.newInstance(new File(tmp), properties);
    assertEquals(f.getCanonicalPath(), provider.getFile().getCanonicalPath());
  }

  @Test

  public void testUserHome() throws IOException {
    System.setProperty("user.home", testDirString);

    String tmp = "~/foo";
    File f = new File(System.getProperty("user.home"), tmp.substring(2));
    mkdir(f);

    FileTaskProvider provider = FileTaskProvider.newInstance(new File(tmp), properties);
    assertEquals(f.getCanonicalPath(), provider.getFile().getCanonicalPath());
  }

  private void mkdir(File dir) {
    if (!dir.mkdir()) {
      fail("Can't make " + testDir);
    }
  }
}