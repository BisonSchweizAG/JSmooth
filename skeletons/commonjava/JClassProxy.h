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

#ifndef __JCLASSPROXY_H_
#define __JCLASSPROXY_H_

#include <string>
#include <vector>

#include "common.h"
#include "StringUtils.h"
#include "SunJVMDLL.h"

/**
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

#include "JMethodCaller.h"
#include "SunJVMDLL.h"
#include "JArgs.h"

class JClassProxy
{
 private:
  jclass    m_class;
  SunJVMDLL*   m_vm;
  std::string m_classname;

 public:
  JClassProxy(SunJVMDLL* vm, const std::string& classname);

  jobject newInstance(const std::string& signature, JArgs args);
  jobject newInstance(const std::string& signature, jvalue arguments[]);

  jvalue  invoke(jobject& obj, const std::string& fqmethod, JArgs args);
  jvalue  invoke(jobject& obj, const std::string& fqmethod, jvalue arguments[]);
  jvalue  invokeStatic(const std::string& fqmethod, JArgs args);
  jvalue  invokeStatic(const std::string& fqmethod, jvalue arguments[]);
};



#endif
