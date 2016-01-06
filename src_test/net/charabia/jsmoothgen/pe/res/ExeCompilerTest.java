/**
 * File Name: VsVersionInfoTest.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.charabia.jsmoothgen.pe.res;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import net.charabia.jsmoothgen.application.ExeCompiler;
import net.charabia.jsmoothgen.application.JSmoothModelBean;
import net.charabia.jsmoothgen.application.JSmoothModelPersistency;
import net.charabia.jsmoothgen.pe.PEFile;
import net.charabia.jsmoothgen.pe.PEResourceDirectory;
import net.charabia.jsmoothgen.pe.PEResourceDirectory.Replacer;
import net.charabia.jsmoothgen.pe.res.validate.IRandomAccess;
import net.charabia.jsmoothgen.pe.res.validate.VsFixedFileInfo;
import net.charabia.jsmoothgen.pe.res.validate.VsVersionInfo;
import net.charabia.jsmoothgen.skeleton.SkeletonBean;
import net.charabia.jsmoothgen.skeleton.SkeletonList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author christian.oetterli
 */
public class ExeCompilerTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void exeCompiler() throws Exception {
    copyResource("sample.jar");
    File jsmooth = copyResource("sample.jsmooth");

    File jsmoothWorkspace = new File(System.getProperty("user.dir"));
    compile(jsmooth, new File(jsmoothWorkspace, "dist/jsmooth-0.9.9-7").getAbsolutePath());
    File exe = new File(folder.getRoot(), "sample.exe");
    assertTrue(exe.exists());

    PEFile pe = new PEFile(exe.getAbsoluteFile());
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

    // try (InputStream in = new FileInputStream(exe); OutputStream out = new FileOutputStream("c://xx/sample.exe")) {
    // VersionInfoBuilder.copyStreams(in, out);
    // }
  }

  private static Map<String, String> getProps() {
    Map<String, String> mm = new LinkedHashMap<>();
    mm.put("CompanyName", "CompanyNamex");
    mm.put("FileDescription", "FileDescriptionx");
    mm.put("FileVersion", "11.22.33.44");
    mm.put("LegalCopyright", "LegalCopyrightx");
    mm.put("OriginalFilename", "OriginalFilenamex");
    mm.put("ProductName", "ProductNamex");
    mm.put("ProductVersion", "55.66.77.88 xx");
    return mm;
  }

  private void parseAndValidate(ByteBuffer source) throws Exception {
    VsVersionInfo versionInfo = new VsVersionInfo(new IRandomAccess.ByteBufferAccess(source));

    Map<String, String> st = versionInfo.getStringTable(VersionInfoBuilder.LANGID_KEY);
    assertNotNull(st);
    VsFixedFileInfo vi = versionInfo.value;

    assertNotNull(versionInfo.sfi);
    assertNotNull(versionInfo.vfi);
    assertEquals("11.22.33.44", VersionInfoBuilder.formatVersion(vi.fileVersionMS, vi.fileVersionLS));
    assertEquals("55.66.77.88", VersionInfoBuilder.formatVersion(vi.productVersionMS, vi.productVersionLS));

    assertEquals(st, getProps());
  }

  private void compile(File prj, String jsmoothbase) throws Exception {
    // taken from CommandLine.java
    JSmoothModelBean model = JSmoothModelPersistency.load(prj);
    File basedir = prj.getParentFile();
    File skelbase = new File("skeletons");
    if (jsmoothbase != null) {
      skelbase = new File(new File(jsmoothbase), "skeletons");
    }

    SkeletonList skelList = new SkeletonList(skelbase);

    File out = new File(basedir, model.getExecutableName());

    SkeletonBean skel = skelList.getSkeleton(model.getSkeletonName());
    File skelroot = skelList.getDirectory(skel);

    ExeCompiler compiler = new ExeCompiler();
    compiler.compile(skelroot, skel, basedir, model, out);
  }

  private File copyResource(String sample) throws IOException, FileNotFoundException {
    File targetFile = new File(folder.getRoot(), sample);
    try (InputStream in = getClass().getResourceAsStream(sample); OutputStream out = new FileOutputStream(targetFile)) {
      ExeCompilerTest.copyStreams(in, out);
    }
    return targetFile;
  }

  public static void copyStreams(final InputStream in, final OutputStream out) throws IOException {
    final byte[] bytes = new byte[4096];
    int bytesRead;
    while ((bytesRead = in.read(bytes)) > -1) {
      out.write(bytes, 0, bytesRead);
    }
  }
}
