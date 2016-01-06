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

#include "JVMEnvVarLookup.h"

vector<SunJVMLauncher> JVMEnvVarLookup::lookupJVM(const string& envvar)
{
    vector<SunJVMLauncher> result;
    char buffer[512];
    DWORD res = GetEnvironmentVariable(envvar.c_str(), buffer, 512);
    
    if (res != 0)
    {
        SunJVMLauncher jvm;
        jvm.JavaHome = buffer;
        DEBUG("JVM Lookup: Env-Var " + envvar + " defined (" + buffer + ")!");
        result.push_back(jvm);
    } 
    else
    {
        DEBUG("JVM Lookup: Env-Var " + envvar + " not defined on this system.");
    }

    return result;
}

