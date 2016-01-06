/*
 * Created on Nov 29, 2003
 */
package net.charabia.jsmoothgen.application.swtgui.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Contains the resources used by JSmooth. Provides static methods and constants
 * for access.
 * 
 * NOTE: Before accessing any constant, the constructor must be called.
 * 
 * @author Dumon
 */
public final class JSmoothResources {
    public static Image IMG_SWITCHER_SKELETON_PAGE;
    public static Image IMG_SWITCHER_APPLICATION;
    public static Image IMG_SWITCHER_EXECUTABLE;
    public static Image IMG_SWITCHER_WELCOME;
    
    public static String TEXT_HELP_WELCOME;

    private ResourceBundle bundle;
    private Display display;
    
    public JSmoothResources(Display display) {
        Class clazz = getClass();
        URL url = clazz.getResource("jsmooth.properties");
        try {
            bundle = new PropertyResourceBundle(url.openStream());
        } catch (IOException e) {
            // Shouldn't happen. Ignore.
        }
        loadImages(this.display = display);
        loadText();
    }
    
    public void loadImages(Display display) {
        System.out.println("[DEBUG] Loading images...");
        String name = bundle.getString("img.switcher.skeleton");
        IMG_SWITCHER_SKELETON_PAGE = new Image(display, getClass().getResourceAsStream(name));
        
        name = bundle.getString("img.switcher.application");
        IMG_SWITCHER_APPLICATION = new Image(display, getClass().getResourceAsStream(name));
        
        name = bundle.getString("img.switcher.executable");
        IMG_SWITCHER_EXECUTABLE = new Image(display, getClass().getResourceAsStream(name));
        
        name = bundle.getString("img.switcher.welcome");
        IMG_SWITCHER_WELCOME = new Image(display, getClass().getResourceAsStream(name));
    }
    
    public void loadText() {
        System.out.println("[DEBUG] Loading text...");
        InputStream stream = getClass().getResourceAsStream("welcome.xml");
        byte[] bytes = new byte[5000]; // 50 KB Should be enough.
        try {
            stream.read(bytes);
            TEXT_HELP_WELCOME = (new String(bytes)).trim();
            stream.close();
        } catch (IOException e1) {
            // TODO: Throw some exception at init time.
        }
    }
}