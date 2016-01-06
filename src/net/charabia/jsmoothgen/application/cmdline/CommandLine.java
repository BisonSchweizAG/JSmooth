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

package net.charabia.jsmoothgen.application.cmdline;

import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.pe.*;

import java.io.*;

public class CommandLine
{

    static void printUsage()
    {
	System.out.println("Usage: jsmoothc [projectfile.jsmooth]");
 	System.out.println(" where projectfile.jsmooth is a project file created by JSmoothGen");
    }

    public static void main(String[] args)
    {
	if (args.length != 1)
	    {
		printUsage();
		System.exit(10);
	    }

	File prj = new File(args[0]);
	if (prj.exists() == false)
	    {
		prj = new File(prj.toString() + ".jsmooth");
	    }

	if (prj.exists() == false)
	    {
		System.err.println("Error: project file <" + args[0]+"> not found");
		System.exit(10);
	    }

	// setup headless mode
	System.setProperty("java.awt.headless", "true");
	java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();

	String jsmoothbase = System.getProperty("jsmooth.basedir");

	try {
	    JSmoothModelBean model = JSmoothModelPersistency.load(prj);
	    File basedir = prj.getParentFile();
	    File skelbase = new File("skeletons");
	    if (jsmoothbase != null)
		{
		    skelbase = new File(new File(jsmoothbase), "skeletons");
		}

	    SkeletonList skelList = new SkeletonList(skelbase);

	    File out = new File(basedir, model.getExecutableName());
	    
	    SkeletonBean skel = skelList.getSkeleton(model.getSkeletonName());
	    File skelroot = skelList.getDirectory(skel);
	    
	    ExeCompiler compiler = new ExeCompiler();
	    compiler.compile(skelroot, skel, basedir, model, out);

	    System.exit(0);

	} catch (Exception exc)
	    {
		//	exc.printStackTrace();
		System.err.println("Incorrect project file!");
	    }

	System.exit(20);
    }

}
