/*
 * Created on May 22, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import net.charabia.jsmoothgen.application.JSmoothModelBean;
import net.charabia.jsmoothgen.application.swtgui.resources.JSmoothResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * @author Dumon
 */
public class JVMOptionsPage extends JSmoothPage {
    private Text jar;
    private Text mainclass;
    private Text args;
    private List classpath;
    private Button usejar;
    private Button setjar;
    private Button addjar;
    private Button addfolder;
    private Button remove;
    
    public JVMOptionsPage(JSmoothApplication js) {
        super(js);
    }

    public Control createPageArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        // Classpath list
        Group group = new Group(top, SWT.NONE);
        group.setText("Classpath");
        group.setLayout(new GridLayout());
        GridData griddata = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(griddata);
        group.setLayout(new GridLayout(2, false));

        classpath = new List(group, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        griddata = new GridData(GridData.FILL_BOTH);
        griddata.widthHint = 250; // TODO: Hardscoded
        griddata.heightHint = classpath.getItemHeight() * 10; // TODO: Hardcoded
        classpath.setLayoutData(griddata);
        classpath.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });
        
        // The classpath Button bar
        Composite composite = new Composite(group, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 2;
        composite.setLayout(layout);

        addjar = new Button(composite, SWT.NONE);
        addjar.setText("Add JAR File...");
        addjar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });
        griddata = new GridData(GridData.FILL_HORIZONTAL);
        griddata.widthHint = 130;
        addjar.setLayoutData(griddata);

        addfolder = new Button(composite, SWT.NONE);
        addfolder.setText("Add Class Folder...");
        addfolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });
        griddata = new GridData(GridData.FILL_HORIZONTAL);
        griddata.widthHint = 130;
        addfolder.setLayoutData(griddata);

        remove = new Button(composite, SWT.NONE);
        remove.setText("Remove");
        remove.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });
        griddata = new GridData(GridData.FILL_HORIZONTAL);
        griddata.widthHint = 130;
        remove.setLayoutData(griddata);
        
        return top;
    }

    public void load() {
        JSmoothModelBean jsmodel = getApplication().getModelBean();
    }

    protected void configureResources() {
        setImage(JSmoothResources.IMG_SWITCHER_APPLICATION);
        setToolTip("JVM Options");
    }
}