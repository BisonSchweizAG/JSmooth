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
import java.util.*;
import net.charabia.jsmoothgen.application.gui.Main;
/**
 * 
 */

public class HelpButton extends JLabel
{
    protected JWindow m_helpWindow = new JWindow(Main.MAIN);

    private final Icon ICON_HELP = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_help-agent-16.png"));

    public HelpButton(String helptext)
    {
	setText("");
	setIcon(ICON_HELP);
	m_helpWindow.getContentPane().setBackground(Color.yellow);
	m_helpWindow.getContentPane().setLayout(new BorderLayout());
	JEditorPane jep = new JEditorPane("text/html", wrap(helptext));
	jep.setBackground(Color.yellow);
	jep.setEditable(false);
	m_helpWindow.getContentPane().add(jep, BorderLayout.CENTER);
	jep.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
	m_helpWindow.pack();
	
	m_helpWindow.addMouseListener( new MouseAdapter() {
		public void mouseEntered(MouseEvent e)
		{
		    HelpButton.this.m_helpWindow.dispose();
		}

		public void mouseExited(MouseEvent e)
		{
		    HelpButton.this.m_helpWindow.dispose();
		}

	    });

	addMouseListener(new MouseAdapter() {
		public void mouseEntered(MouseEvent e)
		{
		    HelpButton.this.requestFocus();
		    HelpButton.this.adjustLocation();
		    m_helpWindow.setVisible(true);
		}

		public void mouseExited(MouseEvent e)
		{
		    m_helpWindow.setVisible(false);
		}
	    });

    }

    public void setVisible(boolean b)
    {
	super.setVisible(b);
	if (b == false)
	    m_helpWindow.setVisible(false);
    }

    public String wrap(String str)
    {
	StringBuffer sb = new StringBuffer();
	StringBuffer line = new StringBuffer();
	StringTokenizer stok = new StringTokenizer(str, " ", true);
	while (stok.hasMoreElements())
	    {
		boolean cut = false;
		String tok = stok.nextToken();
		line.append(tok);

		if (tok.indexOf("<br")>=0 || tok.indexOf("<BR")>=0
		    || tok.indexOf("<p>")>=0 || tok.indexOf("<p/>")>=0
		    || tok.indexOf("<P/>")>=0 || tok.indexOf("<P/>")>=0)
		    {
			sb.append(line);
			line.setLength(0);
		    }

		if (line.length() > 80)
		    {
			sb.append(line);
			sb.append("<br>\n");
			line.setLength(0);
		    }

	    }
	sb.append(line);
	return sb.toString();
    }

    public void adjustLocation()
    {
	// m_helpWindow.setLocationRelativeTo(this);
	Point p = this.getLocationOnScreen();
	p.y += getHeight();
	Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	if ((p.x + m_helpWindow.getWidth()) > bounds.width)
	    p.x = bounds.width - m_helpWindow.getWidth();
	if (p.x < bounds.x)
	    p.x = bounds.x;
	m_helpWindow.setLocation(p.x, p.y);
    }
    
}
