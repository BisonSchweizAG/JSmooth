# JSmooth
JSmooth is a Java Executable Wrapper that builds standard Windows
executable binaries (.exe) that launch java applications. Such binaries
contain all the information needed to launch your java application,
i.e. the classpath, the java properties, the jvm version required, and
so on. If Java is not installed, it helps the users by displaying them a
notice before launching automatically a browser to a web site where they
can download a JVM.

This is a fork of [JSmooth 0.9.9-7](http://jsmooth.sourceforge.net/), with these additional features:
- 64bit support.
- Autodownload wrapper enhancements.
- VERSIONINFO support.
- Custom Java home environment variable.
- Pass Java system properties via command line.
- Native win32 dialogs.

There's no additional UI for configuring the above features. You'll have to edit your `.jsmooth` file manually.

## Installing
Dowload and extract the [latest release](https://github.com/BisonSchweizAG/JSmooth/releases) to a local folder.

## Using the software
See the original JSmooth's [manual](http://jsmooth.sourceforge.net/docs/jsmooth-doc.html).

## Additional features

### 64bit support

To create a 64bit executable, use one of the new `x64` skeletons:

* `Autodownload Wrapper x64`
* `Console Wrapper x64`
* `Windowed Wrapper x64`
* `WinService Wrapper x64`

A 64bit executable needs a 64bit JRE.

### Autodownload wrapper enhancements
The `Autodownload Wrapper` skeleton has been enhanced:
- The `DownloadURL` property may point to local file using the `file:` prefix. I.e. `file:myjre\jre.exe` (use backslash for path information).
- The `DownloadURL` property may point to a file called `autodownload.redirect`.
    This file consists of the effective URL and optional parameters passing to the executable:
    ```
    url=http://mysite/jre.exe
    params=/s
    ```

    The URL can again point also to a local file using the `file:` prefix.

### VERSIONINFO support

To include a VERSIONINFO resource into the generated `.exe`, add this XML fragment to your `.jsmooth` file:

```
<versionInfo>
    <binaryFileVersion>99.99.0.0</binaryFileVersion>
    <binaryProductVersion>99.99.0.0</binaryProductVersion>
    <productVersion>99.99.0.0 HEAD-Build14618</productVersion>
    <productName>my app</productName>
    <legalCopyright>Copyright (C) 2016 - My company</legalCopyright>
    <companyName>My company</companyName>
    <originalFilename>myapp.exe</originalFilename>
    <fileDescription>myapp.exe</fileDescription>
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

### Pass Java system properties via command line

Pass a system property on the command line:
```
myapp.exe -Dmyprop=myvalue
```

This will create a system property named `myprop` with the value `myvalue`.

A previously defined property with the same name will be overwritten.

