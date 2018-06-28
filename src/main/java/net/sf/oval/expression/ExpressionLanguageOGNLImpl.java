/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
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

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageOGNLImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageOGNLImpl.class);

   private final ObjectCache<String, Object> expressionCache = new ObjectCache<String, Object>();

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating OGNL expression: {1}", expression);
      try {
         final OgnlContext ctx = (OgnlContext) Ognl.createDefaultContext(null);

         for (final Entry<String, ?> entry : values.entrySet()) {
            ctx.put(entry.getKey(), entry.getValue());
         }

         Object expr = expressionCache.get(expression);
         if (expr == null) {
            expr = Ognl.parseExpression(expression);
            expressionCache.put(expression, expr);
         }
         return Ognl.getValue(expr, ctx);
      } catch (final OgnlException ex) {
         throw new ExpressionEvaluationException("Evaluating MVEL expression failed: " + expression, ex);
      }
   }
}
