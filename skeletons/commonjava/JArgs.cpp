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

#include "JArgs.h"


JArgs::JArgs()
{
}

JArgs::JArgs(SunJVMDLL* vm)
{
  m_vm = vm;
}

jvalue* JArgs::allocArray()
{
  jvalue* args = new jvalue[m_values.size()];
  for (int i=0; i<m_values.size(); i++)
    {
      args[i] = m_values[i];
    }
  return args;
}


JArgs& JArgs::add(int i)
{
  jvalue v;
  v.i = i;
  m_values.push_back(v);
  return *this;
}

JArgs& JArgs::add(jobject &o)
{
  jvalue v;
  v.l = o;
  m_values.push_back(v);
  return *this;
}

JArgs& JArgs::add(bool b)
{
  jvalue v;
  v.z = b;
  m_values.push_back(v);
  return *this;
}

JArgs& JArgs::add(const std::string& str )
{
  jvalue v;
  v.l = m_vm->newUTFString(str);
  m_values.push_back(v);
  return *this;
}

