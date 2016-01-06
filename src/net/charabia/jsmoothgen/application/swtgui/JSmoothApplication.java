/*
 * Created on Nov 19, 2003
 */
package net.charabia.jsmoothgen.application.swtgui;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.charabia.jsmoothgen.application.ExeCompiler;
import net.charabia.jsmoothgen.application.JSmoothModelBean;
import net.charabia.jsmoothgen.application.JSmoothModelPersistency;
import net.charabia.jsmoothgen.application.JavaPropertyPair;
import net.charabia.jsmoothgen.application.swtgui.resources.JSmoothResources;
import net.charabia.jsmoothgen.skeleton.SkeletonBean;
import net.charabia.jsmoothgen.skeleton.SkeletonList;
import net.charabia.jsmoothgen.skeleton.SkeletonProperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Dumon
 */
public final class JSmoothApplication {
    public final JSmoothAction ACTION_OPEN = new OpenAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_EXIT = new ExitAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_SAVE = new SaveProjectAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_SAVE_AS = new SaveAsProjectAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_NEW = new NewAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_COMPILE = new CompileAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_RUNEXE = new RunexeAction(JSmoothApplication.this);
    public final JSmoothAction ACTION_CONSOLE_CLEAR = new ClearConsoleAction(JSmoothApplication.this);
    
    private Shell shell;
    private Display display;

    // Separators
    private Label menusep;
    private Label switchersep;

    // The layout of the page area composite
    private StackLayout stack;
    
    // JSmooth Pages
    public final JSmoothPage PAGE_SKELETON = new SkeletonPage(JSmoothApplication.this);
    public final JSmoothPage PAGE_EXECUTABLE = new ExecutablePage(JSmoothApplication.this);
    public final JSmoothPage PAGE_WELCOME = new WelcomePage(JSmoothApplication.this);
    public final JSmoothPage PAGE_APPLICATION = new JavaAppPage(JSmoothApplication.this);
    public final JSmoothPage PAGE_JVM_OPTIONS = new JVMOptionsPage(JSmoothApplication.this);
    public final JSmoothPage[] PAGES = new JSmoothPage[] {
                 PAGE_WELCOME,
                 PAGE_SKELETON,
                 PAGE_APPLICATION,
                 PAGE_JVM_OPTIONS,
                 PAGE_EXECUTABLE};
    
    // Page area
    private Composite pagearea;

    private Menu mainmenu;
    private ToolBar switcher;
    private Composite consolearea;
    private Text console;
    private static final int DIM_CONSOLE_HEIGHT = 150;
    
    // JSmooth related fields
    private boolean dirty = false;
    private JSmoothModelBean jsmodel;
    private File projectfile;
    private ExeCompiler compiler;
    private SkeletonList skeletonList;
    
    // Cached list of skeleton names
    private List skeletonNames;
    
    public JSmoothApplication(Display display) {
        new JSmoothResources(this.display = display);
        
        // FIXME: Hardcoded skeletons dir.
        this.skeletonList = new SkeletonList(new File("skeletons"));
    }
    
    public void newProject() {
        // Create a new empty JSmoothModelBean
        jsmodel = new JSmoothModelBean();
        jsmodel.setSkeletonName(getInitialSkeletonName());
        setSkeletonProperties(getInititalSkeletonProperties());
        jsmodel.setExecutableName("");
        jsmodel.setArguments("");
        jsmodel.setBundledJVMPath("");
        jsmodel.setClassPath(new String[0]);
        jsmodel.setCurrentDirectory("");
        jsmodel.setIconLocation("");
        jsmodel.setJarLocation("");
        jsmodel.setJavaProperties(new JavaPropertyPair[0]);
        jsmodel.setJVMSearchPath(new String[0]);
        jsmodel.setMainClassName("");
        jsmodel.setMaximumVersion("");
        jsmodel.setMinimumVersion("");
        jsmodel.setNoJvmMessage("");
        jsmodel.setNoJvmURL("");
        
        projectfile = null;
        compiler = new ExeCompiler();
        compiler.addListener(new SWTCompileListener());
        
        for (int i = 0; i < PAGES.length; i++) {
            PAGES[i].load();
        }
        
        getShell().setText(getProjectName());
    }
    
    public String getProjectName() {
        if (projectfile == null) {
            return "";
        }
        return projectfile.getName();
    }
    
    public void setSkeletonProperty(SkeletonProperty property) {
        System.out.println("[DEBUG] Setting property \"" + property.getIdName() + "\" to value \"" + property.getValue() + "\"");
        JSmoothModelBean.Property[] modelProps = jsmodel.getSkeletonProperties();
        for (int i = 0; i < modelProps.length; i++) {
            if (property.getIdName().equals(modelProps[i].Key)) {
                modelProps[i].setValue(property.getValue());
            }
        }
    }
    
