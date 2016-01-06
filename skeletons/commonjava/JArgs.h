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

#ifndef __JARGS_H_
#define __JARGS_H_

#include "jni.h"
#include "StringUtils.h"
#include "SunJVMDLL.h"

class JArgs
{
 private:
  std::vector<jvalue>    m_values;
  SunJVMDLL*                m_vm;

 public:
  JArgs();
  JArgs(SunJVMDLL* vm);
  JArgs(int i) { add(i); }
  JArgs(bool i) { add(i); }
  JArgs(jobject& o) { add(o); }
  JArgs(const std::string& o) { add(o); }
  
  jvalue* allocArray();

  JArgs& add(int i);
  JArgs& add(bool b);
  JArgs& add(jobject& obj);
  JArgs& add(jobjectArray& arr) { add((jobject)arr); }
  JArgs& add(jstring& obj) { return add((jstring)obj); }
  JArgs& add(const std::string& str );


  JArgs& operator+(int i) { return add(i); }
  JArgs& operator+(bool i) { return add(i); }
  JArgs& operator+(jobject& i) { return add(i); }
  JArgs& operator+(const std::string& i) { return add(i); }

};



#endif
