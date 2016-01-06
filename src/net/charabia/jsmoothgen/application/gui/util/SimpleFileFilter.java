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

package net.charabia.jsmoothgen.application.gui.util;

public class SimpleFileFilter extends javax.swing.filechooser.FileFilter
{
	private String m_suffix;
	private String m_description;
	
	/** Creates a new instance of SimpleFileFilter */
	public SimpleFileFilter(String suffix, String desc)
	{
		m_suffix = suffix;
		m_description = desc;
	}
	
	public boolean accept(java.io.File f)
	{
		String suffix = getSuffix(f);
		if (suffix.equalsIgnoreCase(m_suffix))
			return true;
		if (f.isDirectory())
			return true;
		
		return false;	
	}
	
	public String getDescription()
	{
		return m_description;
	}
	
	private String getSuffix(java.io.File f)
	{
		String fstr = f.getAbsolutePath();
		int lastDot = fstr.lastIndexOf('.');
		if ((lastDot >= 0) && ((lastDot+1) < fstr.length()))
		{
			return fstr.substring(lastDot+1);
		}
		return "";
	}

}
