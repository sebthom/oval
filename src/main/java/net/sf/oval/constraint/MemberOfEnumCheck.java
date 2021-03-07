/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Map;
import java.util.Objects;

import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.exception.OValException;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author shank3
 */
public class MemberOfEnumCheck extends AbstractAnnotationCheck<MemberOfEnum> {

   private Class<? extends Enum> constraintEnum;

   public void setConstraintEnum(final Class<? extends Enum> constraintEnum) {
      this.constraintEnum = constraintEnum;
      requireMessageVariablesRecreation();
   }

   @Override
   public void configure(final MemberOfEnum memberOfEnum) {
      super.configure(memberOfEnum);
      setConstraintEnum(memberOfEnum.value());
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) throws OValException {
      if (Objects.isNull(valueToValidate)) {
         return true;
      }
      try {
         Enum.valueOf(constraintEnum, valueToValidate.toString());
         return true;
      } catch (final IllegalArgumentException e) {
         return false;
      }
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("members", StringUtils.join(constraintEnum.getEnumConstants(), ','));
      return messageVariables;
   }
}
