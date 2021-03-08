/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.exception.OValException;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author shank3
 */
public class MemberOfEnumCheck extends AbstractAnnotationCheck<MemberOfEnum> {

   private static final long serialVersionUID = 1L;

   private Class<? extends Enum<?>> constraintEnum;
   private Set<String> enumValues = Collections.emptySet();
   private boolean ignoreCase;

   @Override
   public void configure(final MemberOfEnum constraintAnnotation) {
      super.configure(constraintAnnotation);
      setConstraintEnum(constraintAnnotation.value());
      setIgnoreCase(constraintAnnotation.ignoreCase());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("members", StringUtils.join(constraintEnum.getEnumConstants(), ','));
      return messageVariables;
   }

   public Class<? extends Enum<?>> getConstraintEnum() {
      return constraintEnum;
   }

   public boolean isIgnoreCase() {
      return ignoreCase;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) throws OValException {
      if (Objects.isNull(valueToValidate))
         return true;

      if (ignoreCase)
         return enumValues.contains(valueToValidate.toString().toLowerCase());
      return enumValues.contains(valueToValidate.toString());
   }

   public void setConstraintEnum(final Class<? extends Enum<?>> constraintEnum) {
      this.constraintEnum = constraintEnum;

      final Object[] enumValues = constraintEnum.getEnumConstants();
      final Set<String> enumValuesSet = getCollectionFactory().createSet(enumValues.length);
      for (final Object enumValue : enumValues) {
         if (ignoreCase) {
            enumValuesSet.add(enumValue.toString().toLowerCase());
         } else {
            enumValuesSet.add(enumValue.toString());
         }
      }
      this.enumValues = enumValuesSet;

      requireMessageVariablesRecreation();
   }

   public void setIgnoreCase(final boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
   }
}
