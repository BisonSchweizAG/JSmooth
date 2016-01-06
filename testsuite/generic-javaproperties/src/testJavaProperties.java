/*
 * Frame.java
 *
 * Created on 10 août 2003, 19:23
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  Rodrigo
 */

public class testJavaProperties extends java.awt.Frame
{
    static public void checkProperty(String propname, String orgvalue)
    {
	String value = System.getProperty(propname);
	System.out.print("Checking " + orgvalue + " (" + value + ")... ");
	if ((value != null) && (orgvalue.equals(value)==false))
	    {
		System.out.println("OK");
	    }
	else
	    {
		System.out.println("ERROR");
		System.exit(99);
	    }
    }
    
    public static void main(String args[])
    {
	try {
	    checkProperty("jsmooth.testsuite.executablepath", "${EXECUTABLEPATH}");
	    checkProperty("jsmooth.testsuite.executablename", "${EXECUTABLENAME}");
	    checkProperty("jsmooth.testsuite.computername", "${COMPUTERNAME}");
	    checkProperty("jsmooth.testsuite.vmselection", "${VMSELECTION}");
	    checkProperty("jsmooth.testsuite.spawntype", "${VMSPAWNTYPE}");
	    checkProperty("jsmooth.testsuite.path", "%PATH%");
	    
	    System.exit(0);

	} catch (Exception exc)
	    {
	    }
    }
	
}
