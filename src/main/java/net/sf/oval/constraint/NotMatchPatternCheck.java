/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
public class NotMatchPatternCheck extends AbstractAnnotationCheck<NotMatchPattern> {
   private static final long serialVersionUID = 1L;

   private final List<Pattern> patterns = getCollectionFactory().createList(2);

   @Override
   public void configure(final NotMatchPattern constraintAnnotation) {
      super.configure(constraintAnnotation);

      synchronized (patterns) {
         patterns.clear();
         final String[] stringPatterns = constraintAnnotation.pattern();
         final int[] f = constraintAnnotation.flags();
         for (int i = 0, l = stringPatterns.length; i < l; i++) {
            final int flag = f.length > i ? f[i] : 0;
            final Pattern p = Pattern.compile(stringPatterns[i], flag);
            patterns.add(p);
         }
         requireMessageVariablesRecreation();
      }
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("pattern", patterns.size() == 1 ? patterns.get(0).toString() : patterns.toString());
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public Pattern[] getPatterns() {
      synchronized (patterns) {
         return patterns.toArray(new Pattern[patterns.size()]);
      }
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      for (final Pattern p : patterns)
         if (p.matcher(valueToValidate.toString()).matches())
            return false;
      return true;
   }

   public void setPattern(final Pattern pattern) {
      synchronized (patterns) {
         patterns.clear();
         patterns.add(pattern);
      }
      requireMessageVariablesRecreation();
   }

   public void setPattern(final String pattern, final int flags) {
      synchronized (patterns) {
         patterns.clear();
         patterns.add(Pattern.compile(pattern, flags));
      }
      requireMessageVariablesRecreation();
   }

   public void setPatterns(final Collection<Pattern> patterns) {
      synchronized (this.patterns) {
         this.patterns.clear();
         this.patterns.addAll(patterns);
      }
      requireMessageVariablesRecreation();
   }

   public void setPatterns(final Pattern... patterns) {
      synchronized (this.patterns) {
         this.patterns.clear();
         ArrayUtils.addAll(this.patterns, patterns);
      }
      requireMessageVariablesRecreation();
   }
}
