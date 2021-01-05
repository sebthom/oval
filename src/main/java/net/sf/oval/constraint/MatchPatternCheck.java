/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternCheck extends AbstractAnnotationCheck<MatchPattern> {
   private static final long serialVersionUID = 1L;

   private final List<Pattern> patterns = getCollectionFactory().createList(2);
   private boolean matchAll = true;

   @Override
   public void configure(final MatchPattern constraintAnnotation) {
      super.configure(constraintAnnotation);

      setMatchAll(constraintAnnotation.matchAll());

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
      return patterns.toArray(new Pattern[patterns.size()]);
   }

   public boolean isMatchAll() {
      return matchAll;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      for (final Pattern p : patterns) {
         final boolean matches = p.matcher(valueToValidate.toString()).matches();

         if (matches) {
            if (!matchAll)
               return true;
         } else if (matchAll)
            return false;
      }
      return matchAll;
   }

   public void setMatchAll(final boolean matchAll) {
      this.matchAll = matchAll;
      requireMessageVariablesRecreation();
   }

   public void setPattern(final Pattern pattern) {
      patterns.clear();
      patterns.add(pattern);
      requireMessageVariablesRecreation();
   }

   public void setPattern(final String pattern, final int flags) {
      patterns.clear();
      patterns.add(Pattern.compile(pattern, flags));
      requireMessageVariablesRecreation();
   }

   public void setPatterns(final Collection<Pattern> patterns) {
      this.patterns.clear();
      this.patterns.addAll(patterns);
      requireMessageVariablesRecreation();
   }

   public void setPatterns(final Pattern... patterns) {
      this.patterns.clear();
      Collections.addAll(this.patterns, patterns);
      requireMessageVariablesRecreation();
   }
}
