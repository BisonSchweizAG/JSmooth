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

#include <WinSock2.h>
#include <windows.h>
#include <iostream>
#include <fstream>
#include <Commctrl.h>
#include "resource.h"
#include "common.h"
#include "ResourceManager.h"
#include "JVMRegistryLookup.h"
#include "JavaMachineManager.h"
#include "SingleInstanceManager.h"

#include "httpdownload.h"
#include "execinfogui.h"

enum Lang {
	EN, DE
};

string APP_TITLE;
string MSG_INSTALLING_JAVA;
string MSG_INSTALLING_JAVA_WAIT;
string MSG_ERROR_DOWNLOAD;
string MSG_ERROR_NO_JAVA;
string MSG_ERROR_NO_JAVA_FIRST;
string MSG_DOWNLOADING_JAVA;
string MSG_DOWNLOADING_JAVA_WAIT;

#define MAX_LINE 1024

bool _silent = false;
ResourceManager* globalResMan;
DebugConsole *DEBUGCONSOLE = 0;

Lang getLanguage() {
	char buf[19];
	int ccBuf = GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_SISO639LANGNAME, buf, 9);
	if (ccBuf > 0 && strncmp(buf, "de", 2) == 0) {
		return DE;
	}
	return EN;
}

void loadLanguage(string jreMinVersion) {
	if (getLanguage() == DE) {
		MSG_INSTALLING_JAVA_WAIT = "Java wird installiert. Bitte warten...";
		MSG_INSTALLING_JAVA = "Java installieren";
		MSG_ERROR_DOWNLOAD = "Beim runterladen von Java ist ein Fehler aufgetreten. Bitte wenden sie sich an ihren System Administrator.";
		MSG_ERROR_NO_JAVA = "Java Laufzeit Umgebung konnte nicht gefunden werden. Bitte wenden sie sich an ihren System Administrator.";
		MSG_DOWNLOADING_JAVA = "Java runterladen";
		MSG_DOWNLOADING_JAVA_WAIT = "Java wird runtergeladen...";
		MSG_ERROR_NO_JAVA_FIRST = "Java Laufzeit Umgebung " + jreMinVersion + " oder grösser konnte nicht gefunden werden. Drücken sie OK um mit der Installation zu beginnen.";
		APP_TITLE = "Java Laufzeit Umgebung installieren";
	}
	else {
		MSG_INSTALLING_JAVA_WAIT = "Java is being installed. Please wait...";
		MSG_INSTALLING_JAVA = "Installing Java";
		MSG_ERROR_DOWNLOAD = "An error occured while downloading Java. Please contact your system administrator.";
		MSG_ERROR_NO_JAVA = "Java Runtime Environment could not be found. Please contact your system administrator.";
		MSG_DOWNLOADING_JAVA = "Downloading Java";
		MSG_DOWNLOADING_JAVA_WAIT = "Java is being downloaded...";
		MSG_ERROR_NO_JAVA_FIRST = "Java Runtime Environment " + jreMinVersion + " or greater has not been found. Press OK to start the installation.";
		APP_TITLE = "Install Java Runtime Environment";
	}
}

void lastExit()
{
	delete DEBUGCONSOLE;
	DEBUGCONSOLE = 0;
	delete globalResMan;
	globalResMan = 0;
}

void _debugOutput(const std::string& text)
{
	if (DEBUGCONSOLE != 0)
		DEBUGCONSOLE->writeline(text);
	printf("%s\n", text.c_str());
	fflush(stdout);
}

void showGenericError() {
	string msg = MSG_ERROR_DOWNLOAD;
	if (_silent) {
		DEBUG(msg);
	}
	else {
		MessageBox(0, msg.c_str(), APP_TITLE.c_str(), MB_OK | MB_APPLMODAL);
	}
}

void _debugWaitKey()
{
	if (DEBUGCONSOLE != 0)
		DEBUGCONSOLE->waitKey();
}

string doDownload(string url, string &outParams) {

	string firstPrioRedirect = FileUtils::getExecutablePath() + "autodownload.redirect";
	if (FileUtils::fileExists(firstPrioRedirect)) {
		url = "file:\\" + firstPrioRedirect;
	}

	string file;
	const string &FILE_PREFIX("file:");

	if (StringUtils::startsWith(url, FILE_PREFIX)) {
		file = FileUtils::getRelOrAbsFile(url.substr(FILE_PREFIX.size()));
	}
	else {
		DEBUG("autodownload: Now downloading " + url);
		file = httpDownload(url, MSG_DOWNLOADING_JAVA, MSG_DOWNLOADING_JAVA_WAIT, _silent);
		printf("autodownload: GOT FILE[%s]\n", file.c_str());
	}

	if (StringUtils::endsWith(file, "autodownload.redirect")){

		DEBUG("autodownloadinfo: detected.");

		string adUrl = "";

		FILE *f = fopen(file.c_str(), "r");
		if (f) {
			const string &URL_KEY("url=");
			const string &PARAMS_KEY("params=");
			char line[1024];
			outParams = "";
			int lines = 0;
			while (fgets(line, MAX_LINE, f)) {
				if (lines++ > 20) {
					break; // had strange situation with endless loop
				}
				string linee = StringUtils::rtrim(line);
				if (StringUtils::startsWith(linee, URL_KEY)) {
					adUrl = linee.substr(URL_KEY.size());
				}
				else if (StringUtils::startsWith(linee, PARAMS_KEY)) {
					outParams = linee.substr(PARAMS_KEY.size());
				}
				if (!adUrl.empty() && !outParams.empty()) {
					break;
				}
			}
			fclose(f);
		}
		else {
			DEBUG("autodownloadinfo: file not found: " + file);
		}

		DEBUG("autodownloadinfo: url=" + adUrl + ", params=" + outParams);

		if (!adUrl.empty()) {
			if (StringUtils::startsWith(adUrl, FILE_PREFIX)) {
				adUrl = adUrl.substr(FILE_PREFIX.size());
				file = FileUtils::getRelOrAbsFile(adUrl);
			}
			else {
				DEBUG("autodownloadinfo: Now downloading " + adUrl);
				file = httpDownload(adUrl, MSG_DOWNLOADING_JAVA, MSG_DOWNLOADING_JAVA_WAIT, _silent);
				printf("autodownloadinfo: GOT FILE[%s]\n", file.c_str());
			}
		}
	}

	return file;
}

