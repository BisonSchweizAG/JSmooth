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

import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.application.gui.*;
import java.util.*;
import com.l2fprod.common.swing.*;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author  Rodrigo
 */
public class PropertyEditorDialog extends BaseDialog
{
    private JavaPropertyPair m_prop;
    private JTextField m_key = new JTextField();
    private JTextField m_value = new JTextField();

    public PropertyEditorDialog(JavaPropertyPair prop)
    {
	super();
	setTitle(Main.local("JAVAPROP_DIALOG_TITLE"));
	setModal(true);
	m_prop = prop;
	getContentPane().setLayout(new PanelLayout());

 	JLabel eq = new JLabel(" = ");
	eq.setHorizontalAlignment(JLabel.CENTER);

	OptionalHelpPanel keypane = new OptionalHelpPanel();
	keypane.setLabel(Main.local("JAVAPROP_DIALOG_LABEL"));
	keypane.setHelpText(Main.local("JAVAPROP_DIALOG_HELP"));
	keypane.getContentPane().setLayout(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = GridBagConstraints.RELATIVE;
	c.gridy = GridBagConstraints.RELATIVE;
	c.gridwidth = 1; // GridBagConstraints.RELATIVE;

	c.weightx = 0.5;
	keypane.getContentPane().add(new JLabel(Main.local("JAVAPROP_NAME")), c);
	c.weightx = 0.1;
	keypane.getContentPane().add(new JLabel(""), c);
	c.weightx = 0.5;
	c.gridwidth = GridBagConstraints.REMAINDER;
	keypane.getContentPane().add(new JLabel(Main.local("JAVAPROP_VALUE")), c);

	c.gridwidth = 1; // GridBagConstraints.RELATIVE;
	c.weightx = 0.5;
	keypane.getContentPane().add(m_key, c);

	c.weightx = 0.1;
	keypane.getContentPane().add(eq, c);

	c.weightx = 0.5;
	keypane.getContentPane().add(m_value, c);

 	getContentPane().add(keypane);
	
	getBanner().setVisible(false);
	setResizable(false);
	pack();

	m_key.setText(m_prop.getName());
	m_value.setText(m_prop.getValue());
    }

    public Dimension getMinimumSize()
    {
	return new Dimension(400,250);
    }

    public Dimension getPreferredSize()
    {
	return new Dimension(400,250);
    }

    public void ok()
    {
	m_prop.setName(m_key.getText());
	m_prop.setValue(m_value.getText());
	super.ok();
    }

}
