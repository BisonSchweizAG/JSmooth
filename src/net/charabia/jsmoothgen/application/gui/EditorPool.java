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

package net.charabia.jsmoothgen.application.gui;

import java.util.*;

public class EditorPool
{
    private Hashtable m_classToInstance = new Hashtable();

    public EditorPool()
    {
	add(net.charabia.jsmoothgen.application.gui.editors.SkeletonChooser.class);
	add(net.charabia.jsmoothgen.application.gui.editors.SkeletonProperties.class);
	add(net.charabia.jsmoothgen.application.gui.editors.ExecutableName.class );
	add(net.charabia.jsmoothgen.application.gui.editors.ExecutableIcon.class );
	add(net.charabia.jsmoothgen.application.gui.editors.CurrentDirectory.class);
	add(net.charabia.jsmoothgen.application.gui.editors.MainClass.class);
	add(net.charabia.jsmoothgen.application.gui.editors.ApplicationArguments.class);
	add(net.charabia.jsmoothgen.application.gui.editors.EmbeddedJar.class);
	add(net.charabia.jsmoothgen.application.gui.editors.ClassPath.class);
	add(net.charabia.jsmoothgen.application.gui.editors.MinVersion.class);
	add(net.charabia.jsmoothgen.application.gui.editors.MaxVersion.class);
	add(net.charabia.jsmoothgen.application.gui.editors.JVMBundle.class);
	add(net.charabia.jsmoothgen.application.gui.editors.JVMSearchSequence.class);
	add(net.charabia.jsmoothgen.application.gui.editors.MaxMemoryHeap.class);
	add(net.charabia.jsmoothgen.application.gui.editors.InitialMemoryHeap.class);
	add(net.charabia.jsmoothgen.application.gui.editors.JavaProperties.class);
    }

    private void add(Class clzz)
    {
	try {
	    m_classToInstance.put(clzz, clzz.newInstance());
	} catch (Exception exc)
	    {
		exc.printStackTrace();
	    }
    }
    
    public Editor getInstance(Class clzz)
    {
	Editor e = (Editor)m_classToInstance.get(clzz);
	if (e == null)
	    {
		try {
		    e = (Editor)clzz.newInstance();
		    m_classToInstance.put(clzz, e);
		} catch (Exception exc)
		    {
			exc.printStackTrace();
		    }
	    }
	return e;
    }
}
