/*
 * Created on May 30, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import net.charabia.jsmoothgen.application.swtgui.resources.JSmoothResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Dumon
 */
public class WelcomePage extends JSmoothPage {

    public WelcomePage(JSmoothApplication js) {
        super(js);
    }

    public Control createPageArea(Composite parent) {
        Display display = parent.getDisplay();
        
        Composite top = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        top.setLayout(layout);
        
        FormToolkit kit = new FormToolkit(parent.getDisplay());
        Form form = kit.createForm(top);
        GridData grid = new GridData(GridData.FILL_BOTH);
        grid.widthHint = 400;
        form.setLayoutData(grid);
        form.setText("Welcome to JSmooth !");
        TableWrapLayout wraplayout = new TableWrapLayout();
        form.getBody().setLayout(wraplayout);
        HyperlinkGroup hypergroup = kit.getHyperlinkGroup();
        hypergroup.setActiveForeground(display.getSystemColor(SWT.COLOR_BLUE));
        hypergroup.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        hypergroup.setHyperlinkUnderlineMode(HyperlinkGroup.UNDERLINE_HOVER);
        
        Label label = kit.createSeparator(form.getBody(), SWT.HORIZONTAL);
        TableWrapData wrapgrid = new TableWrapData(TableWrapData.FILL_GRAB);
        label.setLayoutData(wrapgrid);
        
        FormText text = kit.createFormText(form.getBody(), true);
        wrapgrid = new TableWrapData(TableWrapData.FILL);
        text.setLayoutData(wrapgrid);
        text.setText(JSmoothResources.TEXT_HELP_WELCOME, true, false);
        
        return top;
    }

    public boolean apply() {
        return false;
    }

    protected void configureResources() {
        setImage(JSmoothResources.IMG_SWITCHER_WELCOME);
        setToolTip("Welcome");
    }

    public void load() {
        // Do nothing.
    }

}