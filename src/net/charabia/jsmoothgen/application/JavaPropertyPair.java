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

/*
 * JSmoothModelPersistency.java
 *
 * Created on 7 août 2003, 19:42
 */

package net.charabia.jsmoothgen.application;

import java.io.*;
import java.util.*;

public class JavaPropertyPair
{
    private String m_name;
    private String m_value;

    public JavaPropertyPair()
    {
    }

    public JavaPropertyPair(String name, String value)
    {
	m_name = name;
	m_value = value;
    }
    
    public void setName(String name)
    {
	m_name = name;
    }

    public String getName()
    {
	return m_name;
    }

    public void setValue(String value)
    {
	m_value = value;
    }

    public String getValue()
    {
	return m_value;
    }

    public String toString()
    {
	return m_name + "=" + m_value;
    }

}
