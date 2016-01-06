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

#ifndef __JSMOOTHCORETHREAD_H_
#define __JSMOOTHCORETHREAD_H_


#include <process.h>
#include <windows.h>
#include <winbase.h>

#include <stdio.h>
/**
 * Provides basic string operations.
 * 
 * @author Rodrigo Reyes <reyes@charabia.net>
 */

class Thread
{
 protected:
  unsigned long m_threadId;
  void (*m_callback)(void*);
  void* m_param;
  bool m_stopRequested;

 public:
  Thread();

  /**
   * Starts the thread, and execute the run() method.
   */
  void start();

  /**
   * Starts the thread and execute the run method. If the run method
   * is not overloaded, it executes the callback fonction, and pass
   * the param as parameter to the function.
   */
  void start(void (*callback)(void*), void* param);

  /**
   * The method executed by the thread.
   */
  void run();

  /*
   * Waits for the thread to complete
   */
  void join();

  /*
   * Waits for the thread to complete. If the timeout (millis) is
   * reached, returns false. Returns true if the thread ended.
   */
  bool join(int millis);

  /**
   * Waits a number of milliseconds.
   */
  static void sleep(int millis);

  bool isStopRequested();

  void setStopRequested(bool stop=true);

};


#endif
