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

#include "Thread.h"

void thread_support_fn(void* param)
{
  Thread *t = (Thread*) param;
  try {
    t->run();
  } catch (...)
    {
      // 
    }
  _endthread();
}

Thread::Thread()
{
  m_callback = 0;
}

void Thread::start()
{
  m_threadId = _beginthread((void( __cdecl * )( void * ))thread_support_fn, 0, (void*) this); 
}

void Thread::start(void (*callback)(void*), void* param)
{
  m_callback = callback;
  m_param = param;
  m_threadId = _beginthread((void( __cdecl * )( void * ))thread_support_fn, 0, (void*) this); 
}

void Thread::run()
{
  if (m_callback != 0)
    {
      (*m_callback)(m_param);
    }
}

void Thread::sleep(int millis)
{
  Sleep(millis);
}

void Thread::join()
{
  WaitForSingleObject ( (void*)m_threadId, INFINITE );
}

bool Thread::join(int millis)
{
  int result = WaitForSingleObject( (void*)m_threadId, millis);
  switch(result)
    {
    case WAIT_ABANDONED:
      printf("WAIT_ABANDONED\n");
      break;
    case WAIT_OBJECT_0:
      printf("WAIT_OBJECT_0");
      return true;
      break;
    case WAIT_TIMEOUT:
      printf("WAIT_TIMEOUT");
      break;
    }

  return false;
}


bool Thread::isStopRequested()
{
  return m_stopRequested;
}

void Thread::setStopRequested(bool stop)
{
  m_stopRequested = stop;
}
