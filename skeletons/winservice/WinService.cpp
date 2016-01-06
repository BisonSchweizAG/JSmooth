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

#include "WinService.h"

#include "FileUtils.h"
#include "StringUtils.h"

#include "resource.h"

#include "common.h"
#include "ResourceManager.h"
#include "JVMRegistryLookup.h"
#include "JavaMachineManager.h"
#include "JClassProxy.h"
#include "JArgs.h"

WinService* winservice_ref;


void winservice_thread_support(void* param)
{
  WinService* service = (WinService*)param;
  service->run();
}

void winservice_ctrlhandler(DWORD ccode)
{
  WinService* service = winservice_ref;
  service->serviceCtrlHandler(ccode);
}


WINAPI void *winservice_servicemain_support(DWORD argCount,LPSTR* arguments)
{
  WinService* service = winservice_ref;
  service->serviceMain(argCount,arguments);
}


void winservice_thread_stop(void* param)
{
  WinService* service = (WinService*)param;
  service->kill();  
}

WinService::WinService(const std::string& name, const std::string& filename)
{
  m_log = new Log(filename);
  m_log->out("Constructing winservice object " + name);

  m_jvm = 0;
  m_status_checkpoint = 0;
 
  m_serviceName = name;
  m_serviceDescription = "";
  m_autostart = false;

  strcpy(m_cname, m_serviceName.c_str());
  m_dispatchTable[0].lpServiceName = m_cname;
  m_dispatchTable[0].lpServiceProc = (LPSERVICE_MAIN_FUNCTION)winservice_servicemain_support;
  m_dispatchTable[1].lpServiceName = 0;
  m_dispatchTable[1].lpServiceProc = 0;
  winservice_ref = this;
}

WinService::~WinService()
{
  if (m_jvm != 0)
    {
      SunJVMDLL* runner = m_jvm->getDLL();
      log("Calling java.lang.System.exit(0)...");

      JClassProxy disp(runner, "java.lang.System");
      disp.invokeStatic("void exit(int)", JArgs(0));
      log("exit(0) returned successfully");

      // m_jvm->callDLLStaticMethodInt("java.lang.System", "exit", "(I)V", 0);
    }
}

void WinService::connect()
{
  log(std::string("setting up a connection with the control dispatcher (") + getName() + ")");
  
  int handle = StartServiceCtrlDispatcher(m_dispatchTable);
  if (handle == 0)
    {
      int err = GetLastError();
      printf("connect error %d (%d)!\n", err, ERROR_INVALID_DATA);
    }
}
 
bool WinService::install()
{
  log(std::string("Installation of ") + getName());

  HANDLE scman = OpenSCManager(NULL,NULL,SC_MANAGER_ALL_ACCESS);
  if (scman == 0)
    return false;
  std::string exepath = FileUtils::concFile(FileUtils::getExecutablePath(), FileUtils::getExecutableFileName());

  HANDLE service = (HANDLE)CreateService((SC_HANDLE)scman, m_cname, 
					 m_serviceDisplayName.c_str(), // service name to display
					 SERVICE_ALL_ACCESS, // desired access 
					 SERVICE_WIN32_OWN_PROCESS | (m_interactive?SERVICE_INTERACTIVE_PROCESS:0), // service type 
					 m_autostart?SERVICE_AUTO_START:SERVICE_DEMAND_START, // start type 
					 SERVICE_ERROR_NORMAL, // error control type 
					 exepath.c_str(), // service's binary 
					 NULL, // no load ordering group 
					 NULL, // no tag identifier 
					 NULL, // no dependencies
					 NULL, // LocalSystem account
					 NULL); // no password
  
  CloseServiceHandle((SC_HANDLE)scman);

  if (service == 0)
    {
      log("Failed to install!");
      return false;
    }

  log("Service installed!");
  return true;
}

 
bool WinService::uninstall()
{
  bool result = false;

  HANDLE scman = OpenSCManager(NULL,NULL,SC_MANAGER_ALL_ACCESS);
  if (scman == 0)
    return result;
  std::string exepath = FileUtils::getExecutablePath();

  SC_HANDLE service = OpenService(
				  (SC_HANDLE)scman,	// handle to service control manager database  
				  m_cname,	// pointer to name of service to start 
				  SERVICE_ALL_ACCESS	 	// type of access to service 
				  );
  if (service != 0)
    {
      result = DeleteService(service );
    }

  log("Service uninstalled!");

  CloseServiceHandle((SC_HANDLE)scman);
  return result;
}

bool WinService::setStatus(int currentState)
{
  SERVICE_STATUS status;
  std::string meaning = "";
  switch(currentState)
    {
    case SERVICE_STOPPED:
      meaning = "SERVICE_STOPPED";
      break;
    case SERVICE_START_PENDING:
      meaning = "SERVICE_START_PENDING";
      break;
    case SERVICE_STOP_PENDING:
      meaning = "SERVICE_STOP_PENDING";
      break;
    case SERVICE_RUNNING:
      meaning = "SERVICE_RUNNING";
      break;
    case SERVICE_CONTINUE_PENDING:
      meaning = "SERVICE_CONTINUE_PENDING";
      break;
    case SERVICE_PAUSE_PENDING:
      meaning = "SERVICE_PAUSE_PENDING";
      break;
    case SERVICE_PAUSED:
      meaning = "SERVICE_PAUSED";
      break;
    }

  log("set status " + StringUtils::toString(currentState) + " (" + meaning + ")");

  status.dwServiceType = SERVICE_WIN32_OWN_PROCESS;
  status.dwCurrentState = currentState;
  if ( currentState==SERVICE_START_PENDING )
      status.dwControlsAccepted=0;
  else
      status.dwControlsAccepted = SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN;
  status.dwWin32ExitCode = NO_ERROR;
  status.dwServiceSpecificExitCode = 0;
  status.dwCheckPoint = m_status_checkpoint++;
  status.dwWaitHint = 60*1000;

  return SetServiceStatus(m_serviceStatusHandle,&status);

}

