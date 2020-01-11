/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class HasSubstringCheck extends AbstractAnnotationCheck<HasSubstring> {
   private static final long serialVersionUID = 1L;

   private boolean ignoreCase;

   private String substring;
   private transient String substringLowerCase;

   @Override
   public void configure(final HasSubstring constraintAnnotation) {
      super.configure(constraintAnnotation);
      setIgnoreCase(constraintAnnotation.ignoreCase());
      setSubstring(constraintAnnotation.value());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("ignoreCase", Boolean.toString(ignoreCase));
      messageVariables.put("substring", substring);
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public String getSubstring() {
      return substring;
   }

   private String getSubstringLowerCase() {
      if (substringLowerCase == null && substring != null) {
         substringLowerCase = substring.toLowerCase(Validator.getLocaleProvider().getLocale());
      }
      return substringLowerCase;
   }

   public boolean isIgnoreCase() {
      return ignoreCase;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      if (ignoreCase)
         return valueToValidate.toString().toLowerCase(Validator.getLocaleProvider().getLocale()).indexOf(getSubstringLowerCase()) > -1;

      return valueToValidate.toString().indexOf(substring) > -1;
   }

   public void setIgnoreCase(final boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
      requireMessageVariablesRecreation();
   }

   public void setSubstring(final String substring) {
      this.substring = substring;
      requireMessageVariablesRecreation();
   }
}
