/*
 * Created on May 22, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import net.charabia.jsmoothgen.application.JSmoothModelBean;
import net.charabia.jsmoothgen.application.swtgui.resources.JSmoothResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * @author Dumon
 */
public class JavaAppPage extends JSmoothPage {
    protected SelectionListener LISTENER_USEJAR = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            setModelUsejar(usejar.getSelection());
            updateUsejarWidgets();
        }
    };
    
    private Text jar;
    private Text mainclass;
    private Text args;
    private List classpath;
    private Button usejar;
    private Button setjar;
    private Button addjar;
    private Button addfolder;
    private Button remove;
    
    public JavaAppPage(JSmoothApplication js) {
        super(js);
    }

    public Control createPageArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        Composite composite = new Composite(top, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        usejar = new Button(composite, SWT.CHECK);
        usejar.setText("Use embedded JAR");
        usejar.addSelectionListener(LISTENER_USEJAR);
        GridData grid = new GridData(GridData.FILL);
        grid.horizontalSpan = 3;
        usejar.setLayoutData(grid);
        
        // Jar location
        Label label = new Label(composite, SWT.NONE);
        label.setText("JAR location:");
        grid = new GridData(GridData.FILL);
        label.setLayoutData(grid);

        jar = new Text(composite, SWT.BORDER);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 250;
        jar.setLayoutData(grid);
        jar.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setModelJar(jar.getText());
            }
        });
        
        setjar = new Button(composite, SWT.NONE);
        setjar.setText("Browse...");
        setjar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                dialog.setText("JAR Location");
                String file = dialog.open();
                if (file != null) jar.setText(file);
                setModelJar(file);
            }
        });
        grid = new GridData(GridData.FILL);
        grid.widthHint = 100;
        setjar.setLayoutData(grid);
        
        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.horizontalSpan = 3;
        label.setLayoutData(grid);
        
        // Main class
        label = new Label(composite, SWT.NONE);
        label.setText("Main class:");
        grid = new GridData(GridData.FILL);
        label.setLayoutData(grid);

        mainclass = new Text(composite, SWT.BORDER);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 250;
        mainclass.setLayoutData(grid);
        mainclass.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setModelMainclass(mainclass.getText());
            }
        });
        
        new Label(composite, SWT.NONE); // empty cell

        // Arguments
        label = new Label(composite, SWT.NONE);
        label.setText("Arguments:");
        grid = new GridData(GridData.FILL);
        label.setLayoutData(grid);

        args = new Text(composite, SWT.BORDER);
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 250;
        args.setLayoutData(grid);
        args.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setModelArguments(args.getText());
            }
        });
        
        new Label(composite, SWT.NONE); // empty cell

        // Classpath list
        Group group = new Group(top, SWT.NONE);
        group.setText("Classpath");
        group.setLayout(new GridLayout());
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.horizontalSpan = 3;
        group.setLayoutData(grid);
        group.setLayout(new GridLayout(2, false));

        classpath = new List(group, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        grid = new GridData(GridData.FILL_BOTH);
        grid.widthHint = 250; // TODO: Hardscoded
        grid.heightHint = classpath.getItemHeight() * 10; // TODO: Hardcoded
        classpath.setLayoutData(grid);
        classpath.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateRemoveButton();
            }
        });
        
        // The classpath Button bar
        composite = new Composite(group, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 2;
        composite.setLayout(layout);

        addjar = new Button(composite, SWT.NONE);
        addjar.setText("Add JAR File...");
        addjar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
                dialog.setText("JAR File");
                String choice = dialog.open();
                if (choice != null) {
                    String path = dialog.getFilterPath();
                    String[] filenames = dialog.getFileNames();
                    ArrayList files = new ArrayList();
                    for (int i = 0; i < filenames.length; i++) {
                        files.add(path + File.separator + filenames[i]);
                    }
                    addClasspathItems((String[]) files.toArray(new String[0]));
                }
            }
        });
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 130;
        addjar.setLayoutData(grid);

        addfolder = new Button(composite, SWT.NONE);
        addfolder.setText("Add Class Folder...");
        addfolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
                dialog.setText("Class Folder");
                String folder = dialog.open();
                if (folder != null) addClasspathItems(new String[]{folder});
            }
        });
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 130;
        addfolder.setLayoutData(grid);

        remove = new Button(composite, SWT.NONE);
        remove.setText("Remove");
        remove.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                removeItem();
            }
        });
        grid = new GridData(GridData.FILL_HORIZONTAL);
        grid.widthHint = 130;
        remove.setLayoutData(grid);
        
        updateRemoveButton();
        updateUsejarWidgets();
        
        return top;
    }
    
    private void setModelMainclass(String mainclass) {
        System.out.println("[DEBUG] Setting mainclass to: " + mainclass);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setMainClassName(mainclass);
    }

    private void removeItem() {
        classpath.remove(classpath.getSelectionIndices());
        updateRemoveButton();
        setModelClasspath(classpath.getItems());
    }
    
    private void setModelClasspath(String[] classpath) {
        String classpathString = Arrays.asList(classpath).toString();
        System.out.println("[DEBUG] Setting classpath to: " + classpathString);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setClassPath(classpath);
    }
    
    private void updateRemoveButton() {
        int i = classpath.getItemCount();
        boolean enable = true;
        if (i == 0) {
            enable = false;
        } else {
            int s = classpath.getSelectionCount();
            if (s == 0)
                enable = false;
        }
        remove.setEnabled(enable);
    }

    private void addClasspathItems(String[] items) {
        String[] olditems = classpath.getItems();
        for (int i = 0; i < items.length; i++) {
            // Check for duplicates
            if (Arrays.binarySearch(olditems, items[i]) >= 0) continue;
            classpath.add(items[i]);
        }
        setModelClasspath(classpath.getItems());
    }
    
    private void updateUsejarWidgets() {
        boolean b = usejar.getSelection();
        setjar.setEnabled(b);
        jar.setEnabled(b);
    }
    
    private void setModelJar(String jarfile) {
        System.out.println("[DEBUG] Setting jarfile to: " + jarfile);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setJarLocation(jarfile);
    }
    
    private void setModelUsejar(boolean b) {
        System.out.println("[DEBUG] Setting use embedded jar to: " + b);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setEmbeddedJar(b);
    }
    
    private void setModelArguments(String args) {
        System.out.println("[DEBUG] Setting argument to: " + args);
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        jsmodel.setArguments(args);
    }

    protected void configureResources() {
        setImage(JSmoothResources.IMG_SWITCHER_APPLICATION);
        setToolTip("Java Application");
    }

    public void load() {
        JSmoothModelBean jsmodel = getApplication().getModelBean();
        String[] classpath = jsmodel.getClassPath();
        if (classpath == null) classpath = new String[0];
        this.classpath.setItems(classpath);
        
        boolean usejar = jsmodel.getEmbeddedJar();
        this.usejar.setSelection(usejar);
        LISTENER_USEJAR.widgetSelected(null);
        
        String mainclass = jsmodel.getMainClassName();
        if (mainclass == null) mainclass = "";
        this.mainclass.setText(mainclass);
        
        String jar = jsmodel.getJarLocation();
        if (jar == null) jar = "";
        this.jar.setText(jar);
        
        String args = jsmodel.getArguments();
        if (args == null) args = "";
        this.args.setText(args);
    }
}