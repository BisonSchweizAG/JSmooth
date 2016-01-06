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

#ifndef __SUNJVMEXE_H_
#define __SUNJVMEXE_H_

#include <string>

#include "Version.h"
#include "StringUtils.h"
#include "FileUtils.h"
#include "ResourceManager.h"
#include "JavaProperty.h"
#include "JVMBase.h"

/**
 * Manages a Sun's JVM available on the computer.
 * @author Rodrigo Reyes <reyes@charabia.net>
 */ 

class SunJVMExe : public JVMBase
{
 private:
  std::string m_jrehome;
  Version     m_version;
  int         m_exitCode;

 public:
  SunJVMExe(const std::string& jrehome);
  SunJVMExe(const std::string& jrehome, const Version& v);

  Version guessVersion();

  bool run(const std::string& mainclass, bool useconsole);

  std::string lookUpExecutable(bool useconsole);
  std::string getClassPath(bool full);

  int getExitCode();

 private:
};


#endif
