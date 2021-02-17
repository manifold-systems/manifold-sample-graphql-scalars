# manifold-sample-graphql-scalars

A simple project demonstrating how to make custom GraphQL scalars using the `ICoercionProvider` SPI and implementing
`IJsonFormatTypeCoercer`
for [manifold-graphql](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-graphql).
The [GraphQL Sample Application](https://github.com/manifold-systems/manifold-sample-graphql-app) demonstrates how to
import this custom scalar project.

## Building

Project is configured with Maven:

```
mvn clean compile install
```

This installs the `manifold-sample-graphql-scalars:0.1-SNAPSHOT` release locally, which you can test with your GrpahQL
project...

## Usage

Gradle:

```groovy
dependencies {
    implementation "systems.manifold:manifold-graphql-rt:${manifoldVersion}"
    // Add manifold to -processorpath for javac
    annotationProcessor "systems.manifold:manifold-graphql:${manifoldVersion}"
    // custom scalars
    implementation group = 'systems.manifold' name = 'manifold-sample-graphql-scalars' version = manifoldVersion
    annotationProcessor group = 'systems.manifold' name = 'manifold-sample-graphql-scalars' version = manifoldVersion
}

tasks.withType(JavaCompile) {
    if (JavaVersion.current() != JavaVersion.VERSION_1_8 &&
            sourceSets.main.allJava.files.any { it.name == "module-info.java" }) {
        // if you DO define a module-info.java file:
        options.compilerArgs += ['-Xplugin:Manifold', '--module-path', it.classpath.asPath]
    } else {
        // if you DO NOT define a module-info.java file:
        options.compilerArgs += ['-Xplugin:Manifold']
    }
    // compile .graphql files
    options.compilerArgs += ['-Amanifold.source.graphql=.*']
}
```

Maven:

```xml

<dependencies>
    <dependency>
        <groupId>systems.manifold</groupId>
        <artifactId>manifold-graphql-rt</artifactId>
        <version>${manifold.version}</version>
    </dependency>
    <dependency>
        <groupId>systems.manifold</groupId>
        <artifactId>manifold-sample-graphql-scalars</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>
</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.0</version>
        <configuration>
            <source>11</source>
            <target>11</target>
            <encoding>UTF-8</encoding>
            <compilerArgs>
                <!-- Configure manifold plugin -->
                <arg>-Xplugin:Manifold</arg>
            </compilerArgs>
            <!-- Add the processor path for the plugin -->
            <annotationProcessorPaths>
                <path>
                    <groupId>systems.manifold</groupId>
                    <artifactId>manifold-graphql</artifactId>
                    <version>${manifold.version}</version>
                </path>
                <path>
                    <groupId>systems.manifold</groupId>
                    <artifactId>manifold-sample-graphql-scalars</artifactId>
                    <version>0.1-SNAPSHOT</version>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>
</plugins>
</build>
```

On the server side, scalars must be connected to the GraphQL backend:

```java
import manifold.graphql.rt.api.GqlScalars;
import graphql.schema.idl.RuntimeWiring.Builder;
...
        GqlScalars.transformFormatTypeResolvers().forEach(runtimeWiringBuilder::scalar);
```

For example, you can apply the following patch to the [GraphQL Sample Application](https://github.com/manifold-systems/manifold-sample-graphql-app) to use this project's scalars locally:
```aidl
Index: src/main/resources/manifold/graphql/sample/schema/movies.graphql
IDEA additional info:
Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
<+>UTF-8
===================================================================
diff --git a/src/main/resources/manifold/graphql/sample/schema/movies.graphql b/src/main/resources/manifold/graphql/sample/schema/movies.graphql
--- a/src/main/resources/manifold/graphql/sample/schema/movies.graphql	(revision 1501b6f0e82eb6254265bcf18892589056bca542)
+++ b/src/main/resources/manifold/graphql/sample/schema/movies.graphql	(date 1613595102748)
@@ -44,6 +44,7 @@
   dateOfBirth: Date!
   nationality: String
   height: Float # meters
+  url: URL
 }
 
 type Animal implements Actor {
@@ -86,6 +87,7 @@
 union CastMember = Person | Animal
 
 scalar Date # Java LocalDate
+scalar URL
 
 enum Type {
   Main, Supporting, Antagonist, Tertiary, Flat
Index: pom.xml
IDEA additional info:
Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
<+>UTF-8
===================================================================
diff --git a/pom.xml b/pom.xml
--- a/pom.xml	(revision 1501b6f0e82eb6254265bcf18892589056bca542)
+++ b/pom.xml	(date 1613594812049)
@@ -20,6 +20,11 @@
             <artifactId>manifold-graphql-rt</artifactId>
             <version>${manifold.version}</version>
         </dependency>
+        <dependency>
+            <groupId>systems.manifold</groupId>
+            <artifactId>manifold-sample-graphql-scalars</artifactId>
+            <version>0.1-SNAPSHOT</version>
+        </dependency>
 
         <!-- Manifold : Collections (extension library for collections) -->
         <dependency>
@@ -77,6 +82,11 @@
                             <groupId>systems.manifold</groupId>
                             <artifactId>manifold-graphql</artifactId>
                             <version>${manifold.version}</version>
+                        </path>
+                        <path>
+                            <groupId>systems.manifold</groupId>
+                            <artifactId>manifold-sample-graphql-scalars</artifactId>
+                            <version>0.1-SNAPSHOT</version>
                         </path>
                         <path>
                             <groupId>systems.manifold</groupId>
```
Clone the [GraphQL Sample Application](https://github.com/manifold-systems/manifold-sample-graphql-app) to see how this
works.

### IntelliJ IDEA

Manifold is best experienced in [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).

* Install the Manifold IntelliJ plugin directly from IntelliJ IDEA:

  <kbd>Settings</kbd> ➜ <kbd>Plugins</kbd> ➜ <kbd>Marketplace</kbd> ➜ search: `Manifold`

* Close and relaunch IDEA
* Open this project: `manifold-sample-graphql-scalars`
* Be sure to setup an SDK for <b>Java 8</b>:

  <kbd>Project Structure</kbd> ➜ <kbd>SDKs</kbd> ➜ <kbd>+</kbd> ➜ <kbd>JDK</kbd>
* Or change the `pom.xml` file to use a JDK of your choosing, Manifold fully supports Java 8 - 16

## Questions?

Visit
the [Manifold slack group](https://join.slack.com/t/manifold-group/shared_invite/zt-e0bq8xtu-93ASQa~a8qe0KDhOoD6Bgg)
to ask for help or to discuss anything Manifold. Or, if you prefer, submit a question issue
on [Manifold's github](https://github.com/manifold-systems/manifold/issues).