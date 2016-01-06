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

#ifndef __JMETHODCALLER_H_
#define __JMETHODCALLER_H_

#include <string>
#include <vector>

#include "common.h"
#include "StringUtils.h"
#include "SunJVMDLL.h"

/**
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class JMethodCaller
{
  enum  {
    JBOOLEAN = 1,    JBYTE,    JCHAR,    JSHORT,    JINT,    JLONG,    JFLOAT,
    JDOUBLE,    JVOID,    JOBJECT
  } ;

  struct JDATATYPE
  {
    int TYPE;
    std::string CLASSNAME;
    int ARRAYLEVEL;
    std::string INTERNALNAME;

    std::string toString()
    {
      std::string modifier = "";
      for (int i=0; i<ARRAYLEVEL; i++)
	modifier += "[]";
      switch(TYPE)
	{
	case JBOOLEAN: return "boolean " + INTERNALNAME + modifier;
	case JBYTE:    return "byte " + INTERNALNAME + modifier;
	case JCHAR:    return "char " + INTERNALNAME + modifier;
	case JSHORT:   return "short " + INTERNALNAME + modifier;
	case JINT:     return "int " + INTERNALNAME + modifier;
	case JLONG:    return "long " + INTERNALNAME + modifier;
	case JFLOAT:   return "float " + INTERNALNAME + modifier;
	case JDOUBLE:  return "double " + INTERNALNAME + modifier;
	case JVOID:    return "void " + INTERNALNAME + modifier;
	case JOBJECT:  return "Class " + CLASSNAME + " " +  INTERNALNAME + modifier;
	}
      return "<UnknownJavaType>" + modifier;
    }
  };

 private:
  bool     m_static;
  std::string m_clazz;
  std::string m_methodname;
  JDATATYPE m_returntype;
  std::vector<JDATATYPE> m_arguments;

  std::string m_parsingError;
  bool m_isValid;

 public:
  JMethodCaller(const std::string& clazz, const std::string& method);
  JMethodCaller(const std::string& method);

  jvalue invokeStatic(SunJVMDLL& launcher, jvalue args[]);
  jvalue invoke(SunJVMDLL& jvm, jobject& obj, jvalue args[]);

  std::string toString();
  std::string getJavaSignature();
  
 private:
  bool parseSignature(const std::string& fqmethod);
  std::string getDatatypeSignature(const JDATATYPE& d);
  int parseType(const std::string& tok);
};



#endif
