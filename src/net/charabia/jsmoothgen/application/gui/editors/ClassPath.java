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

import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

public class ClassPath extends Editor
{
    private JFileChooser m_jarLocFileChooser = new JFileChooser();
    private EditableListFileEditor m_fileeditor = new EditableListFileEditor();
    private SortedEditableList m_list = new SortedEditableList() {
	    protected void modelChanged()
	    {
		ClassPath.this.updateModel();
	    }
	};

    public ClassPath()
    {
	m_jarLocFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	m_jarLocFileChooser.setMultiSelectionEnabled(true);
	GenericFileFilter filter = new GenericFileFilter("Zip, Jar, or directories");
	filter.addSuffix("jar");
	filter.addSuffix("zip");
	m_jarLocFileChooser.addChoosableFileFilter(filter);
	m_fileeditor.setFileChooser(m_jarLocFileChooser);
	m_list.setEditor(m_fileeditor);

	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_list);
    }
    
    public void dataChanged()
    {
	if (getBaseDir() != null)
	    {
		m_jarLocFileChooser.setCurrentDirectory(getBaseDir());
	    }

	String[] cp = m_model.getClassPath();
	if (cp == null)
	    m_list.setData(new Object[0]);
	else
	    m_list.setData((Object[])cp);
    }

    public void updateModel()
    {
	Object[] cpels = m_list.getData();
	String[] cp = new String[cpels.length];
	for (int i=0; i<cp.length; i++)
	    {
		cp[i] = cpels[i].toString();
	    }
	m_model.setClassPath(cp);
    }

    public String getLabel()
    {
	return "CLASSPATH_LABEL";
    }

    public String getDescription()
    {
	return "CLASSPATH_HELP";
    }
        
    public boolean needsBigSpace()
    {
	return true;
    }

}

