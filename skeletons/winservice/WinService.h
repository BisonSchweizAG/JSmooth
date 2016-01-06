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

#ifndef __JSMOOTHCOREWINSERVICE_H_
#define __JSMOOTHCOREWINSERVICE_H_
#include "Log.h"

#include <string>
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <process.h>

#include "Thread.h"
#include "SunJVMLauncher.h"

class WinService
{
 protected:
  Log* m_log;
  std::string m_serviceName;
  std::string m_serviceDisplayName;
  std::string m_serviceDescription;
  bool        m_autostart;
  bool        m_interactive;

  SERVICE_TABLE_ENTRY m_dispatchTable[2];
  SERVICE_STATUS_HANDLE m_serviceStatusHandle; 
  DWORD m_serviceCurrentStatusId;
  char m_cname[256];
  int m_status_checkpoint;

  HANDLE m_stopEventHandle;

  Thread m_serviceThread;
  SunJVMLauncher* m_jvm;

 public:
  WinService(const std::string& name, const std::string& filename);
  ~WinService();

  void connect();

  void serviceMain(DWORD argCount,LPSTR* arguments);
  void serviceCtrlHandler(DWORD);

  void run();
  void kill();

  bool install();
  bool uninstall();

  bool startService();
  bool stopService();
  

  bool setStatus(int currentState);

  const char* getName() const;

  void log(const std::string& msg) const;
  void log(const char* msg) const;

  void setDisplayName(const std::string& displayname);
  void setDescription(const std::string& description);
  void setAutostart(bool b);
  void setInteractive(bool b);

};

#endif
