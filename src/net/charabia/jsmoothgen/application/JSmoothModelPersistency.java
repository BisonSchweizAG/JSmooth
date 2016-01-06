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

/*
 * JSmoothModelPersistency.java
 *
 * Created on 7 août 2003, 19:42
 */

package net.charabia.jsmoothgen.application;

import java.io.*;
import java.util.*;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.wutka.jox.*;

public class JSmoothModelPersistency
{
    public static JSmoothModelBean load(File fin) throws IOException
    {
	FileReader fr = new FileReader(fin);
	try
	    {
		JSmoothModelBean jobj = new JSmoothModelBean();
		String INVALID = "INVALID";
		jobj.setSkeletonName(INVALID);
		JOXBeanReader jbr = new JOXBeanReader(fr);
		jbr.readObject(jobj);
		jbr.close();
		fr.close();

		if (jobj.getSkeletonName() == INVALID)
		    {
			throw new Exception("Not a JOX File");
		    }
		//		System.out.println("Loaded jobj " + jobj + " = " + jobj.getJarLocation());
		if ((jobj.getJarLocation() != null) && (jobj.getJarLocation().length()>0))
		    {
			jobj.setEmbeddedJar(true);
			//			System.out.println("Set embeddedjar to " + jobj.getEmbeddedJar());
		    }

		return jobj;

	    } catch (Exception exc)
		{
		    fr.close();

		    try {
			FileInputStream fis = new FileInputStream(fin);
			XMLDecoder dec = new XMLDecoder(fis);
			JSmoothModelBean xobj = (JSmoothModelBean)dec.readObject();
			fis.close();

			if ((xobj.getJarLocation() != null) && (xobj.getJarLocation().length()>0))
			    xobj.setEmbeddedJar(true);

			return xobj;

		    } catch (Exception exc2)
			{
			    exc2.printStackTrace();
			    throw new IOException(exc2.toString());
			}
		}
    }
	
    public static void save(File fout, JSmoothModelBean obj) throws IOException
    {
	//	FileOutputStream fos = new FileOutputStream(fout);
	try
	    {
		// 		XMLEncoder enc = new XMLEncoder(fos);
		// 		enc.writeObject(obj);
		// 		enc.close();
		
		String jarloc = obj.getJarLocation();
		if (obj.getEmbeddedJar() == false)
		    obj.setJarLocation(null);

		FileWriter fw = new FileWriter(fout);
		JOXBeanWriter jbw = new JOXBeanWriter(fw);
		jbw.writeObject("jsmoothproject", obj);
		jbw.close();
		fw.close();

		obj.setJarLocation(jarloc);

	    } catch (Exception ex)
		{
		    throw new IOException(ex.toString());
		}
	    finally
		{
		    //		    fos.close();
		}
    }
	
    // root : z:/a/b/c/d
    // t1:    z:/a/b/e/f
    // t2:    c:/t/r
    // t3:    z:/a/b/c/d/i/m
    // t4:    z:/a/b/c/d
    static public File makePathRelativeIfPossible(File root, File f)
    {
	if (f.toString().indexOf("${")>=0)
	    return f;

	File orgfile = f;
	try
	    {
		if (f.isAbsolute() == false)
		    {
			f = new File(root, f.toString());
		    }
		f = f.getCanonicalFile();
		f = f.getAbsoluteFile();
		root = root.getCanonicalFile();
		root = root.getAbsoluteFile();
	    } catch (IOException iox)
		{
		    iox.printStackTrace();
		    System.out.println("Failed, returning " + orgfile);

		    return orgfile;
		}
	Vector rootvec = new Vector();
	Vector targetvec = new Vector();
	File cur;
	cur = root;
	while (cur != null)
	    {
		String n = cur.getName();
		// lame hack, because getName() returns "" when the file is a drive (like c: or z:)
		if (n.equals(""))
		    n = cur.getAbsolutePath();
		rootvec.add(0, n);
		cur = cur.getParentFile();
	    }
		
	cur = f;
	while (cur != null)
	    {
		String n = cur.getName();
		if (n.equals(""))
		    n = cur.getAbsolutePath();
		targetvec.add(0, n);
		cur = cur.getParentFile();
	    }
		
	// find the lowest common path
	int cursor = 0;
	while ((cursor < rootvec.size()) && (cursor < targetvec.size()))
	    {
		if (rootvec.elementAt(cursor).equals(targetvec.elementAt(cursor)) == false)
		    break;
		cursor++;
	    }
		
	if (cursor == 0)
	    return f;
		
	if ((cursor == rootvec.size()) && (cursor == targetvec.size()))
	    return new File(".");
		
	StringBuffer buffer = new StringBuffer();
	for (int i=cursor; i<rootvec.size(); i++)
	    {
		buffer.append("../");
	    }
		
	for (int i=cursor; i<targetvec.size(); i++)
	    {
		buffer.append(targetvec.elementAt(i).toString());
		buffer.append("/");
	    }
		
	return new File(buffer.toString());
    }
	
    static public void main(String[]args)
    {
	File root = new File("z:/a/b/c/d");
	File t1 = new File("z:/a/b/e/f");
	File t2 = new File("c:/t/r");
	File t3 = new File("z:/a/b/c/d/i/m");
	File t4 = new File("z:/a/b/c/d");
		
	System.out.println("Rel root, t1: " + makePathRelativeIfPossible(root,t1));
	System.out.println("Rel root, t2: " + makePathRelativeIfPossible(root,t2));
	System.out.println("Rel root, t3: " + makePathRelativeIfPossible(root,t3));
	System.out.println("Rel root, t4: " + makePathRelativeIfPossible(root,t4));
		
	File f1 = new File("f:\\a\\b");
	File f2 = new File("f:\\a\\c");
	File f3 = new File(f1, f2.toString());
	System.out.println("f3 = " + f3.toString());
    }
	
	
	
}
