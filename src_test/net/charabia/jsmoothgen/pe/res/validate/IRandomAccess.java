// Copyright (c) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the LGPL 3.0 license available at http://www.gnu.org/licenses/lgpl.txt

package net.charabia.jsmoothgen.pe.res.validate;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A platform-independent interface providing random-access to a file.
 *
 * @author David A. Solin
 * @version %I% %G%
 * @since 1.0
 */
public interface IRandomAccess {

  public static class ByteBufferAccess implements IRandomAccess {

    private ByteBuffer _buf;

    public ByteBufferAccess(ByteBuffer buf) {
      _buf = buf;
    }

    @Override
    public int read() throws IOException {
      return _buf.get();
    }

    @Override
    public void readFully(byte[] buff) throws IOException {
      for (int i = 0, n = buff.length; i < n; i++) {
        buff[i] = _buf.get();
      }
    }

    @Override
    public long getFilePointer() throws IOException {
      return _buf.position();
    }
  }

  /**
   * Read a byte.
   *
   * @since 1.0
   */
  public int read() throws IOException;

  /**
   * Read buff.length bytes.
   *
   * @since 1.0
   */
  public void readFully(byte[] buff) throws IOException;

  /**
   * Get the current position in the IRandomAccess.
   *
   * @since 1.0
   */
  public long getFilePointer() throws IOException;
}
