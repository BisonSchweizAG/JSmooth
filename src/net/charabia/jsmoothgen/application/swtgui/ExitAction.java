/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;


public class ExitAction extends JSmoothAction {
    
    public ExitAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        System.out.println("[DEBUG] Bye");
        getApplication().exit();
        return true;
    }
}