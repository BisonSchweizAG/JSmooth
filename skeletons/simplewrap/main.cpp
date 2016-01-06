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

#include <windows.h>
#include <iostream>

#include "resource.h"
#include "common.h"
#include "ResourceManager.h"
#include "JVMRegistryLookup.h"
#include "JavaMachineManager.h"
#include "SingleInstanceManager.h"

ResourceManager* globalResMan;
DebugConsole *DEBUGCONSOLE = 0;

void lastExit()
{
  delete DEBUGCONSOLE;
  DEBUGCONSOLE = 0;
  delete globalResMan;
  globalResMan = 0;
}

void _debugOutput(const std::string& text)
{
  if (DEBUGCONSOLE != NULL)
    DEBUGCONSOLE->writeline(text);
}

void _debugWaitKey()
{
  if (DEBUGCONSOLE != NULL)
    DEBUGCONSOLE->waitKey();
}
                          
int WINAPI WinMain (HINSTANCE hThisInstance,
                    HINSTANCE hPrevInstance,
                    LPSTR lpszArgument,
                    int nFunsterStil)
{
    atexit(lastExit);
    SingleInstanceManager instanceman;

    globalResMan = new ResourceManager("JAVA", PROPID, JARID, JNISMOOTHID);

    // sets up the command line arguments
    // not sure if lpszArgument can be null on Windows...
     if ((lpszArgument!=NULL) && (strlen(lpszArgument)>0))
       {
	 std::vector<std::string> args = StringUtils::split(lpszArgument, " \t\n\r", "\"'", false);
	 globalResMan->setUserArguments( args );
       }


  bool dodebug = globalResMan->getBooleanProperty("skel_Debug");

  if (dodebug)
    {
      DEBUGCONSOLE = new DebugConsole("JSmooth Debug");
    }

    if (DEBUGCONSOLE!=0)
      globalResMan->printDebug();


  bool singleinstance = globalResMan->getBooleanProperty("skel_SingleInstance");
  if (singleinstance)
    {
      if (instanceman.alreadyExists())
	{
	  instanceman.sendMessageInstanceShow();
	  exit(0);
	}
      else
	{
	  instanceman.startMasterInstanceServer();
	}
    }

    DEBUG(string("Main class: ") + globalResMan->getMainName());

    char curdir[256];
    GetCurrentDirectory(256, curdir);
    DEBUG(string("Currentdir: ") + curdir);

    string newcurdir = globalResMan->getCurrentDirectory();
    SetCurrentDirectory(newcurdir.c_str());

    JavaMachineManager man(*globalResMan);
    man.setAcceptExe(true);
    man.setAcceptDLL(true);
    if (dodebug)
      man.setUseConsole(true);
    else
      man.setUseConsole(false);

    bool singleprocess = globalResMan->getBooleanProperty("skel_SingleProcess");
    bool jnismooth = globalResMan->getBooleanProperty("skel_JniSmooth");

    if (singleprocess || jnismooth)
      man.setPreferDLL(true);
    else
      man.setPreferDLL(false);

    int retvalue = 0;

    if (man.run() == false)
    {
      DEBUG("Displaying error message to user...");
        std::string errmsg = globalResMan->getProperty("skel_Message");
        std::string url = globalResMan->getProperty("skel_URL");
        if (MessageBox(NULL, errmsg.c_str(), "No Java?", MB_OKCANCEL|MB_ICONQUESTION|MB_APPLMODAL) == IDOK)
        {
            ShellExecute(NULL, "open", url.c_str(), NULL, "", 0);
        }
    }
    else
      {
	retvalue = man.getExitCode();
      }

    DEBUG("NORMAL EXIT");
    DEBUGWAITKEY();

    /* The program return-value is 0 - The value that PostQuitMessage() gave */
    return retvalue;
}

