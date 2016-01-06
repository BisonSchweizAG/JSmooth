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

package net.charabia.jsmoothgen.ant;

import net.charabia.jsmoothgen.application.*;
import net.charabia.jsmoothgen.skeleton.*;
import net.charabia.jsmoothgen.pe.*;
import java.io.*;

public class JSmoothGen extends org.apache.tools.ant.Task
{
    /** JSmooth project file. */
    private java.io.File m_prjfile;

    /** JSmooth skeletons root directory. */
    private java.io.File m_skeletonRoot;

    /** Log level. */
    private boolean m_verbose;

    /** Destination directory for the generated executable. */
    private File m_destdir;

    /**
     * Sets the destination directory for the generated executable.
     *
     * @param destdir the destination directory for the generated executable.
     */
    public void setDestdir(java.io.File destdir)
    {
        m_destdir = destdir;
    }

    /**
     * Sets the JSmooth project file.
     *
     * @param prjfile the JSmooth project file.
     */
    public void setProject(java.io.File prjfile)
    {
	m_prjfile = prjfile;
    }

    /**
     * Sets the JSmooth skeletons root directory.
     *
     * @param skeletonRoot the JSmooth skeletons root directory.
     */
    public void setSkeletonRoot(java.io.File skeletonRoot)
    {
	m_skeletonRoot = skeletonRoot;
    }

    /**
     * Sets the log level to verbose or not.
     *
     * @param verbose the log level to verbose or not.
     */
    public void setVerbose(boolean val) 
    {
        m_verbose = val;
    }

    public void execute() throws org.apache.tools.ant.BuildException
    {
	if (m_prjfile == null)
	    throw new org.apache.tools.ant.BuildException("Project file not set");
	if (m_skeletonRoot == null)
	    throw new org.apache.tools.ant.BuildException("Skeleton Root dir file not set");

	File prj = m_prjfile;
	if (prj.exists() == false)
	    {
		prj = new File(prj.toString() + ".jsmooth");
	    }

	if (prj.exists() == false)
	    {
		throw new org.apache.tools.ant.BuildException("Project file " + prj + " not found");
	    }

	try {
	    JSmoothModelBean model = JSmoothModelPersistency.load(prj);

	    File basedir = prj.getParentFile();

            File out;
            if (m_destdir == null)
            {
                out = new File(basedir, model.getExecutableName());
            }
            else
            {
                out = new File(m_destdir, model.getExecutableName());
            }
            log("Executable file is " + out);

	    SkeletonList skelList = new SkeletonList(m_skeletonRoot);

	    SkeletonBean skel = skelList.getSkeleton(model.getSkeletonName());
	    File skelroot = skelList.getDirectory(skel);
	    
	    final ExeCompiler compiler = new ExeCompiler();
	    if (m_verbose) {
                compiler.addListener(new ExeCompiler.StepListener() {
			public void complete() {}
			public void failed() {}
			
			public void setNewState(int percentComplete, String state) {
			    log("jsmooth: " + state + " ( " + percentComplete + "%)");
			}
		    });
            }
	    
	    if (compiler.compile(skelroot, skel, basedir, model, out))
		log("Java application wrapped in " + model.getExecutableName());
	    else
		log("jsmoothgen failed: " + compiler.getErrors());

	} catch (Exception exc)
	    {
		throw new org.apache.tools.ant.BuildException("Error building the jsmooth wrapper", exc);
	    }
    }

}
