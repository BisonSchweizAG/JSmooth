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

public class EmbeddedJar extends Editor
{
    private JCheckBox m_checker = new JCheckBox();
    private FileSelectionTextField m_selector = new FileSelectionTextField();
 
    public EmbeddedJar()
    {
	setLayout(new PanelLayout());
	add(m_checker);
	add(m_selector);

	m_checker.setAction(new AbstractAction(Main.local("EMBEDDEDJAR_CHECKBOX")) {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
// 		    System.out.println("Embedded jar, checkbox action");
		    m_selector.setEnabled(m_checker.isSelected());
		    updateModel();
		}
	    });
	
	m_selector.addListener(new FileSelectionTextField.FileSelected() {
		public void fileSelected(String filename)
		{
		    updateModel();
		}
	    });
    }
    
    public void dataChanged()
    {
	m_checker.setSelected(m_model.getEmbeddedJar());
	m_selector.setBaseDir(getBaseDir());

	if (m_model.getJarLocation() != null)
	    m_selector.setFile(new File(m_model.getJarLocation()));
	else
	    m_selector.setFile(null);

	if (m_checker.isSelected())
	    m_selector.setEnabled(true);
	else
	    m_selector.setEnabled(false);
    }

    public void updateModel()
    {
	m_model.setEmbeddedJar(m_checker.isSelected());
	if (m_selector.getFile() != null)
	    m_model.setJarLocation(m_selector.getFile().toString());
	else
	    m_model.setJarLocation(null);
    }

    public String getLabel()
    {
	return "EMBEDDEDJAR_LABEL";
    }

    public String getDescription()
    {
	return "EMBEDDEDJAR_HELP";
    }
    
}