    public void setSkeletonProperties(SkeletonProperty[] props) {
        // Transfer the SkeletonProperty data to JSmoothModelBean.Property
        JSmoothModelBean.Property[] modelProps = new JSmoothModelBean.Property[props.length];
        String sysoutProps = "";
        for (int i = 0; i < props.length; i++) {
            modelProps[i] = new JSmoothModelBean.Property();
            System.out.println("[DEBUG] Setting property \"" + props[i].getIdName() + "\" to value \"" + props[i].getValue() + "\"");
            modelProps[i].Key = props[i].getIdName();
            modelProps[i].setValue(props[i].getValue());
        }
        jsmodel.setSkeletonProperties(modelProps);
    }
    
    public SkeletonProperty[] getInititalSkeletonProperties() {
        SkeletonBean skeleton = skeletonList.getSkeleton(jsmodel.getSkeletonName());
        return skeleton.getSkeletonProperties();
    }
    
    public SkeletonProperty[] getSkeletonProperties() {
        SkeletonBean skeleton = skeletonList.getSkeleton(jsmodel.getSkeletonName());
        SkeletonProperty[] skeletonProps = skeleton.getSkeletonProperties();
        JSmoothModelBean.Property[] modelProps = jsmodel.getSkeletonProperties();
        for (int i = 0; i < skeletonProps.length; i++) {
            if (skeletonProps[i].getIdName().equals(modelProps[i].Key)) {
                skeletonProps[i].setValue(modelProps[i].getValue());
            }
        }
        return skeletonProps;
    }

    /**
     * Creates the "Page Switcher" toolbar manager and its control. The "Page
     * Switcher" lies on the left of the JSmooth window, and is a vertical
     * toolbar, with toggle items. On item selection, the window contents change.
     */
    private void createSwitcherControl(Shell shell) {
        switcher = new ToolBar(shell, SWT.FLAT | SWT.NO_FOCUS | SWT.VERTICAL);
        PAGE_WELCOME.createToolItem(switcher);
        PAGE_SKELETON.createToolItem(switcher);
        PAGE_APPLICATION.createToolItem(switcher);
        PAGE_EXECUTABLE.createToolItem(switcher);
        // PAGE_JVM_OPTIONS.createToolItem(switcher);
    }
    
