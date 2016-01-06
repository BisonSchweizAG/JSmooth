/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;


public class CompileAction extends JSmoothAction {
    private JSmoothApplication js;
    
    public CompileAction(JSmoothApplication js) {
        super(js);
    }
    
    public boolean run() {
        System.out.println("[DEBUG] Compiling, stand by...");
        JSmoothApplication app = getApplication();
        if (!app.hasProjectFile()) {
            app.consoleMessage("Cannot compile without a project file. Please save the project or load a new one, then try again.");
            return false;
        }
        else {
            app.saveProject();
        }
        return app.compileProject();
    }
}