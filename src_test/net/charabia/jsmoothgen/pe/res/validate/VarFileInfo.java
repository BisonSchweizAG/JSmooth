// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class VarFileInfo {

  public static final String KEY = "VarFileInfo";

  short length;
  short valueLength;
  short type;
  byte[] padding;
  Var children;

  VarFileInfo(short len, short vLen, short type, byte[] buff, int fileOffset) throws IOException {

    this.length = len;

    this.valueLength = vLen;
    this.type = type;
    int offset = 28; // short + short + sizeof("VarFileInfo\0")
    padding = LittleEndian.get32BitAlignPadding(buff, offset, fileOffset);
    offset += padding.length;
    children = new Var(buff, offset, fileOffset);

    // this code was used to extract the static VAR_FILE_INFO bytes
    // ByteBuffer bb = ByteBuffer.wrap(new byte[len]);
    // bb.order(ByteOrder.LITTLE_ENDIAN);
    // bb.putShort(len);
    // bb.put(buff, 0, buff.length);
    //
    // FileOutputStream out = new FileOutputStream("c:/xx/varfileinfo.bin");
    // byte[] aa = bb.array();
    // out.write(aa, 0, aa.length);
    // out.close();
  }

  public List<Var.LangAndCodepage> getTranslations() {
    return children.children;
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
    out.println("key:              " + KEY);
    out.print(indent);
    out.print("padding:          {");
    for (int i = 0; i < padding.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      out.print(LittleEndian.toHexString(padding[i]));
    }
    out.println("}");
    out.print(indent);
    out.println("children: {");
    children.debugPrint(out, level + 1);
    out.print(indent);
    out.println("}");
  }
}
