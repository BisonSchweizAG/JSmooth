/*
  JSmooth: a VM wrapper toolkit for Windows
  Copyright (C) 2003 Rodrigo Reyes <reyes@charabia.net>

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

#ifndef __JAVAMACHINEMANAGER_H_
#define __JAVAMACHINEMANAGER_H_

#include <string>
#include <vector>

#include "common.h"
#include "StringUtils.h"
#include "SunJVMLauncher.h"
#include "JVMRegistryLookup.h"
#include "JVMEnvVarLookup.h"
#include "MSJViewLauncher.h"

/** Manages the JVM available on the computer.  It builds the list of
 * all the JVM available on the computer, and provide a method to run
 * the application according to the ResourceManager object passed to
 * the constructor.
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class JavaMachineManager
{
  vector<SunJVMLauncher> m_registryVms;
  vector<SunJVMLauncher> m_javahomeVm;
  vector<SunJVMLauncher> m_jrepathVm;
  vector<SunJVMLauncher> m_jdkpathVm;
    
  bool                   m_localVMenabled;
  SunJVMLauncher         m_localVM;
    
  MSJViewLauncher        m_jviewVm;
    
  ResourceManager& m_resman;

  int                    m_exitCode;

  bool                   m_useConsole;
  bool                   m_acceptExe;
  bool                   m_acceptDLL;
  bool                   m_preferDLL;

 public:
  /**
   * This constructor builds a JavaMachineManager with a ResourceManager.
   *
   * @param resman a ResourceManager object
   * @see ResourceManager
   */ 
  JavaMachineManager(ResourceManager& resman) ;

  void setUseConsole(bool useConsole);
  void setAcceptExe(bool acceptExe);
  void setAcceptDLL(bool acceptDLL);
  void setPreferDLL(bool prefDLL);

  /**
   * Start the Java application.  The java application started is
   * described by the ResourceManager passed to the constructor.
   * 
   * The noConsole argument specifies whether the launcher prefers having a 
   * console attached or not. If the value of noConsonle is true, the launcher
   * tries to detach the java app from the console i/o of the current process. 
   * Otherwise, it tries to share the console i/o with the java app.
   *
   * The preferSingleProcess argument specifies whether the launcher shall
   * prefer to launch the java app in the same process or not. The main effect
   * if setting this parameter to true, is that the process name that appears
   * in the Windows Task Manager is the name of the application only (otherwise
   * the java.exe or equivalent may appear in the task manager). The main
   * drawback of setting this value to true is that there is a bug in the
   * Sun's DLL that prevents it to exit nicely, and which prevents the 
   * executable to clean up the temporary files used to launch the app. This
   * is a minor issue though, as the behaviour of most Windows application is
   * not to delete themselves the temporary files.
   *
   * @param noConsole if true, the application started is not attached to the console.
   * @param preferSingleProcess if true, the launcher first tries to launch the application in his own process.
   * @return true if the application is successfully started, false otherwise.
   */
  bool run();

  SunJVMLauncher* runDLLFromRegistry(bool justInstanciate=false);

  int getExitCode();

 protected:
  bool internalRun(SunJVMLauncher& launcher, const string& org);

};

#endif
