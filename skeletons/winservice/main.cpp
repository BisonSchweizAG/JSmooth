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

#include <iostream>
#include <stdlib.h>
#include <windows.h>

#include "resource.h"

#include "common.h"
#include "ResourceManager.h"
#include "JVMRegistryLookup.h"
#include "JavaMachineManager.h"

#include "WinService.h"

using namespace std;

extern WinService* winservice_ref;
bool   global_debug;

void _debugOutput(const std::string& text)
{
  if ((winservice_ref != 0) && global_debug)
    winservice_ref->log(text);
}

void _debugWaitKey() { }

int main(int argc, char *argv[])
{ 
  global_debug = false;
  winservice_ref = 0;
  ResourceManager resman("JAVA", PROPID, JARID, JNISMOOTHID);

  string newcurdir = resman.getCurrentDirectory();
  if (newcurdir.length()>0)
    SetCurrentDirectory(newcurdir.c_str());

  std::string rootdir = FileUtils::getExecutablePath();
  
  std::string serviceName = resman.getProperty("skel_ServiceName");
  std::string serviceDisplayName = resman.getProperty("skel_ServiceDisplayName");
  std::string serviceDescription = resman.getProperty("skel_ServiceDescription");
  if (serviceDisplayName == "")
    serviceDisplayName = serviceName;

  global_debug = resman.getBooleanProperty("skel_Debug");

  std::string logfile = resman.getProperty("skel_Logfile");
  if (logfile == "")
    logfile = "service.log";

  if (!FileUtils::isAbsolute(logfile))
    logfile = FileUtils::concFile(rootdir, logfile);

  WinService winserv(serviceName, logfile);
  winserv.setDisplayName(serviceDisplayName);
  winserv.setDescription(serviceDescription);
  winserv.setAutostart( resman.getBooleanProperty("skel_Autostart") );
  winserv.setInteractive( resman.getBooleanProperty("skel_Interactive") );

  if (argc>1)
    {
      if ((strcmp(argv[1], "-i")==0) || (strcmp(argv[1], "install")==0))
	{
	  if (winserv.install())
	    printf("Service %s installed.\n", serviceName.c_str());
	  else
	    printf("Failed to install service %s.\n", serviceName.c_str());
	}
      else if ((strcmp(argv[1], "-d")==0) || (strcmp(argv[1], "uninstall")==0))
	{
	  if (winserv.uninstall())
	    printf("Service %s uninstalled.\n", serviceName.c_str());
	  else
	    printf("Failed to uninstall service %s.\n", serviceName.c_str());
	}
      else if (strcmp(argv[1], "start")==0)
	{
	  if (winserv.startService())
	    printf("Service %s started.\n", serviceName.c_str());
	  else
	    printf("Failed to start service %s.\n", serviceName.c_str());	    
	}
      else if (strcmp(argv[1], "stop")==0)
	{
	  if (winserv.stopService())
	    printf("Service %s stopped.\n", serviceName.c_str());
	  else
	    printf("Failed to stop service %s.\n", serviceName.c_str());	    
	}
    }
  else
    winserv.connect();

  return 0;
}

