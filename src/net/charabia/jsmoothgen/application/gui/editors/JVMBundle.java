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
import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

public class JVMBundle extends Editor
{
    private JCheckBox m_checker = new JCheckBox();
    private FileSelectionTextField m_selector = new FileSelectionTextField();
 
    public JVMBundle()
    {
	setLayout(new PanelLayout());
	add(m_checker);
	add(m_selector);

	m_selector.setFileChooser(new JDirectoryChooser());

	m_checker.setAction(new AbstractAction(Main.local("JVMBUNDLE_CHECKBOX")) {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    m_selector.setEnabled(m_checker.isSelected());
		}
	    });

	if (m_model != null)
	    dataChanged();
    }

    public void dataChanged()
    {
	String bundle = m_model.getBundledJVMPath();
	if (bundle == null)
	    {
		m_checker.setSelected(false);
		m_selector.setBaseDir(getBaseDir());
		m_selector.setFile(null);
		m_selector.setEnabled(false);
	    }
	else
	    {
		m_checker.setSelected(true);
		m_selector.setBaseDir(getBaseDir());
		m_selector.setFile(new java.io.File(bundle));
		m_selector.setEnabled(true);
	    }
    }

    public void updateModel()
    {
	if (m_checker.isSelected())
	    {
		File f = m_selector.getFile();
		if (f != null)
		    m_model.setBundledJVMPath(f.toString());
		else
		    m_model.setBundledJVMPath("");
	    }
	else
	    {
		m_model.setBundledJVMPath(null);
	    }
    }

    public String getLabel()
    {
	return "JVMBUNDLE_LABEL";
    }

    public String getDescription()
    {
	return "JVMBUNDLE_HELP";
    }
    
}
