/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import net.sf.oval.AbstractCheck;
import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Sebastian Thomschke
 */
public class PreCheck extends AbstractCheck {
   private static final long serialVersionUID = 1L;

   private String expr;
   private String lang;

   public void configure(final Pre constraintAnnotation) {
      setMessage(constraintAnnotation.message());
      setErrorCode(constraintAnnotation.errorCode());
      setSeverity(constraintAnnotation.severity());
      setExpr(constraintAnnotation.expr());
      setLang(constraintAnnotation.lang());
      setProfiles(constraintAnnotation.profiles());
   }

   public String getExpr() {
      return expr;
   }

   public String getLang() {
      return lang;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
      throws OValException {
      throw new UnsupportedOperationException();
   }

   public void setExpr(final String condition) {
      expr = condition;
   }

   public void setLang(final String language) {
      lang = language;
   }
}
