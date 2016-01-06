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
 * SkeletonList.java
 *
 * Created on 7 août 2003, 20:09
 */

package net.charabia.jsmoothgen.skeleton;

import java.io.*;
import java.util.*;

public class SkeletonList
{
    Hashtable m_skelsToDirs = new Hashtable();
    Hashtable m_nameToSkel = new Hashtable();
    Hashtable m_nameToDebugSkel = new Hashtable();
    Hashtable m_nameToNoDebugSkel = new Hashtable();

    public class SkelSuffixFilter implements FileFilter
    {
	public boolean accept(File pathname)
	{
	    if (pathname.toString().toLowerCase().endsWith(".skel"))
		return true;
	    return false;
	}
    }
	
    /** Creates a new instance of SkeletonList */
    public SkeletonList(File directoryToScan)
    {
	File[] subdirs = directoryToScan.listFiles();
	SkelSuffixFilter filter = new SkelSuffixFilter();
	
	for (int i=0; i<subdirs.length; i++)
	    {
		if (subdirs[i].isDirectory())
		    {
			File[] skeldescs = subdirs[i].listFiles((java.io.FileFilter)filter);
			for (int si=0; si<skeldescs.length; si++)
			    {
				addSkeletonDirectory(subdirs[i], skeldescs[si]);
			    }
		    }
	    }
    }

    public void addSkeletonDirectory(File dir, File desc)
    {
	try {
	    SkeletonBean skel = SkeletonPersistency.loadWithJox(desc);
	    if ((skel != null) && (skel.getShortName() != null))
		{
		    m_skelsToDirs.put(skel, dir);
		    if (skel.isDebug() == false)
			m_nameToNoDebugSkel.put(skel.getShortName(), skel);
		    else
			m_nameToDebugSkel.put(skel.getShortName(), skel);
		    m_nameToSkel.put(skel.getShortName(), skel);
		}
	} catch (Exception exc)
	    {
		//		System.err.println("Error adding skeleton " + dir + " / " + desc);
		//		iox.printStackTrace();
	    }
    }
	
    public String toString()
    {
	return m_skelsToDirs.toString();
    }
	
    public Iterator getIteratorSkel()
    {
        // return m_skelsToDirs.keySet().iterator();
        return m_nameToSkel.values().iterator();
    }

    public Iterator getIteratorDebugSkel()
    {
        return m_nameToDebugSkel.values().iterator();
    }

    
    public File getDirectory(SkeletonBean skel)
    {
	return (File) m_skelsToDirs.get(skel);
    }
	
    public Iterator getIteratorName()
    {
	return m_nameToSkel.keySet().iterator();
    }

    public Iterator getIteratorDebugName()
    {
	return m_nameToDebugSkel.keySet().iterator();
    }

    public Iterator getIteratorNoDebugName()
    {
	return m_nameToNoDebugSkel.keySet().iterator();
    }

    public SkeletonBean getSkeleton(String name)
    {
        try {
	    return new SkeletonBean((SkeletonBean)m_nameToSkel.get(name));
        } catch (Exception exc)
	    {
		return null;
	    }
    }
}
