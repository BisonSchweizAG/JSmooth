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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import com.l2fprod.common.swing.*;
import net.charabia.jsmoothgen.application.*;

public abstract class Editor extends JPanel
{
    protected JSmoothModelBean m_model;
    protected java.io.File m_basedir;

    public void attach(JSmoothModelBean model, java.io.File basedir)
    {
	m_model = model;
	m_basedir = basedir;
    }

    public void detach()
    {
	m_model = null;
	m_basedir = null;
    }

    protected JSmoothModelBean getModel()
    {
	return m_model;
    }

    protected java.io.File getBaseDir()
    {
	return m_basedir;
    }

    abstract public void dataChanged();
    abstract public void updateModel();

    abstract public String getLabel();
    abstract public String getDescription();

    public boolean needsBigSpace()
    {
	return false;
    }

    public boolean needsFullSpace()
    {
	return false;
    }
    
    public boolean useDescription()
    {
	return true;
    }

    protected java.io.File getAbsolutePath(java.io.File f)
    {
	if (f.isAbsolute())
		return f;
	
	return new java.io.File(m_basedir.getAbsoluteFile(), f.toString());
    }
}
