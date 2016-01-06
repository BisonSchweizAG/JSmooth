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
import java.awt.event.*;
import java.util.*;
import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

import se.datadosen.component.RiverLayout;

public class SkeletonChooser extends Editor implements JSmoothModelBean.SkeletonChangedListener
{
    private JComboBox m_skelcombo = new JComboBox();
//     private HTMLPane m_skeldesc = new HTMLPane() {
// 	    public java.awt.Dimension getPreferredSize()
// 	    {
// 		java.awt.Dimension d = super.getPreferredSize();
// 		if (d.height<100)
// 		    d.height=100;
// 		return d;
// 	    }
// 	};

    private JEditorPane m_skeldesc = new JEditorPane("text/html","<html></html>");

    public SkeletonChooser()
    {
	//	PanelLayout pl = new PanelLayout();
	//	setLayout(pl);
	setLayout(new RiverLayout());
	m_skelcombo.addItem("<none>");
	for (Iterator i=Main.SKELETONS.getIteratorNoDebugName(); i.hasNext(); )
	    {
		m_skelcombo.addItem(i.next().toString());
	    }
	add("hfill", m_skelcombo);
	JPanel jp = new JPanel();
	jp.setLayout(new java.awt.BorderLayout());
	jp.add(new JScrollPane(m_skeldesc) {
		public java.awt.Dimension getMinimumSize()
		{
		    return new java.awt.Dimension(10,100);
		}
		public java.awt.Dimension getPreferredSize()
		{
		    return new java.awt.Dimension(10,100);
		}
	    }, java.awt.BorderLayout.CENTER);
	add("br hfill", jp);
	m_skeldesc.setEditable(false);
	m_skelcombo.addActionListener(new java.awt.event.ActionListener()  {
		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
		    updateModel();
		    doLayout();
		    validate();
		    repaint();
		}
	    });
    }

    private void updateSkeletonData()
    {
 	String skelname = (String) m_skelcombo.getSelectedItem();
 	if (skelname == null)
 	    return;
		
 	SkeletonBean skel = Main.SKELETONS.getSkeleton(skelname);
	
//  	System.out.println("SKEL: " + skel.toString());
 	if (skel != null)
 	    {
 		m_skeldesc.setText(Main.local(skel.getDescription()));
		m_skeldesc.setCaretPosition(0);
 	    }
    }

    public void dataChanged()
    {
	String skelname = m_model.getSkeletonName();
	if (skelname != null)
	    {
		SkeletonBean skel = Main.SKELETONS.getSkeleton(skelname);
		if (skel != null)
		    {
			m_skeldesc.setText(Main.local(skel.getDescription()));
			m_skeldesc.setCaretPosition(0);
			m_skelcombo.setSelectedItem(skelname);
		    }
		else
		    {
			m_skelcombo.setSelectedItem("");
			m_skeldesc.setText("");
			m_skeldesc.setCaretPosition(0);
		    }
	    }
	else
	    {
		m_skelcombo.setSelectedItem("");
		m_skeldesc.setText(Main.local("SKEL_CHOOSER_NONE"));
		m_skeldesc.setCaretPosition(0);
	    }
    }
    
    public void updateModel()
    {
 	String skelname = (String) m_skelcombo.getSelectedItem();
 	if (skelname == null)
 	    return;

	if (skelname.equals("<none>"))
	    skelname = "<none>";

	m_model.setSkeletonName(skelname);
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
	dataChanged();
    }

    public boolean needsBigSpace()
    {
	return true;
    }

}
