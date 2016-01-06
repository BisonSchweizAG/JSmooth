/*
 * Created on 28.08.2004
 */
package net.charabia.jsmoothgen.application.swtgui;

/**
 * @author Dumon
 */
public class CompilationException extends Exception {
    
    private static final long serialVersionUID = 1L;
    public String[] messages;
    
    public CompilationException(String message) {
        super(message);
    }
    
    public CompilationException(String[] messages) {
        this.messages = messages;
    }
    
    public String[] getMessages() {
        return messages;
    }
    
}
