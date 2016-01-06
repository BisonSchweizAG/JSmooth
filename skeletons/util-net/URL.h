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

#ifndef __URL_H_
#define __URL_H_

#include <string>

#include "StringUtils.h"

using namespace std;

class URL
{
 public:

  /**
   * Standard constructors...
   */
  URL(std::string url);
  URL(const URL& base);
  URL();

  /**
   * Builds a new URL based a another one and a string. The string can
   * either be a full url, or just a path. If it's just a path, the
   * protocol, host, and port are taken from the base URL parameter.
   *
   * This deals with path being expressed as relative to an URL.
   */
  URL(const URL& base, std::string url);

  /**
   * Gets the protocol (only http is managed)
   */
  std::string getProtocol() const;

  /**
   * Get the Host name
   */
  std::string getHost() const;

  /**
   * Get the port. 80 is the standard HTTP port, but it can be any
   * other port.
   */
  int getPort() const;

  /**
   * The path is the part after the hostname and the port, as in
   * protocol://hostname.with.dots:port/this/is/a/path
   */
  std::string getPath() const;

  /**
   * Recomposes a url
   */
  std::string toString() const;
  
  /**
   * Gets an URL-encoding of the string
   */
  static string encode(const string& str);

  /**
   * Gets the filename part of the path. ie. in /this/is/a/path, it
   * returns "path".
   */
  std::string getFileName() const;

 protected:
  void setup(string& url);

  std::string m_protocol;
  std::string m_host;
  int m_port;
  std::string m_path;

};

#endif
