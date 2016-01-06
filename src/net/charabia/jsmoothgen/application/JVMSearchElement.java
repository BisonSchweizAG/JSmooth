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

package net.charabia.jsmoothgen.application;

public class JVMSearchElement
{
	static public final JVMSearchElement REGISTRY = new JVMSearchElement("registry", "Windows Registry");
	static public final JVMSearchElement JAVA_HOME = new JVMSearchElement("javahome", "JAVA_HOME environment variable");
	static public final JVMSearchElement JRE_PATH = new JVMSearchElement("jrepath", "JRE_PATH environment variable");
	static public final JVMSearchElement JDK_PATH = new JVMSearchElement("jdkpath", "JDK_PATH environment variable");
	static public final JVMSearchElement EXEPATH = new JVMSearchElement("exepath", "Windows executable path (PATH env var)");
	static public final JVMSearchElement JVIEW = new JVMSearchElement("jview", "Windows JView");
	
	static public final JVMSearchElement[] Elements = {
		REGISTRY, 
		JAVA_HOME,
		JRE_PATH, 
		JDK_PATH,
		EXEPATH,
		JVIEW
	};
	
	public static JVMSearchElement getStandardElement(String id)
	{
		for (int i=0; i<Elements.length; i++)
		{
			if (Elements[i].getId().equals(id))
				return Elements[i];
		}
		return null;
	}
	
	private String m_id;
	private String m_name;
	
	/** Creates a new instance of JVMSearchElement */
	public JVMSearchElement()
	{
	}
	
	public JVMSearchElement(String id, String name)
	{
		m_id = id;
		m_name = name;
	}
	
	public String getId()
	{
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String toString()
	{
		return m_name;
	}
}