    private void createMainMenu(Shell shell) {
        mainmenu = new Menu(shell, SWT.BAR);
        
        /* ==== FILE ACTIONS ==== */
        
        MenuItem topItem = new MenuItem(mainmenu, SWT.CASCADE);
        topItem.setText("File");
        Menu menu = new Menu(shell, SWT.DROP_DOWN);
        topItem.setMenu(menu);
        
        MenuItem item = new MenuItem(menu, SWT.NULL);
        item.setText("New");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_NEW.run();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Open...");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_OPEN.run();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Save");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_SAVE.run();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Save As...");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_SAVE_AS.run();
            }
        });
        
        item = new MenuItem(menu, SWT.SEPARATOR);
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Exit");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_EXIT.run();
            }
        });
        
        /* ==== PROJECT ACTIONS ==== */
        
        topItem = new MenuItem(mainmenu, SWT.CASCADE);
        topItem.setText("Project");
        menu = new Menu(shell, SWT.DROP_DOWN);
        topItem.setMenu(menu);
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Compile");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_COMPILE.run();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Run .exe");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_RUNEXE.run();
            }
        });
        
        /* ==== CONSOLE ACTIONS ==== */
        
        topItem = new MenuItem(mainmenu, SWT.CASCADE);
        topItem.setText("Console");
        menu = new Menu(shell, SWT.DROP_DOWN);
        topItem.setMenu(menu);
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Clear");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_CONSOLE_CLEAR.run();
            }
        });
        
        shell.setMenuBar(mainmenu);
    }

    private void createPages(Shell shell) {
        pagearea = new Composite(shell, SWT.NONE);
        stack = new StackLayout();
        pagearea.setLayout(stack);
        for (int i = 0; i < PAGES.length; i++) {
            PAGES[i].createControl(pagearea);
        }
    }
    
    public void showPage(JSmoothPage page) {
        stack.topControl = page.getControl();
        if (pagearea != null) pagearea.layout();
        ToolItem item = page.getToolItem();
        if (!item.getSelection()) item.setSelection(true);
    }

    public static void main(String[] args) {
        new JSmoothApplication(Display.getDefault()).run();
    }
    
    public void run() {
        if (shell == null) createControls();

        // open the window
        shell.open();
        
        newProject();
        showPage(PAGE_WELCOME);
        
        try {
            while (shell != null && !shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            display.update();
        } finally {
            display.dispose();
        }
    }
    
    private void createControls() {
        // Create the shell
        shell = new Shell(display, SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.RESIZE);

        // The application's menu bar
        createMainMenu(shell);

        // Horizontal separator label.
        // Separes the menu bar from the rest.
        menusep = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);

        createConsole(shell);
        
        // The page switcher toolbar
        createSwitcherControl(shell);

        // Vertical separator label.
        // Separes the switcher toolbar from the rest.
        switchersep = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);

        createPages(shell);
        
        // The JSmooth layout
        // NOTE: it should be set *after* creating the controls
        shell.setLayout(new JSmoothLayout());

        // Initialize the bounds of the shell to that appropriate for the contents
        initializeBounds();
    }

    private void createConsole(Shell shell) {
        consolearea = new Composite(shell, SWT.NULL);
        GridLayout gridlayout = new GridLayout();
        consolearea.setLayout(gridlayout);
        
        console = new Text(consolearea, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER);
        console.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        console.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        GridData gridata = new GridData(GridData.FILL_BOTH);
        console.setLayoutData(gridata);
        
        /* ==== CONSOLE MENU ==== */
        
        Menu menu = new Menu(shell, SWT.NULL);
        
        MenuItem item = new MenuItem(menu, SWT.NULL);
        item.setText("Clear");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ACTION_CONSOLE_CLEAR.run();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Copy");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                console.copy();
            }
        });
        
        item = new MenuItem(menu, SWT.NULL);
        item.setText("Copy All");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                console.selectAll();
                console.copy();
            }
        });
        
        console.setMenu(menu);
    }

    private void initializeBounds() {
        Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        Point location = getInitialLocation(size);
        shell.setBounds(location.x, location.y, size.x, size.y);
    }

    private Point getInitialLocation(Point size) {
        Rectangle screen = display.getClientArea();
        int x = Math.max(0, screen.x + (screen.width - size.x) / 2);
        int y = Math.max(0, screen.y + (screen.height - size.y) / 3);
        return new Point(x, y);
    }

    public Shell getShell() {
        return shell;
    }
    
    private void fillSkeletonList(List list) {
        Iterator i = skeletonList.getIteratorNoDebugName();
        while (i.hasNext()) {
            list.add((String) i.next());
        }
    }

    public List getSkeletonList(boolean flush) {
        // True at the first call to this method
        if (skeletonNames == null) fillSkeletonList(skeletonNames = new Vector());
        if (flush) {
            skeletonNames.clear();
            fillSkeletonList(skeletonNames);
        }

        // From the "cache"
        return skeletonNames;
    }

    public String[] getAllSkeletonNames() {
        return (String[]) getSkeletonList(false).toArray(new String[0]);
    }
    
    public String getInitialSkeletonName() {
        // We want to return the "Window Wrapper" skeleton.
        // Due to the fact that we cannot identify the skeletons :(,
        // we'll have to make a hard guess.
        // It seems like the "Window Wrapper" is the second in the
        // list. In case it is not , we catch the exception,
        // and return the first skeleton in the list.
        String[] skelNames = getAllSkeletonNames();
        try {
            return skelNames[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return skelNames[0];
        }
    }

    public JSmoothModelBean getModelBean() {
        return jsmodel;
    }
    
    public boolean saveProjectAs(String projectfile) {
        System.out.println("[DEBUG] Saving project to file: " + projectfile);
        this.projectfile = new File(projectfile);
        try {
            JSmoothModelPersistency.save(this.projectfile, jsmodel);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed saving project : " + e.getMessage());
            return false;
        }
        getShell().setText(getProjectName());
        return true;
    }
    
    public boolean saveProject() {
        return saveProjectAs(projectfile.getAbsolutePath());
    }
    
    public File getProjectFile() {
        return projectfile;
    }
    
    public boolean hasProjectFile() {
        return projectfile != null;
    }
    
    public void exit() {
        getShell().close();
    }
    
    public boolean openProject(String projectfile) {
        System.out.println("[DEBUG] Opening project : " + projectfile);
        File file = new File(projectfile);
        JSmoothModelBean jsmodel = null;
        try {
            jsmodel = JSmoothModelPersistency.load(file);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed opening project : " + e.getMessage());
            return false;
        }
        
        this.projectfile = file;
        this.jsmodel = jsmodel;
        
        for (int i = 0; i < PAGES.length; i++) {
            PAGES[i].load();
        }
        
        getShell().setText(getProjectName());
        return true;
    }
    
    public boolean compileProject() {
        consoleMessage("=== Compilation ===");
        File basedir = projectfile.getParentFile();
        jsmodel.normalizePaths(basedir);
        String skeletonName = jsmodel.getSkeletonName();
        SkeletonBean skeletonBean = skeletonList.getSkeleton(skeletonName);
        File skeletonRoot = skeletonList.getDirectory(skeletonBean);
        
        // NOTE: We assume the exe name is always relative
        File exename = new File(jsmodel.getExecutableName());
        if (!exename.isAbsolute()) {
            exename = new File(basedir, jsmodel.getExecutableName());
        }
        
        boolean success = false;
        try {
            success = compiler.compile(skeletonRoot, skeletonBean, basedir, jsmodel, exename);
        } catch (Exception e) {
            // Do nothing, bellow we the errors anyway (see bellow).
        }
        
        consoleMessages((String[]) compiler.getErrors().toArray(new String[0]));
        compiler.cleanErrors();
        
        return success;
    }
    
    public void consoleMessage(String msg) {
        if (console.getText().length() != 0) console.append("\r\n"); // New line ?
        console.append(msg);
    }
    
    public void consoleMessages(String[] msg) {
        for (int i = 0; i < msg.length; i++) {
            consoleMessage(msg[i]);
        }
    }
    
    public void clearConsole() {
        console.setText("");
    }
    
    public void consoleSection(String title) {
        StringBuffer buffer = new StringBuffer("-------------------------------------------------------------------------------------");
        if (title == null) {
            consoleMessage(buffer.toString());
            return;
        }
        buffer.replace(0, title.length() + 1, title + " ");
        consoleMessage(buffer.toString());
    }
    
    /**
     * Special layout for the JSmooth Window.
     */
    class JSmoothLayout extends Layout {
        protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }

            Point result = new Point(0, 0);
            Control[] ctrls = composite.getChildren();
            Point p = null;
            for (int i = 0; i < ctrls.length; i++) {
                Control w = ctrls[i];
                if (menusep == w) {
                    p = w.computeSize(wHint, hHint, flushCache);
                    result.y += p.y;
                }
                else if (switcher == w) {
                    p = w.computeSize(wHint, hHint, flushCache);
                    result.x += p.x;
                    result.y += p.y;
                }
                else if (switchersep == w) {
                    p = w.computeSize(wHint, hHint, flushCache);
                    result.x += p.x;
                }
                else if (consolearea == w) {
                    p = w.computeSize(wHint, hHint, flushCache);
                    result.y += DIM_CONSOLE_HEIGHT;
                }
                else if (pagearea == w) {
                    p = w.computeSize(wHint, hHint, flushCache);
                    result.x += p.x;
                    result.y = p.y + DIM_CONSOLE_HEIGHT;
                }
            }

            if (wHint != SWT.DEFAULT) result.x = wHint;
            if (hHint != SWT.DEFAULT) result.y = hHint;

            return result;
        }

        protected void layout(Composite cmp, boolean flushCache) {
            if (flushCache) {
                for (int i = 0; i < PAGES.length; i++) {
                    ((Composite) PAGES[i].getControl()).layout();
                }
            }

            Rectangle clientarea = cmp.getClientArea();
            Control[] wgs = cmp.getChildren();
            Point p = null;
            for (int i = 0; i < wgs.length; i++) {
                Control w = wgs[i];
                if (menusep == w) {
                    p = w.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
                    w.setBounds(clientarea.x, clientarea.y, clientarea.width, p.y);
                    clientarea.y += p.y;
                    clientarea.height += p.y;
                }
                else if (switcher == w) {
                    p = w.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
                    w.setBounds(clientarea.x, clientarea.y, p.x, p.y);
                    clientarea.x += p.x;
                    clientarea.width -= p.x;
                }
                else if (switchersep == w) {
                    p = w.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
                    w.setBounds(clientarea.x, clientarea.y, p.x, clientarea.height);
                    clientarea.x += p.x;
                    clientarea.width -= p.x;
                }
                else if (consolearea == w) {
                    p = w.computeSize(SWT.DEFAULT, DIM_CONSOLE_HEIGHT, flushCache);
                    w.setBounds(clientarea.x, clientarea.height - DIM_CONSOLE_HEIGHT, clientarea.width, p.y);
                    clientarea.height -= DIM_CONSOLE_HEIGHT;
                }
                else if (pagearea == w) {
                    w.setBounds(clientarea.x, clientarea.y, clientarea.width, clientarea.height);
                }
            }

            // Resize to fit all child controls
            if (flushCache) cmp.setSize(cmp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    }
    
    class SWTCompileListener implements ExeCompiler.StepListener {
        public void setNewState(int percent, String state) {
            consoleMessage(state);
        }

        public void failed() {
            consoleMessage("Compile failed.");
        }

        public void complete() {
            consoleMessage("Compile successfull.");
        }
    }
}