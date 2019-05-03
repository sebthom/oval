# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Added
* [github Issue #13](https://github.com/sebthom/oval/issues/13) Support for XInclude in XML config files
* [github Issue #17](https://github.com/sebthom/oval/issues/17) Constraint annotations are now [repeatable annotations](https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html)
* [github Issue #19](https://github.com/sebthom/oval/issues/19) Add support for using BeanValidation annotations as Collection/Map Generic Type Annotation

### Changed
* Raised minimum JRE from Java 5 to **Java 8**
* Changed from EPL1.0 to EPL2.0 license
* Project configuration improvements
  * Enabled Travis CI builds
  * Replaced Cobertura with JaCoCo for code coverage
  * Changed to Semantic Versioning
* Applied new checkstyle/formatting rules
* Updated tested optional dependencies
  * AspectJ 1.9.2
  * commons-jexl3 3.1
  * Groovy 2.5.2
  * Guice 4.2.2
  * Javolution 6.0.0
  * JRuby 9.2.7.0
  * MVEL 2.4.4
  * OGNL 3.2.10
  * Rhino 1.7.7.2
  * Spring Framework 5.1.6.RELEASE
  * trove4j 3.1.0
  * javax.validation-api 2.0.1.Final
* [github Issue #22](https://github.com/sebthom/oval/issues/22) `@NotEmpty` now evaluates Map/Collection/Array based on their size/length properties
* [github Issue #21](https://github.com/sebthom/oval/issues/21)
  * renamed `PreCheck#getExpression` to `PreCheck#getExpr`
  * renamed `PostCheck#getExpression` to `PostCheck#getExpr`
  * renamed `PreCheck#getLanguage` to `PreCheck#getLang`
  * renamed `PostCheck#getLanguage` to `PostCheck#getLang`
* [github Issue #16](https://github.com/sebthom/oval/issues/16) changed `CheckWithCheck.SimpleCheck#isSatisfied(Object, Object)` to `CheckWithCheck.SimpleCheck#isSatisfied(Object, Object, OValContext, Validator)`

### Removed
* `net.sf.oval.integration.spring.BeanInjectingCheckInitializationListener` - use `net.sf.oval.integration.spring.SpringCheckInitializationListener` instead
* `net.sf.oval.logging.LoggerFactoryL4JImpl`
* `net.sf.oval.logging.LoggerL4JImpl`


### Fixed
* warnings declared by ApiUsageAuditor aspect are incomplete
* [github Issue #23](https://github.com/sebthom/oval/issues/23) `ObjectCache` is not thread-safe


## [1.90] - 2017-10-19

### Added
* Support for validation of Bean Validation 2.0 built-in constraints

### Fixed
* [github Issue #10](https://github.com/sebthom/oval/issues/10) Race Condition in `ResourceBundleMessageResolver`
* [github Issue #7](https://github.com/sebthom/oval/issues/7) StackOverflowError when using `@Guarded(checkInvariants=true)` (thanks to Kai Tait)

### Changed
* upgraded to Spring 3.2.18.RELEASE, SLF4J 1.7.25


## [1.87] - 2017-03-04

### Added
* [github Issue #5](https://github.com/sebthom/oval/issues/5) Support for Java 8 java.time Dates in `@Future` and `@Past` (thanks to Pyeroh)
* [github Issue #3](https://github.com/sebthom/oval/issues/3) Improve performance of `ValidateWithMethodCheck` (thanks to an-bel)


## [1.86] - 2016-10-08

### Added
* SpringInjector#initialize(Object)

### Fixed
* [sf.net Issue #91](https://sourceforge.net/p/oval/bugs/91/) Race condition when initializing `ResourceBundleMessageResolver` (thanks to Ville Koskela)
* Improve support for constraint target selection via JXPath

### Changed
* upgraded to AspectJ 1.8.9, BeanShell 2.0b6, OGNL 3.1.3, Spring 3.2.17.RELEASE


## [1.85] - 2015-10-03

### Fixed
* [sf.net Issue #90](https://sourceforge.net/p/oval/bugs/90/) Activation rules are not considered for whole contraints list (thanks to Petras)
* [sf.net Issue #85](https://sourceforge.net/p/oval/bugs/85/) `@Email` validation treats + as invalid character (thanks to David van Geest)
* [sf.net Issue #89](https://sourceforge.net/p/oval/bugs/89/) `ConstraintTarget.RECURSIVE` to specify the constraints should be applied recursively to nested maps/lists Limiting the nesting level for ConstraintTarget.VALUES (thanks to Petras)
* [sf.net Issue #88](https://sourceforge.net/p/oval/bugs/88/) ResourceBundleMessageResolver fails to find a message in ResourceBundle of more generic Locale (thanks to Petras)
* concurrency fix in SerializableMethod/SerializableField/SerializableContructor (thanks to Andrew Malota)
* DigitsCheck's validation message missing from Messages.properties file (thanks to Dirk Buchhorn)
* DigitsCheck's message variables are emtpy (thanks to Dirk Buchhorn)
* [sf.net Issue #86](https://sourceforge.net/p/oval/bugs/86/) Trove is wrongly detected (thanks to Geert Bevin)


### Changed
* upgraded to AspectJ 1.8.6, commons-logging 1.2, Groovy 2.2.2, OGNL 3.1, Paranamer 2.8, Rhino 1.7R5, Spring 3.2.14.RELEASE, SLF4J 1.7.12

## [1.84] - 2013-11-22

### Added
* LocaleProvider
* ThreadLocalLocaleProvider
* Validator.getLocaleProvider
* Validator.setLocaleProvider

### Fixed
* [sf.net Issue #84](https://sourceforge.net/p/oval/bugs/84/) JPAAnnotationsConfigurer shouldn't add Checks twice


## [1.83] - 2013-09-07

### Added
* net.sf.oval.integration.guice.GuiceCheckInitializationListener to support injection of Guice managed objects into constraint check implementations
* Validator.resolveValue methods


### [1.82] - 2013-01-26

### Added
* JSR223 Scripting support

### Fixed
* message variable cache invalidation on MatchPatternCheck and NotMatchPatternCheck
* [sf.net Issue 79](https://sourceforge.net/p/oval/bugs/79/) Wrong generic types used in Check-implementations


## [1.81] - 2012-02-22

### Added
* extended BeanValidationAnnotationsConfigurer to support method parameter constraints and return value checks on non-getter methods

### Fixed
* default message attribute value on JSR303 annotations being misinterpreted
* [sf.net Issue 77](https://sourceforge.net/p/oval/bugs/77/) XStream alias typo "postExcecution" in XMLConfigurer.java
* [sf.net Issue 76](https://sourceforge.net/p/oval/bugs/76/) UnsupportedOperationException when using inspectInterfaces


## [1.80] - 2011-10-03

### Added
* added caching of compiled OGNL expressions
* added caching of compiled MVEL expressions
* added support for target attribute on constraint annotations

### Fixed
* message attribute on JSR303 annotations being ignored


## [1.70] - 2011-04-05

### Added
* added convenience constructors to XMLConfigurer for easier Spring-based configuration
* added support for Spring dependencies being injected into SimpleCheck instances

### Fixed
* [sf.net Issue 70](https://sourceforge.net/p/oval/bugs/70/) OVal fails to load at bean creation time
* [sf.net Issue 71](https://sourceforge.net/p/oval/bugs/71/) Bug in DigitsCheck
* [sf.net Issue 72](https://sourceforge.net/p/oval/bugs/72/) Bug while retrieving profile names from JSR group
* [sf.net Issue 73](https://sourceforge.net/p/oval/bugs/73/) Missing sources JAR for OVal in Maven repository


## 1.61 - 2011-04-05

### Added
* simplified Spring-based configuration
* improved OVal XSD for XML based configuration

### Fixed
* [net.sf Issue 3156080] OVal fails to load at bean creation time
* [net.sf Issue 3173470] Bug in DigitsCheck
* [net.sf Issue 3175737] Bug while retrieving profile names from JSR group
* [net.sf Issue 3220331] Missing sources JAR for OVal in Maven repository


## [1.61] 2010-11-13

### Fixed
* [net.sf Issue 3087277] @NotBlank ignores non-breaking space
* @EMail not validating addresses correct if allowPersonalName=true


## [1.60] - 2010-10-13

### Added and Improvements
* added Validator.reportConstraintViolation allowing check implementations to generate more than one constraint violation
* Validator now eagerly checks Java SecurityManager configuration for suppressAccessChecks ReflectionPermission during instantiation
* added @<Constraint>.List annotations that can hold multiple constraint annotations of the same type
* added @Email(allowPersonalName=true/false) (thanks to Eric Lewis)
* added SimpleCheckWithMessageVariables (thanks to Eric Lewis)
* added add/remove CheckInitializationListener methods to AnnotationsConfigurer and XMLConfigurer
* added BeanInjectingCheckInitializationListener allowing Spring dependencies being injected into Check instances
* [net.sf Issue 2922034] added support for constraints specified for method parameters at interface level: @Guard(inspectInterfaces=true) (thanks to Chris Pheby)

### Fixed
* [net.sf Issue 2994748] added missing French translations (thanks to Patrice Lachance)
* a potential null reference (thanks to jacky163com)


## [1.50] 2010-03-29

### Added and Improvements
* JPAAnnotationsConfigurer now also interprets annotated getter methods
* @Size now checks the length of the value's String representation if the value is not a map, a list, or an array
* added @AssertNull, @Digits constraints
* added BeanValidationAnnotationsConfigurer that allows validation of JSR303 built-in constraint annotations
* implemented [net.sf Issue 2907399] added MessageValueFormatter and Validator.setMessageValueFormatter()


### Fixed
* added missing violation message for @AssertValid (thanks to Eric Lewis)
* [net.sf Issue 2894631] ClassCastException when validating arrays
* [net.sf Issue 2890683] Where is 'net.sf.oval.constraint.AssertURL.violated'
* [net.sf Issue 2879918] Difference in keys between Annotations and Properties file
* [net.sf Issue 2973326] XMLConfigurer doesn't completely cover constraints
* [net.sf Issue 2973323] XSD scheme is not complete
* [net.sf Issue 2973328] XMLConfigurer doesn't parse appliesTo for constraints
* [net.sf Issue 2973339] Defaults are not applied to checks
* [net.sf Issue 2973334] XMLConfigurer doesn't allow to parse when expressions
* [net.sf Issue 2973344] NotNull checks for container values
* changed @EMail regex pattern to support some rare email names [net.sf Issue 2910553]


## [1.40] - 2009-09-27

### Added and Improvements
* added support for conditional constraint activation (added the when="" attribute)
* added tolerance attribute for @DateRange, @Past and @Future constraint [net.sf Issue2821805] (thanks to Eric Lewis)
* added @NotMatchPattern [net.sf Issue2859133] (thanks to Eric Lewis)
* added ConstraintViolation.getCheckDeclaringContext() (thanks to Eric Lewis)
* added appliesTo() attribute to constraints allowing to control if and how validation should be applied to childs of arrays, maps and collections (thanks to tomtran711)

### Fixed
* parameter "declaringClass" of @AssertFieldConstraints was ignored during constraint validation
* [net.sf Issue 2836116] DateRangeCheck contains bug in getMinMillis()
* corrected checking of BigDecimal by @NotNegative constraint (thanks to Saleem)
* [net.sf Issue 2799870] Wrong escaped unicode sequences in Messages_de.properties


### [1.32] - 2009-05-10

### Added and Improvements
* added Brazilian Portuguese translation of the constraint violation messages [net.sf Issue2593104] (thanks to Leonardo Pinto)
* introduced an interface implemented by Validator for supporting easier mocking
* French translation updated (thanks to  Éric Vigeant)
* upgraded Groovy 1.6.2, MVEL 2.0.8, Paranamer 1.3, JRuby 1.2.0, cglib 2.2,Spring 2.5.6SEC01
* added an @Email constraint (thanks to Musachy Barosso)

### Fixed
* [net.sf Issue 2493925] java.util.NoSuchElementException if @AssertValid is used
* [net.sf Issue 2495825] The profile feature of Validator is not thread safe
* [net.sf Issue 2493379] array types in xml based configuration
* [net.sf Issue 2579897] xml based configuration related to post/pre checks
* [net.sf Issue 2723344] Should not call any method on not fully initialized object
* [net.sf Issue 2723413] translated JDK exception should keep the cause
* NPE in JDK Logger implementation (thanks to Carsten Siedentop)


## [1.31] - 2008-21-27

### Added
* added support for multi-valued constraints
* JPAAnnotationsConfigurer now interprets @Column.precision/scale for numeric fields

### Fixed
* Object level constraints defined in XML were ignored by the XMLConfigurator (thanks to Niels Kirkegaard)
* [net.sf Issue 2063142] doc typo
* prefix of default constraint message codes (changed from net.sf.oval.constraints to net.sf.oval.constraint)
* an issue where OVal was translating exception thrown by guarded methods into ValidationFailedException
* [net.sf Issue 2164438] SerializableField bug when dealing with private fields
* [net.sf Issue 2166232] JPAAnnotationsConfigurer should use AssertValidCheck for @ManyToMany
* [net.sf Issue 2192175] Validator.validate checks the same object multiple times
* [net.sf Issue 2406574] wrong scripting language enumerations in xsd and dtd


## [1.30] - 2008-08-10

### Added
* added support for logging via SLF4J
* added constraint check exclusion feature which can be used for fine-grained deactivation of constraints
* added `@net.sf.oval.constraint.exclusion.Nullable` constraint check exclusion
* introduced `net.sf.oval.localization.context.OValContextRenderer`
* `@MatchPattern` can now match against multiple patterns
* added constraint configuration XSD as replacement for the DTD
* ConstraintViolation now provides more details about the violated constraint (checkName, messageVariables, messageTemplate)
* added support for using Spring AOP to enable programming by contract for Spring managed beans
* constraints declared without any profiles are associated with a profile named "default" automatically
* greatly simplified usage of the probe mode feature

### Fixed
* an StackOverflowError that occurs when referencing the same property via \_this._property_name_ in scripted pre/post conditions
* some issues in the DTD
* [net.sf Issue 2023617] `@ValidateWithMethod` in super classes do not work

### Changed
* changed parameter substring() of annotation @HasSubstring() to value() which allows less verbose usage, e.g. @HasSubstring("foo") instead of @HasSubstring(substring="foo")
* renamed MessageResolverImpl to ResourceBundleMessageResolver
* moved MessageResolver classes from net.sf.oval.localization to net.sf.oval.localization.message
* migrated from Ant to Maven as project build system
* Guard.setInProbeMode(...) has been refactored into to separate methods Guard.enableProbeMode(...) and Guard.disableProbeMode(...)


## [1.20] - 2008-04-05

### Added
* added support for Apache Commons JEXL as expression language
* added `@net.sf.oval.constraint.EqualToField` (thanks to anydoby for the idea)
* added `@net.sf.oval.constraint.NotEqualToField` (thanks to anydoby for the idea)
* added `@net.sf.oval.constraint.DateRange` (thanks to anydoby for the idea)
* added Japanse translation of the constraint violation messages [net.sf Issue1911078] (thanks to Shinpei Ohtani)

### Fixed
* [net.sf Issue 1885645] `Validator.validateFieldValue` should not return null
* [net.sf Issue 1834537] NullPointerException in `constraintViolation.getCauses()`
* [net.sf Issue 1852088] Incorrect default message for NotEqual validation
* [net.sf Issue 1852087] Corrupt default localized messages
* [net.sf Issue 1868301] OVal with groovy - Performance Evaluation
* [net.sf Issue 1917978] Length of @Column should be ignored if @Lob is present
* [net.sf Issue 1934263] Version conflict with WLS10.0 Clientlib


## [1.10] - 2007-11-02

### Added
* five new built-in constraints: `@AssertURL` (thanks to Makkari), `@InstanceOfAny`, `@MemberOf`, `@NotEqual`, `@NotMemberOf`
* errorCode and severity can now be specified for constraints
* added support for object-level constraints (compound constraints)
* support for Ruby as constraints expression language
* constraints violation messages translated into new languages:
  * Chinese (thanks to kindloaf)
  * Dutch (thanks to miep)
  * Hungarian (thanks to Gabor Nagy)
  * Italian (thanks to Mastermind X)
  * Norwegian (thanks to Simen Røkaas)
  * Portuguese (thanks to Rubem Azenha)
  * Romanian (thanks to A. Mate)
  * Russian (thanks to Andrey Qwerty)
  * Spanish (thanks to Luis Garcia Sevillano)
  * Swedish (thanks to Johan Hedberg)
  * Turkish (thanks to Merdan Nouryev)
* support for different logging frameworks/facades, so far: JDK logging, Log4J, commons logging

### Changed
* `net.sf.oval.exception.ConstraintsViolatedException` was moved from package `net.sf.oval.guard`
* `net.sf.oval.exception.ExceptionTranslator` was moved from package `net.sf.oval.guard`
* `net.sf.oval.exception.ExceptionTranslatorJDKExceptionsImpl` was moved from package `net.sf.oval.guard`
* `@AssertTrue`, `@AssertFalse` are now evaluating the String representation of a value if it is not of type Boolean/boolean
* `@AssertValid` now recursively checks Arrays, Lists and Maps containing List, Map and Array items
* renamed method `Validator.validateField(...)` to `Validator.validateFieldValue(...)`


## [1.0] - 2007-07-22

### Added
* New constraint `@NotBlank`
* Support for OGNL and MVEL as constraint expression language

### Changed
* `Validator.setMessageResolver` and `Validator.getMessageResolver` are now static,
  this means the same message resolver instance is used with all Validator/Guard instances.
* Class `net.sf.oval.collection.CollectionFactoryHolder` has been moved to package `net.sf.oval.internal`.
  This class should not be used directly, the collection factory in use can be retrieved via
  the static method `Validator.getCollectionFactory` and set with the static method `Validator.setCollectionFactory`.
* `@Guarded.applyFieldConstraintsToSetter` has been renamed to `@Guarded.applyFieldConstraintsToSetters`
* JPAAnnotationsConfigurer constraint mappings extended:\
  `@javax.persistence.OneToOne  => @net.sf.oval.constraints.AssertValid\
  @javax.persistence.OneToMany => @net.sf.oval.constraints.AssertValid\
  @javax.persistence.ManyToOne => @net.sf.oval.constraints.AssertValid`


## [0.9] - 2007-03-18

### Added
* Scripting Support (Groovy, JavaScript, BeanShell) for class invariants, Pre- and Postconditions (`@Assert, @Pre, @Post`)
* Automatic check of class invariants on calls to all non-private methods
* Check of class invariants, pre-, postconditions can be separately enabled or disabled during runtime
* New constraints: `@CheckWith, @Future, @HasSubstring, @Max, @MaxLength, @MaxSize, @Min, @MinLength, @MinSize, @Past`
* validating/guarding of static fields and methods is now supported
* introduced the concept of constraint profiles
* added `Guard.setExceptionTranslator()` allowing you to change the type of exception that is thrown on constraint violations

### Changed
* Packages with names in plural have been changed to singular (e.g. net.sf.oval.constraints => net.sf.oval.constraint)
* Everything related to programming by contract has been moved into a new
  package called net.sf.oval.guard. This includes ConstraintsViolatedListener,
  GuardAspect, Guard, @PreValidateThis, @PostValidateThis and others.
* The Guard class now extends the Validator class, therefore the Guard.getValidator() method has been removed.
* For class invariants are now checked by default for guarded classes on every
  call to non-private methods. If you require the old behaviour you can disable
  automatic invariant checking via `MyGuardAspect.aspectOf().getGuard().setInvariantCheckingActivated(false)`
* Getter methods now need to be annotated with @IsInvariant if their return
  value should be checked on a object validation via `Validator.validate(Object)`
* The Guard's setReportingMode() methods have been replaced with setInProbeMode methods.


## [0.8] - 2006-12-10

### Added
* JPAAnnotationsConfigurer to translate EJB3 JPA annotations into OVal constraints:
  ```
  @javax.persistence.Basic(optional=false)      => @net.sf.oval.constraints.NotNull
  @javax.persistence.OneToOne(optional=false)   => @net.sf.oval.constraints.NotNull
  @javax.persistence.ManyToOne(optional=false)  => @net.sf.oval.constraints.NotNull
  @javax.persistence.Column(nullable=false)     => @net.sf.oval.constraints.NotNull
  @javax.persistence.Column(length=5)           => @net.sf.oval.constraints.Length
  ```
* added more JUnit tests for the xml configuration
* net.sf.oval.configuration.POJOConfigurer and all configuration elements are now serializable
* added support for ParaNamer http://paranamer.codehaus.org/ for method parameter name resolving
* added a DTD for the XML configuration file
* when @AssertValid is used on a collection the collection's elements are now validated too by default (thanks to tahura)

### Fixed
* AssertConstraintSet constraint was ignored when specified for getter methods' return value
* NPE was thrown when validating objects against an XML based configuration that uses constraint sets
* various fixes regarding XML based configuration (thanks to tahura)

### Changed
* net.sf.oval.annotations.Guarded: annotation @Constrained renamed to @Guarded
* net.sf.oval.aspectj.GuardAspect: aspect ConstraintsEnforcerAspect renamed to GuardAspect
* net.sf.oval.aspectj.GuardAspect2: class ConstraintsEnforcerAspect2 renamed to GuardAspect2
* net.sf.oval.Guard: class ConstraintsEnforcer renamed to Guard


## [0.7] - 2006-11-12

### Added
* added pluggable support for alternative collection implementations
* added support for Javolution Collection Classes (http://javolution.org/)
* reworked the configuration mechanism:
  *added net.sf.oval.configuration package
  *added support for POJO based constraint configuration
  *added support for XML based constraint configuration
* added the concept of constraint sets
  * added @net.sf.oval.annotations.DefineConstraintSet
  * added @net.sf.oval.constraints.AssertConstraintSet
  * added net.sf.oval.exceptions.UndefinedConstraintSetException
  * added net.sf.oval.exceptions.ConstraintSetAlreadyDefinedException
  * added net.sf.oval.Validator.addConstraintSet(ConstraintSetConfiguration)
* made resolving of constraint violation messages customizable
  * added net.sf.oval.MessageResolver
  * added net.sf.oval.MessageResolverImpl
  * added net.sf.oval.Validator.getMessageResolver()
  * added net.sf.oval.Validator.setMessageResolver(MessageResolver)

### Fixed
* fixed some serialization related issues
* fixed NPE in InstanceOfCheck

### Changed
* net.sf.oval.AbstractAnnotationCheck: class AbstractCheck renamed to AbstractAnnotationCheck

### Removed
* removed the "check" property from the net.sf.oval.ConstraintViolation class


## [0.6] - 2006-08-04

### Added
* constraints programmatically specified during runtime via net.sf.oval.Validator.addCheck(...) can now be removed using net.sf.oval.Validator.removeCheck(...)
* added french translation for the violation messages of the built-in constraints (provided by mimil)
* added @InstanceOf constraint
* simplified annotation check loading by introducing a check parameter to the @Constraint annotation that is used to specify the constraint check class
* made the code to resolve the variables names of constructor and method parameters configurable via the ParameterNameResolver interface and the Validator.setParameterNameResolver(...) method
* added @AssertValid constraint
* introduced an ignoreIfNull parameter for the @ValidateWithMethod annotation

### Changed
* refactored AspectJ related code into separate classes in the package net.sf.oval.aspectj to potentially support other AOP libraries later
* moved @FieldConstraints from package net.sf.oval.annotations to net.sf.oval.constraints
* @FieldConstraints constraint now also considers constraints added to fields during runtime via Validator.addCheck(...)
* changed all static methods of Validator and ConstraintsEnforcer to non-static to allow multiple validator instances at the same time being configured differently
* net.sf.oval.annotations.PreValidateThis: annotation @PreValidateObject renamed to @PreValidateThis
* net.sf.oval.annotations.PostValidateThis: annotation @PostValidateObject renamed to @PostValidateThis

### Removed
* removed net.sf.oval.Validator.setAnnotationCheckLoader(AnnotationCheckLoader)
* removed net.sf.oval.Validator.getAnnotationCheckLoader()
* removed net.sf.oval.AnnotationCheckLoader
* removed net.sf.oval.AnnotationCheckLoaderImpl


## [0.5] - 2006-04-27

### Added
* added net.sf.oval.ConstraintViolation which now holds the information of a single constraint violation
* using @net.sf.oval.annotations.FieldConstraints you can now apply constraints defined for fields to any constructor/method parameter or getter method in the same class
* added support for localizable error messages
* added support for parameters in constraint violation messages
* net.sf.oval.context.ConstructorParameterContext: added method getParameterName()
* net.sf.oval.context.MethodParameterContext: added method getParameterName()
* the way how the corresponding checks for constraint annotations are resolved/loaded has been made customizable via net.sf.oval.Validator.setAnnotationCheckLoader(AnnotationCheckLoader)
* constraints can now be specified programmatically during runtime via net.sf.oval.Validator.addCheck(...) -> this allows you to implement alternative configuration mechanisms
* added the aspect ApiUsageAuditor that will report warnings about unsupported usage of annotations in Eclipse IDE
* added support for specifying constraints on method return values when using programming by contract (post condition)

### Fixed
* net.sf.oval.Validator: Validation of inherited classes throws NullPointerException [net.sf Issue 1461805]

### Changed
* net.sf.oval.exceptions.ConstraintViolationsException: now holds an array of ConstraintViolations
* net.sf.oval.annotations.Validatable: default value for applyFieldConstraintsToSetter() changed from "true" to "false"
* net.sf.oval.Validator: method validate(Object) now returns List<ConstraintViolation> instead of ArrayList<ConstraintViolationException>
* classes only need to be annotated with @net.sf.oval.annotations.Constrain when using the "design by contract" feature in conjunction with AspectJ
* net.sf.oval.Validator: refactored AspectJ related fields and methods into a separate class ConstraintsEnforcer
* net.sf.oval.ClassChecks: Reduced class visibility from public to package
* net.sf.oval.ConstraintsEnforcer: aspect ValidationAspect renamed to ConstraintsEnforcerAspect
* net.sf.oval.ConstraintsViolatatedListener: class ValidationListener renamed to ConstraintsViolatatedListener
* net.sf.oval.ConstraintsViolatatedListener: renamed method onValidationException to onConstraintsViolatedException
* net.sf.oval.ConstraintsViolatatedAdapter: class ValidationAdapter renamed to ConstraintsViolatatedAdapter
* net.sf.oval.annotations.Constrain: renamed @Validatable to @Constrain
* net.sf.oval.annotations.Constraint: renamed @ConstraintAnnotation to @Constraint
* net.sf.oval.constraints.NotSelfRef: renamed @NotThis to @NotSelfRef
* net.sf.oval.exceptions.ConstraintAnnotationNotPresentException: class ValidatableAnnotationNotPresentException renamed to ConstraintAnnotationNotPresentException
* net.sf.oval.exceptions.ConstraintsViolatedException: class ConstraintValidationException renamed to ConstraintsViolatedException


##[0.4] - 2006-03-17

### Fixed
* net.sf.oval.constraints.AbstractCheck: the message specified in constraint annotations was not used in the ConstraintException [sf.net Issue 1449558]
* net.sf.oval.ValidationAspect: OVal tried to validate inner classes not having the @Validatable annotation which resulted in net.sf.oval.exceptions.ValidatableAnnotationNotPresent

### Changed
* net.sf.oval.ValidationAspect: cleaned up and optimized for performance
* net.sf.oval.Validator: cleaned up and optimized for performance
* net.sf.oval.Validator: the removeListener methods now return a boolean value specifying if the listener was registered with the validator
* net.sf.oval.Validator: renamed onObjectValidationException(..) methods to onConstraintValidationException(..)
* net.sf.oval.exception.ConstraintException: class ConstraintException renamed to ConstraintValidationException
* net.sf.oval.annotations.PreValidateObject: annotation @PreValidate renamed to @PreValidateObject
* net.sf.oval.annotations.PostValidateObject: annotation @PostValidate renamed to @PostValidateObject
* net.sf.oval.contexts: renamed net.sf.oval.context to net.sf.oval.contexts
* net.sf.oval.contexts.ValidationContext: changed from class to interface
* net.sf.oval.Validator: renamed field defaultValidationMode to validationMode
* net.sf.oval.exception.ValidatableAnnotationNotPresent renamed to ValidatableAnnotationNotPresentException


## [0.3] - 2005-10-17

### Added
* net.sf.oval.annotations.Validatable#applyFieldConstraintsToSetter()
  => constraints specified for fields are now automatically applied to the value parameter of the corresponding setter method
* net.sf.oval.test.SetterValidationWithFieldConstraintsTest
* net.sf.oval.constraints.NotNegative

### Changed
* net.sf.oval.constraints.Check: renamed method isValid to isSatisfied
* net.sf.oval.ClassChecks: now uses the class loader of the constraint annotation to load the corresponding Check class
* net.sf.oval.ClassChecks: changed the logging level to from WARNING to FINE
* net.sf.oval.ValidationAspect: changed the logging level to from INFO to FINE
* net.sf.oval.exception.ValidationException: class ValidationException renamed to OValException
* net.sf.oval.exception.InvalidValueException: class InvalidValueException renamed to ConstraintException
* net.sf.oval.exception.ObjectValidationException: dropped
* net.sf.oval.exception.AccessingFieldValueFailedException: now extends net.sf.oval.exception.ReflectionException
* net.sf.oval.exception.InvokingGetterFailedException: now extends net.sf.oval.exception.ReflectionException


## 0.1alpha - 2005-08-17

 initial release