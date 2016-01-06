/**
 * File Name: VersionInfoBuilder.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.charabia.jsmoothgen.pe.res;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.charabia.jsmoothgen.application.VersionInfo;

/**
 * Builds a VS_VERSION_INFO structure.
 * 
 * @author christian.oetterli
 */
public class VersionInfoBuilder {

  private static ByteBuffer newByteBuffer(int capacity) {
    ByteBuffer bb = ByteBuffer.allocate(capacity);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    return bb;
  }

  public static final String LANGID_KEY = "040004b0";
  public static final String STRINGFILEINFO_KEY = "StringFileInfo";
  public static final String VSVERSIONINFO_KEY = "VS_VERSION_INFO";

  private static final String ZERO_TERMINATOR = String.valueOf((char)0);

  // how this was extracted see VarFileInfo.java
  private static final byte[] VAR_FILE_INFO = new byte[] { 68, 0, 0, 0, 1, 0, 86, 0, 97, 0, 114, 0, 70, 0, 105, 0, 108, 0, 101, 0, 73, 0,
      110, 0, 102, 0, 111, 0, 0, 0, 0, 0, 36, 0, 4, 0, 0, 0, 84, 0, 114, 0, 97, 0, 110, 0, 115, 0, 108, 0, 97, 0, 116, 0, 105, 0, 111, 0,
      110, 0, 0, 0, 0, 0, 0, 4, -80, 4 };

  /**
   * @param fileVersion https://technet.microsoft.com/de-de/ms646997(v=vs.71) - dwFileVersionMS/dwFileVersionLS
   * @param productVersion https://technet.microsoft.com/de-de/ms646997(v=vs.71) - dwProductVersionMS/dwProductVersionLS
   * @param infos https://technet.microsoft.com/de-de/ms646987(v=vs.71)
   */
  public static ByteBuffer build(String fileVersion, String productVersion, Map<String, String> infos) {

    byte[] key = toSzString(VSVERSIONINFO_KEY);

    ByteBuffer stringFileInfo = getStringFileInfo(infos);

    short valueLength = 52;

    int len = 6 + key.length + 2 + valueLength + stringFileInfo.position() + VAR_FILE_INFO.length;

    ByteBuffer bb = newByteBuffer(len);

    bb.putShort((short)len);
    bb.putShort(valueLength);
    bb.putShort((short)0);
    bb.put(key);

    pad(bb);

    bb.put(getFixedFileInfo(fileVersion, productVersion));

    pad(bb);
    bb.put(stringFileInfo.array(), 0, stringFileInfo.position());
    bb.put(VAR_FILE_INFO);

    return copyAndWrap(bb);
  }

  private static ByteBuffer copyAndWrap(ByteBuffer bb) {
    byte[] q = new byte[bb.position()];
    System.arraycopy(bb.array(), 0, q, 0, q.length);
    return ByteBuffer.wrap(q);
  }

  private static ByteBuffer getStringFileInfo(Map<String, String> table) {
    ByteBuffer st = getStringTable(table);

    byte[] key = toSzString(STRINGFILEINFO_KEY);

    int lenBeforePadding = 6 + key.length;
    int padding = getPadding(lenBeforePadding);
    int totalLen = lenBeforePadding + padding + st.position();

    ByteBuffer bb = newByteBuffer(totalLen);
    bb.putShort((short)totalLen);
    bb.putShort((short)0);
    bb.putShort((short)1);
    bb.put(key);
    bb.put(st.array(), 0, st.position());
    return bb;
  }

