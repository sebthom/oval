/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

import com.thoughtworks.xstream.io.StreamException;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.pojo.POJOConfigurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.xml.XMLConfigurer;
import net.sf.oval.constraint.AssertCheck;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.ValidateWithMethodCheck;

/**
 * @author Sebastian Thomschke
 */
public class XMLConfigurationTest {

   public static class User {
      // added @Length to test if overwrite=true works
      @Length(min = 10, max = 10)
      protected String userId;
      protected String managerId;
      protected String firstName;
      protected String lastName;
      protected String homepage;

      public String getManagerId() {
         return managerId;
      }

      protected boolean validateMinLength(final String value) {
         if (value == null)
            return true;
         return value.length() > 2;
      }
   }

   public void testMultipleXMLFiles() throws IOException {
      try (InputStream is1 = XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest1.inc.xml");
           InputStream is2 = XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest2.inc.xml")) {
         final XMLConfigurer x1 = new XMLConfigurer();
         x1.fromXML(is1);
         final XMLConfigurer x2 = new XMLConfigurer();
         x2.fromXML(is2);
         validateUser(new Validator(x1, x2));
      }
   }

   public void testMultipleXMLFilesWithSameXStreamInstance() throws IOException {
      try (InputStream is1 = XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest1.inc.xml");
           InputStream is2 = XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest2.inc.xml")) {
         final XMLConfigurer x1 = new XMLConfigurer();
         x1.fromXML(is1);
         final XMLConfigurer x2 = new XMLConfigurer(x1.getXStream());
         x2.fromXML(is2);
         validateUser(new Validator(x1, x2));
      }
   }

   public void testMultipleXMLFilesWithXIncluded() {
      final XMLConfigurer x1 = new XMLConfigurer();
      x1.fromXML(new File("src/test/resources/net/sf/oval/test/validator/XMLConfigurationTest_XInclude.xml"));
      validateUser(new Validator(x1));
   }

   public void testSerializedObjectConfiguration() throws Exception {
      final XMLConfigurer x = new XMLConfigurer();

      /*
       * define a configuration
       */
      final Set<ConstraintSetConfiguration> constraintSetsConfig = new HashSet<>();
      {
         final ConstraintSetConfiguration csf = new ConstraintSetConfiguration();
         constraintSetsConfig.add(csf);

         csf.id = "user.userid";
         csf.checks = new ArrayList<>();
         final NotNullCheck nnc = new NotNullCheck();
         nnc.setMessage("{context} is null");
         csf.checks.add(nnc);
         final MatchPatternCheck rec = new MatchPatternCheck();
         rec.setPattern(Pattern.compile("^[a-z0-9]{8}$", 0));
         rec.setMessage("{context} does not match the pattern {pattern}");
         rec.setProfiles("a", "b");
         csf.checks.add(rec);
      }

      final Set<ClassConfiguration> classConfigs = new HashSet<>();
      {
         final ClassConfiguration cf = new ClassConfiguration();
         classConfigs.add(cf);
         cf.type = User.class;

         cf.objectConfiguration = new ObjectConfiguration();
         {
            cf.objectConfiguration.checks = new ArrayList<>();

            final AssertCheck ac = new AssertCheck();
            ac.setExpr("_this.firstName != _this.lastName");
            ac.setMessage("firstName and lastName must not be the same");
            ac.setLang("groovy");
            cf.objectConfiguration.checks.add(ac);
         }

         cf.fieldConfigurations = new HashSet<>();
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "firstName";
            fc.checks = new ArrayList<>();
            final AssertCheck ac = new AssertCheck();
            ac.setExpr("_value != null && _value.length() <= 3");
            ac.setMessage("{context} cannot be longer than 3 characters");
            ac.setLang("groovy");
            fc.checks.add(ac);
            final ValidateWithMethodCheck vwm = new ValidateWithMethodCheck();
            vwm.setMethodName("validateMinLength");
            vwm.setParameterType(String.class);
            vwm.setMessage("{context} must be longer than 2 characters");
            fc.checks.add(vwm);
         }
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "lastName";
            fc.checks = new ArrayList<>();
            final LengthCheck lc = new LengthCheck();
            lc.setMessage("{context} is not between {min} and {max} characters long");
            lc.setMin(1);
            lc.setMax(5);
            lc.setAppliesTo(ConstraintTarget.CONTAINER, ConstraintTarget.KEYS);
            fc.checks.add(lc);
         }
         {
            final FieldConfiguration fc = new FieldConfiguration();
            fc.overwrite = Boolean.TRUE;
            cf.fieldConfigurations.add(fc);

            fc.name = "userId";
            fc.checks = new ArrayList<>();
            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");
            fc.checks.add(acsc);
         }

