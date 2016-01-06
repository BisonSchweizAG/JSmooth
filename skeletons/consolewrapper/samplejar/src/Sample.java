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
 * Sample.java
 *
 * Created on 3 août 2003, 14:26
 */
import java.io.*;

/**
 *
 * @author  Rodrigo
 */
public class Sample extends javax.swing.JDialog
{
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
	System.out.println("Arguments: " + args.length);
	for (int i=0; i<args.length; i++)
	    System.out.println("args[" + i + "]=" + args[i]);
    }
}
