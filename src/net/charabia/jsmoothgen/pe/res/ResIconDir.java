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

package net.charabia.jsmoothgen.pe.res;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class ResIconDir
{
	private int m_idReserved;   // Reserved (must be 0)
	private int m_idType;       // Resource Type (1 for icons)
	private int m_idCount;      // How many images?

	private IconDirEntry[] m_entries;
	
	public static class IconDirEntry
	{
		public short        bWidth;          // Width, in pixels, of the image
		public short        bHeight;         // Height, in pixels, of the image
		public short        bColorCount;     // Number of colors in image (0 if >=8bpp)
		public short        bReserved;       // Reserved ( must be 0)
		public int         wPlanes;         // Color Planes
		public int         wBitCount;       // Bits per pixel
		public long       dwBytesInRes;    // How many bytes in this resource?
		public int        dwImageOffset;   // Where in the file is this image?
		
		public IconDirEntry(ByteBuffer buf)
		{
			bWidth = buf.get();
			bHeight = buf.get();
			bColorCount = buf.get();
			bReserved = buf.get();
			wPlanes = buf.getShort();
			wBitCount = buf.getShort();
			dwBytesInRes = buf.getInt();
			dwImageOffset = buf.getShort();
		}
		
		public ByteBuffer getData()
		{
			ByteBuffer buf = ByteBuffer.allocate(16);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.position(0);
			
			buf.put((byte)bWidth);
			buf.put((byte)bHeight);
			buf.put((byte)bColorCount);
			buf.put((byte)bReserved);
			
			buf.putShort((short)wPlanes);
			buf.putShort((short)wBitCount);
			
			buf.putInt((int)dwBytesInRes);
			buf.putShort((short)dwImageOffset);
			
			buf.position(0);
			return buf;
		}

	    public String toString()
	    {
		StringBuffer out = new StringBuffer();
		out.append("bWidth: " + bWidth+"\n");          // Width, in pixels, of the image
		out.append("bHeight: " + bHeight + "\n");         // Height, in pixels, of the image
		out.append("bColorCount: " + bColorCount + "\n");     // Number of colors in image (0 if >=8bpp)
		out.append("bReserved: " + bReserved + "\n");       // Reserved ( must be 0)
		out.append("wPlanes: " + wPlanes + "\n");         // Color Planes
		out.append("wBitCount: " + wBitCount + "\n");       // Bits per pixel
		out.append("dwBytesInRes: " + dwBytesInRes + "\n");    // How many bytes in this resource?
		out.append("dwImageOffset: " + dwImageOffset + "\n");   // Where in the file is this image?
		
		return out.toString();
	    }
		
	}
	
	/** Creates a new instance of ResIconDir */
	public ResIconDir(ByteBuffer buf)
	{
		m_idReserved = buf.getShort();
		m_idType = buf.getShort();
		m_idCount = buf.getShort();
		
		m_entries = new ResIconDir.IconDirEntry[m_idCount];
		for (int i=0; i<m_idCount; i++)
		{
			m_entries[i] = new IconDirEntry(buf);
		}
	}
	
	public ByteBuffer getData()
	{
		ByteBuffer buf = ByteBuffer.allocate(6 + (16*m_idCount));
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.position(0);
		
		buf.putShort((short)m_idReserved);
		buf.putShort((short)m_idType);
		buf.putShort((short)m_idCount);
		
		for (int i=0; i<m_idCount; i++)
		{
			ByteBuffer b = m_entries[i].getData();
			b.position(0);
			buf.put(b);
		}
		
		return buf;
	}
	
	public ResIconDir.IconDirEntry[] getEntries()
	{
		return m_entries;
	}

    public String toString()
    {
	StringBuffer out = new StringBuffer();
	out.append("m_idReserved: " + m_idReserved + "\n");   // Reserved (must be 0)
	out.append("m_idType: " + m_idType + "\n");       // Resource Type (1 for icons)
	out.append("m_idCount: " + m_idCount + "\n");      // How many images?
	out.append("entries: ---- \n" );
	for (int i=0; i<m_entries.length; i++)
	    {
		out.append(m_entries[i].toString());
	    }

	return out.toString();
    }
}
