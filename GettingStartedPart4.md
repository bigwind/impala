# Impala web application #

An important feature of Impala is support for web development. An important requirement of any web development environment is a fast turnaround time between making changes and seeing the displayed in your browser. Impala provides this through it's dynamic module loading capability. Of course, Impala allows for modular web application. Event the web portion of an application can be modularised, by allowing each Servlet to be represented by a separate module.

Apart from these capabilities, a Spring MVC web application will look no different from a typical web application, since it uses an otherwise identical programming model.

**Note**: the style of web applications covered here are best suited to single module web applications, or multi-module web applications without complex dynamic requirements. Impala also supports a style of web application development which allows you to reduce your reliance on _web.xml_ to a minimum, with servlets, filters and mappings to these configured within the module itself. See WebModuleBasedHandlerRegistration.

We'll take a look at how Impala supports web applications in the generated starter application next.

## Host project ##

Java web projects are packaged in a well known structure with a web context root directory, a _WEB-INF_ subdirectory, etc. The structure of an Impala web project is shown below:

![http://impala.googlecode.com/svn/wiki/images/getting_started_web.png](http://impala.googlecode.com/svn/wiki/images/getting_started_web.png)

The _context_ folder contains the web context root. The one other difference in terms of file structure is in the _webconfig_ folder, which is also a Java source folder in Eclipse.
The contents of this folder are described in a bit more detail below.

### web.xml ###

A file which is contained in practically every web application is the _web.xml_ descriptor. This file contains servlets definitions, servlet configuration parameters, filter definitions, and web application life cycle listeners. Lets take a look at the Impala _web.xml_ created for us in [part one](GettingStartedPart1.md).

```
<?xml version="1.0" encoding="UTF-8"?>
<web-app ...>
    
    <display-name>Enter display name here</display-name>

    <description>Enter description here</description>
    
    <context-param>
        <param-name>contextLoaderClassName</param-name>
        <param-value>org.impalaframework.web.spring.loader.ExternalModuleContextLoader</param-value>
    </context-param>

    <listener>
        <listener-class>org.impalaframework.web.spring.loader.ImpalaContextLoaderListener</listener-class>
    </listener>
    
    <filter>
        <filter-name>web</filter-name>
        <filter-class>org.impalaframework.web.spring.integration.ModuleProxyFilter</filter-class>
        <load-on-startup>1</load-on-startup>
    </filter>

    <filter-mapping>
        <filter-name>web</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
    
    <servlet>
        <servlet-name>JSP</servlet-name>
        <servlet-class>org.impalaframework.web.jsp.ModuleJspServlet</servlet-class>
        <load-on-startup>2</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>JSP</servlet-name>
        <url-pattern>*.jsp</url-pattern>
    </servlet-mapping>
    
</web-app>
```

In a plain Spring MVC based web application, the Spring application context which backs the application is loaded via a `ServletContextListener` class called `ContextLoaderListener`.
Impala's analogue is `ImpalaContextLoaderListener`, which extends `ContextLoaderListener` to initialise the Impala environment.

Spring MVC applications are usually exposed via the servlet `org.springframework.web.servlet.DispatcherServlet`, or a subclass of this class.
Impala provides a number of subclasses of `DispatcherServlet`. These can be defined either within _web.xml_. However, the recommended,
much more flexible approach is to define application servlets and filters within individual modules. From the web container's point of view
the entry point into an Impala application is `org.impalaframework.web.spring.integration.ModuleProxyFilter`. This filter has the responsibility of
using the Impala infrastructure to determine which module to direct the request to, and to handle dispatch of the request within the module.

In this way, Impala provides the mechanics for genuinely modular web application development - see [more details](WebModuleBasedHandlerRegistration.md).

### Module definitions ###

The recommended approach to creating modules is to use self contained modules. The _moduledefinitions.xml_ is shown below.

```
<parent>
    <names>
    myapp-main
    myapp-module1
    myapp-web
    </names>
</parent>
```

It is possible to override the definition for specific modules. See [module configuration](ModuleConfiguration.md) for more details.

## Web Module ##

While static resources and files as well as the main _web.xml_ is found in the _host_, reloadable web components such as controllers, Spring web configurations, and even filters, servlets, etc.
are normally moved into a web module. In the starter application, this contains the following.

### Spring config file ###

The discussion in the previous section covers some Impala specific aspects of the web application. Next, we cover other elements of the web application, starting with the Spring configuration file for the web module. Spring users will note that the rest of the application is no different from the a typical Spring MVC application, reflecting the fact that Impala does not change the Spring application programming model in any way.

Here's the Spring configuration file. It consists simply of a `ViewResolver` and `HandlerMapping` bean definitions, as well as a controller bean definition (`MessageController`).
Recall from the discussion in [part two](GettingStartedPart2.md) that the sample application is really just a glorified "Hello World" application. The single dependency of the controller is the `MessageService` instance.

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" 
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"      
       xmlns:service="http://www.impalaframework.org/schema/service" 
       xmlns:web="http://www.impalaframework.org/schema/web"   
       xsi:schemaLocation="
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.impalaframework.org/schema/service http://impala.googlecode.com/svn/schema/service-registry.xsd
http://www.impalaframework.org/schema/web http://impala.googlecode.com/svn/schema/web.xsd">

    <!-- We can omit this entry if it already defined in the root module's application context -->
    <service:import id="messageService" proxyTypes="com.application.main.MessageService"/>

    <web:mapping>
        <web:to-module prefix = "/web" setContextPath="true"/> 
        <web:to-handler extension = "htm" servletName="dispatcher"/>
        <web:to-handler extension = "css" servletName="resources"/>
    </web:mapping>
    
    <web:servlet id = "dispatcher"/>
        
    <web:servlet id = "resources" 
        servletClass = "org.impalaframework.web.servlet.ResourceServlet"
        initParameters = "cacheTimeout=10"/>
        
    <web:jsp-servlet id="jsp"/>

    <bean id="jspViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <bean class="com.application.web.MessageController">
        <property name = "messageService" ref = "messageService"/>
    </bean>
    
</beans>
```

Note that the Spring configuration file makes use of the Impala **web** namespace.
The `web:mapping` entry is used to register interest with `ModuleProxyFilter` for handling requests with a particular prefix, in this case
`/web`. The `setContextPath="true"` entry basically modifies the context path visible to the `HttpServletRequest` object passed to the application.
For example, in our example, `request.getContextPath()` would return `/host/web`, instead of simply `host`.

Two servlet instances are set up for the module: a dispatcher servlet (by default `org.impalaframework.web.spring.servlet.InternalModuleServlet`), and
a servlet to serve resources (css files, images, etc.). These are mapped by extension to using the `web:to-handler` element.
Finally, the `web:jsp-servlet` entry is used to add JSP support to the module, by default using an instance of the embedded Tomcat Jasper JSP container.

We'll see shortly how the controller can be updated on the fly in a web container without having to redeploy the full application.

### Web controller implementation ###

Next we take a look at the implemention of `MessageController`, which simply has the responsibility of exposing the message provided by the `MessageService` as a web model attribute, and for selecting a view. Based on the `ViewResolver` being used in our Spring web context definition, the view name `test` is translated to a JSP file named `test.jsp`.

```
@Controller
public class MessageController {
    private MessageService messageService;

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/message.htm")
    public void viewMessage(Map model) {
        model.put("message", messageService.getMessage());
    }
}
```

Note that we are using the Spring annotation-based controller. See http://static.springsource.org/spring/docs/2.5.6/reference/mvc.html.

### JSP implementation ###

We complete our application by showing the rather trivial JSP used to output the message provided by the `MessageService`.

```
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/style.css" />
</head>
<body>

<h1>Welcome to Impala</h1>
<p>If you can see this message, then you have successfully started up a minimal working multi-module application.</p>
<p>As the property <code>auto.reload.modules</code> is set true, any change you make to the application will
be automatically detected, and the affected modules will be reloaded.</p>

...
</body>
</html>
```

Note that from Impala 1.0 RC3 it is possible to host JSPs within modules (rather than simply in the context root directory
in the host project). For more details see [HowToConfigureJSPs](HowToConfigureJSPs.md). If you are working with JSPs, you may wish to [add JSTL support](WebJstl.md).

## Running the web application ##

An important feature of Impala's web support is in the fact that you don't need to create a WAR file, or run an ANT build script, to deploy your web application in a development environment. In fact, you don't need to do anything apart from running up a Java main class generated as part of the quickstart application.

Using CTRL-Shift + T, find the class `StartServer`. Right click, then select Run As ... Java Application.

This will start a Jetty Server and run up a server on port 8080.

The text shown on the console view of Eclipse will look something like this:

```
INFO : log - Logging to org.slf4j.impl.Log4jLoggerAdapter(org.mortbay.log) via org.mortbay.log.Slf4jLog
INFO : log - jetty-6.1.21
INFO : /host - Initializing Spring root WebApplicationContext
INFO : BaseLocationsRetriever - org.impalaframework.bootstrap.ConfigurationSettings@d58dfe
Context locations: [META-INF/impala-bootstrap.xml, META-INF/impala-web-bootstrap.xml, META-INF/impala-graph-bootstrap.xml, META-INF/impala-jmx-bootstrap.xml, META-INF/impala-web-listener-bootstrap.xml, META-INF/impala-web-path-mapper-bootstrap.xml]
Property settings: 
  all.locations: [null]
  auto.reload.check.delay: 10 (default)
  auto.reload.check.interval: 2 (default)
  auto.reload.extension.excludes:  (default)
  auto.reload.extension.includes:  (default)
  auto.reload.modules: true
  auto.reload.monitoring.type: default (default)
  classloader.type: graph (default)
  embedded.mode: true
  enable.web.jmx.operations: false (default)
  expose.jmx.operations: true (default)
  expose.mx4j.adaptor: false (default)
  external.root.module.name:  (default)
  extra.locations: [null]
  graph.bean.visibility.type: graphOrdered (default)
  jmx.locate.existing.server: false (default)
  load.time.weaving.enabled: false (default)
  module.class.dir: bin,target/classes (default)
  module.prefix.mapping.enabled: true
  module.resource.dir: resources,target/classes (default)
  parent.classloader.first: false (default)
  partitioned.servlet.context: true
  preserve.session.on.reload.failure: true (default)
  proxy.allow.no.service: false (default)
  proxy.missing.service.retry.count: 0 (default)
  proxy.missing.service.retry.interval: 1000 (default)
  proxy.set.context.classloader: true (default)
  session.module.protection: true
  touch.file: /WEB-INF/modules/touch.txt (default)
  use.touch.file: false (default)
  web.module.prefix:  (default)
  workspace.root: ../ (default)
--------
INFO : BaseLocationsRetriever - Property source: org.impalaframework.config.PrefixedCompositePropertySource@1bedec - propertySources: [org.impalaframework.web.config.ContextPathAwareSystemPropertySource@a14b94, org.impalaframework.config.SystemPropertiesPropertySource@6909e0, org.impalaframework.config.StaticPropertiesPropertySource@771a6, org.impalaframework.web.config.ServletContextPropertySource@b64deb]
INFO : BaseImpalaContextLoader - Impala context locations: [META-INF/impala-bootstrap.xml, META-INF/impala-web-bootstrap.xml, META-INF/impala-graph-bootstrap.xml, META-INF/impala-jmx-bootstrap.xml, META-INF/impala-web-listener-bootstrap.xml, META-INF/impala-web-path-mapper-bootstrap.xml]
INFO : BaseImpalaContextLoader - Loading bootstrap context from locations [META-INF/impala-bootstrap.xml, META-INF/impala-web-bootstrap.xml, META-INF/impala-graph-bootstrap.xml, META-INF/impala-jmx-bootstrap.xml, META-INF/impala-web-listener-bootstrap.xml, META-INF/impala-web-path-mapper-bootstrap.xml]
INFO : ScheduledModuleChangeMonitor - Starting org.impalaframework.web.module.listener.WebScheduledModuleChangeMonitorBean with fixed delay of 10 and interval of 2
INFO : LoadTransitionProcessor - Loading definition main
INFO : DefaultModuleRuntimeManager - Loading definition main
INFO : ScheduledModuleChangeMonitor - Monitoring for changes in module main: [file [/Users/philzoio/workspaces/newproject/host/../main/bin], file [/Users/philzoio/workspaces/newproject/host/../main/resources]]
INFO : LoadTransitionProcessor - Loading definition module1
INFO : DefaultModuleRuntimeManager - Loading definition module1
INFO : ScheduledModuleChangeMonitor - Monitoring for changes in module module1: [file [/Users/philzoio/workspaces/newproject/host/../module1/bin], file [/Users/philzoio/workspaces/newproject/host/../module1/resources]]
INFO : LoadTransitionProcessor - Loading definition web
INFO : DefaultModuleRuntimeManager - Loading definition web
INFO : /host - Initializing Spring FrameworkServlet 'dispatcher'
INFO : InternalModuleServlet - FrameworkServlet 'dispatcher': initialization started
INFO : InternalModuleServlet - FrameworkServlet 'dispatcher': initialization completed in 145612 ms
INFO : ScheduledModuleChangeMonitor - Monitoring for changes in module web: [file [/Users/philzoio/workspaces/newproject/host/../web/bin], file [/Users/philzoio/workspaces/newproject/host/../web/resources]]
INFO : TransitionsLogger - Module operations succeeded: true
Number of operations: 3
  main: UNLOADED_TO_LOADED
  module1: UNLOADED_TO_LOADED
  web: UNLOADED_TO_LOADED

INFO : log - Started SelectChannelConnector@0.0.0.0:8080
```

You can connect to the server using the URL, assuming of course that you are connecting from the same machine:

```
http://localhost:8080/web/message.htm
```

![http://impala.googlecode.com/svn/wiki/images/scaffold_message.png](http://impala.googlecode.com/svn/wiki/images/scaffold_message.png)

### Dynamic application updates ###

Note that Impala is started up to automatically detect changes in your modules, and reload modules in response to these changes. You can play with this mechanism by making changes to classes such as `MessageController` (in the web project) and `MessageServiceImpl` (in the module project).

So how does all this work? Impala is very flexible in supporting different startup configurations. When `ImpalaContextLoaderListener` is invoked on application startup, a bootstrap Spring context is created. This Spring context is not the application's Spring context. Instead, it is an `ApplicationContext` which reflects the Spring wiring for Impala itself.

We'll understand this better when considering the `StartServer` class used to run up Jetty in the previous section.

```
public class StartServer {
    public static void main(String[] args) {
        System.setProperty(WebConstants.BOOTSTRAP_LOCATIONS_RESOURCE_PARAM, "classpath:impala-embedded.properties");
        StartJetty.main(new String[]{"8080", "../myapp-web/context", "/web"});
    }
}
```

The system property `WebConstants.BOOTSTRAP_LOCATIONS_RESOURCE_PARAM` identifies a property file which is used to bootstrap Impala.

The content of the default _impala-embedded.properties_ is shown below.

```
embedded.mode=true
#Automatically detect changes and reload modules
auto.reload.modules=true
#Maintain session objects across module reloads
session.module.protection=true
#Enables explicit module servet mapping
module.prefix.mapping.enabled=true
#Allow partitioning of servlet context resources
partitioned.servlet.context=true
```

See [details on the properties contained in this file](PropertyConfiguration.md).

Apart from the initial setup, we've done just about all our work in the IDE. In [part five](GettingStartedPart5.md) we show you how deploy an Impala-based application to Tomcat using the built-in ANT support.