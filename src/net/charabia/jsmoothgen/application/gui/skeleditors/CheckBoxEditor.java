/*
  JSmooth: a VM wrapper toolkit for Windows
  Copyright (C) 2003-2007 Rodrigo Reyes <reyes@charabia.net>
 
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

package net.charabia.jsmoothgen.application.gui.skeleditors;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.event.*;

public class CheckBoxEditor extends SkelPropEditor
{
    JCheckBox m_comp;

    public CheckBoxEditor()
    {
	m_comp = new JCheckBox();
    }

    public java.awt.Component getGUI()
    {
	return m_comp;
    }

    public void valueChanged(String val)
    {
	if (val.toString().equals("1"))
	    m_comp.setSelected(true);
	else
	    m_comp.setSelected(false);
    }

    public boolean labelAtLeft()
    {
	return false;
    }

    public void set(String o) { if ("1".equals(o)) m_comp.setSelected(true); else m_comp.setSelected(false); }
    public String get() {return m_comp.isSelected()?"1":"0"; }

}
