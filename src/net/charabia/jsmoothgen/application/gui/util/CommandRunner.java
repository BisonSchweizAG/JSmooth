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

import java.io.*;
import java.util.*;

/**
 *
 */
public class CommandRunner
{
    static private Vector s_runningprocs = new Vector();

    static {
	Thread t = new ProcessCleaner();
	t.setDaemon(true);
	Runtime.getRuntime().addShutdownHook(t);
    }

    static public class CmdStdReader implements Runnable
    {
	InputStream m_in;
		
	public CmdStdReader(InputStream in)
	{
	    m_in = in;
	}
		
	public void run() 
	{
	    try {
		BufferedReader br = new BufferedReader(new InputStreamReader(m_in));
		String line = null;
		while ( (line = br.readLine()) != null)
		    System.out.println(line);
	    } catch (Exception exc)
		{
		    exc.printStackTrace();
		}
	}
		
    }

    static public class ProcessCleaner extends Thread
    {
	public void run()  
	{
	    for (Enumeration e=s_runningprocs.elements(); e.hasMoreElements(); )
		{
		    Process p = (Process)e.nextElement();
		    try {
			int res = p.exitValue();
		    } catch (Exception ex)
			{
			    p.destroy();
			}
		}
	}
    }
	
    static public void run(String[] cmd, File curdir) throws Exception
    {
	Process proc = Runtime.getRuntime().exec(cmd, null, curdir);
	InputStream stdin = proc.getInputStream();
	InputStream stderr = proc.getErrorStream();
		
	new Thread(new CmdStdReader(stdin)).start();
	new Thread(new CmdStdReader(stderr)).start();

	s_runningprocs.add(proc);
    }
}
