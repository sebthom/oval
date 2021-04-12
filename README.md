# OVal - the object validation framework for Java

[![Build Status](https://github.com/sebthom/oval/workflows/Build/badge.svg "GitHub Actions")](https://github.com/sebthom/oval/actions?query=workflow%3A%22Build%22)
[![License](https://img.shields.io/github/license/sebthom/oval.svg?color=blue)](LICENSE.txt)
[![Maintainability](https://api.codeclimate.com/v1/badges/2178a43bf71aaf2622e6/maintainability)](https://codeclimate.com/github/sebthom/oval/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/2178a43bf71aaf2622e6/test_coverage)](https://codeclimate.com/github/sebthom/oval/test_coverage)
[![Changelog](https://img.shields.io/badge/changelog-blue)](CHANGELOG.md)
[![Bintray](https://img.shields.io/bintray/v/sebthom/maven/oval?label=jcenter.bintray)](https://bintray.com/sebthom/maven/oval/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/net.sf.oval/oval)](https://search.maven.org/artifact/net.sf.oval/oval)

1. [What is it?](#what-is-it)
1. [Java Compatibility](#compatibility)
1. [Binaries](#binaries)
1. [User Guide](#user-guide)
1. [Articles about OVal](#articles-about-oval)
1. [Projects using OVal](#projects-using-oval)
1. [License](#license)


## <a name="what-is-it"></a>What is it?

![logo](src/site/resources/images/oval-banner.png)

OVal is a pragmatic and extensible validation framework for any kind of Java objects (not only JavaBeans).
Constraints can be declared with annotations (`@NotNull`, `@MaxLength`), [POJOs](https://en.wikipedia.org/wiki/Plain_old_Java_object) or XML.

Custom constraints can be expressed as custom Java classes or by using scripting languages such as [JavaScript](https://github.com/mozilla/rhino), [Groovy](http://groovy-lang.org/),
[BeanShell](http://www.beanshell.org/), [OGNL](https://github.com/jkuhnert/ognl) or [MVEL](https://github.com/mvel/mvel).

Besides field/property validation OVal implements [Programming by Contract](https://en.wikipedia.org/wiki/Design_by_contract) features by utilizing [AspectJ](https://www.eclipse.org/aspectj/doc/next/progguide/starting-aspectj.html) based aspects or via [Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html). This for example allows runtime validation of constructor/method arguments.


## <a name="compatibility"></a>Java Compatibility

OVal 1.x requires Java 5 or newer

OVal 2.0 or higher requires Java 8 or newer


## <a name="binaries"></a>Binaries

Releases of this project are available at https://search.maven.org/artifact/net.sf.oval/oval

You can add OVal as a dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>net.sf.oval</groupId>
  <artifactId>oval</artifactId>
  <version>[VERSION_GOES_HERE]</version>
</dependency>
```


## <a name="user-guide"></a>User Guide

The user guide is available [USERGUIDE.md](./USERGUIDE.md)


## <a name="articles-about-oval"></a>Articles about OVal

There exist some articles and blog entries talking about and/or referencing OVal:

- Wikipedia (DE): OVal (Framework) \
 <https://de.wikipedia.org/wiki/OVal_(Framework)> [\[English
    Translation\]](https://translate.google.com/translate?sl=de&tl=en&u=https%3A%2F%2Fde.wikipedia.org%2Fwiki%2FOVal_%28Framework%29)

- An Experimental Evaluation of JavaDesign-by-Contract Extensions (2018) \
  <http://jultika.oulu.fi/Record/nbnfioulu-201812063243>

- GlueCoders' Spring Boot Guide: Validation of Request Models using OVal (2017) \
  <https://github.com/GlueCoders/springboot-guide-docs/blob/master/requestvalidation-oval.md>

- A Constraint-Based Approach to Data Quality in Information Systems (2017) \
  <https://www.research-collection.ethz.ch/bitstream/handle/20.500.11850/198644/1/Thesis-DavidWeber.pdf>

- How to integrate Oval validation with Spring 3 (2013) \
  <http://maheshjq.blogspot.com/2013/08/how-to-integrate-oval-validation-with.html>

- OVal - validate your models quickly and effortlessly (2011) \
  <https://touk.pl/blog/2011/07/14/oval-validate-your-models-quickly-and-effortlessly/>

- A Valid Point (2011) \
  <http://www.grinshpoon.com/pblog/2011/06/29/a-valid-point/>

- Validaciones: Hibernate Validator vs OVal (2010) \
  <https://unpocodejava.com/2010/05/07/validaciones-hibernate-validator-vs-oval/>
 [\[English
    Translation\]](https://translate.google.com/translate?sl=es&tl=en&u=https%3A%2F%2Funpocodejava.com%2F2010%2F05%2F07%2Fvalidaciones-hibernate-validator-vs-oval%2F)

- Enkel validering av Java-objekter med OVal (2010) \
  <https://blogg.bekk.no/enkel-validering-av-java-objekter-med-oval-2054b67c7774>

- Oval Validation Framework (2010) \
  <http://vathzala.blogspot.de/2010/03/oval-validation-framework.html>

- OVal - Object Validation With POJO (2009) \
  <https://bitsofwizardry.wordpress.com/2009/04/14/oval-object-validation-with-pojo/>

- Diseño por Contrato con OVal y Spring \[Design by Contract with OVal and Spring\] (2009) \
  <https://dosideas.com/noticias/java/562-diseno-por-contrato-con-oval-y-spring> [\[English
    Translation\]](https://translate.google.com/translate?hl=&sl=es&tl=en&u=https%3A%2F%2Fdosideas.com%2Fnoticias%2Fjava%2F562-diseno-por-contrato-con-oval-y-spring)

- An Aspect-Oriented Infrastructure for Design by Contract in Java \
  <https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.131.4053&rep=rep1&type=pdf>

- OVal Blog Entry Series at the "JEE, BPEL, SOA" blog (2008)
  - [JSR 303 and OVal Validation
  Framework](https://lukaszbudnik.blogspot.com/2008/12/jsr-303-and-oval-validation-framework.html)
  - [OVal and
    profiles](https://lukaszbudnik.blogspot.com/2008/12/oval-and-profiles.html)
  - [Spring Web Flow 1 piecemeal validation and OVal
    integration](https://lukaszbudnik.blogspot.com/2008/12/springs-piecemeal-validation-and-oval.html)

- Pre and post condition validation with OVal as intruments of software architecture (2008) \
  <https://stigl.wordpress.com/2008/07/19/pre-and-post-condition-validation-with-oval-as-intruments-of-software-architecture/>

- OVal: The simple validation framework for Java (2008) \
  <http://takezoe.blogspot.com/2008/03/oval-simple-validation-framework-for.html>

- Validación Java: OVal, una joya escondida (2007) \
  <http://brigomp.blogspot.com/2007/09/hay-veces-que-los-frameworks-menos.html>

- Die Pro-Pix Homepage - Design by Contract (2007) \
  <http://pro-pix.de/joomla/index.php?option=com_content&task=view&id=39&Itemid=1>

- In pursuit of code quality: Defensive programming with AOP (2007) \
  <http://www.ibm.com/developerworks/library/j-cq01307/>

- Limiting conditional complexity with AOP (2006)\
  <https://nofluffjuststuff.com/blog/paul_duvall/2006/12/limiting_conditional_complexity_with_aop>

## <a name="projects-using-oval"></a>Projects using OVal

Active:
- gdv.xport <https://github.com/oboehm/gdv.xport>
- eSciDoc <https://www.escidoc.org/>
- Metawidget <https://github.com/metawidget/metawidget>
- Play! Framework 1.x <https://github.com/playframework/play1>
- Struts 2 OVal Plug-in
  - Documentation: <https://cwiki.apache.org/confluence/display/WW/OVal+Plugin>
  - Source: <https://github.com/apache/struts/tree/master/plugins/oval>

Inactive:
- Jadira Framework (2018)
  - <http://jadira.sourceforge.net/>
  - <https://github.com/JadiraOrg/jadira>
- fustewa (2017) - A full web stack based on Spring <https://github.com/opensource21/fuwesta>
- Omniproperties (2016) - a lightweight configuration utility <https://github.com/siemens/omniproperties>
- Arena Persistence (2016) <http://arena-pers.uqbar.org/>
- NexOpen (2014) <https://sourceforge.net/projects/nexopen/>
- JProvocateur (2012) <https://sourceforge.net/projects/jprovocateur/>
- Cubby (2010) - Simple Web Application Framework <http://cubby.seasar.org/20x/cubby-oval/index.html>
- Cayenne annotations (2007) <http://sourceforge.net/projects/cayannotations/>
- jsfatwork <https://code.google.com/archive/p/jsfatwork/>
- Polyforms <https://code.google.com/archive/p/polyforms/>
- rsser <https://code.google.com/archive/p/rsser/>
- saetc <https://code.google.com/archive/p/saetc/>
- SaferJava <https://code.google.com/archive/p/saferjava/>
- suz-lab-gae <https://code.google.com/archive/p/suz-lab-gae/>
- ultimate-roundtrip <https://code.google.com/archive/p/ultimate-roundtrip/>


## <a name="license"></a>License

All files are released under the [Eclipse Public License 2.0](LICENSE.txt).

Individual files contain the following tag instead of the full license text:
```
SPDX-License-Identifier: EPL-2.0
```

This enables machine processing of license information based on the SPDX License Identifiers that are available here: https://spdx.org/licenses/.
