/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.context;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.oval.Validator;
import net.sf.oval.context.ClassContext;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodExitContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * This renderer searches for a resource file that is in the same package and has the same name as the validated class.
 * It then tries to lookup a localized version of the validation context, e.g.<br>
 * <b>com.acme.model.Person.java<br>
 * com.acme.model.Person.properties<br>
 * com.acme.model.Person_de.properties<br>
 * com.acme.model.Person_fr.properties</b>
 *
 * <p>
 * The properties file is expected to have values following this scheme
 *
 * <pre>
 * label.class=My translated name of the class name
 * label.field.firstname=My translated name of the field "firstname"
 * label.field.lastname=My translated name of the field "lastname"
 * label.parameter.amount=My translated name of a constructor/method parameter "amount"
 * label.method.increase=My translated name of the method "increase"
 * </pre>
 *
 * @author Sebastian Thomschke
 */
public class ResourceBundleValidationContextRenderer implements OValContextRenderer {
   private static final Log LOG = Log.getLog(ResourceBundleValidationContextRenderer.class);

   public static final ResourceBundleValidationContextRenderer INSTANCE = new ResourceBundleValidationContextRenderer();

   private static boolean containsKey(final ResourceBundle bundle, final String key) {
      for (final Enumeration<String> en = bundle.getKeys(); en.hasMoreElements();)
         if (en.nextElement().equals(key))
            return true;
      return false;
   }

   protected Locale getLocale() {
      return Validator.getLocaleProvider().getLocale();
   }

   @Override
   public String render(final OValContext context) {
      final String baseName;
      final String key;
      if (context instanceof ClassContext) {
         final ClassContext ctx = (ClassContext) context;
         baseName = ctx.getClazz().getName();
         key = "label.class";
      } else if (context instanceof FieldContext) {
         final FieldContext ctx = (FieldContext) context;
         baseName = ctx.getField().getDeclaringClass().getName();
         final String fieldName = ctx.getField().getName();
         key = "label.field." + fieldName;
      } else if (context instanceof ConstructorParameterContext) {
         final ConstructorParameterContext ctx = (ConstructorParameterContext) context;
         baseName = ctx.getConstructor().getDeclaringClass().getName();
         key = "label.parameter." + ctx.getParameterName();
      } else if (context instanceof MethodParameterContext) {
         final MethodParameterContext ctx = (MethodParameterContext) context;
         baseName = ctx.getMethod().getDeclaringClass().getName();
         key = "label.parameter." + ctx.getParameterName();
      } else if (context instanceof MethodEntryContext) {
         final MethodEntryContext ctx = (MethodEntryContext) context;
         baseName = ctx.getMethod().getDeclaringClass().getName();
         key = "label.method." + ctx.getMethod().getName();
      } else if (context instanceof MethodExitContext) {
         final MethodExitContext ctx = (MethodExitContext) context;
         baseName = ctx.getMethod().getDeclaringClass().getName();
         key = "label.method." + ctx.getMethod().getName();
      } else if (context instanceof MethodReturnValueContext) {
         final MethodReturnValueContext ctx = (MethodReturnValueContext) context;
         baseName = ctx.getMethod().getDeclaringClass().getName();
         key = "label.method." + ctx.getMethod().getName();
      } else
         return context.toString();

      try {
         final ResourceBundle bundle = ResourceBundle.getBundle(baseName, getLocale());
         if (containsKey(bundle, key))
            return bundle.getString(key);
         LOG.debug("Key {1} not found in bundle {2}", key, baseName);
      } catch (final MissingResourceException ex) {
         LOG.debug("Bundle {1} not found", baseName, ex);
      }
      return context.toString();
   }
}
