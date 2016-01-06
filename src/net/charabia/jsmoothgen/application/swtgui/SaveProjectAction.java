/*
 * Created on Jun 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;



public class SaveProjectAction extends JSmoothAction {
    private JSmoothApplication js;
    
    public SaveProjectAction(JSmoothApplication js) {
        super(js);
    }

    public boolean run() {
        if (getApplication().hasProjectFile()) {
            return getApplication().saveProject();
        }
        else {
            return getApplication().ACTION_SAVE_AS.run();
        }
    }
}