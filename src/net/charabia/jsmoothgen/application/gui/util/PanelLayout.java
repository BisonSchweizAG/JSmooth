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

public class PanelLayout implements LayoutManager
{
    private Dimension m_minimumSize;
    private Hashtable m_componentToLayoutLengthDescriptor = new Hashtable();

    public PanelLayout() 
    {
    }

    public void addLayoutComponent(String name, Component comp) 
    {
	LayoutLengthDescriptor ld = new LayoutLengthDescriptor(name);
	m_componentToLayoutLengthDescriptor.put(comp, ld);
    }

    public void removeLayoutComponent(Component comp) 
    {
	m_componentToLayoutLengthDescriptor.remove(comp);
    }

    private void calculateMinimumSize(Container parent)
    {
	m_minimumSize = new Dimension();
        for (int i = 0; i < parent.getComponentCount(); i++)
	    {
		Component element = parent.getComponent(i);
		if (element.isVisible())
		    {
			Dimension eld = element.getPreferredSize();
			LayoutLengthDescriptor ld = (LayoutLengthDescriptor)m_componentToLayoutLengthDescriptor.get(element);
			if (ld != null)
			    eld.height = ld.getLength(parent.getHeight());

			m_minimumSize.height += eld.height;
			m_minimumSize.width = Math.max(m_minimumSize.width, eld.width);
		    }
	    }
    }

    public Dimension preferredLayoutSize(Container parent) 
    {
	return minimumLayoutSize(parent);
    }
    
    /* Required by LayoutManager. */
    public Dimension minimumLayoutSize(Container parent) 
    {
	//	if (m_minimumSize == null)
	calculateMinimumSize(parent);

        Dimension dim = new Dimension(m_minimumSize);
	
        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
	
        return dim;
    }


    public void layoutContainer(Container parent) 
    {
        Insets insets = parent.getInsets();
	int xoffset = insets.left;
	int yoffset = insets.top;
	int maxcwidth = parent.getWidth()-(insets.right+insets.left);
	int maxcheight = parent.getHeight()-insets.bottom;

        for (int i = 0; i < parent.getComponentCount(); i++)
	    {
		Component element = parent.getComponent(i);
		if (element.isVisible())
		    {
			Dimension eld = element.getPreferredSize();
			LayoutLengthDescriptor ld = (LayoutLengthDescriptor)m_componentToLayoutLengthDescriptor.get(element);
			if (ld != null)
			    eld.height = ld.getLength(parent.getHeight());

			if ((eld.height + yoffset) > maxcheight)
			    eld.height = maxcheight - yoffset;

			element.setBounds(xoffset, yoffset, maxcwidth, eld.height);
			yoffset += eld.height;
		    }
	    }	
    }


    //     public void layoutContainer(Container parent) {
    //         Insets insets = parent.getInsets();
    //         int maxWidth = parent.getWidth() - (insets.left + insets.right);
    //         int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
    //         int nComps = parent.getComponentCount();
    //         int previousWidth = 0, previousHeight = 0;
    //         int x = 0, y = insets.top;
    //         int rowh = 0, start = 0;
    //         int xFudge = 0, yFudge = 0;
    //         boolean oneColumn = false;

    //         // Go through the components' sizes, if neither
    //         // preferredLayoutSize nor minimumLayoutSize has
    //         // been called.
    //         if (sizeUnknown) {
    //             setSizes(parent);
    //         }

    //         if (maxWidth <= minWidth) {
    //             oneColumn = true;
    //         }

    //         if (maxWidth != preferredWidth) {
    //             xFudge = (maxWidth - preferredWidth)/(nComps - 1);
    //         }

    //         if (maxHeight > preferredHeight) {
    //             yFudge = (maxHeight - preferredHeight)/(nComps - 1);
    //         }

    //         for (int i = 0 ; i < nComps ; i++) {
    //             Component c = parent.getComponent(i);
    //             if (c.isVisible()) {
    //                 Dimension d = c.getPreferredSize();

    // 		// increase x and y, if appropriate
    //                 if (i > 0) {
    //                     if (!oneColumn) {
    //                         x += previousWidth/2 + xFudge;
    //                     }
    //                     y += previousHeight + vgap + yFudge;
    //                 }

    //                 // If x is too large,
    //                 if ((!oneColumn) &&
    //                     (x + d.width) >
    //                     (parent.getWidth() - insets.right)) {
    //                     // reduce x to a reasonable number.
    //                     x = parent.getWidth()
    //                         - insets.bottom - d.width;
    //                 }

    //                 // If y is too large,
    //                 if ((y + d.height)
    //                     > (parent.getHeight() - insets.bottom)) {
    //                     // do nothing.
    //                     // Another choice would be to do what we do to x.
    //                 }

    //                 // Set the component's size and position.
    //                 c.setBounds(x, y, d.width, d.height);

    //                 previousWidth = d.width;
    //                 previousHeight = d.height;
    //             }
    //         }
    //     }
    
}
