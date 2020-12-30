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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.oval.context.IterableElementContext;
import net.sf.oval.context.MapKeyContext;
import net.sf.oval.context.MapValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Assert;
import net.sf.oval.internal.util.CollectionUtils;
import net.sf.oval.localization.context.DefaultOValContextRenderer;

/**
 * An instance of this class provides detailed information about a single constraint
 * violation that occurred during validation.
 *
 * @author Sebastian Thomschke
 */
public class ConstraintViolation implements Serializable {
   private static final Log LOG = Log.getLog(ConstraintViolation.class);

   private static final long serialVersionUID = 1L;

   @Deprecated
   private final ConstraintViolation[] causes;
   private final OValContext checkDeclaringContext;
   private final String checkName;
   private final String errorCode;
   private final int severity;

   private transient Object validatedObject;

   private transient Object invalidValue;
   private final List<OValContext> contextPath;

   private final String message;
   private final String messageTemplate;
   private final Map<String, ? extends Serializable> messageVariables;

   /**
    * @since 3.1
    */
   public ConstraintViolation( //
      final Check check, //
      final String message, //
      final Object validatedObject, //
      final Object invalidValue, //
      final List<OValContext> invalidValuePath //
   ) {
      this(check, message, validatedObject, invalidValue, invalidValuePath, (ConstraintViolation[]) null);
   }

   private ConstraintViolation( //
      final Check check, //
      final String message, //
      final Object validatedObject, //
      final Object invalidValue, //
      final List<OValContext> invalidValuePath, //
      final ConstraintViolation... causes//
   ) {
      Assert.argumentNotEmpty("invalidValuePath", invalidValuePath);

      checkName = check.getClass().getName();
      checkDeclaringContext = check.getContext();
      errorCode = check.getErrorCode();
      this.message = message;
      messageTemplate = check.getMessage();
      messageVariables = check.getMessageVariables();
      severity = check.getSeverity();
      this.validatedObject = validatedObject;
      this.invalidValue = invalidValue;
      contextPath = CollectionUtils.clone(invalidValuePath);
      this.causes = causes != null && causes.length == 0 ? null : causes;
   }

   @Deprecated
   public ConstraintViolation(//
      final Check check, //
      final String message, //
      final Object validatedObject, //
      final Object invalidValue, //
      final OValContext context //
   ) {
      this(check, message, validatedObject, invalidValue, Arrays.asList(context), (ConstraintViolation[]) null);
   }

   @Deprecated
   public ConstraintViolation(//
      final Check check, //
      final String message, //
      final Object validatedObject, //
      final Object invalidValue, //
      final OValContext context, //
      final ConstraintViolation... causes//
   ) {
      this(check, message, validatedObject, invalidValue, Arrays.asList(context), causes);
   }

   @Deprecated
   public ConstraintViolation(//
      final Check check, //
      final String message, //
      final Object validatedObject, //
      final Object invalidValue, //
      final OValContext context, //
      final List<ConstraintViolation> causes//
   ) {
      this(check, message, validatedObject, invalidValue, Arrays.asList(context), //
         causes == null || causes.isEmpty() ? null : causes.toArray(new ConstraintViolation[causes.size()]) //
      );
   }

   /**
    * @return the causes or null if no cause exists
    *
    * @deprecated use {@link #getContextPath()}
    */
   @Deprecated
   public ConstraintViolation[] getCauses() {
      return causes == null ? null : causes.clone();
   }

   /**
    * @return the context where the constraint was declared.
    *
    * @see net.sf.oval.context.ClassContext
    * @see net.sf.oval.context.FieldContext
    * @see net.sf.oval.context.MethodEntryContext
    * @see net.sf.oval.context.MethodExitContext
    * @see net.sf.oval.context.MethodParameterContext
    * @see net.sf.oval.context.MethodReturnValueContext
    */
   public OValContext getCheckDeclaringContext() {
      return checkDeclaringContext;
   }

   /**
    * @return the fully qualified class name of the corresponding check
    */
   public String getCheckName() {
      return checkName;
   }

   /**
    * @return the context where the constraint violation occurred.
    *
    * @see net.sf.oval.context.ClassContext
    * @see net.sf.oval.context.FieldContext
    * @see net.sf.oval.context.MethodReturnValueContext
    *
    * @deprecated use {@link #getContextPath()}
    */
   @Deprecated
   public OValContext getContext() {
      final ListIterator<OValContext> listIterator = contextPath.listIterator(contextPath.size());
      OValContext ctx = null;
      while (listIterator.hasPrevious()) {
         ctx = listIterator.previous();
         if (!(ctx instanceof IterableElementContext || ctx instanceof MapKeyContext || ctx instanceof MapValueContext)) {
            break;
         }
      }
      return ctx;
   }

   /**
    * @return the context path to the invalid value
    *
    * @see net.sf.oval.context.ClassContext
    * @see net.sf.oval.context.FieldContext
    * @see net.sf.oval.context.IterableElementContext
    * @see net.sf.oval.context.MapKeyContext
    * @see net.sf.oval.context.MapValueContext
    * @see net.sf.oval.context.MethodReturnValueContext
    */
   public List<OValContext> getContextPath() {
      return contextPath;
   }

   /**
    * @return a string representation of the context path to the invalid value
    */
   public String getContextPathAsString() {
      return DefaultOValContextRenderer.INSTANCE.render(contextPath);
   }

   public String getErrorCode() {
      return errorCode;
   }

   /**
    * @return the value that was validated.
    */
   public Object getInvalidValue() {
      return invalidValue;
   }

   /**
    * @return the localized and rendered message
    */
   public String getMessage() {
      return message;
   }

   /**
    * @return the raw message specified for the constraint without variable resolution and localization
    */
   public String getMessageTemplate() {
      return messageTemplate;
   }

   /**
    * @return an unmodifiable map holding the message variables provided by the corresponding check.
    */
   public Map<String, ? extends Serializable> getMessageVariables() {
      return messageVariables;
   }

   public int getSeverity() {
      return severity;
   }

   public Object getValidatedObject() {
      return validatedObject;
   }

   /**
    * @see Serializable
    */
   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      if (in.readBoolean()) {
         validatedObject = in.readObject();
      }
      if (in.readBoolean()) {
         invalidValue = in.readObject();
      }
   }

   @Override
   public String toString() {
      return getClass().getName() + ": " + message;
   }

   /**
    * @see Serializable
    */
   private void writeObject(final java.io.ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();

      if (validatedObject instanceof Serializable) {
         // indicate validatedObject implements Serializable
         out.writeBoolean(true);

         out.writeObject(validatedObject);
      } else {
         LOG.warn("Field 'validatedObject' not serialized because the field value object " + validatedObject + " of type " + invalidValue.getClass()
            + " does not implement " + Serializable.class.getName());

         // indicate validatedObject does not implement Serializable
         out.writeBoolean(false);
      }

      if (invalidValue instanceof Serializable) {
         // indicate value implements Serializable
         out.writeBoolean(true);

         out.writeObject(invalidValue);
      } else {
         LOG.warn("Field 'invalidValue' could not be serialized because the field value object {1} does not implement java.io.Serializable.", invalidValue);

         // indicate value does not implement Serializable
         out.writeBoolean(false);
      }
   }
}
