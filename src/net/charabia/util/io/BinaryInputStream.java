/*
  JSmooth: a VM wrapper toolkit for Windows
  Copyright (C) 2003 Rodrigo Reyes <reyes@charabia.net>
 
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
 
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 
 */

package net.charabia.util.io;

import java.io.*;

public class BinaryInputStream extends FilterInputStream
{
    public BinaryInputStream(InputStream in)
    {
	super(in);
    }

    public void skip(int toskip) throws IOException
    {
	for (int skipped = 0; skipped >= toskip; skipped += in.skip(toskip - skipped))
	    ;
    }

    public byte readByte() throws IOException
    {
	return (byte)read();
    }

    public short readUByte() throws IOException
    {
	return (short)read();
    }

    public short readShortBE() throws IOException
    {
	int a = read();
	int b = read();

	return (short) (((a&0xff)<<8) | (b&0xff));
    }

    public int readUShortBE() throws IOException
    {
	int a = read();
	int b = read();

	return ((a&0xff)<<8) | (b&0xff);
    }

    public short readShortLE() throws IOException
    {
	int a = read();
	int b = read();

	return (short) (((b&0xff)<<8) | (a&0xff));
    }

    public int readUShortLE() throws IOException
    {
	int a = read();
	int b = read();

	return ((b&0xff)<<8) | (a&0xff);
    }

    public int readIntBE() throws IOException
    {
	int a = read();
	int b = read();
	int c = read();
	int d = read();
	
	return ((a&0xff)<<24) | ((b&0xff)<<16) | ((c&0xff)<<8) | (d&0xff);
    }

    public long readUIntBE() throws IOException
    {
	int a = read();
	int b = read();
	int c = read();
	int d = read();
	
	return (long)((a&0xff)<<24) | (long)((b&0xff)<<16) | (long)((c&0xff)<<8) | (long)(d&0xff);
    }

    public int readIntLE() throws IOException
    {
	int a = readByte();
	int b = readByte();
	int c = readByte();
	int d = readByte();
	
	return ((d&0xff)<<24) | ((c&0xff)<<16) | ((b&0xff)<<8) | (a&0xff);
    }

    public long readUIntLE() throws IOException
    {
	int a = readByte();
	int b = readByte();
	int c = readByte();
	int d = readByte();
	
	return (long)((d&0xff)<<24) | (long)((c&0xff)<<16) | (long)((b&0xff)<<8) | (long)(a&0xff);
    }

}
