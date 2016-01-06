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

#ifndef __JSMOOTHCOREPROCESS_H_
#define __JSMOOTHCOREPROCESS_H_


#include <process.h>
#include <windows.h>
#include <winbase.h>

#include <string>
#include <stdio.h>
/**
 * Provides basic string operations.
 * 
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class Process
{
  PROCESS_INFORMATION m_procinfo;
  bool                m_useconsole;
  std::string         m_commandline;
  bool                m_started;
  std::string         m_redirection;
  bool                m_redirectstderr;
  HANDLE              m_redirectHandle;
  DWORD               m_exitCode;

 public:
  Process(const std::string& commandline, bool useconsole);

  void setRedirect(const std::string& filename);

  bool run();
  void join();

  int getExitCode();
};


#endif
