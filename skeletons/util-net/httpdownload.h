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

#ifndef __HTTPDOWNLOAD_H_
#define __HTTPDOWNLOAD_H_

#include <string>
#include <vector>
#include <winsock2.h>
#include <map>

#include <fstream>
#include <iostream>

#include <windows.h>
#include <process.h>

#include "URL.h"
#include "downloadgui.h"
#include "HttpClient.h"
#include "WinHttpClient.h"

//
// The listener that keeps track of the download progress and update
// the progress bar. 
class HttpUpdater : public WinHttpClientListener
{
  void httpDownloadUpdate(int current, int total);
};


/**
 * Downloads a file in a separate thread and display a progress bar.
 * Returns an empty string if the download failed, of the path to a
 * temporary file if it completed with no error.
 */
std::string httpDownload(const std::string& url);

#endif
