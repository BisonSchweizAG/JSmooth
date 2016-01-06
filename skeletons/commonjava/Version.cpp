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

#include "Version.h"

Version::Version(std::string val)
{
    m_major = m_minor = m_sub = 0;
    parseValue(val);
}

Version::Version()
{
    m_major = m_minor = m_sub = 0;
}

void Version::parseValue(const std::string& val)
{
    std::vector<std::string> tokens = StringUtils::split(val, ".", "");

    if (tokens.size() > 0)
        m_major = StringUtils::parseInt(tokens[0]);
    
    if (tokens.size() > 1)
        m_minor =  StringUtils::parseInt(tokens[1]);
        
    if (tokens.size() > 2)
        m_sub =  StringUtils::parseInt(tokens[2]);
}

int Version::getMajor() const
{
    return m_major;
}
    
int Version::getMinor() const
    {
        return m_minor;
    }

int Version::getSubMinor() const
{
    return m_sub;
}

bool operator < (const Version& v1, const Version& v2)
{
    if ((v1.m_major == 0) && (v1.m_minor == 0) && (v1.m_sub == 0))
        return true;

    if ((v2.m_major == 0) && (v2.m_minor == 0) && (v2.m_sub == 0))
        return true;

    long v1val = (v1.m_major * 1000 * 1000) + (v1.m_minor * 1000) + v1.m_sub;
    long v2val = (v2.m_major * 1000 * 1000) + (v2.m_minor * 1000) + v2.m_sub;
    
    return v1val < v2val;
}

std::string Version::toString() const
{
    char buffer[128];
    sprintf(buffer, "%d.%d.%d", m_major, m_minor, m_sub);
    return string(buffer);
}

bool operator <= (const Version& v1, const Version& v2)
{
    if ((v1.m_major == 0) && (v1.m_minor == 0) && (v1.m_sub == 0))
        return true;

    if ((v2.m_major == 0) && (v2.m_minor == 0) && (v2.m_sub == 0))
        return true;

    long v1val = (v1.m_major * 1000 * 1000) + (v1.m_minor * 1000) + v1.m_sub;
    long v2val = (v2.m_major * 1000 * 1000) + (v2.m_minor * 1000) + v2.m_sub;
       
    return v1val <= v2val;
}

bool Version::isValid() const
{
    if ((m_major == 0) && (m_minor == 0) && (m_sub == 0))
        return false;
    return true;
}

