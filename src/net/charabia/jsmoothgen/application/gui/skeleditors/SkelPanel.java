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

import javax.swing.JLabel;
import javax.swing.JPanel;

import se.datadosen.component.RiverLayout;

public class SkelPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    static public class BTest
    {
	public void setStringTest(String t)
	    { }
	public String getStringTest()
	    { return "TEST"; }
    }

    public SkelPanel()
    {
	setLayout(new RiverLayout());
    }

    public void addBean(SkelPropEditor ed, String label)
    {
	add("p left", new JLabel(label));
	add("tab hfill", ed.getGUI());
    }

}

