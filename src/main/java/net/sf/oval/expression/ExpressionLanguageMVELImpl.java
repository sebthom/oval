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

import org.mvel2.MVEL;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageMVELImpl extends AbstractExpressionLanguage {
   private static final Log LOG = Log.getLog(ExpressionLanguageMVELImpl.class);

   private final ObjectCache<String, Object> expressionCache = new ObjectCache<>(MVEL::compileExpression);

   @Override
   public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      LOG.debug("Evaluating MVEL expression: {1}", expression);
      try {
         final Object expr = expressionCache.get(expression);
         return MVEL.executeExpression(expr, values);
      } catch (final Exception ex) {
         throw new ExpressionEvaluationException("Evaluating MVEL expression failed: " + expression, ex);
      }
   }
}
