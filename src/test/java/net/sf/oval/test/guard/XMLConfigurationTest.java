/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.configuration.xml.XMLConfigurer;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.localization.locale.ThreadLocalLocaleProvider;

/**
 * @author Sebastian Thomschke
 */
public class XMLConfigurationTest {

   @Guarded
   public static class User {
      // added @Length to test if overwrite=true works
      @Length(min = 10, max = 10)
      protected String userId;
      protected String managerId;
      protected String firstName;
      protected String lastName;

      public User() {
         // nothing to do
      }

      public User(final String userId, final String managerId, @SuppressWarnings("unused") final int somethingElse) {
         this.userId = userId;
         this.managerId = managerId;
      }

      public String getManagerId() {
         return managerId;
      }

      public void setManagerId(final String managerId) {
         this.managerId = managerId;
      }
   }

   @SuppressWarnings("unused")
   private static void validateUser() {
      final ConstraintsViolatedAdapter listener = new ConstraintsViolatedAdapter();
      TestGuardAspect.aspectOf().getGuard().addListener(listener, User.class);

      listener.clear();
      try {
         new User(null, null, 1);
         fail("ConstraintViolationException expected");
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).hasSize(2);
         assertThat(violations[0].getMessage()).isEqualTo(User.class.getName()
            + ".<init>(class java.lang.String,class java.lang.String,int) parameter 0 (userId) is null");
         assertThat(violations[1].getMessage()).isEqualTo(User.class.getName()
            + ".<init>(class java.lang.String,class java.lang.String,int) parameter 1 (managerId) is null");
      }

      listener.clear();
      try {
         final User user = new User("12345678", "12345678", 1);
         user.setManagerId(null);
         fail("ConstraintViolationException expected");
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo(User.class.getName() + ".setManagerId(class java.lang.String) parameter 0 (managerId) is null");
      }

      listener.clear();
      try {
         final User user = new User();
         user.getManagerId();
         fail("ConstraintViolationException expected");
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo(User.class.getName() + ".getManagerId() is null");
      }
   }

   @Before
   public void setUp() throws Exception {
      ((ThreadLocalLocaleProvider) Validator.getLocaleProvider()).setLocale(Locale.ENGLISH);
   }

   @After
   public void tearDown() throws Exception {
      ((ThreadLocalLocaleProvider) Validator.getLocaleProvider()).setLocale(null);
   }

   @Test
   public void testImportedFile() {
      final XMLConfigurer x = new XMLConfigurer();
      x.fromXML(XMLConfigurationTest.class.getResourceAsStream("XMLConfigurationTest.xml"));

      final Guard guard = new Guard(x);
      guard.setInvariantsEnabled(false);
      TestGuardAspect.aspectOf().setGuard(guard);

      validateUser();
   }

   @Test
   public void testSerializedObjectConfiguration() {
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
         csf.checks.add(rec);
      }

      final Set<ClassConfiguration> classConfigs = new HashSet<>();
      {
         final ClassConfiguration cf = new ClassConfiguration();
         classConfigs.add(cf);
         cf.type = User.class;

         cf.fieldConfigurations = new HashSet<>();
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "firstName";
            fc.checks = new ArrayList<>();
            final LengthCheck lc = new LengthCheck();
            lc.setMessage("{context} is not between {min} and {max} characters long");
            lc.setMax(3);
            fc.checks.add(lc);
         }
         {
            final FieldConfiguration fc = new FieldConfiguration();
            cf.fieldConfigurations.add(fc);

            fc.name = "lastName";
            fc.checks = new ArrayList<>();
            final LengthCheck lc = new LengthCheck();
            lc.setMessage("{context} is not between {min} and {max} characters long");
            lc.setMax(5);
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

         cf.constructorConfigurations = new HashSet<>();
         {
            final ConstructorConfiguration cc = new ConstructorConfiguration();
            cf.constructorConfigurations.add(cc);
            cc.parameterConfigurations = new ArrayList<>();

            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");

            final ParameterConfiguration pc1 = new ParameterConfiguration();
            pc1.type = String.class;
            pc1.checks = new ArrayList<>();
            pc1.checks.add(acsc);
            cc.parameterConfigurations.add(pc1);
            final ParameterConfiguration pc2 = new ParameterConfiguration();
            pc2.type = String.class;
            pc2.checks = new ArrayList<>();
            pc2.checks.add(acsc);
            cc.parameterConfigurations.add(pc2);
            final ParameterConfiguration pc3 = new ParameterConfiguration();
            pc3.type = int.class;
            cc.parameterConfigurations.add(pc3);
         }

         cf.methodConfigurations = new HashSet<>();
         {
            final AssertConstraintSetCheck acsc = new AssertConstraintSetCheck();
            acsc.setId("user.userid");

            MethodConfiguration mc = new MethodConfiguration();
            cf.methodConfigurations.add(mc);
            mc.name = "getManagerId";
            mc.returnValueConfiguration = new MethodReturnValueConfiguration();
            mc.returnValueConfiguration.checks = new ArrayList<>();
            mc.returnValueConfiguration.checks.add(acsc);

            mc = new MethodConfiguration();
            cf.methodConfigurations.add(mc);
            mc.name = "setManagerId";
            mc.parameterConfigurations = new ArrayList<>();
            final ParameterConfiguration pc1 = new ParameterConfiguration();
            pc1.type = String.class;
            pc1.checks = new ArrayList<>();
            pc1.checks.add(acsc);
            mc.parameterConfigurations.add(pc1);
         }
      }

      x.getPojoConfigurer().setClassConfigurations(classConfigs);
      x.getPojoConfigurer().setConstraintSetConfigurations(constraintSetsConfig);

      /*
       * serialize the configuration to XML
       */
      final String xmlConfig = x.toXML();

      /*
       * deserialize the configuration from XML
       */
      x.fromXML(xmlConfig);

      final Guard guard = new Guard(x);
      guard.setInvariantsEnabled(false);
      TestGuardAspect.aspectOf().setGuard(guard);

      validateUser();
   }
}
