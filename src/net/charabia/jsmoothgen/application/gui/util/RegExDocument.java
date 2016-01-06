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

import java.util.*;
import javax.swing.text.*;
import java.text.*;
import java.util.regex.*;

public class RegExDocument extends PlainDocument
{
    private java.util.regex.Pattern m_pattern;

    public RegExDocument(String pattern)
    {
	m_pattern = java.util.regex.Pattern.compile(pattern);
    }

    
    public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException 
    {
	if (string == null) 
	    return;

	String result;
	int length = getLength();
	if (length == 0) 
	    {
		result = string;
	    } 
	else
	    {
		String currentContent = getText(0, length);
		StringBuffer currentBuffer = new StringBuffer(currentContent);
		currentBuffer.insert(offset, string);
		result = currentBuffer.toString();
	    }
	
	java.util.regex.Matcher m = m_pattern.matcher(result);

	if (m.matches())
	    {
		super.insertString(offset, string, attributes);	    
	    }
	
	java.awt.Toolkit.getDefaultToolkit().beep();
    }

}    
