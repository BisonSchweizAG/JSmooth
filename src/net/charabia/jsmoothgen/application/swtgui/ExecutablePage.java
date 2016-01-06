/*
 * Created on May 19, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import net.charabia.jsmoothgen.application.JSmoothModelBean;
import net.charabia.jsmoothgen.application.swtgui.resources.JSmoothResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExecutablePage extends JSmoothPage {
    private Text exe;
    private Text dir;
    private Label icon;

    public ExecutablePage(JSmoothApplication js) {
        super(js);
    }

    public Control createPageArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(3, false));

        // Executable name
        Label label = new Label(top, SWT.NONE);
        label.setText("Executable name:");
        GridData grid = new GridData(GridData.FILL);
        label.setLayoutData(grid);

        exe = new Text(top, SWT.BORDER);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 250;
        exe.setLayoutData(grid);
        exe.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setModelExename(exe.getText());
            }
        });

        Button button = new Button(top, SWT.NONE);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                dialog.setText("Executable Name");
                String file = dialog.open();
                if (file != null) ExecutablePage.this.exe.setText(file);
            }
        });
        grid = new GridData(GridData.FILL);
        grid.widthHint = 100;
        button.setLayoutData(grid);

        // Current directory
        label = new Label(top, SWT.NONE);
        label.setText("Current directory:");
        grid = new GridData(GridData.FILL);
        label.setLayoutData(grid);

        dir = new Text(top, SWT.BORDER);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 250;
        dir.setLayoutData(grid);
        dir.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setModelCurrentdir(dir.getText());
            }
        });
        
        button = new Button(top, SWT.NONE);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
                dialog.setText("Current Directory");
                String dir = dialog.open();
                if (dir != null) ExecutablePage.this.dir.setText(dir);
            }
        });
        grid = new GridData(GridData.FILL);
        grid.widthHint = 100;
        button.setLayoutData(grid);

        Group group = new Group(top, SWT.NONE);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        grid = new GridData(GridData.FILL | GridData.HORIZONTAL_ALIGN_CENTER);
        grid.horizontalSpan = 3;
        group.setLayoutData(grid);
        group.setText("Executable icon");

        icon = new Label(group, SWT.BORDER | SWT.FLAT);
        grid = new GridData(GridData.FILL | GridData.HORIZONTAL_ALIGN_CENTER);
        grid.widthHint = 48;
        grid.heightHint = 48;
        icon.setLayoutData(grid);

        button = new Button(group, SWT.NONE);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setText("Icon File");
                String file = dialog.open();

                // Means "CANCEL"
                if (file == null) return;

                setModelIcon(setIcon(file) ? file : null);
            }
        });
        grid = new GridData(GridData.FILL);
        grid.widthHint = 100;
        button.setLayoutData(grid);
        
        return top;
    }

    private void setModelCurrentdir(String dir) {
        System.out.println("[DEBUG] Setting current directory to: " + dir);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setCurrentDirectory(dir);
    }

    private void setModelExename(String exename) {
        System.out.println("[DEBUG] Setting exe name to: " + exename);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setExecutableName(exename);
    }

    private boolean setIcon(String file) {
        Image img = null;
        
        img = icon.getImage();
        if (img != null) {
            // Clear the label's image
            icon.setImage(null);
            img.dispose();
        }
        
        if (file == null || file.equals("")) return true;

        try {
            img = new Image(getShell().getDisplay(), file);
            ImageData data = img.getImageData();
            if (data.width > 48 && data.height > 48) {
                // TODO: Output a message to JSmooth Console.
                System.out.println("[DEBUG] The image size is too big, > 48x48");
                return false;
            }
        } catch (SWTException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return false;
        }

        icon.setImage(img);

        return true;
    }
    
    private void setModelIcon(String iconfile) {
        System.out.println("[DEBUG] Setting icon file to: " + iconfile);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setIconLocation(iconfile);
    }

    protected void configureResources() {
        setImage(JSmoothResources.IMG_SWITCHER_EXECUTABLE);
        setToolTip("Windows Executable");
    }

    public void load() {
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        String exename = jsmodel.getExecutableName();
        if (exename == null) exename = "";
        this.exe.setText(exename);
        
        String iconfile = jsmodel.getIconLocation();
        setIcon(iconfile);
        
        String dirname = jsmodel.getCurrentDirectory();
        if (dirname == null) dirname = "";
        this.dir.setText(dirname);
    }
}