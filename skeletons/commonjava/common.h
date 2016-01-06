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

#ifndef _COMMON_H
#define _COMMON_H

#include <vector>
#include <string>
#include <windows.h>

#include "DebugConsole.h"

//#define DEBUGMODE 1
//#undef DEBUGMODE
//#define DEBUGMODE 2

extern std::vector< std::string > LOG;

void _debugOutput(const std::string& text);
void _debugWaitKey();

class ResourceManager;
extern ResourceManager* globalResMan;

/* #ifndef DEBUGMODE */
/* #    define DEBUG(x) */
/* #    define DEBUGWAITKEY() */
/* #else */
/* #  if DEBUGMODE == 1 */
/* #    define DEBUG(x) DEBUGCONSOLE.writeline(x) */
/* #    define DEBUGWAITKEY() DEBUGCONSOLE.waitKey() */
/* #  elif DEBUGMODE == 2 */
/* #    include <iostream> */
/* #    define DEBUG(x) std::cerr << x << "\r\n" */
/* #    define DEBUGWAITKEY() */
/* #  else */
/* #    error Unknown DEBUGMODE value, it should be 1 for CONSOLE, 2 for STDIO */
/* #  endif */
/* #endif */

// #define DEBUG(x) MessageBox(NULL, std::string(x).c_str(), "DEBUG", MB_OKCANCEL|MB_ICONQUESTION|MB_APPLMODAL)

#define DEBUG(x) _debugOutput(x)
#define DEBUGWAITKEY() _debugWaitKey()

#endif