  private static ByteBuffer getStringTable(Map<String, String> table) {

    List<ByteBuffer> strings = new ArrayList<>();

    int tableLen = 0;
    int extra = 0;
    for (Entry<String, String> it : table.entrySet()) {
      byte[] key = toSzString(it.getKey());
      int lenBeforePadding = 6 + key.length; // 6 = sizeof(length) + sizeof(valueLength) + sizeof(type)
      int padding = getPadding(lenBeforePadding);
      int valLength = it.getValue().length() + 1;
      byte[] val = toSzString(it.getValue());
      int sLen = lenBeforePadding + padding + (2 * valLength);

      ByteBuffer bs = newByteBuffer(sLen + 4); // + eventual padding
      bs.putShort((short)sLen);
      bs.putShort((short)(valLength)); // size in words
      bs.putShort((short)1); // type
      bs.put(key);
      pad(bs);
      bs.put(val);
      tableLen += sLen;
      extra += getPadding(bs.position());
      pad(bs);

      strings.add(bs);
    }

    byte[] tableKey = toSzString(LANGID_KEY);
    int lenBeforePadding = 6 + tableKey.length;
    int padding = getPadding(lenBeforePadding);
    int totalLen = lenBeforePadding + padding + tableLen;

    totalLen += extra;

    ByteBuffer st = newByteBuffer(totalLen);
    st.putShort((short)totalLen);
    st.putShort((short)0);// wValueLength
    st.putShort((short)1);// wType, 1 = text
    st.put(tableKey);
    pad(st);
    for (ByteBuffer it : strings) {
      st.put(it.array(), 0, it.position());
    }

    return st;
  }

  private static byte[] getFixedFileInfo(String fileVersion, String productVersion) {
    int length = 13 * 4;
    ByteBuffer bb = newByteBuffer(length);

    bb.putInt(0xfeef04bd); // dwSignature
    bb.putInt(65536); // dwStrucVersion

    int[] fv = parseVersion(fileVersion);
    bb.putInt(fv[0]); // dwFileVersionMS
    bb.putInt(fv[1]);// dwFileVersionLS

    int[] pv = parseVersion(productVersion);
    bb.putInt(pv[0]); // dwProductVersionMS
    bb.putInt(pv[1]);// dwProductVersionLS

    bb.putInt(0x3f);// dwFileFlagsMask
    bb.putInt(0);// dwFileFlags
    bb.putInt(0x40004);// dwFileOS
    bb.putInt(1);// dwFileType
    bb.putInt(0);// dwFileSubtype
    bb.putInt(0);// dwFileDateMS
    bb.putInt(0);// dwFileDateLS

    if (bb.position() != length) {
      throw new RuntimeException("length mismatch");
    }

    return bb.array();
  }

  private static final Charset UTF16LE = Charset.forName("UTF-16LE");

  private static byte[] toSzString(String s) {
    return s.concat(ZERO_TERMINATOR).getBytes(UTF16LE);
  }

  private static void pad(ByteBuffer bb) {
    for (int i = 0, n = getPadding(bb.position()); i < n; i++) {
      bb.put((byte)0);
    }
  }

  private static int getPadding(int pos) {
    int mod = pos % 4;
    return mod == 0 ? 0 : 4 - mod;
  }

  @SuppressWarnings("boxing")
  public static String formatVersion(int ms, int ls) {
    int maj = ms >> 16;
    int min = ms & 0xffff;
    int sub = ls >> 16;
    int build = ls & 0xffff;

    return String.format("%d.%d.%d.%d", maj, min, sub, build);
  }

  public static int[] parseVersion(String version) {
    String[] split = version.split("\\.");
    if (split.length != 4) {
      throw new RuntimeException("invalid version: " + version);
    }
    try {
      int maj = Integer.parseInt(split[0]);
      int min = Integer.parseInt(split[1]);
      int sub = Integer.parseInt(split[2]);
      int build = Integer.parseInt(split[3]);
      return new int[] { maj << 16 | min, sub << 16 | build };
    } catch (Exception e) {
      // ignore e
      throw new RuntimeException("invalid version: " + version);
    }
  }

  public static ByteBuffer build(VersionInfo vi) {
    Map<String, String> props = new LinkedHashMap<>();
    props.put("CompanyName", vi.getCompanyName());
    props.put("FileDescription", vi.getFileDescription());
    props.put("FileVersion", vi.getBinaryFileVersion());
    props.put("LegalCopyright", vi.getLegalCopyright());
    props.put("OriginalFilename", vi.getOriginalFilename());
    props.put("ProductName", vi.getProductName());
    props.put("ProductVersion", vi.getProductVersion());

    return build(vi.getBinaryFileVersion(), vi.getBinaryProductVersion(), props);
  }
}
