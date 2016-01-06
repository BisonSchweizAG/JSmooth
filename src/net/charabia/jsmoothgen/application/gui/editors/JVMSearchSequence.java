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

package net.charabia.jsmoothgen.application.gui.editors;

import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.application.gui.*;
import net.charabia.jsmoothgen.application.gui.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.File;
import java.util.jar.*;

public class JVMSearchSequence extends Editor
{
    private SortedEditableList m_vmSearch = new SortedEditableList();
    
    public JVMSearchSequence()
    {
        m_vmSearch.setEditableItems(false);

	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_vmSearch);
    }
    
    public void dataChanged()
    {
	if (m_model.getJVMSearchPath() == null)
	    {
		m_vmSearch.setData(JVMSearchElement.Elements);
	    } else
		{
		    Vector v = new Vector();
		    String[] els = m_model.getJVMSearchPath();
		    for (int i=0; i<els.length; i++)
			{
			    JVMSearchElement el = JVMSearchElement.getStandardElement(els[i]);
			    if (el != null)
				v.add(el);
			}
		    m_vmSearch.setData(v.toArray());
		}
    }

    public void updateModel()
    {
	String[] ids = new String[m_vmSearch.dataSize()];
	Object[] data = m_vmSearch.getData();
	for (int i=0; i<ids.length; i++)
	    {
		ids[i] = ((JVMSearchElement)data[i]).getId();
	    }
	m_model.setJVMSearchPath(ids);
    }

    public String getLabel()
    {
	return "JVMSEARCH_LABEL";
    }

    public String getDescription()
    {
	return "JVMSEARCH_HELP";
    }

    public boolean needsBigSpace()
    {
	return true;
    }
        
}
