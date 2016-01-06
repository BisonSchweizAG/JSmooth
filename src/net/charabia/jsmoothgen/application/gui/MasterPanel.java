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

import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.application.gui.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import com.l2fprod.common.swing.*;

import se.datadosen.component.RiverLayout;

public class MasterPanel extends JPanel
{
    private JButtonBar m_leftBar = new JButtonBar(JButtonBar.VERTICAL);
    private ButtonGroup m_leftGroup = new ButtonGroup();
    private JPanel m_mainpanel = new JPanel();
    private JScrollBar m_mainpanelVBar;
    private JScrollPane m_scrollpane;

    //    private ResourceBundle m_texts;
    
    private Vector m_displayedElements = new Vector();

    private JSmoothModelBean m_model = new JSmoothModelBean();
    private java.io.File m_modelLocation = null;

    private EditorPool m_edPool = new EditorPool();

    private String m_currentPanelName = "";

    private Object[] m_skelElements = { 
	new net.charabia.jsmoothgen.application.gui.editors.SkeletonChooser(), 
	new net.charabia.jsmoothgen.application.gui.editors.SkeletonPropertiesEditor() 
	//	new net.charabia.jsmoothgen.application.gui.editors.SkeletonProperties()
    };

    private Object[] m_execElements = { 
	"GUI_LABEL_EXECUTABLE_SETTINGS",
	new net.charabia.jsmoothgen.application.gui.editors.ExecutableName() ,
	new net.charabia.jsmoothgen.application.gui.editors.ExecutableIcon() ,
	new net.charabia.jsmoothgen.application.gui.editors.CurrentDirectory()
    };

    private Object[] m_appElements = {
	"GUI_LABEL_APPLICATION_SETTINGS",
	new net.charabia.jsmoothgen.application.gui.editors.MainClass(),
	new net.charabia.jsmoothgen.application.gui.editors.ApplicationArguments(),
	"GUI_LABEL_EMBEDDEDJAR_SETTINGS",
	new net.charabia.jsmoothgen.application.gui.editors.EmbeddedJar(),
	new net.charabia.jsmoothgen.application.gui.editors.ClassPath()
    };

    private Object[] m_jvmSelElements = {
	"GUI_LABEL_JAVA_VERSION",
	new net.charabia.jsmoothgen.application.gui.editors.MinVersion(),
	new net.charabia.jsmoothgen.application.gui.editors.MaxVersion(),
	"GUI_LABEL_BUNDLEDJRE",
	new net.charabia.jsmoothgen.application.gui.editors.JVMBundle(),
	new net.charabia.jsmoothgen.application.gui.editors.JVMSearchSequence()
    };

    private Object[] m_jvmCfgElements = {
	"GUI_LABEL_MEMORYSETTINGS",
	new net.charabia.jsmoothgen.application.gui.editors.MaxMemoryHeap(),
	new net.charabia.jsmoothgen.application.gui.editors.InitialMemoryHeap(),
	new net.charabia.jsmoothgen.application.gui.editors.JavaProperties()
    };

    private Object[] m_jsInfo = {
	new net.charabia.jsmoothgen.application.gui.editors.JSmoothInfo()
    };

    public MasterPanel()
    {
	setLayout(new BorderLayout());
	add(BorderLayout.WEST, m_scrollpane = new JScrollPane(m_leftBar));
 	JScrollPane scp = new JScrollPane(m_mainpanel);
 	m_mainpanelVBar = scp.getVerticalScrollBar();
	add(BorderLayout.CENTER, scp);
	scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	//	add(BorderLayout.CENTER, m_mainpanel);
	m_mainpanel.setLayout(new RiverLayout());

	addAction("Welcome", "/icons/stock_form-properties.png", m_jsInfo);
	addAction("Skeleton", "/icons/stock_new-template.png", m_skelElements);
	addAction("Executable", "/icons/stock_autopilot-24.png", m_execElements);
	addAction("Application", "/icons/stock_form-image-control.png", m_appElements);
	addAction("JVM Selection", "/icons/stock_search.png", m_jvmSelElements);
	addAction("JVM Configuration", "/icons/stock_form-properties.png", m_jvmCfgElements);
	
	setupPanel(m_jsInfo);
    }

    private String getLocaleText(String key)
    {
	try {
	    String value = Main.TEXTS.getString(key);
	    return value;
	} catch (Exception exc)
	    {
	    }
	return key;
    }

