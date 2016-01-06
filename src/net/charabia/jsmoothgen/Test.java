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
 * Test.java
 *
 * Created on 7 août 2003, 19:31
 */

package net.charabia.jsmoothgen;
import net.charabia.jsmoothgen.application.*;

import java.io.File;

public class Test
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		String[] d = new String[2];
		d[0] = "test";
		d[1] = "test2";
		JSmoothModelBean model = new JSmoothModelBean();
		model.setJarLocation("MyJarLocation");
		model.setClassPath(d);
		JSmoothModelPersistency.save(new File("c:/result.xml"), model);	
	}
	
}
