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

#include "execexe.h"

//
// EXEC CMD
void execute(const std::string& commandline, bool noConsole=true)
{
  STARTUPINFO info;
  GetStartupInfo(&info);
  int creationFlags = 0;
  int inheritsHandle;
  if (noConsole == false)
    {
      info.dwFlags = STARTF_USESHOWWINDOW|STARTF_USESTDHANDLES;
      info.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
      info.hStdError = GetStdHandle(STD_ERROR_HANDLE);
      info.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
      creationFlags = NORMAL_PRIORITY_CLASS;
      inheritsHandle = TRUE;
    }
  else
    {
      info.dwFlags = STARTF_USESHOWWINDOW;
      creationFlags = NORMAL_PRIORITY_CLASS | DETACHED_PROCESS;
      inheritsHandle = FALSE;
    }
		  
  std::string arguments = "";
  PROCESS_INFORMATION procinfo;

  int res = CreateProcess(NULL, (char*)commandline.c_str(), NULL, NULL, inheritsHandle, creationFlags, NULL, NULL, &info, &procinfo);

}
