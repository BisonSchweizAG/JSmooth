/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;


public class OpenAction extends JSmoothAction {
    
    public OpenAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        FileDialog dialog = new FileDialog(getApplication().getShell(), SWT.OPEN);
        dialog.setText("Open Project");
        String file = dialog.open();
        if (file != null) {
            boolean ok = getApplication().openProject(file);
            if (ok) getApplication().consoleMessage("Opened the project file " + file);
            return ok;
        }
        else {
            return false;
        }
    }
}