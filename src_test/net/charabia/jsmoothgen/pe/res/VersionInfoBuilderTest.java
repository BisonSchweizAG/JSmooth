/**
 * File Name: QVersionInfoTest.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.charabia.jsmoothgen.pe.res;

import static org.junit.Assert.assertEquals;
import net.charabia.jsmoothgen.pe.res.VersionInfoBuilder;

import org.junit.Test;

/**
 * Tests the {@link VersionInfoBuilder} class.
 * @author christian.oetterli
 */
public class VersionInfoBuilderTest {

  @Test(expected = RuntimeException.class)
  public void invalidVersion0() throws Exception {
    VersionInfoBuilder.parseVersion("");
  }

  @Test(expected = RuntimeException.class)
  public void invalidVersion1() throws Exception {
    VersionInfoBuilder.parseVersion("1.1.1");
  }

  @Test(expected = RuntimeException.class)
  public void invalidVersion2() throws Exception {
    VersionInfoBuilder.parseVersion("1.1.a.1");
  }

  @Test
  public void toVersionString() throws Exception {
    assertConvertion("1.0.0.0", 65536, 0);
    assertConvertion("98.97.96.99", 6422625, 6291555);
    assertConvertion("54.53.32.5", 3538997, 2097157);
  }

  private void assertConvertion(String version, int ms, int ls) {
    int[] vs = VersionInfoBuilder.parseVersion(version);
    assertEquals(ms, vs[0]);
    assertEquals(ls, vs[1]);
    assertEquals(version, VersionInfoBuilder.formatVersion(ms, ls));
  }
}
