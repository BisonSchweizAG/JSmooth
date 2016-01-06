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

import net.charabia.jsmoothgen.application.gui.skeleditors.*;

import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.application.gui.*;
import net.charabia.jsmoothgen.application.gui.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import com.l2fprod.common.swing.*;
import com.l2fprod.common.propertysheet.*;

import se.datadosen.component.RiverLayout;

public class SkeletonPropertiesEditor extends Editor implements JSmoothModelBean.SkeletonChangedListener
{
    private String m_currentSkelName = null;
    private SkeletonBean m_skel = null;
    private SkelPanel    m_panel = new SkelPanel();
    private Vector       m_editors = new Vector();

    public SkeletonPropertiesEditor()
    {
	setLayout(new BorderLayout());
	add(m_panel, BorderLayout.CENTER);
    }

    public void rebuildProperties()
    {
// 	System.out.println("=============================================");
// 	System.out.println("=============================================");
// 	System.out.println("===   REBUILD PROPERTIES !!!!     ===========");
// 	System.out.println("=============================================");
// 	System.out.println("=============================================");

	m_skel = null;
	if (m_currentSkelName != null)
	    m_skel = Main.SKELETONS.getSkeleton(m_currentSkelName);

	SkeletonProperty[] sprops = null;
	if (m_skel != null)
	    sprops = m_skel.getSkeletonProperties();
	else
	    sprops = new SkeletonProperty[0];

	m_panel.removeAll();
	m_panel.setLayout(new RiverLayout());
	m_editors.clear();

	for (int i=0; i<sprops.length; i++)
	    {
		SkelPropEditor spe = null;
		if (sprops[i].getType().equalsIgnoreCase(SkeletonProperty.TYPE_STRING))
		    {
			spe = new StringEditor();
		    }
		else if (sprops[i].getType().equalsIgnoreCase(SkeletonProperty.TYPE_TEXTAREA))
		    {
			spe = new TextAreaEditor();
		    }
		else if (sprops[i].getType().equalsIgnoreCase(SkeletonProperty.TYPE_BOOLEAN))
		    {
			spe = new CheckBoxEditor();
		    }
		else if (sprops[i].getType().equalsIgnoreCase(SkeletonProperty.TYPE_AUTODOWNLOADURL))
		    {
			spe = new AutoDownloadURLEditor();
		    }

		if (spe == null)
		    {
			spe = new StringEditor();
		    }

		m_editors.add(spe);
		spe.bind(sprops[i]);

		if (spe.labelAtLeft())
		    {
			m_panel.add("br", new JLabel(Main.local(sprops[i].getLabel())));
			m_panel.add("tab", new HelpButton(Main.local(sprops[i].getDescription())));
			m_panel.add("tab hfill", spe.getGUI());
		    }
		else
		    {
			m_panel.add("br right", spe.getGUI());
			m_panel.add("tab", new HelpButton(Main.local(sprops[i].getDescription())));
			m_panel.add("tab hfill", new JLabel(Main.local(sprops[i].getLabel())));
		    }
	    }

	revalidate();
	m_panel.revalidate();
	doLayout();
	m_panel.doLayout();
    }

    public void dataChanged()
    {
// 	System.out.println("========================================================");
// 	System.out.println("SkeletonPropertiesEditor: data changed, " + m_model.getSkeletonName());
	if (m_model.getSkeletonName() == null)
	    {
// 		System.out.println("SkeletonPropertiesEditor, no name");
		m_currentSkelName = null;
		rebuildProperties();
	    }

	if ((m_model != null) && (m_model.getSkeletonName() != null) && (!m_model.getSkeletonName().equalsIgnoreCase(m_currentSkelName)))
	    {
// 		System.out.println("SkeletonPropertiesEditor, different...");
		m_currentSkelName = m_model.getSkeletonName();
		rebuildProperties();
	    }

	JSmoothModelBean.Property[] jsprop = m_model.getSkeletonProperties();
// 	System.out.println("jsprop is null ? " + jsprop + " / " + ((jsprop!=null)?jsprop.length:-1));
	if (jsprop != null)
	    {
		for (Enumeration e=m_editors.elements(); e.hasMoreElements(); )
		    {
			SkelPropEditor spe = (SkelPropEditor)e.nextElement();
			JSmoothModelBean.Property p = getPropertyInstance(spe.getIdName());
			if (p != null)
			    spe.valueChanged(p.getValue());
		    }
	    }
	else
	    { // if no properties are defined for this model, we use the default values
		
		SkeletonBean skel = Main.SKELETONS.getSkeleton( m_model.getSkeletonName() );
		SkeletonProperty[] sprops = null;
		if (skel != null)
		    sprops = skel.getSkeletonProperties();

		if (sprops != null)
		    {
			for (Enumeration e=m_editors.elements(); e.hasMoreElements(); )
			    {
				SkelPropEditor spe = (SkelPropEditor)e.nextElement();				
				for (int i=0; i<sprops.length; i++)
				    {
					if (sprops[i].getIdName().equals(spe.getIdName()))
					    spe.valueChanged(sprops[i].getValue());
				    }
			    }			
		    }
	    }

// 	System.out.println("DONE NOTHING! " +m_currentSkelName + "/" + m_model.getSkeletonName());
    }

    JSmoothModelBean.Property getPropertyInstance(String name)
    {
	JSmoothModelBean.Property[] jsprop = m_model.getSkeletonProperties();
	for (int i=0; i<jsprop.length; i++)
	    {
		if (jsprop[i].getKey().equals(name))
		    return jsprop[i];
	    }
	return null;
    }
    
    public void updateModel()
    {
	if (m_skel != null)
	    {
		JSmoothModelBean.Property[] props = new JSmoothModelBean.Property[m_editors.size()];
		int index = 0;
		for (Enumeration e=m_editors.elements(); e.hasMoreElements(); )
		    {
			SkelPropEditor spe = (SkelPropEditor)e.nextElement();
// 			System.out.println("IMODEL property " + spe + "/" + spe.getIdName() + "=" + spe.get());
			props[index] = new JSmoothModelBean.Property();
			props[index].setKey(spe.getIdName());
			props[index].setValue(spe.get());
			index++;
		    }
		m_model.setSkeletonProperties(props);
	    }

// 	if (m_skel != null)
// 	    {
// 		System.out.println("UPDATE MODEL for skeletons...");
// 		SkeletonProperty[] sp = m_skel.getSkeletonProperties();
// 		JSmoothModelBean.Property[] props = new JSmoothModelBean.Property[sp.length];
// 		for (int i=0; i<sp.length; i++)
// 		    {
// 			props[i] = new JSmoothModelBean.Property();
// 			props[i].setKey(sp[i].getIdName());
// 			props[i].setValue(sp[i].getValue());
// 			System.out.println(props[i]);
// 		    }
// 		m_model.setSkeletonProperties(props);
// 	    }

    }


    public void skeletonChanged()
    {
	//	dataChanged();
	rebuildProperties();
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
