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

public class MainClass extends Editor
{
    private JTextField m_classname = new JTextField();
    private JButton m_chooserButton = new JButton("...");
    
    public MainClass()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_classname);
	add(BorderLayout.EAST, m_chooserButton);

	m_chooserButton.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
		    displayChooser();
		}
	    });

    }
    
    public void dataChanged()
    {
	m_classname.setText(m_model.getMainClassName());
    }

    public void updateModel()
    {
	m_model.setMainClassName(m_classname.getText());
    }

    public String getLabel()
    {
	return "MAINCLASS_LABEL";
    }

    public String getDescription()
    {
	return "MAINCLASS_HELP";
    }

    protected void displayChooser()
    {
	ClassChooserDialog chooser = new ClassChooserDialog(Main.MAIN, true);

	Vector jars = new Vector();
	if (m_model.getEmbeddedJar() == true)
	    {
		String ejar = m_model.getJarLocation();
		if (ejar != null)
		    {
			File f = getAbsolutePath(new File(ejar));
			jars.add(f);
		    }
	    }

	String[] cp = m_model.getClassPath();
	if (cp != null)
	    {
		for (int i=0; i<cp.length; i++)
		    {
			jars.add(getAbsolutePath(new File(cp[i])));
		    }
	    }

	chooser.clear();
	for (Iterator i=jars.iterator(); i.hasNext(); )
	    {
		File f = (File)i.next();
// 		System.out.println("Adding jar <" + f + ">");
		try {
		    chooser.addJar(new JarFile(f));
		} catch (Exception ex)
		    {
			ex.printStackTrace();
		    }
	    }

	chooser.setClassName(m_classname.getText());
	chooser.setVisible(true);
	if (chooser.validated())
	    {
		String classname = chooser.getClassName();
		m_classname.setText(classname);
	    }
    }
        
}
