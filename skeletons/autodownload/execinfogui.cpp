#include "execinfogui.h"
#include "execexe.h"
#include "common.h"
#include "Thread.h"
#include "resource.h"

using namespace std;

bool _finished = false;
HWND _execDlg = 0;
char _command[_MAX_PATH];
bool _allowCancel = false;
bool _wasCanceled = false;

void execFile(void*p)
{
	DEBUG("Executing EXE");
	execute(_command, true, true);
	finishEventLoop(_execDlg);
}

INT_PTR CALLBACK DialogProc(HWND dlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg)
	{
	case WM_COMMAND:
		switch (LOWORD(wParam))
		{
		case IDCANCEL:
			if (_allowCancel) {
				SendMessage(dlg, WM_CLOSE, 0, 0);
			}
			return TRUE;
		}
		break;
	case WM_CLOSE:
		if (_allowCancel) {
			_wasCanceled = true;
			finishEventLoop(_execDlg);
		}
		return TRUE;
	case WM_KEYDOWN:

		return TRUE;
	case WM_DESTROY:
		return TRUE;
	}

	return FALSE;
}

HWND makeInfoGui(const string &title, const string &info, bool allowCancel) {
	HWND dlg = CreateDialogParam(0, MAKEINTRESOURCE(IDD_INFO), 0, DialogProc, 0);
	SetDlgItemText(dlg, IDC_INFO, info.c_str());
	SetWindowText(dlg, title.c_str());
	_allowCancel = allowCancel;

	HMONITOR hm = MonitorFromWindow(dlg, MONITOR_DEFAULTTONEAREST);
	MONITORINFO mi = { sizeof(MONITORINFO) };
	GetMonitorInfo(hm, &mi);
	RECT &rcMon = mi.rcMonitor;
	int wMon = rcMon.right - rcMon.left;
	int hMon = rcMon.bottom - rcMon.top;

	RECT rcDlg;
	GetWindowRect(dlg, &rcDlg);
	int wDlg = rcDlg.right - rcDlg.left;
	int hDlgg = rcDlg.bottom - rcDlg.top;

	SetWindowPos(dlg, 0, (wMon - wDlg) / 2, ((hMon - hDlgg) / 2 - 20), 0, 0, SWP_NOZORDER | SWP_NOSIZE);
	ShowWindow(dlg, SW_SHOW);
	return dlg;
}

void runEventLoop(HWND dlg) {
	MSG msg;
	BOOL ret;
	_finished = false;
	while ((ret = GetMessage(&msg, 0, 0, 0)) != 0) {
		if (ret == -1 || _finished) {
			break;
		}

		if (!IsDialogMessage(dlg, &msg)) {
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}
	DestroyWindow(dlg);
}

void finishEventLoop(HWND dlg) {
	_finished = true;
	PostMessage(dlg, 0, 0, 0);
}

bool wasCanceled() {
	return _wasCanceled;
}

void makeExecInfoGui(const string &title, const string &info, const string &cmd, bool silent) {

	strcpy(_command, cmd.c_str());

	if (silent) {
		execFile((void*)&_command[0]);
	}
	else {
		HWND dlg = makeInfoGui(title, info, false);

		Thread th;
		th.start(execFile, (void*)&_command[0]);
		_execDlg = dlg;
		runEventLoop(dlg);
		_execDlg = 0;
	}
}
