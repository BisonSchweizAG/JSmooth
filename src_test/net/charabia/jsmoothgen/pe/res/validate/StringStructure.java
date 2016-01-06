// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.IOException;
import java.io.PrintStream;

public class StringStructure {
  short length;
  short valueLength;
  short type;
  String key;
  byte[] padding;
  String value;

  StringStructure(byte[] buff, int offset, int fileOffset) throws IOException {
    int initialOffset = offset;
    length = LittleEndian.getUShort(buff, offset);
    offset += 2;
    valueLength = LittleEndian.getUShort(buff, offset);
    offset += 2;
    type = LittleEndian.getUShort(buff, offset);
    offset += 2;
    key = LittleEndian.getSzUTF16LEString(buff, offset, -1);
    offset += (2 * key.length()) + 2;
    padding = LittleEndian.get32BitAlignPadding(buff, offset, fileOffset);
    offset += padding.length;
    value = LittleEndian.getSzUTF16LEString(buff, offset, 2 * valueLength);
    offset += 2 * valueLength;

    // System.out.println(key + "=" + padding.length);
    // System.out.println("+++" + key + "=" + length + "," + valueLength);

    int computedLength = offset - initialOffset;
    if (length < computedLength) {
      throw new IOException("PE string length overflow, computed: " + computedLength + ", indicated: " + length);
    } else {
      //
      // Sometimes the specified length is less than the actual length of the structure, so we correct it.
      //
      length = (short)computedLength;
    }
  }

  @Override
  public String toString() {
    return key + "=" + value;
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
    out.print("padding:         {");
    for (int i = 0; i < padding.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      out.print(LittleEndian.toHexString(padding[i]));
    }
    out.println("}");
    out.print(indent);
    out.println("value:            \"" + value + "\"");
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }
}
