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