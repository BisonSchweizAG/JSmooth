/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;


public class NewAction extends JSmoothAction {
    
    public NewAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        System.out.println("[DEBUG] New default project.");
        getApplication().newProject();
        return true;
    }
}