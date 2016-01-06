// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VsVersionInfo data structure. https://github.com/akwolf/app-extractor
 * 
 * 
 * https://github.com/josephspurrier/goversioninfo/blob/master/structbuild.go
 * 
 * 
 * @author David A. Solin
 * @version %I% %G%
 */
public class VsVersionInfo {

  public static final String LANGID_KEY = "040904B0";
  public static final String STRINGFILEINFO_KEY = "StringFileInfo";
  public static final String TRANSLATION_KEY = "Translation";
  public static final String VARFILEINFO_KEY = "VarFileInfo";
  public static final String VSVERSIONINFO_KEY = "VS_VERSION_INFO";

  public short length;
  public short valueLength;
  public short type;
  public String key;
  public byte[] padding1;
  public VsFixedFileInfo value;
  public byte[] padding2;
  public StringFileInfo sfi;
  public VarFileInfo vfi;
  public Map<String, Map<String, String>> stringTables;
  public String defaultLangAndCodepage;

  public VsVersionInfo(IRandomAccess ra) throws IOException {
    length = LittleEndian.readUShort(ra);
    valueLength = LittleEndian.readUShort(ra);
    type = LittleEndian.readUShort(ra);
    key = LittleEndian.readSzUTF16LEString(ra);

    //
    // Padding is used to 32-bit align the Value data structure.
    //
    padding1 = LittleEndian.read32BitAlignPadding(ra);
    if (valueLength > 0) {
      byte[] buff = new byte[valueLength];
      ra.readFully(buff);
      value = new VsFixedFileInfo(buff);
    }

    // DAS: this class worked fine without reading for padding here
    padding2 = LittleEndian.read32BitAlignPadding(ra);
    stringTables = new LinkedHashMap<String, Map<String, String>>();
    for (int i = 0; i < 2; i++) {
      short childLength = LittleEndian.readUShort(ra);
      if (childLength == 0) {
        break;
      }
      byte[] childBuff = new byte[childLength - 2];
      int fileOffset = (int)ra.getFilePointer();
      ra.readFully(childBuff);
      short childValueLength = LittleEndian.getUShort(childBuff, 0);
      short childType = LittleEndian.getUShort(childBuff, 2);
      String childKey = LittleEndian.getSzUTF16LEString(childBuff, 4, -1);
      if (StringFileInfo.KEY.equals(childKey)) {
        sfi = new StringFileInfo(childLength, childValueLength, childType, childBuff, fileOffset);
        for (StringTable st : sfi.getChildren()) {
          String key = st.getKey().toLowerCase();
          Map<String, String> table = new LinkedHashMap<String, String>();
          for (StringStructure string : st.getChildren()) {
            table.put(string.getKey().trim(), string.getValue().trim());
          }
          if (defaultLangAndCodepage == null) {
            defaultLangAndCodepage = key;
          }
          stringTables.put(key, table);
        }
      } else if (VarFileInfo.KEY.equals(childKey)) {
        vfi = new VarFileInfo(childLength, childValueLength, childType, childBuff, fileOffset);
      } else {
        throw new IOException("Unknown VS_VERSION_INFO key: " + childKey);
      }
    }

    /*
     * if (vfi != null) { for (Var.LangAndCodepage lac : vfi.getTranslations()) { if (getStringTable(lac) == null) { // // DAS: a
     * translation was specified that doesn't exist -- an internal inconsistency that is not allowed. // throw new
     * IOException("PE inconsistency: " + lac.toString() + " strings not found"); } } }
     */
  }

  public void debugPrint(PrintStream out, int level) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < level; i++) {
      sb.append("  ");
    }
    String indent = sb.toString();
    out.print(indent);
    out.println("length:           " + LittleEndian.toHexString(length));
    out.print(indent);
    out.println("valueLength:      " + LittleEndian.toHexString(valueLength));
    out.print(indent);
    out.println("type:             " + LittleEndian.toHexString(type));
    out.print(indent);
    out.println("key:              " + key);
    out.print(indent);
    out.print("padding1:         {");
    for (int i = 0; i < padding1.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      out.print(LittleEndian.toHexString(padding1[i]));
    }
    out.println("}");
    out.print(indent);
    out.println("value: {");
    if (value != null) {
      value.debugPrint(out, level + 1);
    }
    out.print(indent);
    out.println("}");
    out.print(indent);
    out.print("padding2:         {");
    for (int i = 0; i < padding2.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      out.print(LittleEndian.toHexString(padding2[i]));
    }
    out.println("}");

    if (sfi != null) {
      out.print(indent);
      out.println("children[StringFileInfo]: {");
      sfi.debugPrint(out, level + 1);
      out.print(indent);
      out.println("}");
    }
    if (vfi != null) {
      out.print(indent);
      out.println("children[VarFileInfo]: {");
      vfi.debugPrint(out, level + 1);
      out.print(indent);
      out.println("}");
    }
  }

  public VsFixedFileInfo getValue() {
    return value;
  }

  public VarFileInfo getVarFileInfo() {
    return vfi;
  }

  public String getDefaultTranslation() {
    return defaultLangAndCodepage == null ? LANGID_KEY : defaultLangAndCodepage;
  }

  public Map<String, String> getStringTable(Var.LangAndCodepage lac) {
    return getStringTable(lac.toString());
  }

  public Map<String, String> getStringTable(String key) {
    return stringTables.get(key.toLowerCase());
  }
}
