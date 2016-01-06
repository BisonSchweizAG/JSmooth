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

#include "SingleInstanceManager.h"

#include <windows.h>
#include <winbase.h>

void pipe_wait_callback(void* param)
{
  SingleInstanceManager* sim = (SingleInstanceManager*)param;
  while (true)
    {
      std::string msg = sim->waitPipeMessage();
      printf("Got MESSAGE: %s\n", msg.c_str());
      sim->processMessage(msg);
      fflush(stdout);
    }
}


SingleInstanceManager::SingleInstanceManager()
{
  std::string name = "{"+FileUtils::getExecutablePath() + "::" + FileUtils::getExecutableFileName() + "}";
  name = StringUtils::replace(name, "\\", ":");
  init(name);
}

SingleInstanceManager::SingleInstanceManager(const std::string& uniqueName)
{
  init(uniqueName);
}

SingleInstanceManager::~SingleInstanceManager()
{
  if (m_mutex != NULL)
    CloseHandle(m_mutex);
}

void SingleInstanceManager::init(const std::string& uniqname)
{
  m_name = uniqname;
  m_mutex = CreateMutex(NULL, FALSE, uniqname.c_str()); //do early
  m_lasterror = GetLastError(); //save for use later...
  m_alreadyexists = (m_lasterror == ERROR_ALREADY_EXISTS)?true:false;
}

void SingleInstanceManager::startMasterInstanceServer()
{
  if (!m_alreadyexists)
    {
      m_pipethread.start(pipe_wait_callback, this);
    }
}

void SingleInstanceManager::sendMessageInstanceShow()
{
  char *message = "SHOWWINDOW PLZ";
  DWORD msglen = strlen(message);
  char bufferout[256];
  DWORD lenout = 255, readlen=0;

  CallNamedPipe(getPipeName().c_str(),
		message, msglen,
		bufferout, lenout,
		&readlen,
		2);

}

bool SingleInstanceManager::alreadyExists()
{
  return m_alreadyexists;
}

std::string SingleInstanceManager::waitPipeMessage()
{
  std::string result = "";

  std::string pipename = getPipeName();

  m_pipe = CreateNamedPipe(pipename.c_str(),
			   PIPE_ACCESS_DUPLEX,
			   PIPE_TYPE_MESSAGE | PIPE_READMODE_MESSAGE,
			   6,
			   256,
			   256,
			   NMPWAIT_USE_DEFAULT_WAIT,
			   NULL);

  if (m_pipe == INVALID_HANDLE_VALUE)
    {
      m_pipe = NULL;
      return "";
    }
  
  if (ConnectNamedPipe(m_pipe, NULL))
    {
      printf("waitPipeMessage...\n");
      fflush(stdout);

      printf("connected to named pipe...\n");
      fflush(stdout);
      char buffer[256];
      DWORD length;
      
      bool res = ReadFile(m_pipe, buffer, sizeof(buffer), &length, NULL); 
      buffer[length] = 0;
      printf("read file %s\n", buffer);
      fflush(stdout);
      
      if (res && (length>0))
	{
	  bool res = WriteFile(m_pipe, buffer, strlen(buffer), &length, NULL); 
	  result = buffer;
	}
      
      DisconnectNamedPipe(m_pipe);
    }

  CloseHandle(m_pipe);

  return result;
}

BOOL CALLBACK displayWindowForProcess(HWND hwnd, LPARAM lParam )
{
  DWORD myprocessid = GetCurrentProcessId();
  DWORD hid;
  GetWindowThreadProcessId(hwnd, &hid);
  printf("Checking %d, %d, %d\n", hwnd, hid, myprocessid); fflush(stdout);
  if (hid == myprocessid)
    {
      LONG style = GetWindowLong(hwnd, GWL_STYLE);
      if ((style &  (WS_VISIBLE)) != 0)
	{
	  SetForegroundWindow(hwnd);
	  return false;
	}
    }
  return true;
}

void SingleInstanceManager::processMessage(const std::string& msg)
{
  printf("PROCESSING MESSAGE\n");
  fflush(stdout);
  EnumWindows(displayWindowForProcess, 0);
}

std::string SingleInstanceManager::getPipeName()
{
  std::string pipename = "\\\\.\\pipe\\";
  if (m_name.length() > (255-9))
    pipename += m_name.substr( m_name.length() - (255-9) );
  else
    pipename += m_name;  

  return pipename;
}
