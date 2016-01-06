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

#ifndef _JVMENVVARLOOKUP_H_
#define _JVMENVVARLOOKUP_H_

#include "common.h"
#include "ResourceManager.h"
#include "SunJVMLauncher.h"

#include <string>
#include <windows.h>

using namespace std;

/** Utility class that checks environment variables for Sun's JVM.
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class JVMEnvVarLookup
{
 public:
  /**
   * This method creates SunJVMLauncher objects from a given
   * environment variable.  If the environment variable exists, it
   * returns a vector with one SunJVMLauncher object. Otherwise, the
   * vector is returned empty.
   *
   * @param envvar an environment variable
   * @return a vector which contains 0 or 1 SunJVMLauncher.
   */
  static vector<SunJVMLauncher> lookupJVM(const string& envvar);
};

#endif
