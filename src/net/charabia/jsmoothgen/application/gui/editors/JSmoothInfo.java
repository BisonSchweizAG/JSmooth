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

import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.application.gui.*;
import net.charabia.jsmoothgen.application.gui.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import se.datadosen.component.RiverLayout;

public class JSmoothInfo extends Editor implements JSmoothModelBean.SkeletonChangedListener
{
    //    private JEditorPane m_skeldesc = new JEditorPane("text/html","<html></html>");
    private HTMLPane m_skeldesc = new HTMLPane();

    public JSmoothInfo()
    {
	setLayout(new java.awt.BorderLayout());
	add(java.awt.BorderLayout.CENTER, m_skeldesc);
	setBackground(java.awt.Color.red);
	String text = Main.MAIN.local("JSMOOTH_WELCOME_SCREEN");
	m_skeldesc.setText(text);
    }

    public void dataChanged() { }

    private void updateSkeletonData()
    {
    }

    
    public void updateModel()
    {
    }


    public String getLabel()
    {
	return "SKELETONCHOOSER_LABEL";
    }

    public String getDescription()
    {
	return "SKELETONCHOOSER_HELP";
    }
    
    public void skeletonChanged()
    {
    }

    public boolean needsBigSpace()
    {
	return true;
    }

    public boolean needsFullSpace()
    {
	return true;
    }

    public String readFile(java.io.File f)
    {
	StringBuffer buffer = new StringBuffer();
	try {
	    FileInputStream fis = new FileInputStream(f);
	    InputStreamReader isr = new InputStreamReader(fis);
	    int c;
	    while ((c=isr.read())!=-1)
		buffer.append((char)c);
	    isr.close();
	    fis.close();
	} catch (Exception ex)
	    {
		ex.printStackTrace();
	    }
	return buffer.toString();
    }

}
