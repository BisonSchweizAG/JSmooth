/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;


public class SaveAsProjectAction extends JSmoothAction {
    private JSmoothApplication js;
    
    public SaveAsProjectAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        FileDialog dialog = new FileDialog(getApplication().getShell(), SWT.SAVE);
        dialog.setText("Save Project");
        String file = dialog.open();
        if (file != null) {
            return getApplication().saveProjectAs(file);
        }
        else {
            return false;
        }
    }
}