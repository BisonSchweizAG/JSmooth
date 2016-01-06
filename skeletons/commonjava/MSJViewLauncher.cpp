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

#include "MSJViewLauncher.h"

bool MSJViewLauncher::runProc(ResourceManager& resource, bool noConsole)
{
      DEBUG("Running JVIEW new process");
      
      //
      // JView is java 1.1 only !
      //
      Version VmVersion("1.1.99");

    Version max(resource.getProperty(ResourceManager:: KEY_MAXVERSION));
    Version min(resource.getProperty(ResourceManager:: KEY_MINVERSION));
    
    if (min.isValid() && (VmVersion < min))
        return false;

    if (max.isValid() && (max < VmVersion))
        return false;

      string javaproperties = "";
      const vector<JavaProperty>& jprops = resource.getJavaProperties();
      for (vector<JavaProperty>::const_iterator i=jprops.begin(); i != jprops.end(); i++)
      {
            JavaProperty jp = *i;
	    string value = jp.getValue();

	    value = StringUtils::replace(value, "${VMSELECTION}", "jview");
	    value = StringUtils::replace(value, "${VMSPAWNTYPE}", "PROC");

            javaproperties += " \"/d:" + jp.getName() + "=" + value + "\"";
      }
    
      string classpath = resource.saveJarInTempFile();
      string cpext = resource.getNormalizedClassPath();
      classpath += cpext;
      classpath += ";" + resource.saveJnismoothInTempFile();

      string classname = resource.getProperty(string(ResourceManager::KEY_MAINCLASSNAME));
      string arguments = javaproperties + " /cp:p \"" + classpath + "\" " + classname;

      DEBUG("CLASSNAME = <" + classname + ">");
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
//            info.wShowWindow = SW_HIDE;
            creationFlags = NORMAL_PRIORITY_CLASS | DETACHED_PROCESS;
            inheritsHandle = FALSE;
      }

      PROCESS_INFORMATION procinfo;
      string exeline = "jview.exe " + arguments;
      DEBUG("EXELINE: " + exeline);
      int res = CreateProcess(NULL, (char*)exeline.c_str(), NULL, NULL, inheritsHandle, creationFlags, NULL, NULL, &info, &procinfo);

      DEBUG("JVIEW result = " + StringUtils::toString(res));

    
      if (res != 0)
      {
            WaitForSingleObject(procinfo.hProcess, INFINITE);
            return true;
      }

      DEBUG("ERROR JVIEW : " + StringUtils::toString(GetLastError()));

      return false;
}

