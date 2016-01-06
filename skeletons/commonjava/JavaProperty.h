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

#ifndef __JAVAPROPERTY_H_
#define __JAVAPROPERTY_H_

#include <string>

using namespace std;

/**
 * Holds a Java Property. A Java Property is a name/value pair that is
 * passed to the Java Virtual Machine, and can be used as a kind of
 * environment variable. The java properties are usually passed to the
 * JVM under the form "-Dname=value", but the form is JVM-specific.
 *
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class JavaProperty
{
  string m_name;
  string m_value;

 public:
  /**
   * The default constructor. The name and value strings are both
   * empty.
   */
  JavaProperty();

  /**
   * This constructor sets up the name/value pair with the values
   * passed as parameter.
   *
   * @param name the name of the property
   * @param value the value of the property
   */
  JavaProperty(const string& name, const string& value);
    
  /**
   * Returns the name of the property
   *
   * @return the name
   */
  const string& getName() const;    

  /**
   * Returns the value of the property
   *
   * @return the value
   */
  const string& getValue() const;
};

#endif

