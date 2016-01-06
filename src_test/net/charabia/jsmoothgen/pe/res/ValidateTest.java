/**
 * File Name: ValidateTest.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.charabia.jsmoothgen.pe.res;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import net.charabia.jsmoothgen.pe.PEFile;
import net.charabia.jsmoothgen.pe.PEResourceDirectory;
import net.charabia.jsmoothgen.pe.PEResourceDirectory.Replacer;
import net.charabia.jsmoothgen.pe.res.validate.IRandomAccess;
import net.charabia.jsmoothgen.pe.res.validate.VsFixedFileInfo;
import net.charabia.jsmoothgen.pe.res.validate.VsVersionInfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author christian.oetterli
 */
public class ValidateTest {
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void parseExisting() throws Exception {

    File targetFile = new File(System.getProperty("user.dir"), "dist/jsmooth-0.9.9-7/skeletons/autodownload-wrapper/autodownload.exe");

    // targetFile = new File("c://xx/sample.exe");

    PEFile pe = new PEFile(targetFile.getAbsoluteFile());
    pe.open();
    PEResourceDirectory resdir = pe.getResourceDirectory();

    boolean[] called = { false };

    Replacer replacer = source -> {
      try {
        called[0] = true;
        parseAndValidate(source);

        source.position(0);
        return source;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
    assertTrue(resdir.replaceResource("#16", 1, 1033, replacer));
    assertTrue(called[0]);

    parseAndValidate(VersionInfoBuilder.build("0.0.0.0", "0.0.0.0", getProps()));
  }

  private static Map<String, String> getProps() {
    Map<String, String> mm = new LinkedHashMap<>();
    mm.put("CompanyName", "CompanyName");
    mm.put("FileDescription", "FileDescription");
    mm.put("FileVersion", "0.0.0.0");
    mm.put("LegalCopyright", "LegalCopyright");
    mm.put("OriginalFilename", "OriginalFilename");
    mm.put("ProductName", "ProductName");
    mm.put("ProductVersion", "0.0.0.0");
    return mm;
  }

  private void parseAndValidate(ByteBuffer source) throws IOException {
    VsVersionInfo versionInfo = new VsVersionInfo(new IRandomAccess.ByteBufferAccess(source));

    Map<String, String> st = versionInfo.getStringTable(VersionInfoBuilder.LANGID_KEY);
    assertNotNull(st);
    VsFixedFileInfo vi = versionInfo.value;

    assertNotNull(versionInfo.sfi);
    assertNotNull(versionInfo.vfi);
    assertEquals("0.0.0.0", VersionInfoBuilder.formatVersion(vi.fileVersionMS, vi.fileVersionLS));
    assertEquals("0.0.0.0", VersionInfoBuilder.formatVersion(vi.productVersionMS, vi.productVersionLS));

    assertEquals(st, getProps());
  }
}
