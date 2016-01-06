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

#include "execcab.h"


LRESULT WINAPI CabinetCallback ( IN PVOID pMyInstallData,
                     IN UINT Notification,
                     IN UINT Param1,
                     IN UINT Param2 )
{
  const char *pathdir = (char*)pMyInstallData;
  LRESULT lRetVal = NO_ERROR;
  //  TCHAR szTarget[MAX_PATH];
  FILE_IN_CABINET_INFO *pInfo = NULL;
  FILEPATHS *pFilePaths = NULL;

  //  lstrcpy(szTarget,g_szTargetPath);

  std::string fullpath(pathdir);

  switch(Notification)
    {
    case SPFILENOTIFY_FILEINCABINET:

      pInfo = (FILE_IN_CABINET_INFO *)Param1;
      fullpath = FileUtils::concFile(fullpath, pInfo->NameInCabinet);
      // lstrcat(szTarget, pInfo->NameInCabinet);
      lstrcpy(pInfo->FullTargetName, fullpath.c_str());
      printf("request extraction of %s\n", pInfo->FullTargetName);
      lRetVal = FILEOP_DOIT;  // Extract the file.
      break;

    case SPFILENOTIFY_FILEEXTRACTED:
      pFilePaths = (FILEPATHS *)Param1;
      printf ( "Extracted %s\n",pFilePaths->Target);
      FileUtils::deleteOnReboot(string(pFilePaths->Target));
      lRetVal = NO_ERROR;
      break;

    case SPFILENOTIFY_NEEDNEWCABINET: // Unexpected.
      lRetVal = NO_ERROR;
      break;
    }

  return lRetVal;
}

std::string cab_extract(std::string cabfilepath)
{
  string tmpdir = FileUtils::createTempFileName("JSMOOTHDIR");
  CreateDirectoryA(tmpdir.c_str(), 0);
  printf("Created %s\n", tmpdir.c_str());


  if (!SetupIterateCabinet(cabfilepath.c_str(), 0, (PSP_FILE_CALLBACK)CabinetCallback, (void *)tmpdir.c_str()))
    {
//       DEBUG("Error extract cabinet)
//       FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER |
// 		     FORMAT_MESSAGE_FROM_SYSTEM |
// 		     FORMAT_MESSAGE_IGNORE_INSERTS, NULL,
// 		     GetLastError(), MAKELANGID(LANG_NEUTRAL,
// 						SUBLANG_DEFAULT), (LPTSTR) &lpMsgBuf, 0, NULL );

//       MessageBox( NULL,(LPTSTR) lpMsgBuf,
// 		  "SetupIterateCabinet() Error :",
// 		  MB_OK | MB_ICONEXCLAMATION | MB_TOPMOST);

      printf("Error extracting the cab %s\n", cabfilepath.c_str());
    }
  return tmpdir;
}


// std::string unpack_cab(std::string cabpath)
// {
//   struct mscab_decompressor *cabd;
//   struct mscabd_cabinet *cab;
//   struct mscabd_file *file;
//   int test;

//   string tmpdir = FileUtils::createTempFileName("JSMOOTHDIR");
//   CreateDirectoryA(tmpdir.c_str(), 0);

//   printf("Created %s\n", tmpdir.c_str());

//   MSPACK_SYS_SELFTEST(test);
//   if (test != MSPACK_ERR_OK) exit(0);

//   if ((cabd = mspack_create_cab_decompressor(NULL))) {
//     if ((cab = cabd->open(cabd, (char*)cabpath.c_str()))) {
//       for (file = cab->files; file; file = file->next) {
// 	string filepath = FileUtils::concFile(tmpdir, file->filename);
//         printf("%s: %d\n", file->filename, file->length);
// 	cabd->extract(cabd, file, (char*)filepath.c_str());
//       }
//       cabd->close(cabd, cab);
//     }
//     mspack_destroy_cab_decompressor(cabd);
//   }
//   return tmpdir;
// }



bool exec_cabdir(string path)
{
  vector<string> infs = FileUtils::recursiveSearch(path, "*.inf");
  for (vector<string>::iterator i=infs.begin(); i!=infs.end(); i++)
    {
      printf("FOUND INF: %s\n", i->c_str()); 
      std::ifstream fileinf(i->c_str());
      string line;

      while (!fileinf.eof())
	{
	  std::getline(fileinf, line);
	  //  cout << line << endl;
	  int sep;
	  if ( (sep = line.find('=')) != string::npos)
	    {
	      string key = line.substr(0, sep);
	      string value = line.substr(sep+1);
	      if (key == "run")
		{
		  cout << "FOUND [" << key << "]=[" << value << "]" << endl;
		  string cmd = StringUtils::replace(value, "%EXTRACT_DIR%", path);

		  cout << "EXEC: " << cmd << endl;

		  execute(cmd, true);
		}
	    }
	}
      fileinf.close();
    }
}


bool exec_cab(const std::string& file)
{
  //  std::string path = unpack_cab(file);
  std::string path = cab_extract(file);
  return exec_cabdir(path);
}