int WINAPI WinMain(HINSTANCE hThisInstance,
	HINSTANCE hPrevInstance,
	LPSTR lpszArgument,
	int nFunsterStil)
{

	atexit(lastExit);
	SingleInstanceManager instanceman;

	globalResMan = new ResourceManager("JAVA", PROPID, JARID, JNISMOOTHID);

	string silentFile = FileUtils::getExecutablePath() + "setup.silent";
	_silent = FileUtils::fileExists(silentFile);

	// sets up the command line arguments
	// not sure if lpszArgument can be 0 on Windows...
	if ((lpszArgument != 0) && (strlen(lpszArgument) > 0))
	{
		// Note that this overwrites an existing KEY_ARGUMENTS
		std::vector<std::string> args = StringUtils::split(lpszArgument, " \t\n\r", "\"'", false);
		globalResMan->setUserArguments(args);
	}

	loadLanguage(globalResMan->getProperty("minversion"));

	bool dodebug = globalResMan->getBooleanProperty("skel_Debug");

	if (dodebug)
	{
		DEBUGCONSOLE = new DebugConsole("JSmooth Debug");
		globalResMan->printDebug();
	}

	bool singleinstance = globalResMan->getBooleanProperty("skel_SingleInstance");
	if (singleinstance)
	{
		if (instanceman.alreadyExists())
		{
			if (!_silent) {
				instanceman.sendMessageInstanceShow();
			}
			exit(0);
		}
		else
		{
			instanceman.startMasterInstanceServer();
		}
	}

	DEBUG(string("Main class: ") + globalResMan->getMainName());

	char curdir[_MAX_PATH];
	GetCurrentDirectory(_MAX_PATH, curdir);
	DEBUG(string("Currentdir: ") + curdir);

	string newcurdir = globalResMan->getCurrentDirectory();
	SetCurrentDirectory(newcurdir.c_str());

	std::string message = MSG_ERROR_NO_JAVA_FIRST;// globalResMan->getProperty("skel_Message");
	std::string url = globalResMan->getProperty("skel_DownloadURL");
	std::string params = globalResMan->getProperty("skel_DownloadURLParams");
	bool singleProcess = globalResMan->getBooleanProperty("skel_SingleProcess");

	int tries = 0;
	while (true) {

		if (tries > 0) {
			DEBUG("try jre again after download...");
		}

		JavaMachineManager *man = new JavaMachineManager(*globalResMan);
		man->setAcceptExe(true);
		man->setAcceptDLL(true);
		man->setUseConsole(dodebug);
		man->setPreferDLL(singleProcess);

		bool ok = man->run();
		delete man;

		if (ok) {
			if (tries > 0) {
				Sleep(2000); // w/o may be the cause for the message 'installer already running'
			}
			break;
		}

		if (tries > 0) {
			if (_silent) {
				DEBUG(MSG_ERROR_NO_JAVA);
			}
			else {
				MessageBox(0, MSG_ERROR_NO_JAVA.c_str(), APP_TITLE.c_str(), MB_OK | MB_APPLMODAL);
			}
			break;
		}

		tries++;

		DEBUG("Displaying message to user...");
		DEBUG("URL=" + url);
		if (url.empty())
		{
			showGenericError();
			break;
		}

		if (!_silent && MessageBox(0, message.c_str(), APP_TITLE.c_str(), MB_OKCANCEL | MB_ICONQUESTION | MB_APPLMODAL) != IDOK) {
			break;
		}

		string adParams = params;
		string file = doDownload(url, adParams);

		if (file.empty())
		{
			showGenericError();
			break;
		}

		string ext = StringUtils::toLowerCase(FileUtils::getFileExtension(file));
		if (ext == "exe")
		{
			makeExecInfoGui(MSG_INSTALLING_JAVA, MSG_INSTALLING_JAVA_WAIT, file + " " + adParams, _silent);
		}
		else // anything else, we try to open it like a document
		{
			ShellExecute(0, "open", file.c_str(), adParams.c_str(), "", 0);
		}
	}

	DEBUG("NORMAL EXIT");
	DEBUGWAITKEY();

	/* The program return-value is 0 - The value that PostQuitMessage() gave */
	return 0;
}
