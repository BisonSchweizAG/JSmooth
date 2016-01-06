JSmooth: a VM wrapper toolkit for Windows
Copyright (C) 2003-2007 Rodrigo Reyes <reyes@charabia.net>

JSmooth is a Java Executable Wrapper that builds standard Windows
executable binaries (.exe) that launch java applications. Such binaries
contain all the information needed to launch your java application,
i.e. the classpath, the java properties, the jvm version required, and
so on. If Java is not installed, it helps the users by displaying them a
notice before launching automatically a browser to a web site where they
can download a JVM.

1. Status of the project

 JSmooth is currently in advanced beta phase. 

2 Using the software

 Double-clik on the JSmoothGen.exe icon.

3. Compiling the project

 -- PREREQUISITE --

The section below only applies if you need to build JSmooth from its
sources. If you just need to create an executable binary for your
application, just run JSmoothGen as it comes from its standard
distribution.
 
 3.1 Get the source

 You can get the source code at sourceforge's:
http://sourceforge.net/projects/jsmooth

 3.2 MINGW for Windows (required)

 You need the MINGW GCC compiler to compile the Windows wrappers. Just
 install the last version (3.1.0 or above) from the following website:

  http://www.mingw.org/

 You need to download the MINGW-v.exe package (where v is the
 version). For instance MinGW-3.1.0-1.exe should be fine. You don't need
 any other package, so don't be afraid of all the stuff available on
 their web site.

 Once installed, you still need to setup your %PATH% environment
 variable to add the bin/ directory where MINGW is installed.
 
 3.3 DOCUMENTATION STUFF (optional)

 If you want to build the whole distribution package (including the
 documentation, you need to install the docbook compilation chain).

 Install:

	- http://xml.apache.org/xalan-j/ and put the xalan jars into
	the lib folder of ANT. This is required to make ANT able to
	process XSLT.

	- docbook-xsl, available at
          http://sourceforge.net/projects/docbook/
	  Just download the docbook-xsl package, you don't need anything
          else here.

	  If you compile with a JDK 1.5 or above, you are very likely
	  to experience issues with the built-in xslt engine. A
	  work-around is to override the default engine with xalan
	  (for instance). Download Xalan and put its jars in the
	  jre/lib/endorsed/ directory (you may have to create it).

	- FOP, available at http://xml.apache.org/fop/

 3.4 FLTK library

 FLTK, available at http://www.fltk.org The easiest way is probably to
 grab a recent source package of fltk, then to run under an MSYS/MINGW
 environment the three commands: "./configure", "make", and "make
 install".

 3.5 Configure:

 - Open the build.xml ant script at the root of the project, and
 change the properties located at the top of the file. They are just
 under the comment:

   <!-- set here the properties specific to your computer -->

 Read carefully the comments and set the properties accordingly.

 3.6 Building the project

  To build the project, run the following command: ant jar
  To build the wrappers: ant compileskels
  To run the program: ant run
  To build a distribution: ant dist

4. License

The JSmooth project is distributed under the terms of the GNU General
Public License. Please read the License.txt file that comes with the
package. Additional software bundled with JSmooth may come with their
own license, please check.

The executable generated (the launchers created by JSmooth) are under
the LGPL with a "runtime exception" similar to the gcc licence
exception: It is not required that you distribute the source code
with, nor that you publish a notice mentionning jsmooth.
