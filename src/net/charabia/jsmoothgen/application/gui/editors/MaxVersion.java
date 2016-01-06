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

public class MaxVersion extends Editor
{
    private JTextField m_args = new JTextField();
    
    public MaxVersion()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER, m_args);
	m_args.setDocument(new RegExDocument("[0-9]+((([.][0-9]+)*)[.]?)"));
    }
    
    public void dataChanged()
    {
	m_args.setText(m_model.getMaximumVersion());
    }

    public void updateModel()
    {
	m_model.setMaximumVersion(m_args.getText());
    }

    public String getLabel()
    {
	return "MAXVERSION_LABEL";
    }

    public String getDescription()
    {
	return "MAXVERSION_HELP";
    }
        
}
