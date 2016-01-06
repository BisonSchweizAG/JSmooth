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

#include "DebugConsole.h"
#include <windows.h>

DebugConsole::DebugConsole(const std::string& title)
{
    if (AllocConsole() == TRUE)
    {
        m_out = GetStdHandle(STD_OUTPUT_HANDLE);
        m_in = GetStdHandle(STD_INPUT_HANDLE);
        
    }
}

DebugConsole::DebugConsole()
{
    if (AllocConsole() == TRUE)
    {
        m_out = GetStdHandle(STD_OUTPUT_HANDLE);
        m_in = GetStdHandle(STD_INPUT_HANDLE);
    }
//    m_out = CreateConsoleScreenBuffer(GENERIC_READ, FILE_SHARE_READ, NULL,CONSOLE_TEXTMODE_BUFFER, NULL);
}
    
void DebugConsole::writeline(const std::string& str)
{
    DWORD reallywritten = 0;
    WriteConsole(m_out, str.c_str(), str.length(), &reallywritten, NULL);
    WriteConsole(m_out, "\r\n", 2, &reallywritten, NULL);
}

void DebugConsole::waitKey()
{
    INPUT_RECORD record;
    DWORD rr;
    writeline("--PRESS A KEY--");
    SetConsoleMode(m_in,ENABLE_PROCESSED_INPUT); 
    ReadConsole(m_in, &record, 1, &rr, NULL);
}

