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

public class TextAreaEditor extends SkelPropEditor
{
    JTextArea m_comp;
    JScrollPane m_sp;
    public TextAreaEditor()
    {
	m_comp = new JTextArea(5, 40);
	PlainDocument doc = new PlainDocument();
	m_comp.setDocument(doc);
	m_sp = new JScrollPane(m_comp);
    }

    public java.awt.Component getGUI()
    {
	return m_sp;
    }

    public void valueChanged(String val)
    {
	m_comp.setText(val);
    }

    public void set(String o) { m_comp.setText(o); }
    public String get() {return m_comp.getText(); }

}
