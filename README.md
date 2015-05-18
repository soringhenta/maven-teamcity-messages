# What does this do?

It can output TeamCity service messages as per
https://confluence.jetbrains.com/display/TCD8/Build+Script+Interaction+with+TeamCity to the build log.

# How do I use it?

Configure the plugin in your `pom.xml`, e.g. to set the parameter `mavenVersion` to the current project's maven 
version:

```XML
[...]
    <build>
        <pluginManagement>
             <plugins>
                 <plugin>
                     <groupId>com.xaidat.mavenplugins</groupId>
                     <artifactId>teamcity-service-messages</artifactId>
                     <version>1.0.0</version>
                     <executions>
                         <execution>
                             <phase>compile</phase>
                             <goals>
                                 <goal>
                                     printTeamcityServiceMessage
                                 </goal>
                             </goals>
                             <configuration>
                                 <name>setParameter</name>
                                 <parameters>
                                     <name>mavenVersion</name>
                                     <value>${project.version}</value>
                                 </parameters>
                             </configuration>
                         </execution>
                     </executions>
                 </plugin>
             </plugins>
        </pluginManagement>
    </build>
[...]
```

**NB:** If you want to use this plugin for setting parameters as above, you also need to create an (empty) 
configuration parameter in the TC web interface. For this example, the parameter would have to be named `mavenVersion`.
This is necessary to use it in e.g. dependent build plans.

**NB:** Make sure not to set your Maven log level to less than info since the messages will be output at that level.

# Contents

File  | Contents
------------- | -------------
`pom.xml` | The project descriptor including profiles for self-tests.
`selftest.bash` | Simple self-test: Installs the plugin in the local repository, then uses it to build itself. Checks for expected outputs for OK cases and error exit for FAIL cases.
`src/main/…/TeamCityServiceMessagesMojo.java` | The mojo itself.
`src/test/…/TeamCityServiceMessagesMojoTest.java` | Junit tests.
`LICENSE` | Spoiler: It's Apache2.
