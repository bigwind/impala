# New Project QuickStart #

This page tells you how to build your first Impala project, creating an Impala workspace and project structure, with a modular, test-backed, web application. The application is effectively the Impala Hello World application, but as you will see in part 2 and part 3, it will take you much further than simply printing a message on your screen.

## Downloading the Distribution ##

First, start by downloading the latest Impala distribution release from the project site.

```
http://code.google.com/p/impala/downloads/list
```

You can do this via the web browser. From Linux or Mac OSX you may find it more convenient to use curl.

```
cd ~
curl -o impala-1.0.3.zip http://impala.googlecode.com/files/impala-1.0.3.zip
```

You can then unzip the Impala distribution:

```
unzip impala-1.0.3.zip
```

## Generating New Project Artifacts ##

Once you've unzipped Impala, set the IMPALA\_HOME environment property.

```
IMPALA_HOME=~/impala-1.0.3
export IMPALA_HOME
```

In windows, you will probably want to use the GUI to do the same.

Now, change to IMPALA\_HOME, and run the following command:

```
ant newproject
```

You will then be guided through an interactive process where you will need to specify the following information:

  * the version of Impala corresponding to the contents of Impala home, which you will need to manually set if not using the SNAPSHOT build. (This step will be removed in the next release: see http://code.google.com/p/impala/issues/detail?id=75).
  * the name of the host project. This is a standard Java web project with the added capability of being able to host Impala modules.
  * the name of the project containing the root Impala module. This project is a kind of a master project, and will also be the project from which you will typically run ANT build scripts when this is necessary.
  * the name of the project containing a non-root module. In a real world application, non-root modules would contain implementations of DAOs, service methods - anything really. In a substantial real world application there will be several if not many non-root modules, together forming a hierarchy of modules.
  * the name of a project containing a web module. Although it is possible to have multiple web modules in a single application, the simple scaffolding starts with just one web module.
  * the name of the repository project. This contains the third party jars used by the different application modules.
  * finally, the name of the test project. The test project is really a convenience from which you can easily run suites of tests for the entire application.

The last time I ran this command, the following output was produced:

```
ant newproject
Buildfile: build.xml

newproject:
     [echo] Creating new project structure, Impala version 1.0

scaffold:input-workspace-root:
    [input] Please enter name of workspace root directory: [/Users/philzoio/workspaces/newproject]


scaffold:input-build-project:
    [input] Please enter build project name, to be used for build module: [build]


scaffold:input-host-project:
    [input] Please enter host project name, to be used for host web application: [host]


scaffold:input-main-project:
    [input] Please enter main project name, to be used for root module: [main]


scaffold:input-module-project:
    [input] Please enter name of first non-root module: [module1]


scaffold:input-web-project:
    [input] Please enter name of web module: [web]


scaffold:input-spring-version:
    [input] Please enter Spring version (e.g. 3.2.2.RELEASE): [3.2.2.RELEASE]


scaffold:input-test-project:
    [input] Please enter name of tests project: [test]


scaffold:input-repository-project:
    [input] Please enter name of repository project: [repository]


scaffold:input-project-prefix:
    [input] Please enter the project name prefix: []


scaffold:input-base-package:
    [input] Please enter the base package to be used for application: [com.application]


scaffold:newproject-confirm:
     [echo] Workspace root location: /Users/philzoio/workspaces/newproject
     [echo] Build project name: host
     [echo] Host web application project name: build
     [echo] Main (root) project name: main
     [echo] First application module project name: module1
     [echo] Web project name: web
     [echo] Spring version: 3.2.2.RELEASE
     [echo] Tests project name: test
     [echo] Repository project name: repository
     [echo] Servlet API version: 2.5 (Please consult http://code.google.com/p/impala/wiki/WebServlet24 if you need to work with the Servlet 2.4 API)
    [input] Press return key to continue, or CTRL + C to quit ...

...

scaffold:copytest:
     [copy] Copying 3 files to /Users/philzoio/workspaces/newproject/test
     [copy] Copied 3 empty directories to 1 empty directory under /Users/philzoio/workspaces/newproject/test

scaffold:copyrepository:
     [copy] Copying 1 file to /Users/philzoio/workspaces/newproject/repository
     [copy] Copied 2 empty directories to 1 empty directory under /Users/philzoio/workspaces/newproject/repository

scaffold:create:

BUILD SUCCESSFUL
Total time: 23 seconds

```

Note that this startup application uses the Servlet 2.5 API. If you wish to use the Servlet 2.4 API, see WebServlet24.

Before we import the Eclipse project, there are just two more steps to follow:

First, go the main newly project, and run the following two commands:

```
cd /Users/philzoio/workspaces/newproject/build
ant fetch get
```

The fetch target will copy the Impala libraries into the repository project of the new workspace, as shown by the following output.

```
ant fetch
Buildfile: build.xml
     [echo] Project using workspace.root: /Users/philzoio/workspaces/newproject
     [echo] Project using impala home: /Users/philzoio/impala-1.0.3

repository:fetch-impala-from-lib:
     [copy] Copying 22 files to /Users/philzoio/workspaces/newproject/repository/main

repository:fetch-impala-from-repository:

repository:fetch-impala:

fetch:

BUILD SUCCESSFUL
Total time: 0 seconds
```

The get command downloads the necessary third party libraries, as defined using a simple format in _dependencies.txt_ files.

```
ant get
Buildfile: build.xml
     [echo] Project using workspace.root: /Users/philzoio/workspaces/newproject
     [echo] Project using impala home: /Users/philzoio/impala-1.0.3

shared:get:

download:get:
    [mkdir] Created dir: /Users/philzoio/workspaces/newproject/repository/build
    [mkdir] Created dir: /Users/philzoio/workspaces/newproject/repository/test
 [download] Using following locations to retrieve resources: 
 [download] -------------------------------------------------
 [download] file:///Users/philzoio/.m2/repository/
 [download] http://repo1.maven.org/maven2/
 [download] http://ibiblio.org/pub/packages/maven2/
 [download] -------------------------------------------------
 [download] Retrieving new resource if available for commons-logging/commons-logging/1.1/commons-logging-1.1.jar
     [copy] Copying 1 file to /Users/philzoio/workspaces/newproject/repository/main
 [download] Retrieving new resource if available for commons-logging/commons-logging/1.1/commons-logging-1.1-sources.jar
 [download] Retrieving new resource if available for log4j/log4j/1.2.13/log4j-1.2.13.jar
 [download] Retrieving new resource if available for log4j/log4j/1.2.13/log4j-1.2.13-sources.jar
 
 ...
 
 [download] xerces/xercesImpl/2.8.1/xercesImpl-2.8.1.jar
 [download] resolved from
 [download] http://ibiblio.org/pub/packages/maven2/xerces/xercesImpl/2.8.1/xercesImpl-2.8.1.jar
 [download] xerces/xercesImpl/2.8.1/xercesImpl-2.8.1-sources.jar
 [download] resolved from
 [download] http://ibiblio.org/pub/packages/maven2/xerces/xercesImpl/2.8.1/xercesImpl-2.8.1-sources.jar
 [download] commons-el/commons-el/1.0/commons-el-1.0.jar
 [download] resolved from
 [download] http://ibiblio.org/pub/packages/maven2/commons-el/commons-el/1.0/commons-el-1.0.jar
 [download] commons-el/commons-el/1.0/commons-el-1.0-sources.jar
 [download] resolved from
 [download] http://ibiblio.org/pub/packages/maven2/commons-el/commons-el/1.0/commons-el-1.0-sources.jar
 [download] 
 [download] ******************************************************

get:

BUILD SUCCESSFUL
Total time: 7 minutes 4 seconds

```

We're now ready to import our projects into Eclipse. Start by opening Eclipse in the newly created workspace.

Use the menus File -> Import ... -> General -> Existing Projects Into Workspace. When prompted, set the import base directory to the workspace root directory. This should bring up a dialog box as shown below.

![http://impala.googlecode.com/svn/wiki/images/scaffold_import.png](http://impala.googlecode.com/svn/wiki/images/scaffold_import.png)

Select all of the projects and import them.

If you reach this point and no errors are showing in your workspace, the congratulations! You have just set up a new Impala workspace, with a working application.

## Workspace structure ##

The workspace we created in part one consists of the following Eclipse projects:

  * **myapp-build** contains the master build scripts for the application.
  * **myapp-host** contains the (static) host web application for deploying the modules, containing the _WEB-INF/web.xml_, JSPs, etc..
  * **myapp-main** the main project, which contains the application's root module.
  * **myapp-module1** a single sub-module, which contains an implementation of one of the services we define in the root module.
  * **myapp-repository** a project which serves purely as a repository for third party dependenciies, including the Impala libraries used by the application.
  * **myapp-web** a web project, consisting of web application classes, view templates, web configuration files, etc. Unlike the host part of application, this project contains web components which can be dynamically reloaded.
  * **myapp-tests** a tests project.

Here's a view of the workspace in Eclipse.

![http://impala.googlecode.com/svn/wiki/images/scaffold_messageService.png](http://impala.googlecode.com/svn/wiki/images/scaffold_messageService.png)

The roles played by the various parts of the project will become more clear in [part two](GettingStartedPart2.md), where we'll get to grips with the simple application that the quick starter has created. In [part three](GettingStartedPart3.md), we explore Impala's test environment in a bit more detail. In [part four](GettingStartedPart4.md), we'll look a bit more closely at working with a the web application.