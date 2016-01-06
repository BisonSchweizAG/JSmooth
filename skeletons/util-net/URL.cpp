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

#include "URL.h"

URL::URL()
{
}

URL::URL(std::string url)
{
  setup(url);
}

URL::URL(const URL& base, std::string url)
{
  int protocolpos = url.find("://");
  if (protocolpos != string::npos)
    {
      setup(url);
    }
  else
    {
      this->m_protocol = base.m_protocol;
      this->m_host = base.m_host;
      this->m_port = base.m_port;
      if (url[0]=='/')
	this->m_path = url.substr(1);
      else
	this->m_path = url;
    }
}

URL::URL(const URL& base)
{
  this->m_protocol = base.m_protocol;
  this->m_host = base.m_host;
  this->m_port = base.m_port;
  this->m_path = base.m_path;
}
 
void URL::setup(string& url)
{
  // set up the default
  m_protocol = "http";
  m_port = -1;

  //
  // First, get the protocol, if any
  int protocolpos = url.find("://");
  if (protocolpos != string::npos)
    {
      m_protocol = url.substr(0, protocolpos);
      url = url.substr(protocolpos+3);
    }

  //
  // Then get the host part, if any
  int pos = url.find(string("/"),0);
  if (pos > 0)
    {
      m_path = url.substr(pos+1);
      m_host = url.substr(0, pos);

      // is there a port number to take care of ?
      int port = url.find(":");
      if (port > 0)
	{
	  m_port = StringUtils::parseInt(m_host.substr(port+1));
	  m_host = url.substr(0, port);
	}
    }
  else
    m_host = url;
}

std::string URL::toString() const
{
  std::string res;
  if (m_protocol == "")
    res = "http";
  else 
    res = m_protocol;

  res += "://" + m_host;
  if (m_port != -1)
    res += ":" + StringUtils::toString(m_port);
  res += "/" + m_path;
  return res;
}


std::string URL::getProtocol() const
{
  return m_protocol;
}

std::string URL::getHost() const
{
  return m_host;
}

int URL::getPort() const
{
  return m_port;
}

std::string URL::getPath() const
{
  return "/" + m_path;
}

string URL::encode(const string& str)
{
  string result = "";
  char hexbuf[4];

  for (int i=0; i<str.length(); i++)
    {
      char c = str[i];
      if ( ((c>= '0') && (c<='9'))
	   || ((c>= 'a') && (c<='z'))
	   || ((c>= 'A') && (c<='Z'))
	   || (c == '/') || (c == '.') || (c == '%') || (c == '-') )
	result += c;
      else
	{
	  sprintf(hexbuf, "%02x", (int)c);
	  result += "%";
	  result += hexbuf;
	}	
    }

  return result;
}

std::string URL::getFileName() const
{
  string fname = m_path;

  int pos = m_path.rfind('/');
  if (pos != std::string::npos)
    fname = m_path.substr(pos+1);

  pos = fname.find("?");
  if (pos != std::string::npos)
    fname = fname.substr(0, pos);

  pos = fname.find("%3f");
  if (pos != std::string::npos)
    fname = fname.substr(0, pos);

  pos = fname.find("%3F");
  if (pos != std::string::npos)
    fname = fname.substr(0, pos);

  return fname;
}
