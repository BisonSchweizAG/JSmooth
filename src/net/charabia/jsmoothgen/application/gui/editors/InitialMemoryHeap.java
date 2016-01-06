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

public class InitialMemoryHeap extends Editor
{
    private JTextField m_args = new JTextField();
    private JComboBox  m_units = new JComboBox();

    public InitialMemoryHeap()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_args);
	m_args.setDocument(new RegExDocument("[0-9]+"));
	m_args.setHorizontalAlignment(JTextField.RIGHT);

	Vector u = new Vector();
	u.add(Main.local("UNIT_MB"));
	u.add(Main.local("UNIT_KB"));
	u.add(Main.local("UNIT_BYTE"));
	DefaultComboBoxModel mod = new DefaultComboBoxModel(u);
	m_units.setModel(mod);
	add(BorderLayout.EAST, m_units);
    }
    
    public void dataChanged()
    {
	setValueCombo(m_model.getInitialMemoryHeap(), m_args, m_units);
    }

    public void updateModel()
    {
	int value;

// 	System.out.println("Parsing " + m_args.getText());

	try {
	    value = Integer.parseInt(m_args.getText());
	} catch (Exception exc)
	    {
		// nothing here
		m_model.setInitialMemoryHeap(-1);
		return;
	    }
// 	System.out.println("sel index: " + m_units.getSelectedIndex() + " / " + value + " / " + (value*1024*1024));

	switch(m_units.getSelectedIndex())
	    {
		case 0:
		    if (value>2047)
			value = 2047;
		    m_model.setInitialMemoryHeap(value * 1024 * 1024);
		    break;
		case 1:
		    m_model.setInitialMemoryHeap(value * 1024);
		    break;
		case 2:
		    m_model.setInitialMemoryHeap(value);
		    break;
	    }

// 	System.out.println("Resulting initmem: " + m_model.getInitialMemoryHeap());
    }

    public String getLabel()
    {
	return "INITIALMEMORY_LABEL";
    }

    public String getDescription()
    {
	return "INITIALMEMORY_HELP";
    }

    public void setValueCombo(int value, JTextField num, JComboBox box)
    {
	if (value >= (1024 * 1024))
	    {
		num.setText( new Integer(value / (1024*1024)).toString());
		box.setSelectedIndex(0);
	    }
	else if (value >= 1024)
	    {
		num.setText( new Integer(value / 1024).toString());
		box.setSelectedIndex(1);		
	    }
	else if (value > 0)
	    {
		num.setText( new Integer(value).toString());
		box.setSelectedIndex(2);
	    }
	else
	    {
		num.setText("");
		box.setSelectedIndex(0);
	    }
    }
        
}
