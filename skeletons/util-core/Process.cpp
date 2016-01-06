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

#include "Process.h"
#include "StringUtils.h"

Process::Process(const std::string& commandline, bool useconsole)
{
  m_commandline = commandline;
  m_useconsole  = useconsole;
  m_started     = false;
  m_redirectHandle = INVALID_HANDLE_VALUE;
}


void Process::setRedirect(const std::string& filename)
{
  m_redirection = filename;
  m_useconsole = true;
}

bool Process::run()
{
  STARTUPINFO info;
  GetStartupInfo(&info);
  int creationFlags = 0;
  int inheritsHandle;

  if (m_useconsole == true)
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

  if (m_redirection.size() > 0)
    {
      SECURITY_ATTRIBUTES secattrs;
      secattrs.nLength = sizeof(SECURITY_ATTRIBUTES);
      secattrs.lpSecurityDescriptor = NULL;
      secattrs.bInheritHandle = TRUE;

      m_redirectHandle = CreateFile(m_redirection.c_str(), GENERIC_WRITE,
			       FILE_SHARE_WRITE, &secattrs,
			       CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);

      if (m_redirectHandle == INVALID_HANDLE_VALUE)
	{
	  return false;
	}
      info.hStdOutput = m_redirectHandle;
      info.hStdError = m_redirectHandle;
      info.dwFlags = STARTF_USESHOWWINDOW|STARTF_USESTDHANDLES;
      info.lpTitle = NULL;
      info.wShowWindow = SW_HIDE; // OR SW_SHOWMINIMIZED? SW_SHOWMINNOACTIVE?
    }

  //  string exeline = StringUtils::fixQuotes(exepath) + " " + arguments;
  int res = CreateProcess(NULL, (char*)m_commandline.c_str(), 
			  NULL, NULL, inheritsHandle, creationFlags, 
			  NULL, NULL, &info, &m_procinfo);
  
  if (res != 0)
    {
      m_started = true;
      return true;
    }

  return false;
}

void Process::join()
{
  if (!m_started)
    return;

  WaitForSingleObject(m_procinfo.hProcess, INFINITE);

  if (m_redirectHandle != INVALID_HANDLE_VALUE)
    {
      CloseHandle(m_redirectHandle);
    }

}

int Process::getExitCode()
{
  GetExitCodeProcess(m_procinfo.hProcess,
		     &m_exitCode);
  return (int)m_exitCode;
}

