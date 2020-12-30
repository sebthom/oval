# OVal User Guide

1. [Introduction](#introduction)
1. [Runtime Dependencies](#runtime_dependencies)
1. [Using OVal for property validation](#property-validation)
   1. [Declaring constraints for class fields](#declaring-field-constraints)
   1. [Declaring constraints for getter methods\' return value](#declaring-getter-constraints)
   1. [Declaring conditional constraints using expression languages](#declaring-conditional-constraints)
   1. [Declaring activation rules for constraints](#declaring-activation-rules)
   1. [Declaring constraints for nested properties](#declaring-nested-property-constraints)
   1. [Recursive Validation](#recursive-validation)
   1. [Interpreting EJB3 JPA annotations](#jpa-annotations)
   1. [Interpreting Bean Validation annotations](#bean-validation-annotations)
1. [Using OVal for programming by contract](#programming-by-contract)
   1. [Preparing your project](#project-preparation)
   1. [Working with preconditions](#preconditions)
   1. [Working with postconditions](#postconditions)
   1. [Working with invariants](#invariants)
   1. [Using the probe mode to simplify UI user input validation](#probe-mode)
   1. [Converting ConstraintsViolatedExceptions](#converting-exceptions)
1. [Creating custom annotation based constraints](#custom-constraint-annotations)
1. [Expressing complex class specific constraints](#complex-class-specific-constraints)
   1. [Using `@ValidateWithMethod`](#using-validate_with_method)
   1. [Using `@CheckWith`](#using-check_with)
1. [XML based configuration](#xml-config)
1. [Additional configuration and customization options](#additional-config)
   1. [Constraint profiles](#constraint-profiles)
   1. [Collection factory](#collection-factory)
   1. [Adding additional expression languages](#additiona-expression-languages)
   1. [Spring framework integration](#spring-integration)
   1. [Apache Struts 2 integration](#apache-struts-integration)


## <a name="introduction"></a>Introduction

OVal is a pragmatic and extensible general purpose validation framework for any kind of Java objects (not only JavaBeans) and allows you:

- to easily validate objects on demand,
- to specify validation constraints for class fields and getter methods' return values,
- to validate objects based on certain EJB3 JPA annotations (namely all field annotations that require a not-null value),
- to configure constraints via annotations, [POJOs](https://en.wikipedia.org/wiki/Plain_old_Java_object) and/or simple XML files,
- to express constraints using scripting languages such as [JavaScript](https://github.com/mozilla/rhino), [Groovy](http://groovy-lang.org/),
[BeanShell](http://www.beanshell.org/), [OGNL](https://github.com/jkuhnert/ognl) or [MVEL](https://github.com/mvel/mvel)
- to easily create custom constraints, and
- to develop new constraint configuration mechanisms.

When using [AspectJ](https://www.eclipse.org/aspectj/doc/next/progguide/starting-aspectj.html) or [Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html) certain [Programming by Contract](https://en.wikipedia.org/wiki/Design_by_contract) (aka Design By Contract or DBC) features are available:
- specifying constraints for **constructor parameters** that are automatically checked when a constructor is called (preconditions),
- specifying constraints for **method parameters** that are automatically checked when a method is called (preconditions),
- requiring a certain object state before a method is called (preconditions)
- enforcing object validation after an object has been created (invariants),
- enforcing object validation before/after a method of an object is/has been called (invariants),
- specifying constrains for a method\'s return value that are automatically checked after a method has been executed (postconditions),
- requiring a certain object state after a method is called (postconditions).


## <a name="runtime_dependencies"></a>Runtime Dependencies

OVal requires **Java 8** or later, which is the only hard requirement, depending on the features you want to use additional libraries are required:

- AspectJ: if you want to use the above mentioned programming by contract features.
- Apache Commons JEXL: to define constraints via JEXL expressions.
- BeanShell: to define constraints via BeanShell expressions.
- Groovy: to define constraints via Groovy expressions.
- JRuby: to define constraints via Ruby expressions.
- Mozilla Rhino: to define constraints via JavaScript expressions.
- MVEL: to define constraints via MVEL expressions.
- OGNL: to define constraints via OGNL expressions.
- JXPath: to use JXPath expressions for the constraint target declarations.
- XStream: to configure OVal via XML configuration files.
- GNU Trove: to have OVal internally use the GNU Trove high performance collections.
- Javolution: to have OVal internally use Javolution\'s high performance collections.


## <a name="property-validation"></a>Using OVal for property validation

### <a name="declaring-field-constraints"></a>Declaring constraints for class fields

You can add constraint annotations to class fields that are checked when an object validation is performed.
There exists a number of pre-built constraints that you can find in the package `net.sf.oval.constraint`.

```java
public class BusinessObject {

  @NotNull
  @NotEmpty
  @Length(max=32)
  private String name;

  //...
}
```

An object can be validated by using the method `net.sf.oval.Validator#validate(Object obj)`.

Example usage:

```java
Validator validator = new Validator();

BusinessObject bo = new BusinessObject(); // name is null

// collect the constraint violations
List<ConstraintViolation> violations = validator.validate(bo);

if(!violations.isEmpty()) {
  LOG.severe("Object " + bo + " is invalid.");
  throw new BussinessException(violations);
}
```

### <a name="declaring-getter-constraints"></a>Declaring constraints for getter methods\' return values

You can specify constraints for the return value of getter methods.  When validating the object the values of all fields
and the return values of the getter methods are checked against the specified constraints.

The methods need to be annotated with `@net.sf.oval.configuration.annotation.IsInvariant`. Return value constraints
specified for methods missing this annotation are ignored during validation.

**Important:** To retrieve the return value of the getter method OVal invokes the getter during the validation process.
Therefore you need to ensure that the getter method really is just a getter method and does not change the object state.

```java
public class BusinessObject {

  private String name = null;

  @IsInvariant
  @NotNull
  @Length(max = 4)
  public String getName() {
    return name;
  }

  // ...
}
```

Example usage:

```java
Validator validator = new Validator();

BusinessObject bo = new BusinessObject("blabla");

// collect the constraint violations
List<ConstraintViolation> violations = validator.validate(bo);

if(!violations.isEmpty()) {
  LOG.severe("Object " + bo + " is invalid.");
  throw new BussinessException(violations);
}
```

### <a name="declaring-conditional-constraints"></a>Declaring conditional constraints using expression languages

When you annotate a field or getter with multiple constraint annotations they are ANDed. If you require other logical
constructs you can use a scripting language to express them. To do so annotate the field or getter with the
`@net.sf.oval.constraint.Assert` annotation as shown in the following example:

```java
public class BusinessObject {

  @NotNull
  public String deliveryAddress;

  @NotNull
  public String invoiceAddress;

  // mailingAddress must either be the delivery address or the invoice address
  @Assert(expr = "_value ==_this.deliveryAddress || _value == _this.invoiceAddress", lang = "groovy")
  public String mailingAddress;
}
```

The `expr` parameter holds the script to be evaluated. If the script returns `true` the constraint is satisfied.
OVal provides two special variables:
- `_value` - contains the value to validate (field value or getter return value)
- `_this` - is a reference to the validated object

The `lang` parameter specifies the scripting language you want to use. In case the required libraries are loaded,
OVal is aware of these languages:
- `bsh` or `beanshell` for BeanShell,
- `groovy` for Groovy,
- `jexl` for JEXL,
- `js` or `javascript` for JavaScript (via Mozilla Rhino),
- `mvel` for MVEL,
- `ognl` for OGNL,
- `ruby` or `jruby` for Ruby (via JRuby)

Additional scripting languages can be registered via `Validator.addExpressionLanguage(String, ExpressionLanguage)`.

### <a name="declaring-activation-rules"></a>Declaring activation rules for constraints

Besides using `@Assert` to declare conditional constraints it is also possible to specify a activation rules for other
constraints using the `when` attribute. This way you can for example turn on or off constraints based on a given state
of the object. The `when` attribute can hold a formula in one of the supported expression languages, it is prefixed by
the id of the expression language to be used.

In the following example `fieldB` must not be `null` only if `fieldA` is not `null` as well. With the prefix `groovy:`
it is indicated that the formula is expressed in the Groovy language.

```java
public class BusinessObject {

  private String fieldA;

  @NotNull(when = "groovy:_this.fieldA != null")
  private String fieldB;
}
```

### <a name="declaring-nested-property-constraints"></a>Declaring constraints for nested properties

Using the `target` attribute of constraints you can specify the path to an object where the constraint should be applied.
In the following example the `@NotNull` constraint will be validated for the `customer.homeAddress.street` field.

```java
public class BusinessObject {
  @AssertValid
  @NotNull(target="homeAddress.street")
  private Customer customer;
}
```

If JXPath is on the classpath you can also use the XPath syntax to specify the target value. In the following example the
`@NotNull` constraint will be checked for `street` field of the first address object in the `customer.addresses`
collection.

```java
public class BusinessObject {
  @AssertValid
  @NotNull(target="jxpath:addresses[0]/street")
  private Customer customer;
}
```

To target values of maps or collections JXPath must be used, the built-in implementation does not support this yet.


### <a name="recursive-validation"></a>Recursive validation

By specifying the special constraint annotation `@AssertValid` you instruct OVal to ensure that all constraints declared
on the referenced object are satisfied too.

```java
public class BusinessObject {
  @NotNull
  @AssertValid
  private Address address;
}
```

### <a name="jpa-annotations"></a>Interpreting EJB3 JPA annotations

OVal\'s configuration mechanism is highly customizable. Using the `net.sf.oval.configuration.Configurer` interface you
can write your own constraint configurers configuring OVal based on other XML schemas, other sets of annotations or
anything else you like.

OVal comes with a configurer that is capable of translating certain EJB3 JPA annotations into equivalent OVal constraints.
The `net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer` interprets the EJB3 JPA annotations as follows:

    @javax.persistence.Basic(optional=false)     => @net.sf.oval.constraint.NotNull
    @javax.persistence.OneToOne(optional=false)  => @net.sf.oval.constraint.NotNull
    @javax.persistence.OneToOne                  => @net.sf.oval.constraint.AssertValid
    @javax.persistence.OneToMany                 => @net.sf.oval.constraint.AssertValid
    @javax.persistence.ManyToOne(optional=false) => @net.sf.oval.constraint.NotNull
    @javax.persistence.ManyToOne                 => @net.sf.oval.constraint.AssertValid
    @javax.persistence.Column(nullable=false)    => @net.sf.oval.constraint.NotNull (only applied for fields not annotated with @javax.persistence.GeneratedValue or @javax.persistence.Version)
    @javax.persistence.Column(length=5)          => @net.sf.oval.constraint.Length

```java
@Entity
public class MyEntity {
  @Basic(optional = false)
  @Column(length = 4)
  public String id;

  @Column(nullable = false)
  public String descr;

  @ManyToOne(optional = false)
  public MyEntity parent;
}
```

Example usage:

```java
// configure OVal to interpret OVal constraint annotations as well as EJB3 JPA annotations
Validator validator = new Validator(new AnnotationsConfigurer(), new JPAAnnotationsConfigurer());

MyEntity entity = new MyEntity();

entity.id = "12345"; // violation - the max length is 4
entity.descr = null; // violation - cannot be null
entity.parent = null; // violation - cannot be null

// collect the constraint violations
List<ConstraintViolation> violations = validator.validate(entity);
```

### <a name="bean-validation-annotations"></a>Interpreting Bean Validation annotations

OVal itself is not a JSR303/JSR380 compliant bean validation framework. However it now comes with a configurer
that can translate the standard Bean Validation constraints (javax.validation.constraints.\*) into equivalent
OVal constraints. The `net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer`
interprets the annotations as follows:

    @javax.validation.constraints.AssertFalse     => @net.sf.oval.constraint.AssertFalse
    @javax.validation.constraints.AssertTrue      => @net.sf.oval.constraint.AssertTrue
    @javax.validation.constraints.DecimalMax      => @net.sf.oval.constraint.Max
    @javax.validation.constraints.DecimalMin      => @net.sf.oval.constraint.Min
    @javax.validation.constraints.Digits          => @net.sf.oval.constraint.Digits
    @javax.validation.constraints.Email           => @net.sf.oval.constraint.Email
    @javax.validation.constraints.Future          => @net.sf.oval.constraint.Future
    @javax.validation.constraints.FutureOrPresent => @net.sf.oval.constraint.Future(min="now")
    @javax.validation.constraints.Max             => @net.sf.oval.constraint.Max
    @javax.validation.constraints.Min             => @net.sf.oval.constraint.Min
    @javax.validation.constraints.Negative        => @net.sf.oval.constraint.Max(max=0, inclusive=false)
    @javax.validation.constraints.NegativeOrZero  => @net.sf.oval.constraint.Max(max=0, inclusive=true)
    @javax.validation.constraints.NotBlank        => @net.sf.oval.constraint.NotNull+NotBlank
    @javax.validation.constraints.NotEmpty        => @net.sf.oval.constraint.NotNull+NotEmpty
    @javax.validation.constraints.NotNull         => @net.sf.oval.constraint.NotNull
    @javax.validation.constraints.Null            => @net.sf.oval.constraint.Null
    @javax.validation.constraints.Past            => @net.sf.oval.constraint.Past
    @javax.validation.constraints.PastOrPresent   => @net.sf.oval.constraint.Past(max="now")
    @javax.validation.constraints.Pattern         => @net.sf.oval.constraint.Pattern
    @javax.validation.constraints.Size            => @net.sf.oval.constraint.Size
    @javax.validation.constraints.Positive        => @net.sf.oval.constraint.Min(min=0, inclusive=false)
    @javax.validation.constraints.PositiveOrZero  => @net.sf.oval.constraint.NotNegative
    @javax.validation.constraints.Valid           => @net.sf.oval.constraint.AssertValid

```java
public class MyEntity {
  @javax.validation.constraints.NotNull
  @javax.validation.constraints.Size(max = 4)
  public String id;

  @javax.validation.constraints.NotNull
  public String descr;

  @javax.validation.constraints.NotNull
  public MyEntity parent;
}
```

Example usage:
```java
// configure OVal to interpret OVal constraint annotations as well as built-in Bean Validation annotations
Validator validator = new Validator(new AnnotationsConfigurer(), new BeanValidationAnnotationsConfigurer());

MyEntity entity = new MyEntity();

entity.id = "12345"; // violation - the max length is 4
entity.descr = null; // violation - cannot be null
entity.parent = null; // violation - cannot be null

// collect the constraint violations
List<ConstraintViolation> violations = validator.validate(entity);
```


## <a name="programming-by-contract"></a>Using OVal for programming by contract

By utilizing AspectJ OVal provides support for several aspects of programming by contract - however it is not a full
blown programming by contract implementation..

With OVal you can
- enforce that a parameterized constructor/method is invoked only if the given arguments satisfy prior defined constraints (precondition)
- enforce that a method is invoked only if the object is in a certain state (precondition/invariant)
- enforce that the return value of a method must satisfy prior defined constraints (postcondition)
- enforce that the object must be in a certain state after a method has been executed (postcondition/invariant)

### <a name="project-preparation"></a>Preparing your project

The easiest way to getting started is to use the [Eclipse IDE](http://www.eclipse.org/)
in conjunction with the [AspectJ plug-in](http://www.eclipse.org/ajdt/).

Create a new AspectJ project or add AspectJ support to an existing Java project by
right-clicking the project in the Package Explorer and selecting `Convert To AspectJ Project`

Add the `net.sf.oval_x.x.jar` file to your library path.

Create a new aspect via `File -> New -> Aspect` that extends the abstract aspect
`net.sf.oval.guard.GuardAspect`. When the new aspect is created the AspectJ builder
will automatically weave the validation related code into your compiled classes annotated
with `@net.sf.oval.guard.Guarded`.

Now you can create all your business classes, add the `@net.sf.oval.guard.Guarded` annotation
and define the required constraints using the built-in or custom constraint annotations.

### <a name="preconditions"></a>Working with preconditions

#### <a name="declaring-constructor-parameter-constraints"></a>Declaring constraints for constructor parameters

Constraints specified for constructor parameters are automatically checked when the constructor is
invoked. Invocations of the constructor will be prohibited if any of the constraints is not satisfied.
In such a case a `net.sf.oval.exception.ConstraintsViolatedException` will be thrown.

```java
@Guarded
public class BusinessObject {
  public BusinessObject(@NotNull String name) {
    this.name = name;
  }

  // ...
}
```

Example usage:

```java
// throws a ConstraintsViolatedException because parameter name is null
 BusinessObject bo = new  BusinessObject(null);
```

#### <a name="declaring-method-parameter-constraints"></a>Declaring constraints for method parameters

Constraints specified for method parameters are automatically checked when the method is invoked.
Invocations of to the method will be prohibited if any of the constraints is not satisfied.
In such a case a `net.sf.oval.exception.ConstraintsViolatedException` will be thrown.

```
@Guarded
public class BusinessObject {
  public void setName(@NotNull String name) {
    this.name = name;
  }
  // ...
}
```

Example usage:

```
BusinessObject bo = new BusinessObject();
bo.setName(null); // throws a ConstraintsViolatedException because parameter name is null
```

#### <a name="applying-field-constraints-to-method-parameters"></a>Applying field constraints to constructor/method parameters

You can apply the constraints specified for a field in the same or a super class to any constructor
or method parameter by using the `@net.sf.oval.constraint.AssertFieldConstraints` annotation.

If you do not specify a field name within the `@net.sf.oval.constraint.AssertFieldConstraints`
annotation the constraints of the field with the same name as the annotated parameter are applied to
the parameter.

If you specify a field name within the `@net.sf.oval.constraint.AssertFieldConstraints` annotation
the constraints of the field with the specified name are applied to the annotated parameter.

```java
@Guarded
public class BusinessObject {
  @NotNull
  @NotEmpty
  @Length(max=10)
  private String name;

  public void setName(@AssertFieldConstraints String name) {
    this.name = name;
  }

  public void setAlternativeName(@AssertFieldConstraints("name") String altName) {
    this.alternativeName = altName;
  }

  // ...
}
```

Example usage:

```java
BusinessObject bo = new BusinessObject();
bo.setName(""); // throws a ConstraintsViolatedException because parameter is empty
bo.setAlternativeName(null); // throws a ConstraintsViolatedException because parameter is null
```

If you like to apply the constraints of all fields to their corresponding setter methods you can
alternatively set the `applyFieldConstraintsToSetters` property of the `Guarded` annotation to
`true`. This is especially useful if you have a lot of setter methods and you are following the
JavaBean convention. *Important:* The setter method must be declared within the same class as the property.

```java
@Guarded(applyFieldConstraintsToSetters=true)
public class BusinessObject {
  @NotNull
  @NotEmpty
  @Length(max=10)
  private String name;

  public void setName(String name) {
    this.name = name;
  }

  // ...
}
```

Another convenience option is the `applyFieldConstraintsToConstructors` property of the `Guarded`
annotation. If set to true, OVal applies the specified field constraints to the corresponding parameters
of the declared constructors within the same class. In this context, a corresponding parameter is a
constructor parameter with the same name and type as the field.

#### <a name="scripted-preconditions"></a>Using scripted expressions for preconditions

Similar to the above described `@net.sf.constraint.Assert` constraint annotation for fields
you can annotate a method with `@net.sf.guard.Pre` allowing you to express conditional
constraints using a scripting language.

```java
@Guarded
public class Transaction {
  private BigDecimal amount;

  // ensure that amount is not null, ensure that value2add is greater than amount
  @Pre(expr = "_this.amount!=null && amount2add > _this.amount", lang = "groovy")
  public void increase(BigDecimal amount2add) {
    amount = amount.add(amount2add);
  }
}
```

The `expr` parameter holds the script to be evaluated. If the script returns `true` the
constraint is satisfied. OVal provides special variables for use within the expression:
- `_args[]` - array holding the method arguments
- `_this` - is a reference to the current object
- additionally variables matching the parameter names are available

The `lang` parameter specifies the scripting language you want to use.
In case the required libraries are loaded, OVal is aware of these languages:
- `bsh` or `beanshell` for BeanShell,
- `groovy` for Groovy,
- `jexl` for JEXL,
- `js` or `javascript` for JavaScript (via Mozilla Rhino),
- `mvel` for MVEL,
- `ognl` for OGNL, or
- `ruby` or `jruby` for Ruby (via JRuby)

#### <a name="disabling-preconditions"></a>Disabling precondition checks

You can globally disable or enable the checking of preconditions via the `Guard.setPreConditionsEnabled(boolean)` method.

Example usage:

```java
MyAspect.aspectOf().getGuard().setPreConditionsEnabled(false);
```

### <a name="declaring-postconditions"></a>Working with postconditions

#### Declaring method return value constraints

By adding constraint annotations to a non-void method you can specify constraints for
the method\'s return value. When the method is invoked and the return value does not
satisfy all constraints a `ConstraintsViolatedException` is thrown.

**Note:** Despite the thrown exception the method code still has been executed, you
have to rollback the changes performed by this method manually.

If a non-void, non-parameterized method is also annotated with `@IsInvariant` it\'s
constraints will also be checked when calling the `Validator.validate(Object)` method
on an object of this type.

```java
@Guarded
public class BusinessObject {
  private String name = null;

  @IsInvariant
  @NotNull
  @Length(max = 4)
  public String getName() {
    return name;
  }

  @NotNull
  @Length(max = 4)
  public String getNameOrDefault(String default) {
    return name == null ? default : name;
  }

  // ...
}
```

Example usage:

```java
BusinessObject bo = new BusinessObject();

// throws a ConstraintsViolatedException because field name is null
bo.getName();

// throws a ConstraintsViolatedException because the field "name" is null and therefore the default parameter will be returned which has a length &lt; 4 characters
bo.getNameOrDefault("abc");

Validator validator = new Validator();

// returns one ConstraintViolation because the getter method getName() is declared as invariant and returns an invalid value (null)
List<ConstraintViolation> violations = validator.validate(bo);
```

#### <a name="scripted-postconditions"></a>Using scripted expressions for postconditions

For declaring scripted postconditions you can use the `@net.sf.guard.Post` annotation which works
similar to `@net.sf.guard.Pre` for preconditions.

```java
@Guarded
public class Transaction {
  private BigDecimal amount;

  // ensure that amount after calling the method is greater than it was before
  @Post(expr = "_this.amount>_old", old = "_this.amount", lang = "groovy")
  public void increase(BigDecimal  amount2add)   {
    amount = amount.add(amount2add);
  }
}
```

The `expr` parameter holds the script to be evaluated. If the script returns `true` the constraint
is satisfied. OVal provides special variables for use within the expression:
- `_args[]` - array holding the method arguments
- `_this` - is a reference to the current object
- `_returns` - the method\'s return value
- `_old` - see the description of the `old` parameter below
- additionally variables matching the parameter names are available

The `lang` parameter specifies the scripting language you want to use.
In case the required libraries are loaded, OVal is aware of these languages:
- `bsh` or `beanshell` for BeanShell,
- `groovy` for Groovy,
- `jexl` for Groovy,
- `js` or `javascript` for JavaScript (via Mozilla Rhino),
- `mvel` for MVEL,
- `ognl` for OGNL, or
- `ruby` or `jruby` for Ruby (via JRuby)

The `old` parameter is optionally, it can hold another expression that is evaluated before the method
is executed. The result is made available in the post constraint expression as a special variable
called `_old`. The old expression can also return an array or a map allowing you to store multiple
values. This way you can \"remember\" the old state of multiple properties of an object.
An expression like `old = "[amount:_this.amount, date:_this.date]"` in Groovy returns a map with
the keys `amount` and `date` holding the values of the object\'s properties `amount` and `date`.
These values then can be used in the constraint expression like this:
```java
expression = "_this.amount>_old.amount && _this.date>_old.date"
```

#### <a name="disabling-postconditions"></a>Disabling postcondition checks

You can globally disable or enable the checking of postconditions via the
`Guard.setPostConditionsEnabled(boolean)` method.

Example usage:

```java
MyAspect.aspectOf().getGuard().setPostConditionsEnabled(false);
```

### <a name="invariants"></a>Working with invariants

#### <a name="disabling-invariants"></a>Disabling automatic invariants checks

By default OVal checks class invariants before and after calls to any non-private method of guarded
classes and after constructor execution. If required you can globally disable the automatic checking of
invariants via the `Guard.setInvariantsEnabled(boolean)` method or for all objects of a specific class
using the `Guard.setInvariantsEnabled(Class<?>, boolean)` method.

Example usage:

```java
MyAspect.aspectOf().getGuard().setInvariantsEnabled(false);
```

#### <a name="object-validation-before-method-execution"></a>Enforcing object validation before method execution

If you disabled the automatic check of class invariants you can still enable the checking of the invariants
prior calls to a certain method by annotating the respective method with `@net.sf.oval.guard.PreValidateThis`.

In case of constraint violations a ConstraintsViolatedException is thrown and the method will not be executed.

```java
@Guarded
public class BusinessObject {
  @NotNull
  private String name = null;

  @PreValidateThis
  public void save() {
    // do something fancy
  }

  // ...
}
```

Example usage:

```java
// create a new business object and leaving the field name null
BusinessObject bo = new  BusinessObject();

// the save() method will throw a ConstraintsViolatedException because field name is null
bo.save();
```

#### <a name="object-validation-after-constructor-execution"></a>Enforcing object validation after constructor execution

If you disabled the automatic check of class invariants you can still enable the checking of the invariants
after an object has been instantiated by annotating the constructors of the corresponding class with
`@net.sf.oval.guard.PostValidateThis`.

In case of constraint violations the constructor will throw a `ConstraintsViolatedException` which
effectively means the code trying to instantiate the object cannot get hold of a reference to the object
being in an invalidate state and the invalid object will get garbage collected.

```java
@Guarded
public class BusinessObject {
  @NotNull
  private String name;

  /**
   * constructor
   */
  @PostValidateThis
  public BusinessObject() {
    super();
  }

  ...
}
```

Example usage:

```java
// throws a ConstraintsViolatedException because the name field is null
BusinessObject bo = new  BusinessObject();
```

#### <a name="object-validation-after-method-execution"></a>Enforcing object validation after method execution

If you disabled the automatic check of class invariants you can still enable the checking of the
invariants after a method has been executed by annotating the method with `@net.sf.oval.guard.PostValidateThis`.

In case of constraint violations the method will throw an ConstraintsViolatedException.

**Note:** Despite the thrown exception the method code still has been executed, you have to manually
rollback the changes performed by this method.

```
@Guarded
public class BusinessObject {
  @Length(max=10)
  private String name = "12345";

  @PostValidateThis
  public appendToName(String appendix) {
    name += appendix;
  }

  //...
}
```

Example usage:

```java
BusinessObject bo = new BusinessObject();
bo.appendToName("123456"); // throws a ConstraintsViolatedException because field name is now too long
```


### <a name="probe-mode"></a>Using the probe mode to simplify UI user input validation

OVal provides a so called probe mode in which you can execute all methods of an object and the guard will only
check the preconditions (e.g. parameter constraints) and not execute the method.

This is especially useful if you want to test input values received from the end user in the UI layer against
the setter methods of a business object. You can simply pass the values to the corresponding setters and
have any detected violations collect by a `ConstraintsViolatedListener`.
Afterwards you can report all violations back to the UI layer and have them displayed to the end user.

Example business object:

```java
@Guarded
public class Person {

  @NotNegative
  private int age;

  @Min(5)
  private String name = "";

  @Length(min=5, max=5)
  private String zipCode = "";

  public void setAge(@AssertFieldConstraints int age) {
    this.age = age;
  }

  public void setName(@AssertFieldConstraints String name) {
    this.name = name;
  }

  public void setZipCode(@AssertFieldConstraints String zipCode) {
    this.zipCode = zipCode;
  }

  // ...
}
```

Example usage:

```java
/* *****************************************************
 * somewhere in the UI layer
 * *****************************************************/
 inputForm.setName("1234");
 inputForm.setAge(-4);
 inputForm.setZipCode("123");

//...

/* *****************************************************
 * later in the business layer
 * *****************************************************/
public Person createPerson(PersonInputForm inputForm) throws ConstraintsViolatedException {
  Person person = new Person();

  Guard guard = MyGuardAspect.aspectOf().getGuard();

  // enable the probe mode in the current thread for the person object
  guard.enableProbeMode(person);

  // simulate applying the values to the person bean
  person.setName(inputForm.getName());
  person.setAge(inputForm.getAge());
  person.setZipCode(inputForm.getZipCode());

  // disable the probe mode in the current thread for the person object
  ProbeModeListener result = guard.disableProbeMode(person);

  // check if any constraint violations occurred
  if(!result.getConstraintViolations().isEmpty()) {
     // report the collected constraint violations to the UI layer
     throw new ConstraintsViolatedException(result.getConstraintViolations());
  } else {
    // apply the values to the person bean
    result.commit();

    dao.save(person);
    return person;
  }
}
```

### <a name="converting-exceptions"></a>Converting ConstraintsViolatedExceptions

When calling methods on guarded objects these methods will throw `ConstraintsViolatedException`s
in case any pre- or postconditions are violated. Their might be good reasons why you may want to have
other exceptions thrown instead of OVal\'s proprietary exceptions, e.g. JRE standard exceptions such
as `IllegalArgumentException` or `IllegalStateException`.

OVal's Guard class allows you to register an exception translator. The exception translator defines
a `translateException()` method that is executed for all occurring exceptions during runtime validation.
This allows you to translate any `OValException` thrown during validation into another
`RuntimeException` which will be thrown instead. As an example have a look at the
`net.sf.oval.exception.ExceptionTranslatorJDKExceptionsImpl` class how an implementation could look like.

Example usage:

```java
public aspect MyAspect extends GuardAspect {
  public MyAspect() {
    super();

    // specify an exception translator
    getGuard().setExceptionTranslator(new net.sf.oval.exception.ExceptionTranslatorJDKExceptionsImpl());
  }
}
```


## <a name="custom-constraint-annotations"></a>Creating custom annotation based constraints

Developing custom annotation based constraints is fairly easy. All you need to do is:

1. Create a constraint check class that implements `net.sf.oval.AnnotationCheck` or extends `net.sf.oval.AbstractAnnotationCheck`.

   ```java
   public class UpperCaseCheck extends AbstractAnnotationCheck<UpperCase> {
     public boolean isSatisfied(Object validatedObject, Object valueToValidate, OValContext context, Validator validator) {
       if (valueToValidate == null) return true;
       String val = valueToValidate.toString();
       return val.equals(val.toUpperCase());
     }
   }
   ```
1. Create an annotation for your constraint and annotated it with `@net.sf.oval.configuration.annotation.Constraint`. Specify your
   check class as value for parameter \"check\".

   ```java
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
   @net.sf.oval.configuration.annotation.Constraint(checkWith = UpperCaseCheck.class)
   public @interface UpperCase {
     /**
     * message to be used for the ConstraintsViolatedException
     *
     * @see ConstraintsViolatedException
     */
     String message() default "must be upper case";
   }
   ```

1. Use the custom constraint annotation in your code.

   ```java
   public class BusinessObject {
     @UpperCase
     private String userId;

     // ...
   }
   ```

Localization of the constraint violation message can be achieved as follows:

1. Specify a unique message key for the default message string of the constraint annotation, e.g.

   ```java
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
   @net.sf.oval.configuration.annotation.Constraint(checkWith = UpperCaseCheck.class)
   public @interface UpperCase
   {
     /**
      * message to be used for the ConstraintsViolatedException
      *
      * @see ConstraintsViolatedException
      */
     String message() default "UpperCase.violated";
   }
   ```

1. Create custom message bundles (one per language) and specify the translated sting in each bundle:

   ```
   UpperCase.violated={context} must be upper case
   ```

   There exist two default variables that can be used in the message string:
   - `{context}` = the validation context (e.g. a field, a method return value or a constructor/method parameter
   - `{invalidValue}` = the value that has been checked

   If required, you can introduce additional variables - e.g. which reflect additional configuration properties of your
   constraint such as {max}, {min}, {size} - by overriding the `createMessageVariables` method of your custom check class:

   ```java
   @Override
   public Map<String, String> createMessageVariables() {
     Map<String, String> messageVariables = new HashMap<String, String>(2)
     messageVariables.put("max", Integer.toString(max));
     messageVariables.put("min", Integer.toString(min));
     return messageVariables;
   }
   ```

   The message variables can then be used in the corresponding message strings.

   For performance reasons the `createMessageVariables` method is only executed once and the returned map is cached by OVal
   for this constraint check instance. If you change one of the variables you need to invalidate the cache and require
   rebuilding of the variables map by calling `requireMessageVariablesRecreation`.
   For the built-in constraints `requireMessageVariablesRecreation` is invoked in each setter on the constraint check
   implementations, e.g.:

   ```java
   public void setMax(final int max) {
     this.max = max;
     requireMessageVariablesRecreation();
   }
   ```

1. Register your message bundle with OVal:

   ```java
   ResourceBundleMessageResolver resolver = (ResourceBundleMessageResolver) Validator.getMessageResolver();
   resolver.addMessageBundle(ResourceBundle.getBundle("mypackage/CustomMessages"));
   ```

   If you would like to store your message strings other than in message bundles (e.g. in a database):
   Either implement the `MessageResolver` interface or extend the `ResourceBundleMessageResolver` and
   then configure OVal using the static `Validator.setMessageResolver(...)` method to use an instance
   of your custom message resolver instead.


## <a name="complex-class-specific-constraints"></a>Expressing complex class specific constraints

If you have to express a rather complex constraint that is used only within one class you might not want to implement
it as a custom constraint. You have the following two alternatives expressing such class specific constraints in a
convenient way.

### <a name="using-validate_with_method"></a>Using `@ValidateWithMethod`

You can write a method within the class that has a single parameter to receive the value to validate
and that returns true if the constraint is satisfied and false if it is violated.

Example:

```java
private static class TestEntity {
  @Min(1960)
  private int year = 1977;

  @Range(min=1, max=12)
  private int month = 2;

  @ValidateWithMethod(methodName = "isValidDay", parameterType = int.class)
  private int day = 31;

  private boolean isValidDay(int day)   {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setLenient(false);
    cal.set(GregorianCalendar.YEAR, year);
    cal.set(GregorianCalendar.MONTH, month - 1);
    cal.set(GregorianCalendar.DATE, day);
    try {
      cal.getTimeInMillis(); // throws IllegalArgumentException
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }
}
```

### <a name="using-check_with"></a>Using `@CheckWith`

You can write an inner static class extending `net.sf.oval.constraint.CheckWithCheck.SimpleCheck` which then is
referenced via a `@net.sf.oval.constraint.CheckWith` constraint annotation.

Example:

```java
private static class DayEntity {

  @Min(1960)
  private int year;

  @Range(min=1, max=12)
  private int month;

  @CheckWith(DayCheck.class)
  private int day;

  private static class DayCheck implements CheckWithCheck.SimpleCheck {

    public boolean isSatisfied(Object validatedObject, Object value) {
      try {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setLenient(false);
        cal.set(GregorianCalendar.YEAR, ((DayEntity) validatedObject).year);
        cal.set(GregorianCalendar.MONTH, ((DayEntity) validatedObject).month - 1);
        cal.set(GregorianCalendar.DATE, ((DayEntity) validatedObject).day);
        cal.getTimeInMillis(); // may throw IllegalArgumentException return true;
      } catch (IllegalArgumentException e) {}
      return false;
    }

  }
}
```


## <a name="xml-config"></a>XML based configuration

By default the constraints configuration is done by adding annotations representing the constraints to the respective
locations in the source code. Alternatively constraints can also be declared via XML - either for a complete configuration
or to overwrite the annotations based constraint configurations for specific classes, fields, etc.

You can used the [net.sf.oval.configuration.xml.XMLConfigurer](https://github.com/sebthom/oval/blob/master/src/main/java/net/sf/oval/configuration/xml/XMLConfigurer.java)
for loading constraint definitions from an XML file:

```java
XMLConfigurer xmlConfigurer = new XMLConfigurer(new File("oval-config.xml"));
Validator validator = new Validator(xmlConfigurer);
```

Here is an example XML configuration:

```xml
<?xml version="1.0" ?>
<oval
  xmlns="http://oval.sf.net/oval-configuration"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://oval.sf.net/oval-configuration https://raw.githubusercontent.com/sebthom/oval/master/src/main/resources/net/sf/oval/configuration/xml/oval-configuration.xsd"
>
  <!-- define a constraint set -->
  <constraintSet id="user.userid">
    <notNull />
    <matchPattern>
      <pattern pattern="^[a-z0-9]{8}$" flags="0" />
    </matchPattern>
  </constraintSet>

  <!-- define checks for the acme.model.User class -->
  <!-- overwrite=false means already defined checks for this class will not be removed -->
  <class type="acme.model.User" overwrite="false" applyFieldConstraintsToSetter="true">

    <field name="firstName">
      <length min="0" max="3" />
    </field>

    <field name="lastName">
      <length min="0" max="5" />
    </field>

    <!-- overwrite=true means previously defined checks for this field will be overwritten by the checks defined here -->
    <field name="managerId" overwrite="true">
      <!-- use the checks defined for the constraint set "user.userid" -->
      <assertConstraint id="user.userid" />
    </field>

    <field name="userId" overwrite="true">
      <!-- use the checks defined for the constraint set "user.userid" -->
      <assertConstraintSet id="user.userid" />
    </field>

    <!-- define constructor parameter checks -->
    <constructor>
      <!-- parameter 1 -->
      <parameter type="java.lang.String">
        <notNull />
      </parameter>

      <!-- parameter 2 -->
      <!-- the types of all parameters must be listed, even if no checks are defined -->
      <parameter type="java.lang.String" />
    </constructor>

    <!-- define method parameter checks -->
    <method name="setPasswordExpirationDays">
      <!-- parameter 1 -->
      <parameter type="int">
        <notNull />
      </parameter>
     </method>
  </class>
</oval>
```

### Loading custom checks

To load XML files with custom checks, the packages containing the checks must be registered to the XStream instance of the respective
XMLConfigurer for security reasons:

 ```java
XMLConfigurer xmlConfigurer = new XMLConfigurer(new File("oval-config.xml"));
xmlConfigurer.getXStream().allowTypesByWildcard(new String[] {"com.acme.mychecks.**"});
Validator validator = new Validator(xmlConfigurer);
```

### Loading multiple XML files

When loading multiple XML files the XStream instance used for deserializing the configuration can be shared by multiple XMLConfigurers
to improve memory usage and configuration load time.

```java
XStream xStream = XMLConfigurer.createXStream();
xStream.allowTypesByWildcard(new String[] {"com.acme.mychecks.**"});
XMLConfigurer xmlCfg1 = new XMLConfigurer(xStream, new File("oval-config1.xml"));
XMLConfigurer xmlCfg2 = new XMLConfigurer(xStream, new File("oval-config2.xml"));
XMLConfigurer xmlCfg3 = new XMLConfigurer(xStream, new File("oval-config3.xml"));
Validator validator = new Validator(xmlCfg1, xmlCfg2, xmlCfg3);
```


## <a name="additional-config"></a>Additional configuration and customization options

### <a name="constraint-profiles"><a>Constraint profiles

You may come across the requirement to turn on or off the checking of certain constraints across your
domain model, e.g. based on some global configuration settings of the application.
OVal helps you to implement this by introducing constraint profiles. For each declared constraint
you can specify an infinite number of profiles this constraint belongs to.
During runtime you then can enable or disable all constraints associated with a given profile by using
the `disableProfile`, and `enableProfile` methods of the `Validator` instance.
Constraints not having any profiles declared are automatically assigned a profile named `default`.

```java
public class Person {
  @NotNull(profiles = {"profile1"})
  public String firstName;

  @NotNull(profiles = {"profile1"})
  public String lastName;

  @NotNull(profiles = {"profile2", "profile3"})
  public String zipCode;
}
```

Example usage:

```java
Validator v = new Validator();
v.disableProfile("profile1");

Person p = new Person();
v.validate(p);
```

In this case only the null value `zipCode` property will result in a `ConstraintViolation`.
The `@NotNull` constraints of `firstName` and `lastName` are ignored since they are associated with
`profile1` and `profile1` has been disabled.


### <a name="collection-factory"></a>Collection factory

OVal instantiates all internally used collections indirectly via a `CollectionFactory`. OVal comes with three
different implementations of the collection factory (Javolution Collections, GNU Trove Collections, and
JDK Collections). If the Javolution or GNU Trove collection classes are detected in the classpath, OVal
automatically uses the respective CollectionFactory otherwise the collection factory for standard JDK collections is used.

The Collection Factory to be used by OVal can be configured via the static `Validator.setCollectionFactory(factory)` method.

You can implement the `net.sf.oval.collection.CollectionFactory` interface to support other collection implementations.

### <a name="additional-expression-languages"></a>Adding additional expression languages

If you want to express constraints in a scripting language not supported by OVal out of the box, you need to implement the
`net.sf.oval.expression.ExpressionLanguage` interface for the desired language and register your implementation with
OVal using the `Validator.addExpressionLanguage(languageId, expressionLanguage)` method.
Then you can use the specified languageId with expression language aware constraint annotations such as `@Assert`, `@Pre`,
or `@Post` and start expressing constraints in the new expression language.

### <a name="spring-integration"></a>Spring framework integration

#### <a name="spring-validation"></a>Spring Validation

The class `net.sf.oval.integration.spring.SpringValidator` provides an implementation of Spring\'s
`org.springframework.validation.Validator` interface and thus can be used for [Spring Validation](https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/html/validation.html).

#### <a name="spring-aop"></a>Guarding Spring managed beans using Spring AOP

It is possible to use OVal\'s programming by contract feature with AOP solutions other than AspectJ.
Spring for example uses the AOP Alliance API in conjunction with JDK or CGLib proxies to provide certain
features of AOP to Spring managed beans.

With the class `net.sf.oval.guard.GuardInterceptor` we provide an AOP Alliance implementation of the
Guard aspect that enables you to use OVal\'s guarding feature in conjunction with Spring managed beans
without the need for AspectJ.

Here is an example how to configure the guarding of the methods of a Spring managed service.
This configuration requires CGLIB to be on the classpath.

```xml
<beans>
  <bean id="myService" class="MyServiceImpl" />

  <bean id="ovalGuardInterceptor" class="net.sf.oval.guard.GuardInterceptor" />

  <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
    <property name="proxyTargetClass" value="true" />
    <property name="beanNames" value="*Service" />
    <property name="interceptorNames"><list><value>ovalGuardInterceptor</value></list></property>
  </bean>
</beans>
```

#### <a name="injecting-spring-beans-into-constraints"></a>Injecting Spring beans into constraint check classes

To have Spring beans injected automatically into Check classes you need to:

1. annotate the required fields in your check implementation class with `@Autowired`
1. add an instance of `net.sf.oval.integration.spring.BeanInjectingCheckInitializationListener` to the configurer
   ```java
   AnnotationConfigurer myConfigurer = new AnnotationConfigurer();
   myConfigurer.addCheckInitializationListener(BeanInjectingCheckInitializationListener.INSTANCE);
   Validator myValidator = new Validator(myConfigurer);
   ```
1. enable annotation-based configuration in your Spring application context configuration:
   ```xml
   <beans>
      <context:annotation-config />
   </beans>
   ```
1. setup an SpringInjector bean in your Spring application context configuration:
   ```xml
   <beans>
      <bean class="net.sf.oval.integration.spring.SpringInjector" />
   </beans>
   ```

#### <a name="configuring-oval-with-spring"></a>Configuring OVal validator instances with Spring

You can instantiate OVal validators with Spring like any other Spring managed bean.
Here is an example setting up a validator with XML based constraint configuration:

```xml
<beans>
  <bean id="validator" class="net.sf.oval.Validator">
    <constructor-arg>
      <list>
        <bean class="net.sf.oval.configuration.xml.XMLConfigurer">
           <constructor-arg type="java.io.InputStream" value="classpath:com/acme/OValConfiguration.xml" />
        </bean>
      </list>
    </constructor-arg>
  </bean>
</beans>
```

### <a name="apache-struts-integration"></a>Apache Struts 2 integration

Musachy Barroso has developed an OVal Plug-in for Apache Struts 2, which is documented at
<https://cwiki.apache.org/confluence/display/S2PLUGINS/OVal+Plugin>
