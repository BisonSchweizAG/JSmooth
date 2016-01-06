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

#include "JMethodCaller.h"

JMethodCaller::JMethodCaller(const std::string& clazz, const std::string& method) : m_isValid(false), m_static(false)
{
  m_clazz = clazz;
  parseSignature(method);
}

JMethodCaller::JMethodCaller(const std::string& method)
{
  parseSignature(method);
}

jvalue JMethodCaller::invoke(SunJVMDLL& jvm, jobject& obj, jvalue args[])
{
  jvalue res;
  res.l = 0;

  DEBUG("invoking " + m_clazz + ": " + m_methodname + ", " + getJavaSignature());

  jclass cl = jvm.findClass(m_clazz);

  if (cl == 0)
    {
      DEBUG("Can't find class " + m_clazz + " !!");
      return res;
    }

  jmethodID m = jvm.findMethod(cl, m_methodname, getJavaSignature(), false);
  if (m == 0)
    {
      DEBUG("Can't find the method " + m_methodname + " / " + getJavaSignature());
      return res;
    }

  switch (m_returntype.TYPE)
    {
    case JVOID:
      jvm.invokeVoid(obj, m, args);
      break;
    case JBOOLEAN:
      res.z = jvm.invokeBoolean(obj, m, args);
      break;
    case JBYTE:
      res.b = jvm.invokeByte(obj, m, args);
      break;
    case JCHAR:
      res.c = jvm.invokeChar(obj, m, args);
      break;
    case JSHORT:
      res.s = jvm.invokeShort(obj, m, args);
      break;
    case JINT:
      res.i = jvm.invokeInt(obj, m, args);
      break;
    case JLONG:
      res.j = jvm.invokeLong(obj, m, args);
      break;
    case JFLOAT:
      res.f = jvm.invokeFloat(obj, m, args);
      break;
    case JDOUBLE:
      res.d = jvm.invokeDouble(obj, m, args);
      break;
    case JOBJECT:
      res.l = jvm.invokeObject(obj, m, args);
      break;
    }

  return res;
}


jvalue JMethodCaller::invokeStatic(SunJVMDLL& launcher, jvalue args[])
{
  jvalue res;
  jclass cl = launcher.findClass(m_clazz);
  jmethodID m = launcher.findMethod(cl, m_methodname, getJavaSignature(), true);

  DEBUG("Invoke Static " + m_methodname + " " + getJavaSignature());

  switch (m_returntype.TYPE)
    {
    case JVOID:
      launcher.invokeVoidStatic(cl, m, args);
      break;
    case JBOOLEAN:
      res.z = launcher.invokeBooleanStatic(cl, m, args);
      break;
    case JBYTE:
      res.b = launcher.invokeByteStatic(cl, m, args);
      break;
    case JCHAR:
      res.c = launcher.invokeCharStatic(cl, m, args);
      break;
    case JSHORT:
      res.s = launcher.invokeShortStatic(cl, m, args);
      break;
    case JINT:
      res.i = launcher.invokeIntStatic(cl, m, args);
      break;
    case JLONG:
      res.j = launcher.invokeLongStatic(cl, m, args);
      break;
    case JOBJECT:
      res.l = launcher.invokeObjectStatic(cl, m, args);
      break;
    }

  return res;
}

std::string JMethodCaller::getJavaSignature()
{
  std::string res = "(";
  for (std::vector<JDATATYPE>::iterator i=m_arguments.begin(); i != m_arguments.end(); i++)
    {
      res += StringUtils::replace(getDatatypeSignature(*i), ".", "/");
    }
  res += ")";
  res += getDatatypeSignature(m_returntype);
  return res;
}

std::string JMethodCaller::getDatatypeSignature(const JDATATYPE& d)
{
  std::string res;

  for (int j=0; j<d.ARRAYLEVEL; j++)
    res += "[";
    
  switch(d.TYPE)
    {
    case JBOOLEAN:
      res += "Z";
      break;
    case JBYTE:
      res += "B";
      break;
    case JCHAR:
      res += "C";
      break;
    case JSHORT:
      res += "S";
      break;
    case JINT:
      res += "I";
      break;
    case JLONG:
      res += "J";
      break;
    case JFLOAT:
      res += "F";
      break;
    case JDOUBLE:
      res += "D";
      break;
    case JVOID:
      res += "V";
      break;
    case JOBJECT:
      res += "L" + d.CLASSNAME + ";";
      break;
    }  
  return res;
}

std::string JMethodCaller::toString()
{
  if (m_parsingError != "")
    return m_parsingError;

  std::string res = "";
  res += "Returntype(" + m_returntype.toString() + ") ";
  res += "methodname(" + m_methodname + ") ";
  res += "argumentlist(";
  for (std::vector<JDATATYPE>::iterator i=m_arguments.begin(); i != m_arguments.end(); i++)
    {
      res += "::" + i->toString();
    }

  res += ") ";
  return res;
}

