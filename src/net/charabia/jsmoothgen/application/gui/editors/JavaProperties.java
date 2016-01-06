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

public class JavaProperties extends Editor
{
    private SortedEditableList m_props = new SortedEditableList();

    public class PropEditor implements SortedEditableList.Editor
    {
	public Object createNewItem(SortedEditableList selist)
	{
	    JavaPropertyPair jpp = new JavaPropertyPair("", "");
	    PropertyEditorDialog dia = new PropertyEditorDialog(jpp);

	    if (dia.ask() && (jpp.getName().trim().length() > 0))
		return jpp;
	    else
		return null;
	}
                
	public Object editItem(SortedEditableList selist, Object item)
	{
	    JavaPropertyPair jpp = (JavaPropertyPair)item;
	    PropertyEditorDialog dia = new PropertyEditorDialog(jpp);                    
	    dia.setVisible(true);
	    return item;
	}
                
	public boolean removeItem(SortedEditableList selist, Object item)
	{
	    return true;
	}
    }
    
    public JavaProperties()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_props);
	m_props.setEditor(new PropEditor());
    }
    
    public void dataChanged()
    {
	JavaPropertyPair[] props = m_model.getJavaProperties();
	m_props.setData(props);
    }

    public void updateModel()
    {
	Object[] po = m_props.getData();
	JavaPropertyPair[] props = new JavaPropertyPair[po.length];
	for (int i=0; i<po.length; i++)
	    {
		props[i] = (JavaPropertyPair)po[i];
	    }
	m_model.setJavaProperties(props);
    }

    public String getLabel()
    {
	return "JAVAPROP_LABEL";
    }

    public String getDescription()
    {
	return "JAVAPROP_HELP";
    }

    public boolean needsBigSpace()
    {
	return true;
    }
        
}
