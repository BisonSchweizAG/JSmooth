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

public class StringEditor extends SkelPropEditor
{
    JTextField m_comp;

    public StringEditor()
    {
	m_comp = new JTextField();
	PlainDocument doc = new PlainDocument();
// 	doc.addDocumentListener(new DocumentListener() {
// 				public void insertUpdate(DocumentEvent e) { StringEditor.this.set(m_comp.getText());  }
// 				public void removeUpdate(DocumentEvent e) { StringEditor.this.set(m_comp.getText());  }
// 				public void changedUpdate(DocumentEvent e){ StringEditor.this.set(m_comp.getText());  }
// 	    });
	m_comp.setDocument(doc);
    }

    public java.awt.Component getGUI()
    {
	return m_comp;
    }

    public void valueChanged(String val)
    {
	m_comp.setText(val);
    }

    public void set(String o) { m_comp.setText(o); }
    public String get() {return m_comp.getText(); }

}
