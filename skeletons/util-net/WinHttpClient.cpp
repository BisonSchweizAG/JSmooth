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

#include "WinHttpClient.h"

#include "StringUtils.h"

WinHttpClient::WinHttpClient()
{
  m_connection = InternetOpen("JSmooth wrapper", 
			      INTERNET_OPEN_TYPE_PRECONFIG,
			      NULL, NULL,
			      0); // INTERNET_FLAG_ASYNC);
  printf("Got connection: %d\n", m_connection);
  m_file = NULL;
  m_contentlength = -1;
  m_cancelled = false;
}

WinHttpClient::~WinHttpClient()
{
  if (m_file != NULL)
    InternetCloseHandle(m_file);
  if (m_connection != NULL)
    InternetCloseHandle(m_connection);
}

bool WinHttpClient::sendGet()
{
  if (m_connection != NULL)
    {
      m_file = InternetOpenUrl(m_connection, 
			      m_url.toString().c_str(),
			       NULL,
			       0,
			       0, //INTERNET_FLAG_PASSIVE,
			       0);

      if (m_file != NULL)
	{
	  std::string protocol = StringUtils::toLowerCase(m_url.getProtocol());
	  m_contentlength = -1;
	  if (protocol == "http")
	    {
	      DWORD size;
	      DWORD sizesize = sizeof(DWORD);
	      if (HttpQueryInfo(m_file, HTTP_QUERY_CONTENT_LENGTH | HTTP_QUERY_FLAG_NUMBER, &size, &sizesize, NULL))
		m_contentlength = size;
	    }
	  else if (protocol == "ftp")
	    {
	      DWORD size;
	      m_contentlength = FtpGetFileSize(m_file, &size);
	    }
	}
    }
      
  return m_file != NULL;
}


bool WinHttpClient::saveResponseBody()
{
  if (m_file == NULL)
    return false;

  //
  // Create a temporary filename
  string filebase = m_url.getFileName();
  string extension = FileUtils::getFileExtension(filebase);
  string filepath = FileUtils::createTempFileName(".httpdl."+extension);
  m_receivedFilePath = filepath;

  ofstream output(filepath.c_str(), ios::out | ios::binary);

  char buffer[1024];
  DWORD readbytes = 0;
  bool result;
  int currentlength = 0;

  do {
    result = InternetReadFile(m_file, buffer, 1023, &readbytes);

    if (readbytes > 0)
      {
	output.write(buffer, readbytes);
	currentlength += readbytes;
	updateListeners(currentlength, m_contentlength);
      }

  } while (result && (readbytes > 0) && (m_cancelled == false));

  output.close();

  if (m_cancelled)
    {
      DeleteFile(filepath.c_str());
      return false;
    }

  //
  // Assume that if we downloaded nothing, something went wrong...
  //
  if (currentlength <= 0)
    return false;

  return true;
}

void WinHttpClient::addListener(WinHttpClientListener& listener)
{
  m_listeners.push_back(&listener);
}

void WinHttpClient::updateListeners(int current, int total)
{
  for (vector<WinHttpClientListener*>::iterator i = m_listeners.begin(); i != m_listeners.end(); i++)
    {
      (*i)->httpDownloadUpdate(current, total);
    }
}

bool WinHttpClient::cancel()
{
  m_cancelled = true;
}

bool WinHttpClient::isCancelled()
{
  return m_cancelled;
}


void WinHttpClient::setURL(const URL& url)
{
  m_url = url;
}

std::string WinHttpClient::getTemporaryFile()
{
  return m_receivedFilePath;
}
