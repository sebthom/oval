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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageJavaScriptImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageJavaScriptImpl.class);

   private final Scriptable parentScope;

   private final ObjectCache<String, Script> expressionCache = new ObjectCache<>(expression -> {
      final Context ctx = ContextFactory.getGlobal().enterContext();
      ctx.setOptimizationLevel(9);
      return ctx.compileString(expression, "<cmd>", 1, null);
   });

   public ExpressionLanguageJavaScriptImpl() {
      final Context ctx = ContextFactory.getGlobal().enterContext();
      try {
         parentScope = ctx.initStandardObjects();
      } finally {
         Context.exit();
      }
   }

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating JavaScript expression: {1}", expression);
      try {
         final Context ctx = ContextFactory.getGlobal().enterContext();
         final Scriptable scope = ctx.newObject(parentScope);
         scope.setPrototype(parentScope);
         scope.setParentScope(null);
         for (final Entry<String, ?> entry : values.entrySet()) {
            scope.put(entry.getKey(), scope, Context.javaToJS(entry.getValue(), scope));
         }
         final Script expr = expressionCache.get(expression);
         return expr.exec(ctx, scope);
      } catch (final EvaluatorException ex) {
         throw new ExpressionEvaluationException("Evaluating JavaScript expression failed: " + expression, ex);
      } finally {
         Context.exit();
      }
   }
}
