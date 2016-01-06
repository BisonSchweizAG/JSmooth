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

#include "JniSmooth.h"

#include "common.h"
#include "StringUtils.h"
#include "FileUtils.h"
#include "jni.h"
#include "JMethodCaller.h"

SunJVMDLL* jnismooth_dll = 0;

JNIEXPORT jstring JNICALL testString (JNIEnv *env, jobject obj, jstring s)
{
  jboolean copy = true;
  const char* str = jnismooth_dll->env()->GetStringUTFChars(s, &copy);
  std::string result = StringUtils::toLowerCase(str);
  jnismooth_dll->env()->ReleaseStringUTFChars(s, str);
  return jnismooth_dll->newUTFString(result);
}

JNIEXPORT jstring JNICALL jnm_getExecutablePath(JNIEnv *env, jobject obj)
{
  return jnismooth_dll->newUTFString(FileUtils::getExecutablePath());
}

JNIEXPORT jstring JNICALL jnm_getExecutableName(JNIEnv *env, jobject obj)
{
  return jnismooth_dll->newUTFString(FileUtils::getExecutableFileName());
}

JNIEXPORT jboolean JNICALL jnm_deleteFileOnReboot(JNIEnv *env, jobject obj, jstring s)
{
  jboolean copy = true;
  const char* str = jnismooth_dll->env()->GetStringUTFChars(s, &copy);
  std::string filename = str;
  jnismooth_dll->env()->ReleaseStringUTFChars(s, str);

  if (!FileUtils::fileExists(filename))
    {
      filename = StringUtils::replace(filename, "/", "\\");
      if (!FileUtils::fileExists(filename))
	{
	  return JNI_FALSE;
	}
    }
  
  FileUtils::deleteOnReboot(filename);

  return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL jnm_exitWindows(JNIEnv *env, jobject obj, jint s)
{

  DWORD dwVersion = GetVersion();
  if ( dwVersion < 0x80000000)
    {
      // Windows NT4/2000/XP
      HANDLE hToken;
      LUID tmpLuid;
      
      HANDLE handleProcess=GetCurrentProcess();
      
      if (!OpenProcessToken(handleProcess,TOKEN_ADJUST_PRIVILEGES|TOKEN_QUERY, &hToken))
	return JNI_FALSE;

      if (!LookupPrivilegeValue(0, SE_SHUTDOWN_NAME, &tmpLuid))
	return JNI_FALSE;

      TOKEN_PRIVILEGES NewState;
      LUID_AND_ATTRIBUTES luidattr;

      NewState.PrivilegeCount = 1;
      luidattr.Luid=tmpLuid;
      luidattr.Attributes=SE_PRIVILEGE_ENABLED;
      NewState.Privileges[0]=luidattr;

      if (!AdjustTokenPrivileges(hToken, false, &NewState, sizeof(TOKEN_PRIVILEGES), 0, 0))
	return JNI_FALSE;
    }

  if (ExitWindowsEx(s, 0))
    return JNI_TRUE;

  return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL jnm_shellexecute(JNIEnv *env, jobject obj, jstring action, jstring file, jstring parameters, jstring directory, jint showcmd)
{
  jboolean copy = true;
  const char* s_action = (action != NULL)?jnismooth_dll->env()->GetStringUTFChars(action, &copy):NULL;
  const char* s_file = (file != NULL)?jnismooth_dll->env()->GetStringUTFChars(file, &copy):NULL;
  const char* s_parameters = (parameters != NULL)?jnismooth_dll->env()->GetStringUTFChars(parameters, &copy):NULL;
  const char* s_directory = (directory != NULL)?jnismooth_dll->env()->GetStringUTFChars(directory, &copy):NULL;

  bool res = ShellExecute(NULL, s_action, s_file, s_parameters, s_directory, showcmd);

  if (s_action != NULL)
    jnismooth_dll->env()->ReleaseStringUTFChars(action, s_action);
  if (s_file != NULL)
    jnismooth_dll->env()->ReleaseStringUTFChars(file, s_file);
  if (s_parameters != NULL)
    jnismooth_dll->env()->ReleaseStringUTFChars(parameters, s_parameters);
  if (s_directory != NULL)
    jnismooth_dll->env()->ReleaseStringUTFChars(directory, s_directory);

  return res?JNI_TRUE:JNI_FALSE;
}


JNIEXPORT jobject JNICALL jnm_getdriveinfo(JNIEnv *env, jobject obj, jobject file)
{
  jclass dic = jnismooth_dll->findClass("jsmooth.DriveInfo");
  if (dic == 0)
    {
      return NULL;
    }

  jmethodID construc = jnismooth_dll->findMethod(dic, "<init>", "()V", false);
  if (construc == 0)
    {
      return NULL;
    }

  JMethodCaller canonicalcaller("java.io.File", "java.lang.String getCanonicalPath()");
  jvalue vals[0];

  jvalue canonicalval = canonicalcaller.invoke(*jnismooth_dll, file, vals);
  jstring jcanstr = (jstring)canonicalval.l;

  jboolean copy = true;
  const char* str = jnismooth_dll->env()->GetStringUTFChars(jcanstr, &copy);
  std::string canonicalfile = str;
  jnismooth_dll->env()->ReleaseStringUTFChars(jcanstr, str);

  //  int driveType = GetDriveType();
  jobject driveinfo = jnismooth_dll->env()->NewObject(dic, construc);
  if ((canonicalfile.length()>1) && (canonicalfile[1] == ':'))
    {
      std::string driveletter = canonicalfile[0] + std::string(":\\");
      int drivetype = GetDriveType(driveletter.c_str());
      jnismooth_dll->setIntField(dic, driveinfo, "m_driveType", drivetype);

      void * pGetDiskFreeSpaceEx = (void*)GetProcAddress(GetModuleHandle("kernel32.dll"), "GetDiskFreeSpaceExA");
      long freeBytes = 0, totalBytes = -1, totalFreeBytes = 0;

      if ((pGetDiskFreeSpaceEx != 0) && (drivetype > 1))
	{
	  unsigned __int64 lpFreeBytesAvailable, lpTotalNumberOfBytes, lpTotalNumberOfFreeBytes;
	  if (GetDiskFreeSpaceEx(driveletter.c_str(), (_ULARGE_INTEGER*)&lpFreeBytesAvailable, (_ULARGE_INTEGER*)&lpTotalNumberOfBytes, (_ULARGE_INTEGER*)&lpTotalNumberOfFreeBytes))
	    {
	      freeBytes = lpFreeBytesAvailable;
	      totalBytes = lpTotalNumberOfBytes;
	      totalFreeBytes = lpTotalNumberOfFreeBytes;
	    }
	}
      else if (drivetype > 1)
	{
	  DWORD dwSectPerClust, dwBytesPerSect, dwFreeClusters, dwTotalClusters;
	  if (GetDiskFreeSpace(driveletter.c_str(), &dwSectPerClust, &dwBytesPerSect, &dwFreeClusters, &dwTotalClusters))
	    {
	      freeBytes = ((long)dwBytesPerSect * (long)dwSectPerClust * (long)dwFreeClusters);
	      totalBytes = ((long)dwBytesPerSect * (long)dwSectPerClust * (long)dwTotalClusters);
	      totalFreeBytes = ((long)dwBytesPerSect * (long)dwSectPerClust * (long)dwFreeClusters);
	    }
	}

      jnismooth_dll->setLongField(dic, driveinfo, "m_freeBytesForUser", freeBytes);      
      jnismooth_dll->setLongField(dic, driveinfo, "m_totalFreeBytes", totalFreeBytes);      
      jnismooth_dll->setLongField(dic, driveinfo, "m_totalBytes", totalBytes);

      
      if (drivetype > 1)
	{
	  char volumename[MAX_PATH+1], filesystemname[MAX_PATH+1];
	  DWORD serialnumber, maxcomposize, systemflags;
	  if (GetVolumeInformation( driveletter.c_str(), volumename, MAX_PATH, 
				    &serialnumber, &maxcomposize, &systemflags,
				    filesystemname, MAX_PATH))
	    {
	      jnismooth_dll->setIntField(dic, driveinfo, "m_serialNumber", serialnumber);
	      jnismooth_dll->setIntField(dic, driveinfo, "m_maxComponentSize", maxcomposize);
	      jnismooth_dll->setIntField(dic, driveinfo, "m_systemFlags", systemflags);

	      jstring jvolumename = jnismooth_dll->newUTFString(volumename);
	      jstring jfilesystemname = jnismooth_dll->newUTFString(filesystemname);
	      jnismooth_dll->setObjectField(dic, driveinfo, "m_volumeName", "java.lang.String", (jobject)jvolumename);
	      jnismooth_dll->setObjectField(dic, driveinfo, "m_fileSystemName", "java.lang.String", (jobject)jfilesystemname);
	    }	  
	}
    }
  
    //  jfieldID binding = broker->env()->GetFieldID(nat, "m_", "I");
  // broker->env()->SetStaticBooleanField(nat, binding, JNI_TRUE);

  return driveinfo;
}


void registerNativeMethods(SunJVMDLL* broker)
{
  jnismooth_dll = broker;

  jclass nat = broker->findClass("jsmooth.Native");
  if (nat != 0)
    {
      if (
	  broker->registerMethod("jsmooth.Native", "getExecutablePath", "()Ljava/lang/String;", (void*)jnm_getExecutablePath)
	  && broker->registerMethod("jsmooth.Native", "getExecutableName", "()Ljava/lang/String;", (void*)jnm_getExecutableName)
	  && broker->registerMethod("jsmooth.Native", "deleteFileOnReboot", "(Ljava/lang/String;)Z", (void*)jnm_deleteFileOnReboot)
	  && broker->registerMethod("jsmooth.Native", "exitWindows", "(I)Z", (void*)jnm_exitWindows)
	  && broker->registerMethod("jsmooth.Native", "shellExecute", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)Z", (void*)jnm_shellexecute)
	  && broker->registerMethod("jsmooth.Native", "getDriveInfo", "(Ljava/io/File;)Ljsmooth/DriveInfo;", (void*)jnm_getdriveinfo)
	  )
	{
	  jfieldID binding = broker->env()->GetStaticFieldID(nat, "s_bound", "Z");
	  broker->env()->SetStaticBooleanField(nat, binding, JNI_TRUE);
	}
    }
}
