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
import net.charabia.jsmoothgen.skeleton.*;
import java.util.*;
import net.charabia.jsmoothgen.application.gui.util.*;
import net.charabia.jsmoothgen.application.*;
import java.util.prefs.*;

public class Main extends JFrame
{
    final static public SkeletonList SKELETONS = new SkeletonList(new java.io.File("skeletons"));
    final static public String VERSION = "@{VERSION}@";
    final static public String RELEASEINFO = "@{RELEASEINFO}@";

    final static public ResourceBundle TEXTS = PropertyResourceBundle.getBundle("locale.Texts");

    static public Main MAIN;

    private MasterPanel m_panel;

    private javax.swing.JFileChooser m_projectFileChooser = new JFileChooser();
    private RecentFileMenu m_recentFiles = null;

    private Main()
    {
	Splash splash = new Splash(this, "/icons/splash.png", false);
	splash.setVersion(VERSION);
	splash.show();

	m_projectFileChooser.addChoosableFileFilter(new SimpleFileFilter("jsmooth", "JSmooth Project Files"));

	getContentPane().setLayout(new BorderLayout());
	m_panel = new MasterPanel();
	getContentPane().add(BorderLayout.CENTER, m_panel);

	setupMenus();
	setupToolBar();

        addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent evt)
		{
		    EXIT.actionPerformed(null);
		}
	    });

	setTitle("Untitled");
	loadWindowSettings();
	splash.dispose();
    }

    private void setupMenus()
    {
	JMenuBar bar = new JMenuBar();
	setJMenuBar(bar);

	JMenu menu = new JMenu(local("MENU_SYSTEM"));
	menu.add(new JMenuItem(NEW));
	menu.addSeparator();
	menu.add(new JMenuItem(OPEN));
	menu.add(new JMenuItem(SAVE));
	menu.add(new JMenuItem(SAVE_AS));
	menu.addSeparator();
	JMenu recentfiles = new JMenu(Main.local("MENU_RECENT"));
	m_recentFiles = new RecentFileMenu(recentfiles, 5, Main.class, 
					   new RecentFileMenu.Action() {
					       public void action(String path)
					       {
						   if (m_panel.openFile(new java.io.File(path)))
						       setTitle(path);
					       }
					   });
	
	menu.add(recentfiles);
	menu.addSeparator();
	menu.add(new JMenuItem(EXIT));
	bar.add(menu);

	menu = new JMenu(local("MENU_PROJECT"));
	menu.add(new JMenuItem(COMPILE));
	menu.add(new JMenuItem(RUNEXE));
	bar.add(menu);

	menu = new JMenu(local("MENU_HELP"));
	menu.add(new JMenuItem(ABOUT));
	bar.add(menu);
    }

    private void setupToolBar()
    {
	JToolBar bar = new JToolBar();
	bar.add(NEW);
	bar.addSeparator();
	bar.add(OPEN);
	bar.add(SAVE);
	bar.add(SAVE_AS);
	bar.addSeparator();
	bar.add(COMPILE);
	bar.add(RUNEXE);
	bar.addSeparator();
	bar.add(ABOUT);
	getContentPane().add(BorderLayout.NORTH, bar);
    }

    public static String local(String key)
    {
	try {
	    String value = Main.TEXTS.getString(key);
	    return value;
	} catch (Exception exc)
	    {
	    }
	return "["+key+"]";
    }


    private final Icon ICON_NEW = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_new.png"));
    public final Action NEW = new AbstractAction(local("NEW"), ICON_NEW) {
	    public void actionPerformed(ActionEvent e)
	    {
		m_panel.newModel();
		setTitle("Untitled");
	    }
	};

    
    private final Icon ICON_OPEN = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_open.png"));
    public final Action OPEN = new AbstractAction(local("OPEN"), ICON_OPEN) {
	    public void actionPerformed(ActionEvent e)
	    {
	      	if (m_projectFileChooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION)
		    {
			java.io.File f = m_projectFileChooser.getSelectedFile();
			if (m_panel.openFile(f))
			    {
				// TODO
				m_recentFiles.add(f.getAbsolutePath());
				setTitle(f.getAbsolutePath());
			    }

// 			if (openDirect(m_projectFileChooser.getSelectedFile()))
// 			    m_recentMenuManager.add(m_projectFileChooser.getSelectedFile().getAbsolutePath());
		    }
	    }
	};

    private final Icon ICON_SAVE = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_save.png"));
    public final Action SAVE = new AbstractAction(local("SAVE"), ICON_SAVE) {
	    public void actionPerformed(ActionEvent e)
	    {
		if (m_panel.getProjectFile() == null)
		    SAVE_AS.actionPerformed(e);
		else
		    {
			m_panel.save();
		    }
	    }
	};

    private final Icon ICON_SAVE_AS = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_save_as.png"));
    public final Action SAVE_AS = new AbstractAction(local("SAVE_AS"), ICON_SAVE_AS) {
	    public void actionPerformed(ActionEvent e)
	    {
		if (m_projectFileChooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION)
		    {
			if ((m_panel.getModel() != null) && (m_panel.getProjectFile() != null))
			    m_panel.getModel().normalizePaths(m_panel.getProjectFile().getParentFile(), false);

			java.io.File nf = m_projectFileChooser.getSelectedFile();
			String suf = getSuffix(nf);
			if ("jsmooth".equalsIgnoreCase(suf) == false)
			    {
				nf = new java.io.File(nf.toString() + ".jsmooth");
			    }
			if (m_panel.getModel() != null)
			    m_panel.getModel().normalizePaths(nf.getParentFile(), true);
			
			m_panel.setProjectFile(nf);
			setTitle(nf.getAbsolutePath());
			m_panel.save();
			m_recentFiles.add(nf.getAbsolutePath());
		    }

	    }
	};



    private final Icon ICON_EXIT = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_exit-16.png"));
    public final Action EXIT = new AbstractAction(local("EXIT"), ICON_EXIT) {
	    public void actionPerformed(ActionEvent e)
	    {
		m_recentFiles.savePrefs();
		saveWindowSettings();
		if (m_panel.getProjectFile() != null)
		    {
			m_panel.save();
		    }
		System.exit(0);
	    }
	};


    private final Icon ICON_COMPILE = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_autopilot-24.png"));
    public final Action COMPILE = new AbstractAction(local("COMPILE"), ICON_COMPILE) {
	    public void actionPerformed(ActionEvent e)
	    {
		m_panel.fireUpdateModel();

		SAVE.actionPerformed(e);
		if (m_panel.getProjectFile() == null)
		    return;

		final ExeCompiler.CompilerRunner compiler = m_panel.getCompiler();

		CompilationDialog dia = new CompilationDialog(Main.this, true);
		dia.setTitle(Main.local("COMPILATION_DIALOG_TITLE"));
		dia.pack();

		if (compiler != null)
		    {
			dia.setCompiler(compiler.getCompiler());
			dia.compile(compiler);
			//			return dia.getResult();
		    }
		else
		    {
			dia.setNewState(100, "Error, compiler couldn't be created. Error description should follow:");
			Vector v = m_panel.getLastErrors();
			for (Iterator i=v.iterator(); i.hasNext(); )
			    {
				dia.setNewState(100, "- " + (i.next().toString()));
			    }
			dia.setVisible(true);
		    }
	    }
	};

    private final Icon ICON_RUNEXE = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_next.png"));
    public final Action RUNEXE = new AbstractAction(local("RUNEXE"), ICON_RUNEXE) {
	    public void actionPerformed(ActionEvent e)
	    {
		m_panel.runexe();
	    }
	};

    private final Icon ICON_ABOUT = new javax.swing.ImageIcon(getClass().getResource("/icons/stock_about.png"));
    public final Action ABOUT = new AbstractAction(local("ABOUT"), ICON_ABOUT) {
	    public void actionPerformed(ActionEvent e)
	    {
		AboutBox ab = new AboutBox(Main.this, true);
		ab.setVersion(Main.VERSION + " (" + Main.RELEASEINFO + ")");
		ab.setVisible(true);
	    }
	};


    private String getSuffix(java.io.File f)
    {
	String fstr = f.getAbsolutePath();
	int lastDot = fstr.lastIndexOf('.');
	if ((lastDot >= 0) && ((lastDot+1) < fstr.length()))
	    {
		return fstr.substring(lastDot+1);
	    }
	return "";
    }

    public void setTitle(String title)
    {
	super.setTitle("JSmooth " + Main.VERSION + ": " + title);
    }
    
    public void saveWindowSettings()
    {
	Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
	prefs.putInt("window-state", this.getExtendedState());
	//	setExtendedState(NORMAL);

	prefs.putInt("window-x", (int)this.getLocation().getX());
	prefs.putInt("window-y", (int)this.getLocation().getY());

	prefs.putInt("window-width", (int)this.getWidth());
	prefs.putInt("window-height", (int)this.getHeight());
	
    }

    public void loadWindowSettings()
    {
	Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
	this.setExtendedState(prefs.getInt("window-state", Frame.NORMAL));

	if (prefs.getInt("window-x", -1) > 0)
	    {
		this.setLocation(prefs.getInt("window-x", 10), prefs.getInt("window-y", 10));
		int w = prefs.getInt("window-width", 500);
		int h = prefs.getInt("window-height", 400);
		if (w <= 0)
		    w = 400;
		if (h <= 0)
		    h = 400;
		this.setSize(w,h);
		setExtendedState(prefs.getInt("window-state", NORMAL));
	    }
	else
	    {
		this.setSize(500, 400);
		setExtendedState(prefs.getInt("window-state", NORMAL));
	    }
    }

    public static void main(String args[])
    {
	System.out.println("Running JSmooth...");
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) { e.printStackTrace(); }

	Main.MAIN = new Main();

	if (args.length>0)
	    {
		java.io.File f = new java.io.File(args[0]);
		if (f.exists())
		    {
			Main.MAIN.m_panel.openFile(f);
			Main.MAIN.setTitle(f.toString());
		    }
		else
		    {
			JOptionPane.showMessageDialog(null, Main.MAIN.local("GENERAL_CANTOPENFILE"), Main.MAIN.local("GENERAL_ERROR_LABEL"), JOptionPane.ERROR_MESSAGE); 
		    }
	    }
	    
	Main.MAIN.setVisible(true);
    }
}
