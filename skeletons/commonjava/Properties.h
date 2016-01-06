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

#ifndef __PROPERTIES_H_
#define __PROPERTIES_H_

#include <string>
#include <map>
#include "common.h"
#include "StringUtils.h"

using namespace std;

/**
 * Manages a set of properties. Properties are pairs of name/value
 * texts. The properties object is created from a char array that stores
 * properties under the form:
 *
 * <CODE>
name1=value1 <BR>
name2=value 2 is a string<BR>
name3=<BR>
name4=can be empty<BR>
   </CODE>
 * 
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class Properties
{
  map<string, string> m_data;

 public:
  /**
   * This constructor builds a Properties object based on the text
   * passed in the char array.
   *
   * @param data the array of chars
   * @param datalen the length of the array
   */
  Properties(const char *data, int datalength);

  /**
   * This default constructor builds an empty Properties object.
   */
  Properties();

  /**
   * Sets up the properties data from a char array.
   *
   * @param data the array of chars
   * @param datalen the length of the array
   */
  void setData(const char *data, int datalength);
  
  /**
   * Retrieves a value from a property name.
   *
   * @param key the name of the property to retrieve
   * @return the value associated to the name, or an empty string if the key is not defined.
   */
  string get(const string& key) const;

  bool contains(const string& key) const;

  /**
   * Adds a new property.
   *
   * @param key the name of the property
   * @param value the value associated to the property
   */
  void set(const string& key, const string& value);

  map<string, string> getDataCopy() const;

        
 private:
  string getNextToken(const char* data, int datalen, int& cursor, char stopchar);    
  std::string unescape(const string& val);
};

#endif