const char* WinService::getName() const
{
  return m_cname;
}

void WinService::log(const std::string& msg) const
{
  m_log->out(msg);
}

void WinService::log(const char* msg) const
{
  m_log->out(msg);
}

void WinService::serviceMain(DWORD argCount,LPSTR* arguments)
{
  log(std::string("in service main... (") + StringUtils::toString(argCount) + std::string(" args)"));
  for (int i=0; i<argCount; i++)
    {
      log( std::string("argument ") + StringUtils::toString(i) + ": " + arguments[i]);
    }
  m_serviceStatusHandle = RegisterServiceCtrlHandler(getName(),
						     (LPHANDLER_FUNCTION)winservice_ctrlhandler);

  setStatus(SERVICE_START_PENDING);

  m_stopEventHandle = CreateEvent(NULL,TRUE, FALSE,NULL);
  
  if (!setStatus(SERVICE_START_PENDING))
    return;

  m_serviceThread.start(winservice_thread_support, (void*)this);
  setStatus(SERVICE_RUNNING);
  m_serviceThread.join();
  setStatus(SERVICE_STOPPED);
}

void WinService::serviceCtrlHandler(DWORD nControlCode)
{
  switch(nControlCode)
    {	
    case SERVICE_CONTROL_SHUTDOWN:
    case SERVICE_CONTROL_STOP:
      setStatus(SERVICE_STOP_PENDING);
      m_serviceThread.start(winservice_thread_stop, (void*)this);
      return;
      
    default:
      break;
    }

  setStatus(SERVICE_RUNNING);
}

void WinService::run()
{
  setStatus(SERVICE_START_PENDING);
  log("Run...");

  ResourceManager* globalResMan = new ResourceManager("JAVA", PROPID, JARID, JNISMOOTHID);

  //
  // sets up the debug mode, if requested
  std::string dodebug = globalResMan->getProperty("skel_Debug");
  dodebug = "1";
  if (StringUtils::parseInt(dodebug) != 0)
    {
      globalResMan->printDebug();
    }

  JavaMachineManager man(*globalResMan);
  man.setAcceptExe(false);
  man.setAcceptDLL(true);
  man.setUseConsole(false);
  man.setPreferDLL(true);

  setStatus(SERVICE_RUNNING);
  SunJVMLauncher* launcher = man.runDLLFromRegistry(true);

  if (launcher != 0)
    {
      m_jvm = launcher;
      log("JVM found and instanciated successfully (" + launcher->toString() + ")");

      setStatus(SERVICE_RUNNING);

      SunJVMDLL* jdll = m_jvm->getDLL();
      if (jdll != 0)
	{
	  jdll->run( globalResMan->getProperty(ResourceManager::KEY_MAINCLASSNAME), true);
	}
      else
	log("ERROR: JVM is launched, but can't get a DLL... ?");

    }
  else
    {
      log("ERROR: could not find any suitable Java Virtual Machine");
      std::string errmsg = globalResMan->getProperty("skel_Message");
      MessageBox(NULL, 
		 errmsg.c_str(), FileUtils::getExecutableFileName().c_str(), 
		 MB_OK|MB_ICONQUESTION|MB_APPLMODAL);
    }

  setStatus(SERVICE_STOPPED);
  return;
}

void WinService::kill()
{
  log("Stopping the service...");
  if (m_jvm != 0)
    {
      setStatus(SERVICE_STOPPED);
      // We are never here for a very long time, as the
      // SERVICE_STOPPED signal kills this thread asap.  Therefore, we
      // do nothing here, we delay the java.lang.System.exit() call to
      // the destructor of this class
    }
  else
    log("no jvm available");
}

bool WinService::startService()
{
  HANDLE scman = OpenSCManager(NULL,NULL,SC_MANAGER_ALL_ACCESS);
  if (scman == 0)
    return false;

  SC_HANDLE service = OpenService((SC_HANDLE)scman,	// handle to service control manager database  
				  m_cname,	// pointer to name of service to start 
				  SERVICE_ALL_ACCESS	 	// type of access to service 
				  );
  if (service != 0)
    {
      return StartService(service, 0, NULL);
    }

  return false;
}

bool WinService::stopService()
{
  HANDLE scman = OpenSCManager(NULL,NULL,SC_MANAGER_ALL_ACCESS);
  if (scman == 0)
    return false;

  SC_HANDLE service = OpenService((SC_HANDLE)scman,	// handle to service control manager database  
				  m_cname,	// pointer to name of service to start 
				  SERVICE_ALL_ACCESS	 	// type of access to service 
				  );
  if (service != 0)
    {
      SERVICE_STATUS status;

      status.dwServiceType = SERVICE_WIN32_OWN_PROCESS;
      status.dwCurrentState = SERVICE_STOP_PENDING;
      status.dwControlsAccepted = SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN;
      status.dwWin32ExitCode = NO_ERROR;
      status.dwServiceSpecificExitCode = 0;
      status.dwCheckPoint = m_status_checkpoint++;
      status.dwWaitHint = 60*1000;

      return ControlService(service, SERVICE_CONTROL_STOP, &status);
    }

  return false;
}


void WinService::setDescription(const std::string& description)
{
  m_serviceDescription = description;
}

void WinService::setAutostart(bool b)
{
  m_autostart = b;
}

void WinService::setInteractive(bool b)
{
  m_interactive = b;
}

void WinService::setDisplayName(const std::string& displayname)
{
  m_serviceDisplayName = displayname;
}
