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

/*
 * PEOldMSHeader.java
 *
 * Created on 28 juillet 2003, 22:05
 */

package net.charabia.jsmoothgen.pe;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 *
 * @author  Rodrigo
 */
public class PEOldMSHeader implements Cloneable
{
    int e_cblp;          // Bytes on last page of file //  2
    int e_cp;            // Pages in file //  4
    int e_crlc;          // Relocations //  6
    int e_cparhdr;       // Size of header in paragraphs //  8
    int e_minalloc;      // Minimum extra paragraphs needed //  A
    int e_maxalloc;      // Maximum extra paragraphs needed //  C
    int e_ss;            // Initial (relative) SS value //  E
    int e_sp;            // Initial SP value // 10
    int e_csum;          // Checksum // 12
    int e_ip;            // Initial IP value // 14
    int e_cs;            // Initial (relative) CS value // 16
    int e_lfarlc;        // File address of relocation table // 18
    int e_ovno;          // Overlay number // 1A
    int[] e_res = new int[4];        // Reserved words // 1C
    int e_oemid;         // OEM identifier (for e_oeminfo) // 24
    int e_oeminfo;       // OEM information; e_oemid specific // 26
    int[] e_res2 = new int[10];      // Reserved words // 28
    long e_lfanew;       // File address of new exe header // 3C

    private PEFile m_pe;

    /** Creates a new instance of PEOldMSHeader */
    public PEOldMSHeader(PEFile pe)
    {
	m_pe = pe;
    }

    public Object clone() throws CloneNotSupportedException
    {
	return super.clone();
    }

    public void read() throws IOException
    {
	FileChannel ch = m_pe.getChannel();
	ByteBuffer mz = ByteBuffer.allocate(64);
	mz.order(ByteOrder.LITTLE_ENDIAN);

	ch.read(mz, 0);
	mz.position(0);
	
	byte m = mz.get();
	byte z = mz.get();
	if ((m == 77) && (z == 90))
	    {
		//		System.out.println("MZ found !");
	    }
	
	e_cblp = mz.getShort();          // Bytes on last page of file //  2
	e_cp = mz.getShort();            // Pages in file //  4
	e_crlc = mz.getShort();          // Relocations //  6
	e_cparhdr = mz.getShort();       // Size of header in paragraphs //  8
	e_minalloc = mz.getShort();      // Minimum extra paragraphs needed //  A
	e_maxalloc = mz.getShort();      // Maximum extra paragraphs needed //  C
	e_ss = mz.getShort();            // Initial (relative) SS value //  E
	e_sp = mz.getShort();            // Initial SP value // 10
	e_csum = mz.getShort();          // Checksum // 12
	e_ip = mz.getShort();            // Initial IP value // 14
	e_cs = mz.getShort();            // Initial (relative) CS value // 16
	e_lfarlc = mz.getShort();        // File address of relocation table // 18
	e_ovno = mz.getShort();          // Overlay number // 1A
    
	for (int i=0; i<4; i++)
	    e_res[i] = mz.getShort();

	e_oemid = mz.getShort();         // OEM identifier (for e_oeminfo) // 24
	e_oeminfo = mz.getShort();       // OEM information; e_oemid specific // 26
	
	for (int i=0; i<10; i++)
	    e_res2[i] = mz.getShort();      // Reserved words // 28
	
	e_lfanew = mz.getInt();       // File address of new exe header // 3C
	
	//	System.out.println("exe header : " + e_lfanew);
    }

    
    public void dump(PrintStream out)
    {
	out.println("MSHeader:");

	out.println("e_cblp: " + e_cblp + " // Bytes on last page of file //  2");
	out.println("e_cp: " + e_cp + " // Pages in file //  4");
	out.println("e_crlc: " + e_crlc + " // Relocations //  6");
	out.println("e_cparhdr: " + e_cparhdr + " // Size of header in paragraphs //  8");
	out.println("e_minalloc: " + e_minalloc + " // Minimum extra paragraphs needed //  A");
	out.println("e_maxalloc: " + e_maxalloc + " // Maximum extra paragraphs needed //  C");
	out.println("e_ss: " + e_ss + " // Initial (relative) SS value //  E");
	out.println("e_sp: " + e_sp + " // Initial SP value // 10");
	out.println("e_csum: " + e_csum + " // Checksum // 12");
	out.println("e_ip: " + e_ip + " // Initial IP value // 14");
	out.println("e_cs: " + e_cs + " // Initial (relative) CS value // 16");
	out.println("e_lfarlc: " + e_lfarlc + " // File address of relocation table // 18");
	out.println("e_ovno: " + e_ovno + " // Overlay number // 1A");
	//	int[] e_res = new int[4];        // Reserved words // 1C
	out.println("e_oemid: " + e_oemid + " // OEM identifier (for e_oeminfo) // 24");
	out.println("e_oeminfo: " + e_oeminfo + " // OEM information; e_oemid specific // 26");
	//	int[] e_res2 = new int[10];      // Reserved words // 28
	out.println("e_lfanew: " + e_lfanew + " // File address of new exe header // 3C");
    }

   
    public ByteBuffer get()
    {
	ByteBuffer mz = ByteBuffer.allocate(64);
	mz.order(ByteOrder.LITTLE_ENDIAN);
	mz.position(0);

	mz.put((byte)77);
	mz.put((byte)90);

	mz.putShort((short)e_cblp);          // Bytes on last page of file //  2
	mz.putShort((short)e_cp);            // Pages in file //  4
	mz.putShort((short)e_crlc);          // Relocations //  6
	mz.putShort((short)e_cparhdr);       // Size of header in paragraphs //  8

	mz.putShort((short)e_minalloc);      // Minimum extra paragraphs needed //  A
	mz.putShort((short)e_maxalloc);      // Maximum extra paragraphs needed //  C
	mz.putShort((short)e_ss);            // Initial (relative) SS value //  E
	mz.putShort((short)e_sp);            // Initial SP value // 10
	mz.putShort((short)e_csum);          // Checksum // 12
	mz.putShort((short)e_ip);            // Initial IP value // 14
	mz.putShort((short)e_cs);            // Initial (relative) CS value // 16
	mz.putShort((short)e_lfarlc);        // File address of relocation table // 18
	mz.putShort((short)e_ovno);          // Overlay number // 1A
    
	for (int i=0; i<4; i++)
	    mz.putShort((short)e_res[i]);

	mz.putShort((short)e_oemid);         // OEM identifier (for e_oeminfo) // 24
	mz.putShort((short)e_oeminfo);       // OEM information; e_oemid specific // 26
	
	for (int i=0; i<10; i++)
	    mz.putShort((short)e_res2[i]);      // Reserved words // 28
	
	mz.putInt((int)e_lfanew);       // File address of new exe header // 3C
	
	mz.position(0);
	return mz;
    }

}
