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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.oval.context.OValContext;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.internal.util.StringUtils;

/**
 * Partial implementation of check classes.
 *
 * @author Sebastian Thomschke
 */
public abstract class AbstractCheck implements Check {
   private static final long serialVersionUID = 1L;

   private OValContext context;
   private String errorCode;
   private String message;
   private Map<String, ? extends Serializable> messageVariables;
   private Map<String, ? extends Serializable> messageVariablesUnmodifiable;
   private boolean messageVariablesUpToDate = true;
   private String[] profiles;
   private int severity;
   private ConstraintTarget[] appliesTo;
   private String target;
   private String when;
   private transient String whenFormula;
   private transient String whenLang;

   protected Map<String, ? extends Serializable> createMessageVariables() {
      return null;
   }

   @Override
   public ConstraintTarget[] getAppliesTo() {
      return appliesTo == null ? getAppliesToDefault() : appliesTo;
   }

   /**
    *
    * @return the default behavior when the constraint is validated for a array/map/collection reference.
    */
   protected ConstraintTarget[] getAppliesToDefault() {
      // default behavior is only validate the array/map/collection reference and not the contained keys/values
      return new ConstraintTarget[] {ConstraintTarget.CONTAINER};
   }

   @Override
   public OValContext getContext() {
      return context;
   }

   @Override
   public String getErrorCode() {
      /*
       * if the error code has not been initialized (which might be the case when using XML configuration),
       * construct the string based on this class' name minus the appendix "Check"
       */
      if (errorCode == null) {
         final String className = getClass().getName();
         if (className.endsWith("Check")) {
            errorCode = className.substring(0, getClass().getName().length() - "Check".length());
         } else {
            errorCode = className;
         }
      }
      return errorCode;
   }

   @Override
   public String getMessage() {
      /*
       * if the message has not been initialized (which might be the case when using XML configuration),
       * construct the string based on this class' name minus the appendix "Check" plus the appendix ".violated"
       */
      if (message == null) {
         final String className = getClass().getName();
         if (className.endsWith("Check")) {
            message = className.substring(0, getClass().getName().length() - "Check".length()) + ".violated";
         } else {
            message = className + ".violated";
         }
      }
      return message;
   }

   /**
    * Values that are used to fill place holders when rendering the error message.
    * A key "min" with a value "4" will replace the place holder {min} in an error message
    * like "Value cannot be smaller than {min}" with the string "4".
    *
    * <b>Note:</b> Override {@link #createMessageVariables()} to create and fill the map
    *
    * @return an unmodifiable map
    */
   @Override
   @SuppressWarnings("javadoc")
   public final Map<String, ? extends Serializable> getMessageVariables() {
      if (!messageVariablesUpToDate) {
         messageVariables = createMessageVariables();
         if (messageVariables == null) {
            messageVariablesUnmodifiable = null;
         } else {
            messageVariablesUnmodifiable = Collections.unmodifiableMap(messageVariables);
         }
         messageVariablesUpToDate = true;
      }
      return messageVariablesUnmodifiable;
   }

   @Override
   public String[] getProfiles() {
      return profiles;
   }

   @Override
   public int getSeverity() {
      return severity;
   }

   @Override
   public String getTarget() {
      return target;
   }

   @Override
   public String getWhen() {
      return when;
   }

   @Override
   public boolean isActive(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (when == null)
         return true;

      // this triggers parsing of when, happens when this check instance was deserialized
      if (whenLang == null) {
         setWhen(when);
      }

      final Map<String, Object> values = getCollectionFactory().createMap();
      values.put("_value", valueToValidate);
      values.put("_this", validatedObject);

      final ExpressionLanguage el = cycle.getValidator().getExpressionLanguageRegistry().getExpressionLanguage(whenLang);
      return el.evaluateAsBoolean(whenFormula, values);
   }

   /**
    * Calling this method indicates that the {@link #createMessageVariables()} method needs to be called before the message
    * for the next violation of this check is rendered.
    */
   protected void requireMessageVariablesRecreation() {
      messageVariablesUpToDate = false;
   }

   @Override
   public void setAppliesTo(final ConstraintTarget... targets) {
      appliesTo = targets;
   }

   @Override
   public void setContext(final OValContext context) {
      this.context = context;
   }

   @Override
   public void setErrorCode(final String failureCode) {
      errorCode = failureCode;
   }

   @Override
   public void setMessage(final String message) {
      this.message = message;
   }

   @Override
   public void setProfiles(final String... profiles) {
      this.profiles = profiles;
   }

   @Override
   public void setSeverity(final int severity) {
      this.severity = severity;
   }

   @Override
   public void setTarget(final String target) {
      this.target = target;
   }

   @Override
   public void setWhen(final String when) {
      if (when == null || when.length() == 0) {
         this.when = null;
         whenFormula = null;
         whenLang = null;
      } else {
         final List<String> parts = StringUtils.split(when, ':', 2);
         if (parts.size() < 2)
            throw new IllegalArgumentException("[when] is missing the scripting language declaration");
         this.when = when;
         whenLang = parts.get(0);
         whenFormula = parts.get(1);
      }
   }
}
