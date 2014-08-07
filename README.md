jsdoc3-maven-plugin
===================

An automatic documentation generator for JavaScript within the Maven Reporting lifecycle.

## What is the jsdoc3-maven-plugin? ##
The jsdoc3-maven-plugin is a [maven plugin](http://maven.apache.org/plugin-tools/maven-plugin-plugin/examples/using-annotations.html)
and [maven report](http://maven.apache.org/ref/2.2.1/maven-reporting/maven-reporting-api/apidocs/org/apache/maven/reporting/MavenReport.html)
built for the purposes of generating [jsdoc](http://usejsdoc.org/) along with a Maven Build, or
Maven Reporting lifecycle.  By default, the plugin will bind to the _site_ phase when used as a Maven Mojo or Maven Report,
though this is configurable as with any other Maven plugin with goals.

## Why Maven Mojo and Maven Report? ##
It has been requested by enough users that we should provide both a Maven Mojo for people who prefer to generate
documentation using the command line or don't care about producing a complete site, while other users prefer to simply
generate a complete Maven Site with JSDoc included.

## Getting Started ##
The following examples enumerate the most common POM configurations for the jsdoc3-maven-plugin.  This plugin is made
available through [Sonatype](http://www.sonatype.org/) and is synchronized with the central Maven repository.

## Release ##
The current release version is 1.0.9, using jsdoc3 [3.3](https://github.com/jsdoc3/jsdoc/branches/releases/3.3).

## Current Status ##
[![Build Status](https://travis-ci.org/phasebash/jsdoc3-maven-plugin.png)](https://travis-ci.org/phasebash/jsdoc3-maven-plugin)

## Example Configurations ##
The jsdoc3-maven-plugin can fit into both the Maven Build and Maven Report lifecycle and offers 
both a Mojo and MavenReport which provide the same API.  The snippets of POM configuration below can be placed both
within reporting.plugins as well as build.plugins.

### Example POM: Recurse subdirectories only ##
    <reporting>
        <plugins>
            <plugin>
                <groupId>com.github.phasebash</groupId>
                <artifactId>jsdoc3-maven-plugin</artifactId>
                <version>1.0.9</version>
                <configuration>
                    <recursive>true</recursive>
                    <directoryRoots>
                        <directoryRoot>${basedir}/src/main/webapp/resources/js</directoryRoot>
                        <directoryRoot>${basedir}/src/main/javascript</directoryRoot>
                    </directoryRoots>
                </configuration>
            </plugin>
        </plugins>
    </reporting>

### Example POM: Recurse subdirectories + errant files ##
    <reporting>
        <plugins>
            <plugin>
                <groupId>com.github.phasebash</groupId>
                <artifactId>jsdoc3-maven-plugin</artifactId>
                <version>1.0.9</version>
                <configuration>
                    <recursive>true</recursive>
                    <directoryRoots>
                        <directoryRoot>${basedir}/src/main/webapp/resources/js</directoryRoot>
                        <directoryRoot>${basedir}/src/main/javascript</directoryRoot>
                    </directoryRoots>
                    <sourceFiles>
                        <sourceFile>${baseDir}/src/main/resources/js/classic.js</sourceFile>
                    </sourceFiles>
                </configuration>
            </plugin>
        </plugins>
    </reporting>

### Example POM: Cherry pick ##
    <reporting>
        <plugins>
            <plugin>
                <groupId>com.github.phasebash</groupId>
                <artifactId>jsdoc3-maven-plugin</artifactId>
                <version>1.0.9</version>
                <configuration>
                    <sourceFiles>
                        <sourceFile>${baseDir}/src/main/resources/js/menu.js</sourceFile>
                        <sourceFile>${baseDir}/src/main/resources/js/header.js</sourceFile>
                        <sourceFile>${baseDir}/src/main/resources/js/content.js</sourceFile>
                        <sourceFile>${baseDir}/src/main/resources/js/ads.js</sourceFile>
                    </sourceFiles>
                </configuration>
            </plugin>
        </plugins>
    </reporting>
    
### Commandline ###
Run the Mojo.

    mvn clean jsdoc3:jsdoc3

## Mojo Parameters ##
* directoryRoots - File[]: An Array of Files which will be used as directory roots, any file within this directory will be included into the final jsdoc argument list.
* recursive (Optional) - Boolean: A flag to indicate whether or not all directory roots should be searched and all files included recursively.
* lenient (Optional) - Boolean: A flag to indicate whether or not the generator should tolerate errors (false), or keep plodding along despite them (true).
* sourceFiles (Optional) - File[]: An Array of Files which will be included into the final jsdoc argument list.
* outputDirectory (Optional) - File: The place where jsdoc should be written.  default: "${project.build.directory}/site/jsdoc"
* includePrivate (Optional) - Boolean: A flag to indicate whether @private symbols are included in the generated documentation.
* tutorialsDirectory (Optional) - File: A file indicating where [jsdoc tutorial](http://usejsdoc.org/about-tutorials.html) resources can be found.
* templateDirectory (Optional) - File: A file inddicating where [jsdoc templates]() resources can be found.
* configFile (Optional) - File: A [configuration](http://usejsdoc.org/about-configuring-jsdoc.html#configuration-file) file to be passed to jsdoc for more detailed project configuration.

## System Properties ##
* maven.jsdoc.skip - Boolean: A flag to skip JSDoc reporting.  Utilized by both the Maven Report and Mojo.

## Questions, Feedback? ##
Feel free to submit an issue ticket through github or contact me directly.  I will help you.

## License ##
The jsdoc3-maven-plugin has been released under [Apache 2.0](https://github.com/phasebash/jsdoc3-maven-plugin/blob/master/LICENSE.md) as per all it's dependencies.

## Future Enhancements and Limitations ##
We still have work to do...

### Enhancements ###
* Migrate Mojo, Maven Report, and Task classes to Groovy
* Consolidate duplicated Plexus parameters between Mojo and Maven Report (using Groovy)
* Support remaining command line arguments

### Limitations ###
* Test coverage could be better
* Missing Mojo integration tests (so fix it already)
