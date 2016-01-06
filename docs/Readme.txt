JSmooth: a VM wrapper toolkit for Windows
Copyright (C) 2003 Rodrigo Reyes <reyes@charabia.net>

JSmooth is a Java Executable Wrapper that makes a standard Windows
executable binary (.exe) from a jar file. It makes java deployment
much smoother and user-friendly, as it is able to find a Java VM by
itself. When no VM is available, it provides feed-back to the users, 
and can launch the default web browser to an URL that explains how 
to download a Java VM.

Current version: @{VERSION}@
Current release: @{RELEASEINFO}@

1. Status of the project

 JSmooth is still in beta phase. However, note that the jsmooth
 package uses a jsmooth wrapper for its own deployment, which should
 be a good indicator of the reliability of the software.

2. Installation

 The JSmooth software is distributed with a nice installer, so that
 installation shouldn't be an issue.

 However, JSmooth requires Java 1.4 or higher. If you are not sure it
 is installed, just run the JSmoothGen.exe program, and it will
 automatically try to locate it.

 In case of problem, you can use the run.bat script in the bin/
 directory.

3. Additional software

 If you want to be able to use .ico files for your icons (instead of
 the default .GIF, .PNG, and .JPG), you need to install Sun's JIMI
 package. This package is not free software, this is why you need to
 download it separately.

 Download it on http://www.javasoft.com website, then extract the
 JimiProClasses.zip file, and copy it in the lib/ directory where
 JSmooth is installed (probably on c:\program files\jsmooth).

4. License

All the JSmooth project is distributed under the terms of the GNU
General Public License. Please read the License.txt file that comes
with the package.

This license applies to all the files of the project, but not on the
generated executable. This means that you are free to generate
executable wrappers for proprietary software and distribute them
without applying the terms of the GPL to them.

Of course, this is the one and only exception. All other kind of
distribution of any file of the JSmooth package must conform with the
terms of the GNU General Public License.
