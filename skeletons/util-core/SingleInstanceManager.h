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

#ifndef _SINGLEINSTANCEMANAGER_H_
#define _SINGLEINSTANCEMANAGER_H_

#include "windows.h"
#include <string>
#include "FileUtils.h"
#include "Thread.h"

class SingleInstanceManager
{
 protected:
  std::string      m_name;
  bool             m_alreadyexists;
  HANDLE           m_mutex;
  HANDLE           m_pipe;
  DWORD            m_lasterror;
  Thread           m_pipethread;

 public:
  
  SingleInstanceManager();
  SingleInstanceManager(const std::string& uniqueName);
  
  ~SingleInstanceManager();

  bool alreadyExists();
  std::string waitPipeMessage();
  void processMessage(const std::string& msg);

  void startMasterInstanceServer();

  void sendMessageInstanceShow();



 private:
  void init(const std::string& uniqname);
  std::string getPipeName();

};

#endif
