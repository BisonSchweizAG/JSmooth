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

#include "Properties.h"

Properties::Properties(const char* data, int datalen)
{
    setData(data, datalen);
}


Properties::Properties()
{

}

void Properties::setData(const char* data, int datalen)
{
    int cursor = 0;
    DEBUG("Extracting properties from resource:");

    while (cursor < datalen)
    {
        string key = getNextToken(data, datalen, cursor, '=');
        string value = getNextToken(data, datalen, cursor, '\n');
        m_data[key] = unescape(StringUtils::trim(value));
        
        DEBUG("  - <" + key + "> == <" + m_data[key] + ">");
    }
}

string Properties::getNextToken(const char* data, int datalen, int& cursor, char stopchar)
{
    string buf = "";
    while (cursor<datalen)
    {
        if (data[cursor] == stopchar)
        {
                cursor++;
                return buf;
        }
        buf += data[cursor];
        cursor++;
    }
    return buf;
}

string Properties::get(const string& key) const
{
    map<string, string>::const_iterator i = m_data.find(key);
    if (i == m_data.end())
        return "";
    return i->second;
}

void Properties::set(const string& key, const string& value)
{
    m_data[key] = value;
}

bool Properties::contains(const string& key) const
{
    map<string, string>::const_iterator i = m_data.find(key);
    if (i == m_data.end())
        return false;
    return true;
}

string Properties::unescape(const string& val)
{
    string result;
    for (string::const_iterator i=val.begin(); i != val.end(); i++)
    {
        switch (*i)
        {
                case '\\':
                {
                                i++;
                                if (i != val.end())
                                {
                                      switch(*i)
                                      {
                                         case 'n':
                                             result += '\n';
                                             break;
                                         case 't':
                                             result += '\t';
                                             break;
                                         case 'r':
                                             result += '\r';
                                             break;
                                         case '\\':
                                             result += '\\';
                                             break;
                                      }
                                }
                }
                 break;
                 
                default:
                {
                                result  += *i;
                                break;
                }
        }
    }
    return result;
}

map<string, string> Properties::getDataCopy() const
{
  return m_data;
}
