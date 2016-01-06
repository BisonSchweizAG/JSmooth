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

#ifndef __WINHTTPCLIENT_H_
#define __WINHTTPCLIENT_H_

#include <string>
#include <vector>
#include <winsock2.h>
#include <wininet.h>

#include <fstream>
#include <iostream>

using namespace std;

#include "FileUtils.h"
#include "URL.h"


/**
 * This listener class provides a way to get the download
 * status. Implements this class and use HttpClient::addListener() to
 * keep track of the status.
 */
class WinHttpClientListener
{
 public:
  virtual void httpDownloadUpdate(int current, int total) = 0;
};


/**
 * A simplistic HTTP client class that manages chunked content and can
 * only save GET request on disk. 
 *
 * To use it, one should typicall call the following methods:
 *
 *  HttpClient client; // creates an instance
 *  client.addListener(updater); // to add a listener, if any
 *  client.setURL(anUrl);
 *  client.sendGet(); // Send the request
 *  bool result = httpClient.saveResponseBody(); // Did it work ?
 *  string file = httpClient.getTemporaryFile(); // Get the filepath
 */
class WinHttpClient
{
 public:
  WinHttpClient();
  ~WinHttpClient();

  void addListener(WinHttpClientListener& listener);
  void setURL(const URL& url);

  bool sendGet();
  bool saveResponseBody();

  bool cancel();
  bool isCancelled();

  std::string getTemporaryFile();

 protected:
  HINTERNET m_connection;
  HINTERNET m_file;

  int    m_contentlength;
  bool   m_cancelled;
  URL    m_url;
  std::string m_receivedFilePath;

  vector<WinHttpClientListener*> m_listeners;
  void updateListeners(int current, int total);

};

#endif
