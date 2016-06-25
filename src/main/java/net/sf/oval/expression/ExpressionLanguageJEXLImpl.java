/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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