// Copyright (c) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the LGPL 3.0 license available at http://www.gnu.org/licenses/lgpl.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Some stream utilities.
 *
 * @author David A. Solin
 * @version %I% %G%
 * @since 1.0
 */
public class StreamTool {
  /**
   * Useful in debugging...
   *
   * @since 1.0
   */
  public static final void hexDump(byte[] buff, PrintStream out) {
    int numRows = buff.length / 16;
    if (buff.length % 16 > 0) numRows++; // partial row

    int ptr = 0;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < 16; j++) {
        if (ptr < buff.length) {
          if (j > 0) System.out.print(" ");
          String iHex = Integer.toHexString((int)buff[ptr++]);
          if (iHex.length() == 0) {
            out.print("0");
          }
          out.print(iHex);
        } else {
          break;
        }
      }
      out.println("");
    }
  }

  /**
   * Read from the stream until the buffer is full.
   *
   * @since 1.0
   */
  public static final void readFully(InputStream in, byte[] buff) throws IOException {
    int offset = 0;
    do {
      int bytesRead = in.read(buff, offset, buff.length);
      if (bytesRead == 0) {
        throw new EOFException("EOFException");
      } else {
        offset += bytesRead;
      }
    } while (offset < buff.length);
  }

  /**
   * Copy from in to out asynchronously (i.e., in a new Thread). Closes the InputStream when done, but not the OutputStream.
   *
   * @return the new Thread
   *
   * @since 1.0
   */
  public static Thread copyAsync(InputStream in, OutputStream out) {
    Thread thread = new Thread(new Copier(in, out));
    thread.start();
    return thread;
  }

  /**
   * Copy completely from in to out. Closes the InputStream when done, but not the OutputStream.
   *
   * @since 1.0
   */
  public static void copy(InputStream in, OutputStream out) {
    copy(in, out, false);
  }

  /**
   * Copy completely from in to out. Closes the InputStream when done. Closes the OutputStream according to closeOut.
   *
   * @since 1.0
   */
  public static void copy(InputStream in, OutputStream out, boolean closeOut) {
    try {
      new Copier(in, out).run();
    } finally {
      if (closeOut && out != null) {
        try {
          out.close();
        } catch (IOException e) {
        }
      }
    }
  }

  /**
   * Read the BOM (Byte-Order marker) from a stream.
   *
   * @since 1.0
   */
  public static Charset detectEncoding(InputStream in) throws IOException {
    switch (in.read()) {
      case 0xEF:
        if (in.read() == 0xBB && in.read() == 0xBF) {
          return StringTools.UTF8;
        }
        break;
      case 0xFE:
        if (in.read() == 0xFF) {
          return StringTools.UTF16;
        }
        break;
      case 0xFF:
        if (in.read() == 0xFE) {
          return StringTools.UTF16LE;
        }
        break;
    }
    throw new java.nio.charset.CharacterCodingException();
  }

  // Private

  private static class Copier implements Runnable {
    InputStream in;
    OutputStream out;

    Copier(InputStream in, OutputStream out) {
      this.in = in;
      this.out = out;
    }

    // Implement Runnable

    @Override
    public void run() {
      try {
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = in.read(buff)) > 0) {
          out.write(buff, 0, len);
        }
      } catch (IOException e) {
      } finally {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
