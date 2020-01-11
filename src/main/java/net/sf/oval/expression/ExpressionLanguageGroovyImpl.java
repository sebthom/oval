/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.expression;

import java.util.Map;
import java.util.Map.Entry;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ThreadLocalObjectCache;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageGroovyImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageGroovyImpl.class);

   private static final GroovyShell GROOVY_SHELL = new GroovyShell();

   private final ThreadLocalObjectCache<String, Script> expressionCache = new ThreadLocalObjectCache<String, Script>() {
      @Override
      protected Script load(final String expression) {
         return GROOVY_SHELL.parse(expression);
      }
   };

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating Groovy expression: {1}", expression);
      try {
         final Script script = expressionCache.get().get(expression);

         final Binding binding = new Binding();
         for (final Entry<String, ?> entry : values.entrySet()) {
            binding.setVariable(entry.getKey(), entry.getValue());
         }
         script.setBinding(binding);
         return script.run();
      } catch (final Exception ex) {
         throw new ExpressionEvaluationException("Evaluating script with Groovy failed.", ex);
      }
   }
}
