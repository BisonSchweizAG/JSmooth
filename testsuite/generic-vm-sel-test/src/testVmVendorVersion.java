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

public class testVmVendorVersion extends java.awt.Frame
{
    static public void displayMessage(String message)
    {
	Frame f = new Frame("JSmooth TestSuite");
	f.setLayout(new BorderLayout());
	f.add(new Label(message));

	f.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e)
		{
		    System.exit(0);
		}
	    });
	f.pack();
	f.show();
    }

    public static void main(String args[])
    {
	System.out.println("Running testsuite " + testVmVendorVersion.class.toString());
	try {
	    String selection = System.getProperty("jsmooth.testsuite.selection");
	    String selectionCheck = System.getProperty("jsmooth.testsuite.selection.check");
	    
	    System.out.println("Selection      : " + selection);
	    System.out.println("Selection Check: " + selectionCheck);

	    String spawntype = System.getProperty("jsmooth.testsuite.spawntype");
	    String spawntypecheck = System.getProperty("jsmooth.testsuite.spawntype.check");
	    
	    System.out.println("SpawnType      : " + spawntype);
	    System.out.println("SpawnType Check: " + spawntypecheck);

	    if (spawntype.equalsIgnoreCase(spawntypecheck) == false)
		{
		    displayMessage("Error, spawntype " + spawntype + " != " + spawntypecheck);
		}
	    else if (selection.equalsIgnoreCase(selectionCheck) == false)
		{
		    displayMessage("Error, selection " + selection + " != " + selectionCheck);
		}
	    else 
		displayMessage("OK! " + selectionCheck + " / " + spawntypecheck);

	} catch (Exception exc)
	    {
		displayMessage(exc.toString());
	    }


    }
	
}
