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
#include "execinfogui.h"

#include "Thread.h"
#include "resource.h"

#include <stdlib.h>

WinHttpClient httpClient;
char _downloadLabel[512];
unsigned long downloadThread;
HttpUpdater updater;
HWND _downloadDlg;
char _downloadMsg[512];

#define TIMER_ID 999
#define UPDATE_TIMEOUT 300

void format_num(int n, char *out)
{
	int c;
	char buf[20];
	char *p;

	sprintf(buf, "%d", n);
	c = 2 - strlen(buf) % 3;
	for (p = buf; *p != 0; p++) {
		*out++ = *p;
		if (c == 1) {
			*out++ = '\'';
		}
		c = (c + 1) % 3;
	}
	*--out = 0;
}

void HttpUpdater::httpDownloadUpdate(int current, int total)
{
	char num[50];
	format_num(current / 1024, num);
	strcpy(_downloadLabel, "");
	strcat(_downloadLabel, _downloadMsg);
	strcat(_downloadLabel, num);
	strcat(_downloadLabel, "KB");
}

VOID CALLBACK TimerProc(HWND hwnd, UINT uMsg, UINT_PTR idEvent, DWORD dwTime) {
	SetDlgItemText(_downloadDlg, IDC_INFO, _downloadLabel);
	SetTimer(hwnd, TIMER_ID, UPDATE_TIMEOUT, TimerProc);
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
	map<string, string> headr;
	httpClient.sendGet();
	dparam->downloadResult = httpClient.saveResponseBody();

	finishEventLoop(_downloadDlg);
}

//
// The main download class... it spawns a thread that runs the
// download, and waits for it to complete.
std::string httpDownload(const std::string& url, const std::string &title, const std::string&msg, bool silent)
{
	downloadThreadParams params;
	params.outputFile = url;
	params.downloadResult = false;

	if (silent) {
		downloadHttpThread(&params);
	}
	else {
		strcpy(_downloadMsg, msg.c_str());
		strcpy(_downloadLabel, msg.c_str());
		HWND dlg = makeInfoGui(title, _downloadLabel, true);
		_downloadDlg = dlg;

		SetTimer(dlg, TIMER_ID, UPDATE_TIMEOUT, TimerProc);

		Thread th;
		th.start(downloadHttpThread, &params);

		runEventLoop(dlg);

		_downloadDlg = 0;
	}

	if (wasCanceled()) {
		httpClient.cancel();
		return "";
	}

	if (params.downloadResult) {
		return httpClient.getTemporaryFile();
	}

	return "";
}
