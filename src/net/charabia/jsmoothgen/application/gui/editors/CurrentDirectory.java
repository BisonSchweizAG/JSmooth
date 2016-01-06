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
import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

public class CurrentDirectory extends Editor
{
    private FileSelectionTextField m_selector = new FileSelectionTextField();
    private JCheckBox m_forceExePath = new JCheckBox();
    
    public CurrentDirectory()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_selector);
	
	JPanel jpc = new JPanel();
	jpc.setLayout(new BorderLayout());
	jpc.add(BorderLayout.WEST, m_forceExePath);
	jpc.add(BorderLayout.CENTER, new HelpButton(Main.local("CURRENTDIR_FORCEEXEPATH_HELP")));
	add(BorderLayout.SOUTH, jpc);

	m_forceExePath.setAction(new AbstractAction(Main.local("CURRENTDIR_FORCEEXEPATH")) {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (m_forceExePath.isSelected())
			m_selector.setEnabled(false);
		    else
			m_selector.setEnabled(true);
		}
	    });

	m_selector.setFileChooser(new JDirectoryChooser());
    }
    
    public void dataChanged()
    {
// 	System.out.println("CurDir, basedir=" + getBaseDir());
	m_selector.setBaseDir(getBaseDir());
	String dir = m_model.getCurrentDirectory();
// 	System.out.println("Cur Directory data changed: " + dir);

	if ("${EXECUTABLEPATH}".equals(dir))
	    {
		m_selector.setEnabled(false);
		m_forceExePath.setSelected(true);
	    }
	else
	    {
		m_forceExePath.setSelected(false);
		m_selector.setEnabled(true);

		if ((dir != null) && (dir.trim().length()>0))
		    {
			m_selector.setFile(new java.io.File(dir));
		    }
		else
		    {
			m_selector.setFile(null);
		    }
	    }
    }

    public void updateModel()
    {
// 	System.out.println("UPDATE MODEL: " + m_selector.getFile());

	if (m_forceExePath.isSelected())
	    {
		m_model.setCurrentDirectory("${EXECUTABLEPATH}");
	    }
	else
	    {
		if (m_selector.getFile() != null)
		    m_model.setCurrentDirectory(m_selector.getFile().toString());
		else
		    m_model.setCurrentDirectory(null);
	    }
    }

    public String getLabel()
    {
	return "CURRENTDIR_LABEL";
    }

    public String getDescription()
    {
	return "CURRENTDIR_HELP";
    }
        
}
