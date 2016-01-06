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

#include "JClassProxy.h"

JClassProxy::JClassProxy(SunJVMDLL* vm, const std::string& classname)
{
  m_vm = vm;
  m_class = m_vm->findClass(classname);
  m_classname = classname;
}

jobject JClassProxy::newInstance(const std::string& signature, JArgs args)
{
  jvalue* jv = args.allocArray();
  jobject res = newInstance(signature, jv);
  delete jv;
  return res;
}

jobject JClassProxy::newInstance(const std::string& signature, jvalue arguments[])
{
  JMethodCaller caller(signature);
  jmethodID m = m_vm->findMethod(m_class, "<init>", caller.getJavaSignature(), false);
  printf("Constructor %s:%s: %d/%d\n", signature.c_str(), caller.getJavaSignature().c_str(), m, m_class);
  return m_vm->newObject(m_class, m, arguments);
}

jvalue JClassProxy::invoke(jobject& obj, const std::string& fqmethod, JArgs args)
{
  JMethodCaller jmc(m_classname, fqmethod);
  jvalue* jv = args.allocArray();
  jvalue res = jmc.invoke(*m_vm, obj, jv);
  delete jv;
  return res;
}

jvalue JClassProxy::invoke(jobject& obj, const std::string& fqmethod, jvalue arguments[])
{
  DEBUG("INVOKING " + m_classname + " :: " + fqmethod);
  JMethodCaller jmc(m_classname, fqmethod);
  return jmc.invoke(*m_vm, obj, arguments);
}

jvalue JClassProxy::invokeStatic(const std::string& fqmethod, JArgs args)
{
  JMethodCaller jmc(m_classname, fqmethod);
  jvalue* jv = args.allocArray();
  jvalue res = jmc.invokeStatic(*m_vm, jv);
  delete jv;
  return res;
}

jvalue JClassProxy::invokeStatic(const std::string& fqmethod, jvalue arguments[])
{
  JMethodCaller jmc(m_classname, fqmethod);
  return jmc.invokeStatic(*m_vm, arguments);
}

