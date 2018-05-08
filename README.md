# OVal - the object validation framework for Java 6 or later

[![Build Status](https://travis-ci.org/sebthom/oval.svg?branch=master)](https://travis-ci.org/sebthom/oval)
[![License](https://img.shields.io/badge/License-Eclipse%20Public%20License%202.0-blue.svg)](LICENSE.txt)
[![Download](https://api.bintray.com/packages/sebthom/maven/oval/images/download.svg)](https://bintray.com/sebthom/maven/oval/_latestVersion)

[comment]: # (https://img.shields.io/github/license/sebthom/oval.svg?label=License)

1. [What is it?](#what-is-it)
1. [Documentation](#docs)
1. [License](#license)


## <a name="what-is-it"></a>What is it?

![logo](src/site/resources/images/oval-banner.png)

OVal is a pragmatic and extensible validation framework for any kind of Java objects (not only JavaBeans). 
Constraints can be declared with annotations (`@NotNull`, `@MaxLength`), POJOs or XML. 

Custom constraints can be expressed as custom Java classes or by using scripting languages such as JavaScript, Groovy,
BeanShell, OGNL or MVEL. 

Besides field/property validation OVal implements Programming by Contract features by utilizing AspectJ based aspects. 
This for example allows runtime validation of method arguments.


## Documentation

The documentation is available at http://oval.sourceforge.net/userguide.html


## <a name="license"></a>License

All files are released under the [Eclipse Public License 2.0](LICENSE.txt).
