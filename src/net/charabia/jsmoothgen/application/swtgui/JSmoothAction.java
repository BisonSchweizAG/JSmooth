/*
 * Created on Jun 12, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

/**
 * @author Dumon
 */
public abstract class JSmoothAction {
    private JSmoothApplication js;
    
    public JSmoothAction(JSmoothApplication js) {
        this.js = js;
    }
    
    protected JSmoothApplication getApplication() {
        return js;
    }
    
    public abstract boolean run();
}