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

import java.util.*;

public class GenericFileFilter extends javax.swing.filechooser.FileFilter
{
    private Vector m_suffix = new Vector();;
    private String m_description;
	
    public GenericFileFilter()
    {
	m_description = "?";
    }

    public GenericFileFilter(String desc)
    {
	m_description = desc;
    }
    
    public GenericFileFilter(String suffix, String desc)
    {
	m_suffix.add(suffix.toUpperCase());
	m_description = desc;
    }

    public void addSuffix(String suffix)
    {
	m_suffix.add(suffix.toUpperCase());
    }
	
    public boolean accept(java.io.File f)
    {
	if (f.isDirectory())
	    return true;

	String suffix = getSuffix(f).toUpperCase();
	return m_suffix.contains(suffix);
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
