# JSmooth
JSmooth is a Java Executable Wrapper that builds standard Windows
executable binaries (.exe) that launch java applications. Such binaries
contain all the information needed to launch your java application,
i.e. the classpath, the java properties, the jvm version required, and
so on. If Java is not installed, it helps the users by displaying them a
notice before launching automatically a browser to a web site where they
can download a JVM.

This is a fork of [JSmooth 0.9.9-7](http://http://jsmooth.sourceforge.net/), with these additional features:
- 64bit support.
- Autodownload wrapper silent JRE installation from web/local file system with redirect support.
- VERSIONINFO support.
- Custom Java home environment variable.
- Pass Java system properties via command line.
- Native win32 dialogs.

There's no additional UI for configuring the above features. You'll have to edit your `.jsmooth` file manually.

## Using the software

### 64bit support
a

### Autodownload wrapper silent JRE installation
The `Custom Web Download Wrapper` skeleton has been enhanced:
- The `DownloadURL` skeleton property may point to local file using the `file:` prefix. I.e. `file:myjre\jre.exe` (use backslash for path information).
- The `DownloadURL` skeleton property may point to a file called `autodownload.redirect`, which resides in the same folder as the executable.
    This file consists of the effective URL and optional parameters passing to the executable:
    ```
    url=http://mysite/jre.exe
    params=/s
    ```
    
    The URL can again point also a local file using the `file:` prefix.

### VERSIONINFO support

To include a VERSIONINFO resource into the generated `.exe`, add this XML fragment to your `.jsmooth` file:

```
<versionInfo>
    <binaryFileVersion>99.99.0.0</binaryFileVersion>
    <binaryProductVersion>99.99.0.0</binaryProductVersion>
    <productVersion>99.99.0.0 HEAD-Build14618</productVersion>
    <productName>my app</productName>
    <legalCopyright>Copyright (C) 2015 - My company</legalCopyright>
    <companyName>My company</companyName>
    <originalFilename>app.exe</originalFilename>
    <fileDescription>app.exe</fileDescription>
</versionInfo>
```

See the <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa381058(v=vs.85).aspx">Microsoft Documentation</a> for a description of all the elements.

### Custom Java home environment variable
Besides searching for the JRE at the fixed locations JAVA_HOME, JRE_HOME etc, one can specify a custom environment variable:

```
...
<JVMSearchPath>customjavahome=MY_JAVA_HOME</JVMSearchPath>
...
```
This will search for a JRE at the location contained in the environment variable `MY_JAVA_HOME`.

## Compiling the project
The section below only applies if you need to build JSmooth from its
sources. If you just need to create an executable binary for your
application, just run JSmoothGen as it comes from its standard
distribution.

### MINGW for Windows (required)

 You need the MINGW GCC compiler to compile the Windows wrappers. Just
 install the last version (3.1.0 or above) from the following website:

  http://www.mingw.org/

 You need to download the MINGW-v.exe package (where v is the
 version). For instance MinGW-3.1.0-1.exe should be fine. You don't need
 any other package, so don't be afraid of all the stuff available on
 their web site.

 Once installed, you still need to setup your %PATH% environment
 variable to add the bin/ directory where MINGW is installed.

### Documentation (optional)

 If you want to build the whole distribution package, including the
 documentation, you need to install the docbook compilation chain.

 Install:
- http://xml.apache.org/xalan-j/ and put the xalan jars into
the lib folder of ANT. This is required to make ANT able to
process XSLT.

- docbook-xsl, available at http://sourceforge.net/projects/docbook/
    Just download the docbook-xsl package, you don't need anything else here.

    If you compile with a JDK 1.5 or above, you are very likely
    to experience issues with the built-in xslt engine. A
    work-around is to override the default engine with xalan
    (for instance). Download Xalan and put its jars in the
    jre/lib/endorsed/ directory (you may have to create it).

- FOP, available at http://xml.apache.org/fop/

### Configure

- Open the build.xml ant script at the root of the project, and change the properties located at the top of the file. They are just under the comment:

   `<!-- set here the properties specific to your computer -->`

Read carefully the comments and set the properties accordingly.

### Building the project

To build the project, run the following command:
   
`ant jar`

To build the wrappers:

`ant compileskels`

To run the program:

`ant run`

To build a distribution:

`ant dist`
