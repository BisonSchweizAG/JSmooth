/*
 * SkeletonProperty.java
 *
 * Created on 13 août 2003, 18:53
 */

package net.charabia.jsmoothgen.skeleton;

/**
 *
 * @author  Rodrigo
 */
public class SkeletonProperty
{
    static public String TYPE_STRING = "string";
    static public String TYPE_TEXTAREA = "textarea";
    static public String TYPE_BOOLEAN = "boolean";
    static public String TYPE_AUTODOWNLOADURL = "autodownloadurl";
	
    private String m_idName = "";
    private String m_shortName = "";
    private String m_description = "";
    private String m_type = "";
    private String m_value = "";
	
    /** Creates a new instance of SkeletonProperty */
    public SkeletonProperty()
    {
    }

    public SkeletonProperty(SkeletonProperty sp)
    {
	m_idName = sp.m_idName;
	m_shortName = sp.m_shortName;
	m_description = sp.m_description;
	m_type = sp.m_type;
	m_value = sp.m_value;
    }

    public void setIdName(String idName)
    {
	m_idName = idName;
    }
	
    public String getIdName()
    {
	return m_idName;
    }

    public void setLabel(String name)
    {
	m_shortName = name;
    }
	
    public String getLabel()
    {
	return m_shortName;
    }
	
    public void setDescription(String desc)
    {
	m_description = desc;
    }
	
    public String getDescription()
    {
	return m_description;
    }

    public void setType(String type)
    {
	m_type = type;
    }
	
    public String getType()
    {
	return m_type;
    }

    public void setValue(String value)
    {
	m_value = value;
    }
	
    public String getValue()
    {
	return m_value;
    }

}


