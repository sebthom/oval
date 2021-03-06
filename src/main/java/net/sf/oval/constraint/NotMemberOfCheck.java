/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class NotMemberOfCheck extends AbstractAnnotationCheck<NotMemberOf> {
   private static final long serialVersionUID = 1L;

   private boolean ignoreCase;
   private List<String> members;
   private transient List<String> membersLowerCase;

   @Override
   public void configure(final NotMemberOf constraintAnnotation) {
      super.configure(constraintAnnotation);
      setIgnoreCase(constraintAnnotation.ignoreCase());
      setMembers(constraintAnnotation.value());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("ignoreCase", Boolean.toString(ignoreCase));
      messageVariables.put("members", StringUtils.join(members, ','));
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public List<String> getMembers() {
      final List<String> v = getCollectionFactory().createList();
      v.addAll(members);
      return v;
   }

   private List<String> getMembersLowerCase() {
      List<String> membersLowerCase = this.membersLowerCase;
      if (membersLowerCase == null) {
         membersLowerCase = getCollectionFactory().createList(members.size());
         final Locale locale = Validator.getLocaleProvider().getLocale();
         for (final String val : members) {
            membersLowerCase.add(val.toLowerCase(locale));
         }
         this.membersLowerCase = membersLowerCase;
      }
      return membersLowerCase;
   }

   public boolean isIgnoreCase() {
      return ignoreCase;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      if (ignoreCase)
         return !getMembersLowerCase().contains(valueToValidate.toString().toLowerCase(Validator.getLocaleProvider().getLocale()));

      return !members.contains(valueToValidate.toString());
   }

   public void setIgnoreCase(final boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
      requireMessageVariablesRecreation();
   }

   public void setMembers(final List<String> members) {
      this.members = getCollectionFactory().createList();
      this.members.addAll(members);
      membersLowerCase = null;
      requireMessageVariablesRecreation();
   }

   public void setMembers(final String... members) {
      this.members = getCollectionFactory().createList();
      Collections.addAll(this.members, members);
      membersLowerCase = null;
      requireMessageVariablesRecreation();
   }
}
