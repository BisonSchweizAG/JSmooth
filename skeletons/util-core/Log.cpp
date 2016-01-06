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

#include "Log.h"

#include <windows.h>
#include <winbase.h>

Log::Log()
{
  m_out = 0;
}

Log::Log(const std::string& filepath)
{
  m_out = fopen(filepath.c_str(), "a");
}

Log::~Log()
{
  if (m_out != 0)
    fclose(m_out);
  m_out = 0;
}

void Log::out(const std::string& msg)
{
  if (m_out != 0)
    {
      SYSTEMTIME time;
      GetSystemTime(&time);
      char timestamp[64];
      sprintf(timestamp, "%02d:%02d.%02d", time.wHour, time.wMinute, time.wSecond);
      std::string m = std::string(timestamp) + " :" + msg;
      fprintf(m_out, "%s\n", m.c_str());
      fflush(m_out);
    }
}
