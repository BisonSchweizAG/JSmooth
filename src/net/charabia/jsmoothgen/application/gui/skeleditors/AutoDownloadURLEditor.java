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

public class AutoDownloadURLEditor extends SkelPropEditor
{
    protected String[] m_autourls = new String[] {
	"JRE 1.6.0",  	"http://java.sun.com/update/1.6.0/jinstall-6-windows-i586.cab",
	"JRE 1.5.0_11", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_11-windows-i586.cab",
	"JRE 1.5.0_10", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_10-windows-i586.cab",
	"JRE 1.5.0_07", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_07-windows-i586.cab",
	"JRE 1.5.0_06", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_06-windows-i586.cab",
	"JRE 1.5.0_03", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_03-windows-i586.cab",
	"JRE 1.5.0_02", 	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_02-windows-i586.cab",
	"JRE 1.5.0_01",  	"http://java.sun.com/update/1.5.0/jinstall-1_5_0_01-windows-i586.cab",
	"JRE 1.5.0", "http://java.sun.com/update/1.5.0/jinstall-1_5_0-windows-i586.cab",
	"JRE 1.4.2_03", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_03-windows-i586.cab",
	"JRE 1.4.2_02", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_02-windows-i586.cab",
	"JRE 1.4.2_01", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_01-windows-i586.cab",
	"JRE 1.4.2", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_2-windows-i586.cab",
	"JRE 1.4.1_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_03-windows-i586.cab",
	"JRE 1.4.1_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_02-windows-i586.cab",
	"JRE 1.4.1_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_01-windows-i586.cab",
	"JRE 1.4.1", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1-windows-i586.cab",
	"JRE 1.4.0_04", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_04-win.cab",
	"JRE 1.4.0_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_03-win.cab",
	"JRE 1.4.0_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_02-win.cab",
	"JRE 1.4.0_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_01-win.cab",
	"JRE 1.4.0", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0-win.cab",
	"JRE 1.3.1_08", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_08-windows-i586.cab",
	"JRE 1.3.1_07", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_07-windows-i586.cab",
	"JRE 1.3.1_06", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_06-windows-i586.cab",
	"JRE 1.3.1_05", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_05-windows-i586.cab",
	"JRE 1.3.1_04", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_04-win.cab",
	"JRE 1.3.1_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_03-win.cab",
	"JRE 1.3.1_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_02-win.cab",
	"JRE 1.3.1_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_01-win.cab",
	"JRE 1.3.0_05", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_0_05-win.cab"
    };

    protected JComboBox m_comp;

    public AutoDownloadURLEditor()
    {
	String[]s=new String[m_autourls.length/2];
	for (int i=0; i<s.length; i++)
	    s[i] = m_autourls[i*2];
	m_comp = new JComboBox(s);
    }

    public java.awt.Component getGUI()
    {
	return m_comp;
    }

    public String findURL(String name)
    {
	for (int i=0; i<m_autourls.length/2; i++)
	    {
		if (m_autourls[i*2].equals(name))
		    return m_autourls[i*2+1];
	    }
	return name;
    }

    public void valueChanged(String val)
    {
	m_comp.setSelectedItem(findURL(val));
    }

    public boolean labelAtLeft()
    {
	return true;
    }

    public void set(String o) { m_comp.setSelectedItem(findURL(o.toString())); }

    public String get() {return m_autourls[m_comp.getSelectedIndex()*2+1]; }

}
