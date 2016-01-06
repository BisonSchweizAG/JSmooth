/*
  JSmooth: a VM wrapper toolkit for Windows
  Copyright (C) 2003-2007 Rodrigo Reyes <reyes@charabia.net>

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Library General Public
  License as published by the Free Software Foundation; either
  version 2 of the License, or (at your option) any later version.
  
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Library General Public License for more details.
  
  You should have received a copy of the GNU Library General Public
  License along with this library; if not, write to the Free
  Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
  
*/

#ifndef _DEBUGCONSOLE_H_
#define _DEBUGCONSOLE_H_

#include <string>
#include <windows.h>


/** Manages a Debug Console.  
 * The DebugConsole manages a debug console. This is mainly a standard
 * windows console that can be used for output.
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class DebugConsole
{
 private:
  HANDLE m_out;
  HANDLE m_in;
    
 public:

  /**
   * The default constructor
   */
  DebugConsole();

  /** 
   * Creates a console with a given title.
   * This constructor creates the console and set up the title of the
   * console windows with the name passed as parameter.
   *
   * @param title the title of the console
   */
  DebugConsole(const std::string& title);

  
  /** 
   * Output a line of text to the console.
   * @param str the text string
   */
  void writeline(const std::string& str);
    
  /// Makes the console to wait a key press.
  void waitKey();

};

#endif
