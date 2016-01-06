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

#ifndef __SUNJVMDLL_H_
#define __SUNJVMDLL_H_

#include <string>
#include "jni.h"

#include "Version.h"
#include "StringUtils.h"
#include "FileUtils.h"
#include "ResourceManager.h"
#include "JavaProperty.h"
#include "JVMBase.h"

typedef jint (JNICALL *CreateJavaVM_t)(JavaVM **pvm, JNIEnv **env, void *args);
typedef jint (JNICALL *GetDefaultJavaVMInitArgs_t)(void *args);

/**
 * Manages a Sun's JVM available on the computer.
 * @author Rodrigo Reyes <reyes@charabia.net>
 */ 

class SunJVMDLL : public JVMBase
{
 public:
   
   enum StatusCode
     {
       JVM_NOT_STARTED=0,
       JVM_DLL_CANT_LOAD ,
       JVM_CANT_CREATE_VM ,
       JVM_CANT_USE_VM,
       JVM_LOADED
     } ;
   
  /**
   * The path to the .DLL that can be used to create a JVM
   * instance. The string may be empty is the information is not
   * known.
   */ 
  std::string                 m_dllpath;
  Version                     m_version;

  HINSTANCE                   m_vmlib;

  StatusCode                  m_statusCode;
  
 protected:
  JavaVM *m_javavm;
  JNIEnv *m_javaenv;
  
 public:
  SunJVMDLL(const std::string& jvmdll, const Version& v);
  ~SunJVMDLL();

  bool instanciate();

  bool setupVM12DLL(CreateJavaVM_t CreateJavaVM, GetDefaultJavaVMInitArgs_t GetDefaultJavaVMInitArgs);
  bool setupVM11DLL(CreateJavaVM_t CreateJavaVM, GetDefaultJavaVMInitArgs_t GetDefaultJavaVMInitArgs);

  bool registerMethod(const std::string& classname, const std::string& methodname, const std::string& signature, void* fn);
  bool registerJniSmooth();

  JNIEnv* env()
    {
      JNIEnv* env;
      jint result = m_javavm->AttachCurrentThread((void**)&env, 0);
      return env;
    }

  bool run(const std::string& mainclass, bool waitDeath = true);
  void join();

  jclass findClass(const std::string& clazz);
  jmethodID findMethod(jclass& cls, const std::string& methodname, const std::string& signature, bool isStatic);
  JavaVM* getJavaVM();

  void setIntField(jclass cls, jobject obj, const std::string& fieldName, int value);
  void setLongField(jclass cls, jobject obj, const std::string& fieldName, jlong value);
  void setObjectField(jclass cls, jobject obj, const std::string& fieldName, const std::string& fieldclass, jobject value);

  jstring       newUTFString(const std::string& str);
  jobject       newObject(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jobjectArray  newObjectArray(int size, jclass clazz, jobject initialValue);
  jobjectArray  newObjectArray(int size, const std::string& classname, jobject initialValue);

  void      invokeVoidStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jboolean  invokeBooleanStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jbyte     invokeByteStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jchar     invokeCharStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jshort    invokeShortStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jint      invokeIntStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jlong     invokeLongStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jfloat    invokeFloatStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jdouble   invokeDoubleStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);
  jobject   invokeObjectStatic(jclass clazz, jmethodID& methodid, jvalue arguments[]);


  void      invokeVoid(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jboolean  invokeBoolean(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jbyte     invokeByte(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jchar     invokeChar(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jshort    invokeShort(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jint      invokeInt(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jlong     invokeLong(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jfloat    invokeFloat(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jdouble   invokeDouble(jobject& obj, jmethodID& methodid, jvalue arguments[]);
  jobject   invokeObject(jobject& obj, jmethodID& methodid, jvalue arguments[]);

};


#endif
