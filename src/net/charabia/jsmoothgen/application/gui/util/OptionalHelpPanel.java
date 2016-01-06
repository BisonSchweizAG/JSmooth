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

package net.charabia.jsmoothgen.application.gui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import com.l2fprod.common.swing.*;

/**
 * Represents a JTextField associated with a button that pops up a
 * file dialog selector.
 *
 */

public class OptionalHelpPanel extends JPanel
{
    private JPanel m_panel = new JPanel();
    protected boolean m_limitHeight = true;

    private HTMLPane m_helpPanel = new HTMLPane() {
	    public Dimension getPreferredSize()
	    {
		Dimension d = super.getPreferredSize();
		if (OptionalHelpPanel.this.m_limitHeight)
		    {
			if (d.height>180)
			    d.height = 180;
		    }
		return d;
	    }
	};

    private JPanel m_titlePanel = new JPanel();
    private JLabel m_title = new JLabel();
    private JButton m_helptoggle = new JButton("?");

    private boolean m_helpActive = false;

    public OptionalHelpPanel()
    {
	setLayout(new GridBagLayout());

	m_helptoggle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
		    m_helpActive = !m_helpActive;
		    toggleHelpCheck();
		}
	    });


	setLayout(new PanelLayout());

	m_titlePanel.setLayout(new BorderLayout());
       	m_titlePanel.add(BorderLayout.CENTER, m_title);
	m_titlePanel.add(BorderLayout.EAST, m_helptoggle);
	m_titlePanel.setOpaque(true);
	m_helptoggle.setOpaque(false);
	m_helptoggle.setBorder(null);
	m_title.setBorder(BorderFactory.createEmptyBorder(2,10,2,2));
 	add(m_titlePanel);
	
 	add(m_panel);
	add(m_helpPanel);

	setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2,2,2,2), BorderFactory.createBevelBorder(BevelBorder.RAISED)), BorderFactory.createEmptyBorder(2,2,2,2)));

	java.net.URL helpimgurl = getClass().getResource("/icons/stock_help-agent.png");
	if (helpimgurl != null)
	    {
		javax.swing.ImageIcon leaf = new javax.swing.ImageIcon(helpimgurl);
		m_helptoggle.setIcon(leaf);
		m_helptoggle.setText("");
	    }
	else
	    {
		m_helptoggle.setText("(?)");
	    }
	setLabelColor(Color.white, Color.darkGray);

	toggleHelpCheck();
    }

    private void toggleHelpCheck()
    {
	m_helpPanel.setVisible(m_helpActive);
	validate();
	repaint();
    }

    public Dimension getPreferredSize()
    {
	Dimension d = super.getPreferredSize();
	d.width = 1;
	return d;
    }


    public JPanel getContentPane()
    {
	return m_panel;
    }

    public void setLabel(String label)
    {
	label = "<html><b>" + label + "</b></hmtl>";
	m_title.setText(label);
    }

    public void setHelpText(String help)
    {
	m_helpPanel.setText(help);
    }

    public void setLabelColor(Color fore, Color back)
    {
	m_titlePanel.setBackground(back);
	m_title.setForeground(fore);
	repaint();
    }

    public void setLimitHeight(boolean b)
    {
	m_limitHeight = b;
	validate();
	repaint();
    }

    public static void main(String[] args)
    {
	JFrame f = new JFrame("test");

	OptionalHelpPanel ohp = new OptionalHelpPanel();
	ohp.setHelpText("This is my help text");
	ohp.setLabel("My label1");
	ohp.getContentPane().setLayout(new BorderLayout());
	//	ohp.getContentPane().add(BorderLayout.CENTER, new JScrollPane(new JTextArea(40,40)));
	ohp.getContentPane().add(BorderLayout.CENTER, new FileSelectionTextField());

	OptionalHelpPanel ohp2 = new OptionalHelpPanel();
	ohp.setLabel("hop2");
	ohp2.setHelpText("<html><h1>This is my second help text</h1>le géant du logiciel va-t-il écouter et arrêter le déluge promotionel qui vise à faire connaitre les nouveautés de Office 2003 alors que les utilisateurs sont satisfaits des versions précédentes et que les responsables zieutent lourdement du coté de Linux et d'OpenOffice ?");
	ohp2.getContentPane().setLayout(new BorderLayout());
	ohp2.getContentPane().add(BorderLayout.CENTER, new JTextField());

	f.getContentPane().setLayout(new PanelLayout());
	f.getContentPane().add(ohp);
	f.getContentPane().add(ohp2);
	f.setSize(300,300);
	f.setVisible(true);
    }
}
