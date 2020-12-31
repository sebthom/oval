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

import java.util.Collections;
import java.util.List;
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
      if (membersLowerCase == null) {
         membersLowerCase = getCollectionFactory().createList(members.size());
         for (final String val : members) {
            membersLowerCase.add(val.toLowerCase(Validator.getLocaleProvider().getLocale()));
         }
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
