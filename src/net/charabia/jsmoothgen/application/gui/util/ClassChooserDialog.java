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

import java.util.jar.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class ClassChooserDialog extends javax.swing.JDialog
{
    JarEntryTreeNode m_root;
    
    private boolean m_valid = false;
    
    public class ClassTreeListener implements TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent e)
        {
            TreePath tp = e.getPath();
            JarEntryTreeNode last = (JarEntryTreeNode)tp.getLastPathComponent();
            if (last.getChildCount()>0)
            {
                m_buttonSelect.setEnabled(false);
            }
            else
            {
                m_buttonSelect.setEnabled(true);
            }
        }
    }
    
    public class JarEntryTreeNode extends javax.swing.tree.DefaultMutableTreeNode
    {
        public JarEntryTreeNode(String value)
        {
            super(value);
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    java.net.URL imgurl = getClass().getResource("/icons/stock_form-autopilots-16.png");
	    if (imgurl != null)
		{
		    javax.swing.ImageIcon leaf = new javax.swing.ImageIcon(imgurl);
		    renderer.setLeafIcon(leaf);
		}
            m_tree.setCellRenderer(renderer);
            m_tree.addTreeSelectionListener(new ClassTreeListener());
            m_tree.setEditable(false);
        }

        /**
         * Attach a java path to this node.
         */
        public void add(String[] items)
        {
            JarEntryTreeNode current = this;
            for (int i=0; i<items.length; i++)
            {
                JarEntryTreeNode next = null;
                
                for (Enumeration e=current.children(); (e.hasMoreElements()) && (next == null); )
                {
                    JarEntryTreeNode jtn = (JarEntryTreeNode)e.nextElement();
                    if (jtn.getUserObject().equals(items[i]))
                    {
                        next = jtn;
                    }
                }
                
                if (next == null)
                {
                    next = new JarEntryTreeNode(items[i]);
                    current.add(next);
                    current = next;
                }
                else
                {
                    current = next;
                }
            }
        }
    }
    
    
    /** Creates new form ClassChooserDialog */
    public ClassChooserDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }

    public void clear()
    {
        JarEntryTreeNode root = new JarEntryTreeNode("Available Classes");
        m_root = root;	
        m_tree.setModel(new DefaultTreeModel(m_root));
    }

    public void addJar(JarFile jf)
    {
        for (Enumeration e=jf.entries(); e.hasMoreElements(); )
        {
            JarEntry entry = (JarEntry)e.nextElement();
            String[] res = entry.toString().split("/");
	    //            System.out.println("JarEntry: " + entry);
            if ((res.length > 0) && (res[res.length-1].toLowerCase().endsWith(".class")))
            {
                String rs = res[res.length-1];
                rs = rs.substring(0, rs.length()-6);
                res[res.length-1] = rs;
                m_root.add(res);
            }
        }
    }
    
    public void setJar(JarFile jf)
    {
        JarEntryTreeNode root = new JarEntryTreeNode(jf.getName());
        m_root = root;

        m_tree.setModel(new DefaultTreeModel(root));
    }

    public String getClassName()
    {
        TreePath path = m_tree.getSelectionPath();
        Object[] objs = path.getPath();
        StringBuffer cname = new StringBuffer();
        for (int i=1; i<objs.length; i++)
        {
            if (i>1)
                cname.append(".");
            cname.append(((JarEntryTreeNode)objs[i]).getUserObject().toString());
        }
        return cname.toString();
    }
    
    public void setClassName(String classname)
    {
        if (m_root == null)
               return;

        String[] res = classname.split("[.]");
        JarEntryTreeNode node = m_root;
        JarEntryTreeNode best = m_root;

        for (int i=0; i<res.length; i++)
        {
            String item = res[i];
            JarEntryTreeNode next = null;
            for (Enumeration e=node.children(); e.hasMoreElements(); )
            {
                JarEntryTreeNode opt = (JarEntryTreeNode)e.nextElement();

                if (item.equals(opt.getUserObject()))
                    next = opt;
            }

            node = next;
            if (node != null)
                best = node;
        }

        TreePath tp = new TreePath(best.getPath());
        m_tree.setSelectionPath(tp);
        m_tree.makeVisible(tp);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_tree = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        m_buttonSelect = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        m_buttonCancel = new javax.swing.JButton();

        setTitle("Class Selector");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jPanel1.setFocusable(false);
        jLabel1.setText("Select a class...");
        jLabel1.setFocusable(false);
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanel3.setFocusable(false);
        jScrollPane1.setFocusable(false);
        jScrollPane1.setViewportView(m_tree);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setFocusable(false);
        m_buttonSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stock_calc-accept-16.png")));
        m_buttonSelect.setText("Select");
        m_buttonSelect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonSelectActionPerformed(evt);
            }
        });

        jPanel2.add(m_buttonSelect);

        jPanel2.add(jSeparator1);

        m_buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stock_calc-cancel-16.png")));
        m_buttonCancel.setText("Cancel");
        m_buttonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonCancelActionPerformed(evt);
            }
        });

        jPanel2.add(m_buttonCancel);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-371)/2, (screenSize.height-260)/2, 371, 260);
    }//GEN-END:initComponents

    private void buttonSelectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonSelectActionPerformed
    {//GEN-HEADEREND:event_buttonSelectActionPerformed
        // Add your handling code here:
        m_valid = true;
        setVisible(false);
    }//GEN-LAST:event_buttonSelectActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonCancelActionPerformed
    {//GEN-HEADEREND:event_buttonCancelActionPerformed
        // Add your handling code here:
        m_valid = false;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        m_valid = false;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        new ClassChooserDialog(new javax.swing.JFrame(), true).setVisible(true);
    }
    
    public boolean validated()
    {
        return m_valid;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton m_buttonCancel;
    private javax.swing.JButton m_buttonSelect;
    private javax.swing.JTree m_tree;
    // End of variables declaration//GEN-END:variables
    
}