         cf.methodConfigurations = new HashSet<>();
         {
            final MethodConfiguration mc = new MethodConfiguration();
            cf.methodConfigurations.add(mc);
            mc.name = "getManagerId";
            mc.isInvariant = true;
            mc.returnValueConfiguration = new MethodReturnValueConfiguration();
            mc.returnValueConfiguration.checks = new ArrayList<>();
            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");
            mc.returnValueConfiguration.checks.add(acsc);
         }
      }

      x.getPojoConfigurer().setClassConfigurations(classConfigs);
      x.getPojoConfigurer().setConstraintSetConfigurations(constraintSetsConfig);

      /*
       * test POJO Configurer object serialization
       */
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(x.getPojoConfigurer());
      oos.flush();
      oos.close();

      final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
      final ObjectInputStream ois = new ObjectInputStream(bin);
      x.setPojoConfigurer((POJOConfigurer) ois.readObject());
      ois.close();

      /*
       * test XML de/serialization
       */
      final String xmlConfig = x.toXML();
      //System.out.println(xmlConfig);
      x.fromXML(xmlConfig);
      validateUser(new Validator(x));
   }

   @Test
   public void testVulnerability_ExternalEntityReferences() {
      final XMLConfigurer x1 = new XMLConfigurer();
      try {
         x1.fromXML(new File("src/test/resources/net/sf/oval/test/validator/XMLConfigurationTest_Vulnerability_ExternalEntityReferences.xml"));
         failBecauseExceptionWasNotThrown(StreamException.class);
      } catch (final StreamException ex) {
         final String msg = ex.getCause().getMessage();
         assertThat(msg).satisfiesAnyOf( //
            m -> assertThat(m).contains("External Entity: Failed to read external document 'XMLConfigurationTest1.inc.xml', "
               + "because 'file' access is not allowed due to restriction set by the accessExternalDTD property."), //
            m -> assertThat(m).contains("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.") //
         );
      }
   }

   @Test
   public void testVulnerability_NonXmlFile() {
      final XMLConfigurer x1 = new XMLConfigurer();
      try {
         x1.fromXML(new File("src/test/resources/net/sf/oval/test/validator/XMLConfigurationTest_Vulnerability_NonXmlFile.xml"));
         failBecauseExceptionWasNotThrown(StreamException.class);
      } catch (final StreamException ex) {
         assertThat(ex.getCause().getMessage()).contains("Referencing entity [file:///C:/Windows/System32/drivers/etc/hosts] is not allowed");
      }
   }

   @Test
   public void testVulnerability_RecursiveInclude() {
      final XMLConfigurer x1 = new XMLConfigurer();
      try {
         x1.fromXML(new File("src/test/resources/net/sf/oval/test/validator/XMLConfigurationTest_Vulnerability_RecursiveInclude.xml"));
         failBecauseExceptionWasNotThrown(StreamException.class);
      } catch (final StreamException ex) {
         assertThat(ex.getCause().getMessage()).contains("Recursive include detected");
      }
   }

   public void validateUser(final Validator validator) {
      final User usr = new User();

      usr.lastName = "1";
      usr.userId = "12345678";
      usr.managerId = "12345678";

      /*
       * check constraints for firstName
       */
      usr.firstName = "123456";
      List<ConstraintViolation> violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".firstName cannot be longer than 3 characters");

      usr.firstName = "";
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".firstName must be longer than 2 characters");

      usr.firstName = "123";

      /*
       * check constraints for lastName
       */
      usr.lastName = "123456";
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".lastName is not between 1 and 5 characters long");

      usr.lastName = "1";

      /*
       * check constraints for userId
       */
      usr.userId = null;
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".userId is null");

      usr.userId = "%$$e3";
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".userId does not match the pattern ^[a-z0-9]{8}$");
      usr.userId = "12345678";

      /*
       * check constraints for managerId
       */
      usr.managerId = null;
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".getManagerId() is null");

      usr.managerId = "%$$e3";
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo(User.class.getName() + ".getManagerId() does not match the pattern ^[a-z0-9]{8}$");

      /*
       * check object constraints
       */
      usr.userId = "12345678";
      usr.managerId = "12345678";

      usr.lastName = "abc";
      usr.firstName = "abc";
      violations = validator.validate(usr);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("firstName and lastName must not be the same");
   }
}
