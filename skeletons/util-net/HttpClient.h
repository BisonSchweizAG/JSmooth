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

#ifndef __HTTPCLIENT_H_
#define __HTTPCLIENT_H_

#include <string>
#include <vector>
#include <winsock2.h>
#include <map>

#include <fstream>
#include <iostream>

using namespace std;

#include "FileUtils.h"

#include "URL.h"

/**
 * A simple HTTP Client
 * 
 * @author Rodrigo Reyes <reyes@charabia.net>
 */


/**
 * This listener class provides a way to get the download
 * status. Implements this class and use HttpClient::addListener() to
 * keep track of the status.
 */
class HttpClientListener
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
class HttpClient
{
 public:
  HttpClient();
  void addListener(HttpClientListener& listener);

  void setURL(const URL& url);
  std::string getContentType();

  bool sendGet();
  bool saveResponseBody();

  bool cancel();
  bool isCancelled();

  std::string getTemporaryFile();

 protected:

  int    m_socket;
  bool   m_connected;
  char*  m_inputbuffer;
  int    m_inputbufferlen;
  int    m_buffersize;
  struct sockaddr_in m_server;
  map<string,string> m_headers;
  bool   m_chunkedTransfer;
  int    m_inputcursor;
  int    m_contentlength;
  int    m_currentlength;
  bool   m_cancelled;
  URL    m_url;
  std::string m_receivedFilePath;

  vector<HttpClientListener*> m_listeners;
  void updateListeners(int current, int total);

  void refillInputBuffer();
  bool connectTo(const URL& url);

  bool receiveChar(char& c);
  int receiveData(char* buffer, int length);
  bool receiveLine(string& result);


};

#endif
