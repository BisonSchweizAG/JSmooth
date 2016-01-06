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
import java.awt.*;
import java.util.*;
import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

public class SkeletonProperties extends Editor implements JSmoothModelBean.SkeletonChangedListener
{
    private PropertySheetPanel m_skelprops = new PropertySheetPanel();
    private String m_currentSkelName = null;
    private SkeletonBean m_skel = null;

    protected String[] m_autourls = new String[] {
	"JRE 1.5.0", "http://java.sun.com/update/1.5.0/jinstall-1_5_0-windows-i586.cab",
	"JRE 1.4.2_03", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_03-windows-i586.cab",
	"JRE 1.4.2_02", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_02-windows-i586.cab",
	"JRE 1.4.2_01", "http://java.sun.com/update/1.4.2/jinstall-1_4_2_01-windows-i586.cab",
	"JRE 1.4.2", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_2-windows-i586.cab",
	"JRE 1.4.1_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_03-windows-i586.cab",
	"JRE 1.4.1_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_02-windows-i586.cab",
	"JRE 1.4.1_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1_01-windows-i586.cab",
	"JRE 1.4.1", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_1-windows-i586.cab",
	"JRE 1.4.0_04", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_04-win.cab",
	"JRE 1.4.0_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_03-win.cab",
	"JRE 1.4.0_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_02-win.cab",
	"JRE 1.4.0_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0_01-win.cab",
	"JRE 1.4.0", "http://java.sun.com/products/plugin/autodl/jinstall-1_4_0-win.cab",
	"JRE 1.3.1_08", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_08-windows-i586.cab",
	"JRE 1.3.1_07", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_07-windows-i586.cab",
	"JRE 1.3.1_06", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_06-windows-i586.cab",
	"JRE 1.3.1_05", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_05-windows-i586.cab",
	"JRE 1.3.1_04", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_04-win.cab",
	"JRE 1.3.1_03", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_03-win.cab",
	"JRE 1.3.1_02", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_02-win.cab",
	"JRE 1.3.1_01", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_1_01-win.cab",
	"JRE 1.3.0_05", "http://java.sun.com/products/plugin/autodl/jinstall-1_3_0_05-win.cab"
    };

    public class SkelEditorFactory implements com.l2fprod.common.propertysheet.PropertyEditorFactory
    {

	public java.beans.PropertyEditor createPropertyEditor(Property property)
	{
	    if (property instanceof SkeletonPropertyProxy)
		{
		    SkeletonPropertyProxy spp = (SkeletonPropertyProxy)property;
		    String pname = spp.getTypeName();
		    
		    if (pname.equals("boolean"))
			new com.l2fprod.common.beans.editor.BooleanAsCheckBoxPropertyEditor();
		    else if (pname.equals("textarea"))
			return new com.l2fprod.common.beans.editor.StringPropertyEditor();
		    else if (pname.equals("string"))
			return new com.l2fprod.common.beans.editor.StringPropertyEditor();
		    else if (pname.equals("autodownloadurl"))
			{
			    com.l2fprod.common.beans.editor.ComboBoxPropertyEditor ed = new com.l2fprod.common.beans.editor.ComboBoxPropertyEditor();
			    Object values[] = new Object[m_autourls.length / 2];
			    for (int i=0; i<m_autourls.length; i+=2)
				{
				    values[(i+1)/2] = new com.l2fprod.common.beans.editor.ComboBoxPropertyEditor.Value(m_autourls[i+1], m_autourls[i]);
				}
			    ed.setAvailableValues(values);
			    return ed;
			}
		    return new com.l2fprod.common.beans.editor.BooleanAsCheckBoxPropertyEditor();
		}
	    else
		{
		    return new com.l2fprod.common.beans.editor.BooleanAsCheckBoxPropertyEditor();		    
		}
	}
    }


    public class SkeletonPropertyProxy extends DefaultProperty
    {
	SkeletonProperty m_property;

	public SkeletonPropertyProxy(SkeletonProperty prop)
	{
	    m_property = prop;
	}

	public String getDisplayName()
	{
	    return m_property.getLabel();
	}

