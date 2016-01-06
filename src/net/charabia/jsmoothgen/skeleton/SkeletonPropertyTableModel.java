/*
 * SkeletonPropertyTableModel.java
 *
 * Created on 14 août 2003, 22:12
 */

package net.charabia.jsmoothgen.skeleton;

import java.util.*;
/**
 *
 * @author  Rodrigo
 */
public class SkeletonPropertyTableModel extends javax.swing.table.AbstractTableModel
{
	private Vector m_props = new Vector();
	
	/** Creates a new instance of SkeletonPropertyTableModel */
	public SkeletonPropertyTableModel(SkeletonProperty[] props)
	{
		if (props == null)
			return;
		
		for (int i=0; i<props.length; i++)
		{
			m_props.add(props[i]);
		}
	}
	
	public SkeletonPropertyTableModel()
	{
		m_props.add(new SkeletonProperty());
	}
	
	public void add(SkeletonProperty prop)
	{
		m_props.addElement(prop);
		fireTableRowsInserted(m_props.size()-1, m_props.size());
		fireTableStructureChanged();
	}
	
	public void add(SkeletonProperty prop, int row)
	{
		m_props.insertElementAt(prop, row);
		fireTableRowsInserted(row, row);
		fireTableStructureChanged();
	}
	
	public void removeRow(int row)
	{
		m_props.removeElementAt(row);
		fireTableRowsDeleted(row, row);
	}
	

	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Id";
			case 1:
				return "GUI Label";
			case 2:
				return "Description";
			case 3:
				return "Type";
			case 4:
				return "Default Values";
			default:
				return "";
		}
	}
	
	public int getColumnCount()
	{
		return 5;
	}
	
	public int getRowCount()
	{
		return m_props.size();
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		SkeletonProperty sp = (SkeletonProperty)m_props.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				return sp.getIdName();
			case 1:
				return sp.getLabel();
			case 2:
				return sp.getDescription();
			case 3:
				return sp.getType();
			case 4:
				return sp.getValue();
			default:
				return "";
		}
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		SkeletonProperty sp = (SkeletonProperty)m_props.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				sp.setIdName(aValue.toString());
				break;
			case 1:
				sp.setLabel(aValue.toString());
				break;
			case 2:
				sp.setDescription(aValue.toString());
				break;
			case 3:
				sp.setType(aValue.toString());
				break;
			case 4:
				sp.setValue(aValue.toString());
				break;
		}
	}
	
	public SkeletonProperty[] getProperties()
	{
		SkeletonProperty[] result = new SkeletonProperty[m_props.size()];
		for (int i=0; i<result.length; i++)
		{
			result[i] = (SkeletonProperty)m_props.get(i);
		}	
		return result;
	}
	
	public void swapItems(int offset1, int offset2)
	{
		if ((offset1>=0) && (offset1<m_props.size())
			&& (offset2>=0) && (offset2<m_props.size()))
		{
			Object o1 = m_props.get(offset1);
			Object o2 = m_props.get(offset2);
			m_props.set(offset1, o2);
			m_props.set(offset2, o1);
		}
		fireTableRowsUpdated(Math.min(offset1, offset2), Math.max(offset1, offset2));
	}
}
