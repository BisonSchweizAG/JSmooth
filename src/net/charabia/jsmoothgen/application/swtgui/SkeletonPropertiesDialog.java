/*
 * Created on May 15, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.charabia.jsmoothgen.skeleton.SkeletonProperty;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Dumon
 */
public class SkeletonPropertiesDialog extends Dialog {
    private Text text;
    private Button check;
    private List controls = new Vector();
    private SkeletonPage page;
    private JSmoothApplication app;
    
    public SkeletonPropertiesDialog(SkeletonPage page) {
        super(page.getApplication().getShell());
        this.page = page;
    }

    protected Control createDialogArea(Composite parent) {
        Composite cmpDlgArea = new Composite(parent, SWT.NONE);
        cmpDlgArea.setLayout(new GridLayout());

        SkeletonProperty[] props = page.getApplication().getSkeletonProperties();
        for (int i = 0; i < props.length; i++) {
            System.out.println("[DEBUG] Loading skeleton property: " + props[i].getIdName() + "=" + props[i].getValue());
        }
        
        for (int i = 0; i < props.length; i++) {
            Control c = createPropertyControl(cmpDlgArea, props[i]);
            c.setData(props[i]);
            controls.add(c);
        }

        return cmpDlgArea;
    }

    private Control createPropertyControl(Composite wParent, SkeletonProperty prop) {
        Group group = null;
        GridData grid = null;
        if (prop.getType().equals(SkeletonProperty.TYPE_STRING)) {
            group = new Group(wParent, SWT.NONE);
            grid = new GridData(GridData.FILL);
            grid.widthHint = 400;
            group.setLayoutData(grid);
            group.setLayout(new GridLayout());
            group.setText(prop.getLabel());

            text = new Text(group, SWT.SINGLE | SWT.BORDER);
            grid = new GridData(GridData.FILL_BOTH);
            text.setLayoutData(grid);
            text.setText(prop.getValue());

            return text;
        }
        else if (prop.getType().equals(SkeletonProperty.TYPE_TEXTAREA)) {
            group = new Group(wParent, SWT.NONE);
            grid = new GridData(GridData.FILL);
            grid.widthHint = 400;
            grid.heightHint = 100;
            group.setLayoutData(grid);
            group.setLayout(new GridLayout());
            group.setText(prop.getLabel());

            text = new Text(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            grid = new GridData(GridData.FILL_BOTH);
            text.setLayoutData(grid);
            text.setText(prop.getValue());

            return text;
        }
        else if (prop.getType().equals(SkeletonProperty.TYPE_BOOLEAN)) {
            Button chk = new Button(wParent, SWT.CHECK);
            chk.setText(prop.getLabel());
            chk.setSelection("1".equals(prop.getValue()));

            return chk;
        }
        else {
            throw new UnsupportedOperationException("Unknown skeleton property type.");
        }
    }

    protected void okPressed() {
        Iterator it = controls.iterator();
        
        JSmoothApplication app = page.getApplication();
        Control ctrl = null;
        String value = null;
        SkeletonProperty prop = null;
        while (it.hasNext()) {
            ctrl = (Control) it.next();
            prop = (SkeletonProperty) ctrl.getData();
            if (prop.getType().equals(SkeletonProperty.TYPE_STRING)) {
                value = ((Text) ctrl).getText();
                prop.setValue(value);
            }
            else if (prop.getType().equals(SkeletonProperty.TYPE_TEXTAREA)) {
                value = ((Text) ctrl).getText();
                prop.setValue(value);
            }
            else if (prop.getType().equals(SkeletonProperty.TYPE_BOOLEAN)) {
                boolean b = ((Button) ctrl).getSelection();
                value = (b == true) ? "1" : "0";
                prop.setValue(value);
            }
            app.setSkeletonProperty(prop);
        }

        super.okPressed();
    }

}