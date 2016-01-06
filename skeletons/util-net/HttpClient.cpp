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

#include "httpclient.h"

HttpClient::HttpClient()
{

  WSADATA inidat;
  if (WSAStartup(MAKEWORD(1, 1), &inidat) != 0)
    {
      exit(99); // should throw an exception
    }

  m_socket = 0;
  m_buffersize = 1024;
  m_inputbuffer = new char[m_buffersize+1];
  m_inputcursor = 0;
  m_inputbufferlen = 0;
  m_chunkedTransfer = false;
  m_contentlength = -1;
  m_currentlength = 0;
  m_cancelled = false;

}

bool HttpClient::connectTo(const URL& url)
{
  if (m_socket != 0)
    {
      closesocket(m_socket);
      m_socket = 0;
    }      

  m_socket = socket(AF_INET, SOCK_STREAM, 0);
  if (m_socket == INVALID_SOCKET)
    {
      return false;
    }

  // get ip of the host
  struct  hostent *hp;
  hp = gethostbyname(url.getHost().c_str());

  if (hp == 0)
    {
      m_connected = false;
      return false;
    }

  //
  // build the sockaddr structure

  memset(&m_server,0,sizeof(struct sockaddr_in));
  m_server.sin_family = AF_INET;
  
  //
  // If no port if defined in the URL, use 80
  if (url.getPort() <= 0)
    m_server.sin_port = htons(80);
  else
    m_server.sin_port = htons(url.getPort());
  memcpy(&m_server.sin_addr,hp->h_addr,hp->h_length);
  
  //
  // connect !
  int res = connect(m_socket, (struct sockaddr*)&m_server, sizeof(m_server));
  if (res == 0)
    m_connected = true;
  else
    m_connected = false;

  return m_connected;
}

bool HttpClient::sendGet()
{
  map<string,string> header;
  connectTo(m_url);

  m_chunkedTransfer = false;
  //
  // No keep-alive is the default
  header["Connection"] = "close";
  
  std::string data = "GET " + URL::encode(m_url.getPath()) + " ";
  data += "HTTP/1.1\r\nHost: " + m_url.getHost() + "\r\n";

  for(map<string,string>::iterator i=header.begin(); i!=header.end(); i++)
    {
      data += i->first + ": " + i->second + "\r\n";
    }
  data += "\r\n";

  send(m_socket, data.c_str(), data.size(), 0);

  //
  // Once the request is sent, we immediatly get the headers
  //
  m_headers.clear();
  string line;
  
  if (!receiveLine(line))
    return false;

  while (receiveLine(line))
    {
      if (line.size() == 0)
	break;
      
      int sep = line.find(':');
      if (sep != string::npos)
	{
	  string key = StringUtils::toLowerCase(line.substr(0, sep));
	  string value = line.substr(sep+1);
	  if ((value.size()>0) && (value[0]==' '))
	    value = value.substr(1);
	  m_headers[key] = value;
	  //	  DEBUG("HEADER: " + key + ": " + value);
	}
    }

  if (m_headers.find("location") != m_headers.end())
    {
      URL relocurl(m_url, m_headers["location"]);
      m_url = relocurl;
      setURL(relocurl);
      return sendGet();
    }

  if (m_headers.find("transfer-encoding") != m_headers.end())
    {
      if (StringUtils::toLowerCase(m_headers["transfer-encoding"]) == "chunked")
	m_chunkedTransfer = true;
    }

  if (m_headers.find("content-length") != m_headers.end())
    m_contentlength = StringUtils::parseInt(m_headers["content-length"]);
  
  return true;
}

void HttpClient::refillInputBuffer()
{
  if (!m_connected)
    {
      m_inputbufferlen = 0;
      return;
    }

  m_inputbufferlen = recv(m_socket, m_inputbuffer, m_buffersize, 0);

  if (m_inputbufferlen <= 0)
    m_connected = false;

  m_inputcursor = 0;
}


int HttpClient::receiveData(char* buffer, int length)
{
  for (int i=0; i<length; i++)
    {
      if (m_inputcursor >= m_inputbufferlen)
	  refillInputBuffer();

      if (m_inputcursor < m_inputbufferlen)
	{
	  buffer[i] = m_inputbuffer[m_inputcursor++];
	}
      else
	{
	  return i;
	}
    }
  return length;
}


bool HttpClient::receiveChar(char& c)
{
  if (m_connected == false)
    return false;

  if (m_inputcursor >= m_inputbufferlen)
    {
      refillInputBuffer();
    }
  if (m_connected == false)
    return false;
  
  if (m_inputcursor < m_inputbufferlen)
    {
      c = m_inputbuffer[m_inputcursor++];
      return true;
    }
  else
    {
      return false;
    }
}


bool HttpClient::receiveLine(string& result)
{
  result = "";
  char c;
  while (receiveChar(c))
    {
      if (c == '\n')
	{
	  if ((result.size()>0) && (result[result.size()-1] == '\r'))
	    result = result.substr(0, result.size()-1);
	  return true;
	}
      result += c;
    }
  if (result.length() == 0)
    return false;

  return true;
}


bool HttpClient::saveResponseBody()
{
  //
  // Create a temporary filename
  string filebase = m_url.getFileName();
  string extension = FileUtils::getFileExtension(filebase);
  string filepath = FileUtils::createTempFileName(".httpdl."+extension);
  m_receivedFilePath = filepath;

  ofstream output(filepath.c_str(), ios::out | ios::binary);

  if (m_chunkedTransfer)
    {
      string lenline;
      while (receiveLine(lenline) && (!m_cancelled))
	{
	  int size = StringUtils::parseHexa(lenline);
	  char c;
	  for (int i=0; i<size; i++)
	    {
	      if (! receiveChar(c))
		break;
	      
	      output.write(&c, 1);
	      m_currentlength++;
	      
	      updateListeners(m_currentlength, m_contentlength);
	    }
	}
      updateListeners(m_contentlength, m_contentlength);
    }
  else
    {
      char buffer[129];
      int len;
      while (((len=receiveData(buffer, 128)) != 0) && (!m_cancelled))
	{
	  output.write(buffer, len);
	  m_currentlength+=len;

	  updateListeners(m_currentlength, m_contentlength);
	}
      updateListeners(m_contentlength, m_contentlength);
    }

  output.close();

  if (m_cancelled)
    {
      DeleteFile(filepath.c_str());
      return false;
    }

  //
  // Assume that if we downloaded nothing, something went wrong...
  //
  if (m_currentlength <= 0)
    return false;

  return true;
}

void HttpClient::addListener(HttpClientListener& listener)
{
  m_listeners.push_back(&listener);
}

void HttpClient::updateListeners(int current, int total)
{
  for (vector<HttpClientListener*>::iterator i = m_listeners.begin(); i != m_listeners.end(); i++)
    {
      (*i)->httpDownloadUpdate(current, total);
    }
}

bool HttpClient::cancel()
{
  m_cancelled = true;
}

bool HttpClient::isCancelled()
{
  return m_cancelled;
}


void HttpClient::setURL(const URL& url)
{
  m_url = url;
}

std::string HttpClient::getContentType()
{
  if (m_headers.find("content-type") != m_headers.end())
    {
      return m_headers["content-type"];
    }
  return "";
}

std::string HttpClient::getTemporaryFile()
{
  return m_receivedFilePath;
}
