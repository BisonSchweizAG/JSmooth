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

#include <iostream>
#include <stdlib.h>

#include "StringUtils.h"
#include <winnls.h>
#include "FileUtils.h"
#include "Process.h"

#include "SingleInstanceManager.h"

void _debugOutput(const std::string& text)
{
  printf("%s\n", text.c_str());
}

void _debugWaitKey()
{
}

int main(int argc, char *argv[])
{
//   char buffer[256];
//   buffer[0] = 0;
//   int res = GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_SABBREVLANGNAME,
// 			  buffer, 255);

//   if (res > 0)
//     printf("result: %s\n", buffer);

  SingleInstanceManager singinst;
  if (singinst.alreadyExists())
    {
      printf("Already running...\n");
      exit(0);
    }
  else
    Sleep(50000);

  std::string exeline = "C:\\Program Files\\Java\\jdk1.5.0_11\\bin\\java.exe -version";
  string tmpfilename = FileUtils::createTempFileName(".tmp");

  Process proc(exeline, true);
  proc.setRedirect(tmpfilename);
  proc.run();
  proc.join();

  std::string voutput = FileUtils::readFile(tmpfilename);
  //  printf("GOT: %s\n", voutput.c_str());

//   std::string cmdlinetest = " \"this is my\ test\\\" \"here and then\"";
//   cmdlinetest = " \"this is my\ test here\\\" \"and\" \"then\"";
//   printf("splitting line <%s>\n", cmdlinetest.c_str());
//   std::vector<std::string> args = StringUtils::split(cmdlinetest, " \t\n\r", "\"'", false, false);
//   for (int i=0; i<args.size(); i++)
//     {
//       printf("ARG[%d]=%s\n", i, args[i].c_str(), false, false);
//     }

  //   std::string fqmethod = "void mytest1(java.lang.String test[ ] ) ";
  //   std::vector<std::string> t1 = StringUtils::split(fqmethod, " \t(,);][", "", false, true);

  //   for (std::vector<std::string>::iterator i=t1.begin(); i != t1.end(); i++)
  //     printf("TOK: %s\n" , i->c_str());

  //   std::string readf = FileUtils::readFile("Log.h");
  //   printf("READ: <<%s>>", readf.c_str());
  
}
