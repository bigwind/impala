## Introduction ##

One of the problems that occasionally crops up in Java application development is library class version clashes.
For some reason, you have to use a particular version of some library, but that library, or some of dependencies,
clashes with other libraries that you are using for different parts of the application.
For example, suppose you are forced to use the legacy Axis 1.4 library to use the web services of one third party to your application,
but for other integrations from the same application, you prefer to or need to use the more modern Apache Axis 2 library.

The ability to deal with these kinds of problems is often cited as one of the main reasons for using OSGi.

Impala provides a simple, more limited solution to this problem.
The new mechanism simply involves the following:

  * add any module-specific library in the module's _lib_ directory. For example, this might involve adding axis-1.4.jar and a couple of its dependencies to the module's _lib_ directory
  * add the setting `supports.module.libraries=true` to _impala.properties_

## How does it work? ##

In the normal Impala application, a class is loaded first by using the web application or system class loader (typically to load third party libraries),
then using a topologically sorted list of the dependent module class loaders (typically to find application classes).

With module-specific libraries, the scheme is a bit more complex.
For any module which has module-specific libraries, the module-specific library locations are searched first.
Suppose we are loading module A, and we need to load the class `Foo` is in both the shared library area _(WEB-INF/lib)_
and in a module-specific location for module A. In this case, the class `Foo` will be found in and loaded from the module-specific location.

## Caveats ##

The main requirement is that the class Foo should never be 'published' from module A,
for example as the return type in a method which might be called from another module, otherwise `ClassCastExceptions` or `LinkageErrors` can result.
Instead, the interfaces to module A should ensure that appropriate abstractions are in place to ensure that this does not occur.
In our Axis example, we need to ensure that instances of classes which hold references to Axis libraries are not passed around between modules.

## Library Resources ##

In some cases, resources (files which are not classes) may need to be loaded from module-specific libraries. For example, in our Axis 1.4 example above, the Axis 1.4 runtime relies on the loading of resources which are contained in the Axis 1.4 jars and its dependents.

By default, if module library loading is supported, then this option will be available. When looking for a resource, the Impala class loader will look in the following order:
  * within the module itself
  * within a module-specific library jar
  * from dependent module jars
  * from the web application class loader
  * from the system class laoder

In order to disable module-specific library resource loading, the following entry in _impala.properties_ is required:

```
loads.module.library.resources=false
```