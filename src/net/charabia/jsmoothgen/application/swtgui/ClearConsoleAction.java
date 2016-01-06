/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;


public class ClearConsoleAction extends JSmoothAction {
    
    public ClearConsoleAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        System.out.println("[DEBUG] Clearing console.");
        getApplication().clearConsole();
        return true;
    }
}