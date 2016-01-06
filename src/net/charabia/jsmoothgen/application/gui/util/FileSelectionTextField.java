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
import java.io.*;


/**
 * Represents a JTextField associated with a button that pops up a
 * file dialog selector.
 *
 */

public class FileSelectionTextField extends javax.swing.JPanel
{
    private JButton           m_buttonFileSelection;
    private JFileChooser      m_fileChooser;
    private JTextField        m_filename;
	
    private File              m_basedir =  null;
    private java.util.Vector  m_listeners = new java.util.Vector();
	
    public interface FileSelected
    {
	public void fileSelected(String filename);
    }

    public FileSelectionTextField()
    {
        m_fileChooser = new javax.swing.JFileChooser();
        m_filename = new javax.swing.JTextField();
        m_buttonFileSelection = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        add(m_filename, java.awt.BorderLayout.CENTER);

        m_buttonFileSelection.setText("...");
        m_buttonFileSelection.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
		    buttonFileSelectionActionPerformed(evt);
		}
	    });

	m_filename.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
		    if (m_filename.getText().length() > 0)
			setFile(new java.io.File(m_filename.getText()));
		    else
			setFile(null);
		    notifyListeners(m_filename.getText());
		}
	    });
	m_filename.addFocusListener(new java.awt.event.FocusAdapter() {
		public void focusLost(java.awt.event.FocusEvent e) 
		{
		    if (m_filename.getText().length() > 0)
			setFile(new java.io.File(m_filename.getText()));
		    else
			setFile(null);
		    //		    setFile(new java.io.File(m_filename.getText()));
		    notifyListeners(m_filename.getText());
		}
	    });

        add(m_buttonFileSelection, java.awt.BorderLayout.EAST);
    }

    public void addListener(FileSelected fs)
    {
	m_listeners.add(fs);
    }

    public void removeListener(FileSelected fs)
    {
	m_listeners.remove(fs);
    }
	
    public void notifyListeners(String filename)
    {
	for (int i=0; i<m_listeners.size(); i++)
	    {
		FileSelected fs = (FileSelected)m_listeners.get(i);
		fs.fileSelected(filename);
	    }
    }

    private void buttonFileSelectionActionPerformed(java.awt.event.ActionEvent evt)
    {
	String fname = m_filename.getText().trim();
	java.io.File cur = new java.io.File(fname);
	if ((cur.isAbsolute() == false) && (m_basedir != null))
	    {
		cur = new File(m_basedir, cur.toString()).getAbsoluteFile();
		try {
		    cur = cur.getCanonicalFile();
		} catch (IOException iox)
		    {
			iox.printStackTrace();
		    }
	    }
	m_fileChooser.setSelectedFile(cur);
	if (m_fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION)
	    {
		java.io.File f = m_fileChooser.getSelectedFile();
		if (m_basedir != null)
		    {
			File rel = net.charabia.jsmoothgen.application.JSmoothModelPersistency.makePathRelativeIfPossible(m_basedir, f);
			m_filename.setText(rel.toString());
			notifyListeners(rel.toString());
		    }
		else
		    {
			m_filename.setText(f.getAbsolutePath());
			notifyListeners(f.getAbsolutePath());
		    }
	    }
    }
	
    public void setFile(java.io.File f)
    {
	//	System.out.println("Setting File: " + f + " / " + m_basedir);
	if (f == null)
	    {
		m_filename.setText("");
	    }
	else if (m_basedir != null)
	    {
		try {
		    File rel = net.charabia.jsmoothgen.application.JSmoothModelPersistency.makePathRelativeIfPossible(m_basedir, f);
		    m_filename.setText(rel.toString());
		} catch (Throwable thr)
		    {
			m_filename.setText(f.toString());
		    }
	    }
	else
	    {
		m_filename.setText(f.toString());
	    }
	//	System.out.println("Filename: " + m_filename.getText());
    }
    
    public java.io.File getFile()
    {
	if (m_filename.getText().trim().length() == 0)
	    return null;
	return new java.io.File(m_filename.getText());
    }
	
    public void setFileChooser(JFileChooser jfc)
    {
	m_fileChooser = jfc;
    }

    public void setFileSelectionMode(int mode)
    {
	m_fileChooser.setFileSelectionMode(mode);
    }
	
    public void setBaseDir(File root)
    {
	m_fileChooser.setCurrentDirectory(root);
	m_basedir = root;
    }
	
    public File getBaseDir()
    {
	return m_basedir;
    }
	
    public void setEnabled(boolean b)
    {
	m_buttonFileSelection.setEnabled(b);
	m_filename.setEnabled(b);
    }
	
	
}
