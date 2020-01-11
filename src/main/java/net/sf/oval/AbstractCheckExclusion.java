/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.expression.ExpressionLanguage;

/**
 * Partial implementation of exclusion classes.
 *
 * @author Sebastian Thomschke
 */
public abstract class AbstractCheckExclusion implements CheckExclusion {
   private static final long serialVersionUID = 1L;

   private String[] profiles;

   private String when;
   private String whenFormula;
   private String whenLang;

   public Map<String, String> getMessageVariables() {
      return null;
   }

   @Override
   public String[] getProfiles() {
      return profiles;
   }

   @Override
   public String getWhen() {
      return whenLang + ":" + when;
   }

   @Override
   public boolean isActive(final Object validatedObject, final Object valueToValidate, final Validator validator) {
      if (when == null)
         return true;

      final Map<String, Object> values = getCollectionFactory().createMap();
      values.put("_value", valueToValidate);
      values.put("_this", validatedObject);

      final ExpressionLanguage el = validator.getExpressionLanguageRegistry().getExpressionLanguage(whenLang);
      return el.evaluateAsBoolean(whenFormula, values);
   }

   @Override
   public void setProfiles(final String... profiles) {
      this.profiles = profiles;
   }

   @Override
   public void setWhen(final String when) {
      if (when == null || when.length() == 0) {
         this.when = null;
         whenFormula = null;
         whenLang = null;
      } else {
         this.when = when;
         final String[] parts = when.split(":", 2);
         if (parts.length == 0)
            throw new IllegalArgumentException("[when] is missing the scripting language declaration");
         whenLang = parts[0];
         whenFormula = parts[1];
      }
   }
}
