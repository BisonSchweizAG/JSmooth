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

package net.charabia.jsmoothgen.application.gui;

import java.awt.*;
import java.awt.event.*;

public class Splash
{
    protected Image m_splashImage;
    protected Window  m_window;
    protected String m_version= "";

    public class MyWindow extends Window
    {
	public MyWindow(Frame f)
	{
	    super(f);
	}
	public void paint(Graphics g) 
	{
	    g.drawImage(Splash.this.m_splashImage, 0, 0, this);
	    g.setFont(g.getFont().deriveFont(Font.BOLD).deriveFont(16.0f));
	    FontMetrics fm = g.getFontMetrics();
	    int width = fm.stringWidth(Splash.this.m_version) + 20;
	    int height = fm.getHeight();
	    g.setColor(Color.black);
	    g.drawString(Splash.this.m_version, this.getWidth() - width, this.getHeight() - height);
	}
    }

    public class MyDialog extends Dialog
    {
	public MyDialog(Frame f)
	{
	    super(f, true);
	    setResizable(false);
	}
	public void paint(Graphics g) 
	{
	    g.drawImage(Splash.this.m_splashImage, 0, 0, this);
	}                
    }

    public Splash(Frame parent, String imagefilename, boolean dialog)
    {
	if (dialog)
	    {
                m_window = new MyDialog(parent);
	    }
	else
	    {
                m_window = new MyWindow(parent);
	    }
	javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(imagefilename));
	m_splashImage = icon.getImage();
	MediaTracker loader = new MediaTracker(m_window);
	loader.addImage(m_splashImage, 0);
	try { 
            loader.waitForAll(); 
	} catch (Exception e) {}
    }

    public void show()
    {
	Dimension screendim = Toolkit.getDefaultToolkit().getScreenSize();
	m_window.setSize(m_splashImage.getWidth(m_window), m_splashImage.getHeight(m_window));
	m_window.setLocation( (screendim.width - m_splashImage.getWidth(m_window))/2,
			      (screendim.height- m_splashImage.getHeight(m_window))/2);
	m_window.setVisible(true);
    }

    public void dispose()
    {
	m_window.dispose();       
    }

    public void setVersion(String version)
    {
	m_version = version;
    }

    public void toFront()
    {
	m_window.toFront();
    }

}