    private void addAction(final String name, String iconloc, final Object[] els)
    {
	final Action a = new AbstractAction( name, new ImageIcon(getClass().getResource(iconloc))) {
		public void actionPerformed(ActionEvent e) 
		{
		    if (m_currentPanelName.equals(name))
			return;
		    setupPanel(els);
		    m_currentPanelName = name;
		}
	    };
	
	JToggleButton jtb = new JToggleButton(a);
	m_leftGroup.add(jtb);
	m_leftBar.add(jtb);
    }

    public void setupPanel(Object[] els)
    {
	fireUpdateModel();
	detachAll();
	
	m_mainpanel.removeAll();
	m_displayedElements.removeAllElements();

	//	System.out.println("Adding " + els);

	if (els == null)
	    return;

	JPanel pgroup = null;

	for (int i=0; i<els.length; i++)
	    {
		if (els[i] instanceof Editor)
		    {
			//			Editor ed = m_edPool.getInstance(els[i]);
			Editor ed = (Editor)els[i];
			if (ed.needsBigSpace() && (pgroup != null))
			    {
				m_mainpanel.add("br hfill", pgroup);
				pgroup = null;
			    }

			if (pgroup == null)
			    {
				pgroup = new JPanel();
				pgroup.setLayout(new RiverLayout());
				javax.swing.border.TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "");
			    }

			if (ed.needsFullSpace())
			    {
				pgroup.add("left hfill vfill", ed);
				String gc = "br hfill vfill";
				m_mainpanel.add(gc, ed);
				ed.setMaximumSize(new java.awt.Dimension(300,300));
				m_mainpanel.add("tab", new JLabel("..."));
				pgroup = null;
				// m_scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
				//m_scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			    }
			else if (ed.needsBigSpace())
			    {
				javax.swing.border.TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), getLocaleText(ed.getLabel()));
				pgroup.setBorder(title);
				pgroup.add("left vtop", new HelpButton(getLocaleText(ed.getDescription())));
				pgroup.add("tab left hfill", ed);
				String gc = "br hfill";
 				if (i+1>=els.length)
 				    gc = "p hfill vfill";
				m_mainpanel.add(gc, pgroup);
				pgroup = null;
			    }
			else
			    {
				if (ed.useDescription())
				    {
					pgroup.add("br left", new JLabel(getLocaleText(ed.getLabel())));
					pgroup.add("tab", new HelpButton(getLocaleText(ed.getDescription())));
					pgroup.add("tab hfill", ed);
				    }
				else
				    {
					pgroup.add("left hfill", ed);
				    }
			    }

			m_displayedElements.add(ed);
		    }
		else if (els[i] instanceof String)
		    {
			//			System.out.println("TITLE: " + els[i]);
			if (pgroup != null)
			    {
				m_mainpanel.add("br hfill", pgroup);
				pgroup = null;
			    }

			pgroup = new JPanel();
			pgroup.setLayout(new RiverLayout());

			javax.swing.border.TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), getLocaleText((String)els[i]));
			pgroup.setBorder(title);
		    }

		// 		try {

		// 		} catch (Exception exc)
		// 		    {
		// 			exc.printStackTrace();
		// 		    }
	    }
	if (pgroup != null)
	    {
		m_mainpanel.add("p hfill", pgroup);
	    }
	m_mainpanelVBar.setValue(0);
	attachAll();

	validate();
	repaint();
    }

    public void fireUpdateModel()
    {
	for (Iterator i=m_displayedElements.iterator(); i.hasNext(); )
	    {
		Editor ed = (Editor)i.next();
		ed.updateModel();
	    }	
    }

    public void fireModelChanged()
    {
	for (Iterator i=m_displayedElements.iterator(); i.hasNext(); )
	    {
		Editor ed = (Editor)i.next();
		ed.dataChanged();
	    }	
    }

    private void detachAll()
    {
	for (Iterator i=m_displayedElements.iterator(); i.hasNext(); )
	    {
		Editor ed = (Editor)i.next();

		if (ed instanceof JSmoothModelBean.Listener)
		    m_model.removeListener((JSmoothModelBean.Listener)ed);
		if (ed instanceof JSmoothModelBean.SkeletonChangedListener)
		    m_model.removeSkeletonChangedListener((JSmoothModelBean.SkeletonChangedListener)ed);

		ed.detach();
	    }
    }

    private void attachAll()
    {
	//	System.out.println("Attaching all with " + m_modelLocation + ": " + m_model);
	for (Iterator i=m_displayedElements.iterator(); i.hasNext(); )
	    {
		Editor ed = (Editor)i.next();

		File basedir = null;
		if (m_modelLocation != null)
		    basedir = m_modelLocation.getParentFile();

		ed.attach(m_model, basedir);
		if (ed instanceof JSmoothModelBean.Listener)
		    m_model.addListener((JSmoothModelBean.Listener)ed);
		if (ed instanceof JSmoothModelBean.SkeletonChangedListener)
		    m_model.addSkeletonChangedListener((JSmoothModelBean.SkeletonChangedListener)ed);

		ed.dataChanged();
	    }	
    }

    public void newModel()
    {
	JSmoothModelBean bean = new JSmoothModelBean();
	newModel(bean, null);
    }

    public void newModel(JSmoothModelBean bean, java.io.File location)
    {
	detachAll();

	m_model = bean;
	m_modelLocation = location;

	attachAll();
    }

    public boolean openFile(java.io.File f)
    {
	m_modelLocation = f;

	try
	    {
		JSmoothModelBean model = JSmoothModelPersistency.load(m_modelLocation);
		newModel(model, f);
		return true;
	    } catch (java.io.IOException iox)
		{
		    iox.printStackTrace();
		    return false;
		}
    }

    public boolean save()
    {
	if (m_modelLocation == null)
	    return false;
	try {
	    fireUpdateModel();
	    m_model.normalizePaths(m_modelLocation.getParentFile(), true);
	    JSmoothModelPersistency.save(m_modelLocation, m_model);
	    //	    System.out.println("saving model " + m_model);
	    //	    fireModelChanged();
	    return true;
	} catch (java.io.IOException iox)
	    {
		iox.printStackTrace();
	    }
	return false;
    }

    private Vector m_lastErrors = new Vector();

    public Vector getLastErrors()
    {
	return m_lastErrors;
    }

    public ExeCompiler.CompilerRunner getCompiler()
    {
	fireUpdateModel();
	m_model.normalizePaths(m_modelLocation.getParentFile());
	m_lastErrors.removeAllElements();

	SkeletonBean skel = Main.SKELETONS.getSkeleton(m_model.getSkeletonName());
	if (skel == null)
	    {
		m_lastErrors.add(Main.local("UNKNOWN_SKEL"));
		return null;
	    }

	File skelroot = Main.SKELETONS.getDirectory(skel);
	File basedir = m_modelLocation.getParentFile();
	File exedir = basedir;

	try {
	    File out = null;
	    if (new File(m_model.getExecutableName()).isAbsolute() == false)
		out = new File(exedir, m_model.getExecutableName());
	    else
		out = new File(m_model.getExecutableName());

	    //	    System.out.println("out = "+ out.getAbsolutePath());
	    ExeCompiler compiler = new ExeCompiler();
	    ExeCompiler.CompilerRunner runner = compiler.getRunnable(skelroot, skel, basedir, m_model, out);
	    return runner;
	} catch (Exception exc)
	    {
		exc.printStackTrace();
		m_lastErrors.add(exc.getMessage());
		return null;
	    }
    }
    
    public void runexe()
    {
	fireUpdateModel();
		
	try {
	    File basedir = m_modelLocation.getParentFile();
	    File f = new File(basedir, m_model.getExecutableName());
	    String[] cmd = new String[]{ f.getAbsolutePath() };
	    
	    //	    System.out.println("RUNNING " + cmd[0] + " @ " + basedir);
	    CommandRunner.run(cmd, f.getParentFile());
	} catch (Exception exc)
	    {
		exc.printStackTrace();
	    }
    }

    public java.io.File getProjectFile()
    {
	return m_modelLocation;
    }

    public void setProjectFile(java.io.File prjfile)
    {
	m_modelLocation = prjfile;
    }
    

    public JSmoothModelBean getModel()
    {
	return m_model;
    }

    public static void main(String args[])
    {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) { }
	JFrame test = new JFrame("test");
	test.getContentPane().add(new MasterPanel());
	test.pack();
	test.setVisible(true);
    }
}
