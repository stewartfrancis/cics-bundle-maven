# cics-bundle-maven [![Maven Central Latest](https://maven-badges.herokuapp.com/maven-central/com.ibm.cics/cics-bundle-maven-plugin/badge.svg)](https://search.maven.org/search?q=g:com.ibm.cics%20AND%20a:cics-bundle-maven-plugin) [![Build Status](https://travis-ci.com/IBM/cics-bundle-maven.svg?branch=master)](https://travis-ci.com/IBM/cics-bundle-maven) [![Nexus Snapshots](https://img.shields.io/nexus/s/com.ibm.cics/cics-bundle-maven.svg?server=https%3A%2F%2Foss.sonatype.org&label=snapshot&color=success)](https://oss.sonatype.org/#nexus-search;gav~com.ibm.cics~cics-bundle-maven-plugin~~~)

A collection of Maven plugins and utilities that can be used to build CICS bundles, ready to be installed into CICS TS.

This project contains:
 - `cics-bundle-maven-plugin`, a Maven plugin that authors CICS bundles for deploying resources into CICS TS. It supports a subset of bundleparts, including Java assets. Read the [Maven doc](https://ibm.github.io/cics-bundle-maven/plugin-info.html) for information about this plugin's goals.
 - `cics-bundle-reactor-archetype`, a Maven archetype that provides a simple reactor build that contains a CICS bundle and a Dynamic Web Project (WAR). This archetype builds and packages the WAR and CICS bundle.
 - `cics-bundle-deploy-reactor-archetype`, a Maven archetype that provides a simple reactor build that contains a CICS bundle and a Dynamic Web Project (WAR). This archetype packages the WAR into a CICS Bundle and installs it into CICS.
 - `samples`, a collection of samples that demonstrate the different ways in which to use the plugin.

## Supported bundlepart types
The `cics-bundle-maven-plugin` currently supports the following CICS bundleparts:
 - EAR
 - OSGi bundle
 - WAR
 - EBA
 - EPADAPTER
 - EPADAPTERSET
 - EVENTBINDING
 - FILE
 - LIBRARY
 - PACKAGESET
 - POLICY
 - PROGRAM
 - TCPIPSERVICE
 - TRANSACTION
 - URIMAP

## Prerequisites
 To use the plugins, make sure that:
 * Maven is installed into your environment, or
 * You use a Java IDE that supports Maven, e.g. Eclipse, IntelliJ, VS Code...
 
 The CICS bundle deployment API is supported by the CMCI JVM server that must be set up in a WUI region (consult the [CICS TS doc](https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.6.0/fundamentals/cpsm/cics-bundle-api.html) for details). To use this make sure that:
 * You have a CICS region that is at CICS® TS V5.6 open beta or later
 * This region is configured to be a WUI region for the CICSplex that contains the deployment target region
 * This WUI region is configured to use the CMCI JVM server


## Create a CICS bundle using `cics-bundle-maven-plugin`

The `cics-bundle-maven-plugin` contributes a new packaging type called `cics-bundle`. This is bound to a new `build` goal that will use the information in the `pom.xml` and dependencies to create a CICS bundle, ready to be stored in an artifact repository or installed into CICS.

To use the `cics-bundle-maven-plugin`:

1. Create a new Maven module for the CICS bundle.
1. Register the plugin to the `pom.xml` of the CICS bundle module:

    ```xml
    <build>
      <plugins>
        <plugin>
          <groupId>com.ibm.cics</groupId>
          <artifactId>cics-bundle-maven-plugin</artifactId>
          <version>0.0.1-SNAPSHOT</version>
          <extensions>true</extensions>
        </plugin>
      </plugins>
    </build>
    ```
    Note that the version should be the latest version of this plugin, which can be found at the top of this page. This plugin uses [SNAPSHOT versions](https://maven.apache.org/guides/getting-started/index.html#What_is_a_SNAPSHOT_version) for development.
    
1. Change the packaging type of the CICS bundle module to the new CICS bundle type:

    ```xml
    <packaging>cics-bundle</packaging>
    ```

    If at this point you build the CICS bundle module, it will create a valid CICS bundle! However, it doesn't do much, because it doesn't define any bundle parts.

1. In the CICS bundle module, define a dependency on another module for a Java project that [can be included](#supported-bundlepart-types) into CICS, such as an OSGi bundle, WAR, or EAR.

    ```xml
    <dependencies>
      <dependency>
        <groupId>my.group.id</groupId>
        <artifactId>my-web-project</artifactId>
        <version>1.0.0</version>
        <type>war</type>
      </dependency>
    </dependencies>
    ```
1. The plugin requires very little configuration. However, an important property for Java bundleparts is the name of the JVM server that the application will be installed into. To define this, alter how you registered the plugin to add some configuration, with the `<defaultjvmserver>` property:

    ```xml
    <build>
      <plugins>
        <plugin>
          <groupId>com.ibm.cics</groupId>
          <artifactId>cics-bundle-maven-plugin</artifactId>
          <version>0.0.1-SNAPSHOT</version>
          <extensions>true</extensions>
          <configuration>
            <defaultjvmserver>JVMSRV1</defaultjvmserver>
          </configuration>
        </plugin>
      </plugins>
    </build>
    ```
    Now if you build the CICS bundle it will pull in the dependency, add it into the CICS bundle, and define it in the CICS bundle's manifest. The CICS bundle is ready to be stored in an artifact repository or deployed to CICS.

    The generated CICS bundle takes its bundle ID from the Maven module's `artifactId` and its version from the Maven module's `version`.

1. To include CICS bundleparts like FILE or URIMAP, put the bundlepart files in your Maven project's resources directory, for instance `src/main/resources`. Files in your Maven project's resources directory will be included within the output CICS bundle, and supported types will have a define added to the CICS bundle's cics.xml.

## Install a CICS bundle using `cics-bundle-maven-plugin`

Following the instructions above, you will now have built a CICS bundle. You can use the `cics-bundle-maven-plugin` to install this into CICS by using the CICS bundle deployment API. This requires some setup in CICS as a [prerequisite](#Prerequisites).

1. Have your system programmer create your bundle definition in CSD and tell you the CSD group and bundle definition name they have used.
The bundle directory of the bundle definition your system programmer creates should be set as follows: `<bundle_deploy_root>/<bundle_id>_<bundle_version>`. 

1. In the `pom.xml`, extend the plugin configuration to include the extra parameters below:

  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.ibm.cics</groupId>
        <artifactId>cics-bundle-maven-plugin</artifactId>
        <version>0.0.2-SNAPSHOT</version>
        <executions>
          <execution>
            <goals>
              <goal>bundle-war</goal>
              <goal>deploy</goal>
            </goals>
            <configuration>
              <defaultjvmserver>JVMSRV1</defaultjvmserver>
              <url>http://yourcicsurl.com:9080</url>
              <username>${cics-user-id}</username>
              <password>${cics-password}</password>
              <bunddef>DEMOBUNDLE</bunddef>
              <csdgroup>BAR</csdgroup>
              <cicsplex>CICSEX56</cicsplex>
              <region>IYCWEMW2</region>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  ```

1. Edit the values in the configuration section to match your CICS configuration.
   * `url` - Set the transport, hostname, and port for your CMCI
   * `username` & `password` - These are your credentials for CICS. Use Maven's password encryption, or supply your credentials using environment variables or properties
   * `bunddef` - The name of the bundle definition to be installed
   * `csdgroup` - The name of the CSD group that contains the bundle definition
   * `cicsplex` - The name of  the CICSplex that the target region belongs to
   * `region` - The name of the region that the bundle should be installed to

  Now if you run the Maven build it will create the CICS bundle as above, and install this in CICS. 
  Each time you make a change to the Java project and rerun the build it will redeploy the bundle and publish your changes. 
  

## Using nightly/snapshot development builds

Snapshot builds are published to the Sonatype OSS Maven snapshots repository which is not available in a default Maven install.  To try a snapshot build, you will need to add the following plugin repository to your `pom.xml`:

```xml
<project>
  ...
  <pluginRepositories>
    <!-- Configure Sonatype OSS Maven snapshots repository -->
    <pluginRepository>
      <id>sonatype-nexus-snapshots</id>
      <name>Sonatype Nexus Snapshots</name>
      <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
      <releases>
        <enabled>false</enabled>
      </releases>
    </pluginRepository>
  </pluginRepositories>
  ...
</project>
```


## Samples

Use of this plugin will vary depending on what you're starting with and the structure of your project. We have included some samples to demonstrate the different methods. 

[Reactor sample](https://github.com/IBM/cics-bundle-maven/tree/master/samples/bundle-reactor-deploy)  
This sample is the best starting place if you don't already have a Java project you want you build and want to have a go at building and deploying straight away. This is a reactor project with one module including the source for a web page (including a JCICS call), which will be packaged into a WAR. It has a second module, which creates the bundle and installs this in CICS. 
Further information can be found [here](samples/bundle-reactor-deploy/README.md)

[WAR sample](https://github.com/IBM/cics-bundle-maven/tree/master/samples/bundle-war-deploy)  
This sample shows how you can add to the pom of an existing Java Maven project, to build it into a bundle and install it in CICS. 
Further information can be found [here](samples/bundle-war-deploy/README.md)


## Archetypes

Another way to get started with the plugin is to use one of the provided archetypes. Maven archetypes provide parameterized templates for how a module could, or should, be structured. 
There are two archetypes, one which builds and packages a WAR into a CICS Bundle, and another which then installs this bundle to CICS using the CICS bundle deployment API. 
Further details on how to use the archetypes can be found [here](ARCHETYPES-README.md).

## Versioning your Maven-build CICS bundles

Maven best practice is to version your code `<version>-SNAPSHOT` during development (for instance `0.0.1-SNAPSHOT` or `1.0.0-SNAPSHOT` - for more information see the Maven doc about [SNAPSHOT versions](https://maven.apache.org/guides/getting-started/index.html#What_is_a_SNAPSHOT_version)).

Developing your code using a SNAPSHOT version will ensure that every time you build your code the previous SNAPSHOT is overwritten in your local Maven repository.

When you are happy with the quality of your code and want to make a release version, reversion your plugin to remove the `-SNAPSHOT` qualifier. The [maven-release-plugin](http://maven.apache.org/guides/mini/guide-releasing.html) provides facilities to automatically remove qualifiers, automatically updating dependencies and promoting code in your source control.

If you do not want to use the maven-release-plugin, you can update the version numbers of your parent reactor project, which will also update child project version numbers. Run this command within your parent reactor project:

```
mvn versions:set -DnewVersion=0.0.1
```

Note that this `versions:set` approach will not update dependencies (e.g. from your bundle project to your Java project) which you will need to update manually.

After releasing your code, update to your next development version number, for instance `0.0.2-SNAPSHOT`.

## Building the cics-bundle-maven project

You shouldn't need to build the cics-bundle-maven project unless you plan to contribute code. In normal use you will automatically reference the built plugin from Maven Central.

However if you do want to build this project, clone the repository and build cics-bundle-maven/pom.xml. Because this is a reactor modules, all the children will also be built.

To build all projects and install them into the local Maven repository, run:

```
mvn install
```

## Contributing

We welcome contributions! Find out how in our [contribution guide](CONTRIBUTING.md).

## Licence

This project is licensed under the Eclipse Public License, Version 2.0.
