/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import java.io.File;

import org.eclipse.swt.program.Program;

import net.charabia.jsmoothgen.application.JSmoothModelBean;


public class RunexeAction extends JSmoothAction {
    
    public RunexeAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        System.out.println("[DEBUG] Running exe...");
        JSmoothApplication app = getApplication();
        if (!app.hasProjectFile()) {
            app.consoleMessage("Cannot run without a project file. Please save the project or load a new one, then try again.");
            return false;
        }
        
        JSmoothModelBean jsmodel = app.getModelBean();
        String basedir = app.getProjectFile().getParent();
        File exe = new File (basedir, jsmodel.getExecutableName());
        app.consoleMessage("Running exe " + exe.getAbsolutePath());
        return Program.launch(exe.getAbsolutePath());
    }
}