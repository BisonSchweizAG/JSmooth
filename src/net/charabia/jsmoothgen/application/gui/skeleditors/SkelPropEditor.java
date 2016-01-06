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

import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.application.gui.*;
import net.charabia.jsmoothgen.application.gui.util.*;

import net.charabia.jsmoothgen.skeleton.*;

abstract public class SkelPropEditor
{
    protected SkeletonProperty m_property;
    
    public void bind(SkeletonProperty prop)
    {
	m_property = prop;
    }

    public String getIdName()
    {
	return m_property.getIdName();
    }
    
    
    public boolean labelAtLeft()
    {
	return true;
    }

    public abstract java.awt.Component getGUI();
    public abstract void valueChanged(String val);

    public abstract void set(String o);
    public abstract String get();
//     {
// 	System.out.println("setvalue of " + o + " on " + m_property);
// 	m_property.setValue(o.toString());
//     }

//     {
// 	System.out.println("getvalue from " + m_property + "=" + m_property.getValue());
// 	return m_property.getValue();
//     }

}

