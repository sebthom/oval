# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/) and
[Keep a CHANGELOG](http://keepachangelog.com/).

## [Unreleased]

### Changed
* Changed from EPL1.0 to EPL2.0 license
* Raised minimum JRE from Java 5 to Java 6
* Project configuration improvements
  * Enabled Travis CI builds
  * Replaced Cobertura with JaCoCo for code coverage
  * Changed to semantic versioning 
* Updated tested optional dependencies
  * Groovy 2.4.15
  * Guice 4.1.0
  * Javolution 6.0.0
  * MVEL 2.4.0
  * OGNL 3.1.17
  * Rhino 1.7.7.2
  * Spring Framework 4.3.16.RELEASE
  

### Removed
* net.sf.oval.integration.spring.BeanInjectingCheckInitializationListener - use SpringCheckInitializationListener instead


## [Previous Releases]

See the old changelogs at:
* https://github.com/sebthom/oval/blob/master/src/site/changes.xml
* https://github.com/sebthom/oval/blob/master/src/site/changes.txt