bool JMethodCaller::parseSignature(const std::string& fqmethod)
{
  JDATATYPE emptyData;
  emptyData.ARRAYLEVEL = 0;
  emptyData.CLASSNAME = "";
  emptyData.TYPE = -1;
  emptyData.INTERNALNAME = "";

  //
  // Clear data
  //
  m_returntype = emptyData;
  m_arguments.clear();
  m_methodname = "";
  m_parsingError = "";
  m_isValid = false;

  std::vector<std::string> tokens = StringUtils::split(fqmethod, " \t(,);[]", "", false, true);
  
  // states of the automata:
  //  0: expecting the return type (goes to 0 or 1)
  //  1: expecting the method name or a modifier (goes to 1 or 2)
  //  2: expecting a ( (goes to 3)
  //  3: expecting a type (goes to 4)
  //  4: expecting a modifier, a name, a ",", or a ")" (goes to 3 or 4 or 9)
  //  9: end of the automata (end)
  int state = 0;
  JDATATYPE curData;

  //  DEBUG("Start parsing " + fqmethod);

  for (std::vector<std::string>::iterator i=tokens.begin(); i != tokens.end(); i++)
    {
      while ( (i != tokens.end() )
	      && ((*i == " ") || (*i == "\t")))
	i++;

      if (i == tokens.end())
	{
	  if (state == 9)
	    {
	      m_isValid = true;
	      return true;
	    }
	  m_parsingError = "Uncomplete parsing";
	  return false;
	}

      //      DEBUG("Current non-space token: " + *i + ", state: " + StringUtils::toString(state));

      string tok = *i;
      switch (state)
	{
	  //  0: expecting the return type
	case 0:
	  {
	    int type = parseType(tok);
	    curData = emptyData;
	    if (type == JOBJECT)
	      {
		curData.TYPE = JOBJECT;
		curData.CLASSNAME = tok;
	      }
	    else
	      curData.TYPE = type;

	    //	    DEBUG("Return type found : " + StringUtils::toString(curData.TYPE));
	    state = 1;
	  }
	  break;

	case 1:
	  if (tok == "[")
	    {
	      while ( (i != tokens.end()) && (*i != "]") )
		i++;
	      if (i == tokens.end())
		{
		  m_parsingError = "Unfinished array modifier";
		  return false;
		}
	      curData.ARRAYLEVEL++;
	      //	      DEBUG("Return type modifier: array+1");
	    }
	  else
	    {
	      m_returntype = curData;
	      curData = emptyData;
	      m_methodname = tok;
	      //	      DEBUG("Found method name: " + tok);
	      state = 2;
	    }
	  break;

	case 2:
	  if (tok == "(")
	    state = 3;
	  else
	    {
	      m_parsingError = "Expecting a (, got a " + tok;
	      return false;
	    }
	  //	  DEBUG("Now parsing arguments...");
	  break;

	case 3:
	    {
	      int type = parseType(tok);	  
	      //	      DEBUG("Argument type parsed: " + StringUtils::toString(type));
	      curData = emptyData;
	      if (type == JOBJECT)
		{
		  curData.TYPE = JOBJECT;
		  curData.CLASSNAME = tok;
		}
	      else
		curData.TYPE = type;
	      state = 4;
	    }
	  break;
      
	case 4:
	  if (tok == ")")
	    {	      
	      //	      DEBUG("End of argument list");
	      m_arguments.push_back(curData);
	      state = 9;
	    }
	  else if (tok == ",")
	    {
	      m_arguments.push_back(curData);
	      state = 3;
	      //	      DEBUG("New argument starting");
	    }
	  else if (tok == "[")
	    {
	      //	      DEBUG("Array started, looking for end");
	      while ( (i != tokens.end()) && (*i != "]") )
		{
		  //		  DEBUG("Array-skipping " + *i);
		  i++;
		}

	      if (i == tokens.end())
		{
		  m_parsingError = "Unfinished array modifier";
		  //		  DEBUG(m_parsingError);
		  return false;
		}
	      else 
		i++;
	      curData.ARRAYLEVEL++;
	      //	      DEBUG("Parsed array modifier");
	    }
	  else
	    {
	      if (curData.INTERNALNAME == "")
		curData.INTERNALNAME = tok;
	      else
		{
		  m_parsingError = "Unexpected token " + tok;
		  return false;
		}
	    }
	  break;

	case 9:
	  //	  DEBUG("Completed successfully");
	  m_isValid = true;
	  return true;

	default:
	  DEBUG("ERROR, unknown state " + StringUtils::toString(state));
	}

      //      DEBUG("New state: " + StringUtils::toString(state));
    }

  if (state == 9)
    {
      //      DEBUG("Parsed correctly");
      m_isValid = true;
      return true;
    }

  //  DEBUG("INCORRECT STATE");
  return false;
}


int JMethodCaller::parseType(const std::string& tok)
{
  if (tok == "boolean")
    return JBOOLEAN;
  else if (tok == "byte")
    return JBYTE;
  else if (tok == "char")
    return JCHAR;
  else if (tok == "short")
    return JSHORT;
  else if (tok == "int")
    return JINT;
  else if (tok == "long")
    return JLONG;
  else if (tok == "double")
    return JDOUBLE;
  else if (tok == "void")
    return JVOID;
  else 
    return JOBJECT;
}