	public String getName()
	{
	    System.out.println("proxy, getname " + m_property.getIdName());
	    return m_property.getIdName();
	}

	public String getShortDescription()
	{
	    return m_property.getDescription();
	}

	public Class getType()
	{
	    if (m_property.getType().equals("boolean"))
		return Boolean.class;
	    else if (m_property.getType().equals("textarea"))
		return String.class;
	    else if (m_property.getType().equals("string"))
		return String.class;
	    else if (m_property.getType().equals("autodownloadurl"))
		return String.class;

	    return String.class;
	}


	public String getTypeName()
	{
	    return m_property.getType();
	}
	
	public boolean isEditable()
	{
	    return true;
	}
	
	public Object getValue()
	{
	    if (m_property.getType().equals("boolean"))
		return new Boolean(m_property.getValue().equals("1"));
	    return m_property.getValue();
	}
	
	public void setValue(Object o)
	{
	    System.out.println("SET VALUE " + o);
	    if (o instanceof Boolean)
		{
		    if (((Boolean)o).booleanValue() == true)
			m_property.setValue("1");
		    else
			m_property.setValue("0");
		}
	    else
		{
		    m_property.setValue(o.toString());
		}
	}
    }

    public SkeletonProperties()
    {
	setLayout(new BorderLayout());

	add(BorderLayout.CENTER, m_skelprops);
	m_skelprops.setDescriptionVisible(true);
	m_skelprops.setToolBarVisible(false);
    }

    public Dimension getPreferredSize()
    {
	return new Dimension(100,200);
    }

    public void setProperties()
    {
	m_skel = null;
	if (m_currentSkelName != null)
	    m_skel = Main.SKELETONS.getSkeleton(m_currentSkelName);

	SkeletonProperty[] sprops = null;
	if (m_skel != null)
	    sprops = m_skel.getSkeletonProperties();
	else
	    sprops = new SkeletonProperty[0];

	m_skelprops.setEditorFactory(new SkelEditorFactory());
	SkeletonPropertyProxy[] proxy = new SkeletonPropertyProxy[sprops.length];
	for (int i=0; i<sprops.length; i++)
	    {
		proxy[i] = new SkeletonPropertyProxy( sprops[i] );
		System.out.println("Added property " + sprops[i].getLabel());
	    }
	
	JSmoothModelBean.Property[] jsprop = m_model.getSkeletonProperties();
	if (jsprop != null)
	    {
		for (int i=0; i<jsprop.length; i++)
		    {
			// search for the proxy and set the value
			for (int j=0; j<proxy.length; j++)
			    {
				if (proxy[j].getName().equals(jsprop[i].getKey()))
				    {
					proxy[j].setValue(jsprop[i].getValue());
					break;
				    }
			    }
		    }
	    }

	m_skelprops.setProperties(proxy);
    }

    public void dataChanged()
    {
	System.out.println("SkeletonProperties: data changed, " + m_model.getSkeletonName());
	if (m_model.getSkeletonName() == null)
	    {
		m_currentSkelName = null;
		setProperties();
		return;
	    }
	if ( ! m_model.getSkeletonName().equalsIgnoreCase(m_currentSkelName))
	    {
		m_currentSkelName = m_model.getSkeletonName();
		setProperties();
	    }
    }
    
    public void updateModel()
    {
	if (m_skel != null)
	    {
		SkeletonProperty[] sp = m_skel.getSkeletonProperties();
		JSmoothModelBean.Property[] props = new JSmoothModelBean.Property[sp.length];
		for (int i=0; i<sp.length; i++)
		    {
			props[i] = new JSmoothModelBean.Property();
			props[i].setKey(sp[i].getIdName());
			props[i].setValue(sp[i].getValue());
			System.out.println(props[i]);
		    }
		m_model.setSkeletonProperties(props);
	    }
    }


    public void skeletonChanged()
    {
	dataChanged();
    }
    
    public String getLabel()
    {
	return "SKELETONPROPERTIES_LABEL";
    }

    public String getDescription()
    {
	return "SKELETONPROPERTIES_HELP";
    }

    public boolean needsBigSpace()
    {
	return true;
    }
    
}
