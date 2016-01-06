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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class LayoutLengthDescriptor
{
    public static final int PIXEL = 1;
    public static final int PERCENT = 2;

    private int m_length;
    private int m_unit;

    public LayoutLengthDescriptor(String s) 
    {
	try {
	    s = s.trim();

	    StringBuffer len = new StringBuffer();
	    StringBuffer unit = new StringBuffer();

	    int offset = 0;

	    for (; offset < s.length(); offset++)
		{
		    char c = s.charAt(offset);
		    if (Character.isDigit(c) == false)
			break;
		    len.append(c);
		}

	    for (; offset < s.length(); offset++)
		{
		    char c = s.charAt(offset);
		    if (Character.isWhitespace(c) == false)
			break;
		}

	    for (; offset < s.length(); offset++)
		{
		    char c = s.charAt(offset);
		    unit.append(c);
		}
	    System.out.println("len: " + len);
	    System.out.println("unit: " + unit);

	    m_length = Integer.parseInt(len.toString());

	    String sunit = unit.toString();
	    if (sunit.equals("px"))
		m_unit = PIXEL;
	    else if (sunit.equals("%"))
		m_unit = PERCENT;

	} catch (Exception exc)
	    {
		throw new RuntimeException("Error parsing " + s);
	    }
    }

    public int getLength(int totalLength)
    {
	if (m_unit == PIXEL)
	    return m_length;
	if (totalLength == 0)
	    return 0;

	return (totalLength * 100) / totalLength;
    }

}
