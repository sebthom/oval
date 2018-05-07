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

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageJEXLImpl extends AbstractExpressionLanguage {
    private static final Log LOG = Log.getLog(ExpressionLanguageJEXLImpl.class);

    private static final JexlEngine jexl = new JexlEngine();

    private final ObjectCache<String, Expression> expressionCache = new ObjectCache<String, Expression>();

    @Override
    @SuppressWarnings("unchecked")
    public Object evaluate(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
        LOG.debug("Evaluating JEXL expression: {1}", expression);
        try {
            Expression expr = expressionCache.get(expression);
            if (expr == null) {
                expr = jexl.createExpression(expression);
                expressionCache.put(expression, expr);
            }
            return expr.evaluate(new MapContext((Map<String, Object>) values));
        } catch (final Exception ex) {
            throw new ExpressionEvaluationException("Evaluating JEXL expression failed: " + expression, ex);
        }
    }
}