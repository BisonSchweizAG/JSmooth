/*
 * Created on May 13, 2004
 */
package net.charabia.jsmoothgen.application.swtgui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Dumon
 */
public abstract class JSmoothPage {
    private Control control;
    private JSmoothApplication js;
    private Set modifyListeners = new HashSet();
    private String toolTip = "";
    private Image image;
    private boolean hidden = false;
    private String id = "";
    private ToolItem item;
    
    public JSmoothPage(JSmoothApplication js) {
        this.js = js;
    }
    
    public final Control createControl(Composite parent) {
        return control = createPageArea(parent);
    }
    
    public ToolItem createToolItem(final ToolBar toolbar) {
        configureResources();
        item = new ToolItem(toolbar, SWT.RADIO);
        item.setImage(getImage());
        item.setToolTipText(getToolTip());
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                js.showPage(JSmoothPage.this);
                ToolItem[] items = toolbar.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] != item) items[i].setSelection(false);
                }
            }
        });
        return item;
    }
    
    public ToolItem getToolItem() {
        return item;
    }
    
    protected abstract Control createPageArea(Composite parent);
    protected abstract void configureResources();
    
    public Control getControl() {
        return control;
    }

    protected void setControl(Control cmp) {
        control = cmp;
    }

    protected Shell getShell() {
        return js.getShell();
    }
    
    protected void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }
    
    protected String getToolTip() {
        return toolTip;
    }
    
    public String getId() {
        return id;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
    
    protected Image getImage() {
        return image;
    }
    
    protected void setImage(Image image) {
        this.image = image;
    }
    
    protected JSmoothApplication getApplication() {
        return js;
    }
    
    protected void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public abstract void load();
}