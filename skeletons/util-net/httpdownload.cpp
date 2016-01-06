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

#include "httpdownload.h"

#include "Thread.h"

#include <stdlib.h>

Fl_Window *httpWindow ;
WinHttpClient httpClient;
char downloadLabel[256];
unsigned long downloadThread;
HttpUpdater updater;

void HttpUpdater::httpDownloadUpdate(int current, int total)
{
  Fl::lock();
  hcg_progressbar->maximum((float)total);
  hcg_progressbar->value((float)current);

  if (total != 0)
    sprintf(downloadLabel, "Downloading %d%%", (current*100)/total);
  else
    sprintf(downloadLabel, "Downloading...");

  printf("%s\n", downloadLabel);

  hcg_progressbar->label(downloadLabel);
  hcg_progressbar->redraw();
  Fl::unlock();
}

//
// A timeout'ed method to refresh the progress bar display
//
void updateProgressBar(void*p)
{
  hcg_progressbar->redraw();
  Fl::repeat_timeout(0.15, updateProgressBar);
}

//
// The struct for the download thread
//
struct downloadThreadParams
{
  std::string outputFile;
  bool downloadResult;
};


//
// The download thread... it sets up the http client class and sends
// the requests.
//
void downloadHttpThread(void* param)
{
  downloadThreadParams* dparam = (downloadThreadParams*)param;

  std::string outputFile = (std::string) dparam->outputFile;

  URL urltarget(outputFile);
  httpClient.addListener(updater);
  httpClient.setURL(urltarget);
  map<string,string> headr;
  httpClient.sendGet();
  if (httpClient.saveResponseBody())
    dparam->downloadResult = true;
  else 
    dparam->downloadResult = false;
  
  Fl::lock();
  httpWindow->hide();
  Fl::unlock();
}


//
// Callback for the cancel button
//
void cancelDownload(Fl_Widget* param)
{
  httpClient.cancel();

  Fl::lock();
  httpWindow->hide();
  Fl::unlock();
}

//
// The main download class... it spawns a thread that runs the
// download, and waits for it to complete.
std::string httpDownload(const std::string& url)
{
  httpWindow = make_window_downloadgui();
  httpWindow->end();
  char bu[0][0];
  httpWindow->show(0, (char**)bu);

  hcg_cancelbutton->callback(cancelDownload);

  downloadThreadParams params;
  params.outputFile = url;
  params.downloadResult = false;

  Thread th;
  th.start(downloadHttpThread, &params);

  Fl::add_timeout(1.0, updateProgressBar);
  Fl::run();  

  if (httpClient.isCancelled())
    return "";

  if (params.downloadResult)
    return httpClient.getTemporaryFile();
  else 
    return "";
}
