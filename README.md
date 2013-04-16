[![Build Status](https://buildhive.cloudbees.com/job/mp911de/job/configurator-maven-plugin/badge/icon)](https://buildhive.cloudbees.com/job/mp911de/job/configurator-maven-plugin/)

Introduction
=============

The configurator-maven-plugin is used to configure a JAR, WAR or EAR file after the build with properties.
You define one or more template files (naming pattern *.template.*) which will
be processed. Template variables are replaced by property values.

Example:

Template-File **config.template.properties**

```
username=@db.username@
url=@db.url@
```


Properties-File **production.properties**

```
db.username=mark
db.url=jdbc:mysql://...
```

will become then

**config.properties**

```
username=mark
url=jdbc:mysql://...
```

This method is intended primarily for Java Web Applications and Java Enterprise Applications which are packaged as WAR
or EAR file. As soon as you start packaging and deploying (artifact repository) environment-configured artifacts, the are
not portable anymore. With this plugin you are able to create templated artifacts. Just before deployment you finalize
the configuration by injecting property values into your WAR/EAR.

All WAR's/JAR's within an EAR/WAR/JAR will be processed (recursive).

See also [Maven Site](http://mp911de.github.io/configurator-maven-plugin) for more details.

Maven Configuration
-------------------

```xml

 <project>
   ...
   <build>
     <!-- To define the plugin version in your parent POM -->
     <pluginManagement>
       <plugins>
         <plugin>
           <groupId>de.paluch.maven.configurator</groupId>
           <artifactId>configurator-maven-plugin</artifactId>
           <version>1.0-SNAPSHOT</version>
         </plugin>
         ...
       </plugins>
     </pluginManagement>
     <!-- To use the plugin goals in your POM or parent POM -->
     <plugins>
       <plugin>
         <groupId>de.paluch.maven.configurator</groupId>
         <artifactId>configurator-maven-plugin</artifactId>
         <version>1.0-SNAPSHOT</version>
         <configuration>
           <groupId>junit</groupId>
           <artifactId>junit</artifactId>
           <version>4.11</version>
           <propertySources>
             <propertySource>production.properties</propertySource>
           </propertySources>
          </configuration>
       </plugin>
       ...
     </plugins>
   </build>
   ...
 </project>
 ```

Goals
----------

* **configurator:configure** configures the build file from your current project
* **configurator:configure-artifact** configures an arbitrary, external artifact.


# License
* [MIT License](http://www.opensource.org/licenses/mit-license.php)

