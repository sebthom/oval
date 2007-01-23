/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
package net.sf.oval.guard;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodPreExecutionContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.OValException;

/**
 * Translates OVal specific exceptions to standard exceptions part of the JRE:
 * <ul>
 * <li><code>ConstraintsViolatedException</code> for constructor/method parameter translated to <code>IllegalArgumentException</code>
 * <li><code>ConstraintsViolatedException</code> for class field translated to <code>IllegalStateException</code>
 * <li><code>ConstraintsViolatedException</code> for method return values translated to <code>IllegalStateException</code>
 * <li>Other exceptions based on <code>OValException</code> translated to <code>RuntimeException</code>
 * </ul>
 * @author Sebastian Thomschke
 */
public class ExceptionTranslatorJREExceptionsImpl implements ExceptionTranslator
{
	private final static Logger LOG = Logger.getLogger(ExceptionTranslatorJREExceptionsImpl.class
			.getName());

	public RuntimeException translateException(final OValException ex)
	{
		// translate ConstraintsViolatedException based on the validation context
		if (ex instanceof ConstraintsViolatedException)
		{
			final ConstraintsViolatedException cex = (ConstraintsViolatedException) ex;
			final ConstraintViolation cv = cex.getConstraintViolations()[0];
			final OValContext ctx = cv.getContext();

			// translate exceptions for preconditions to IllegalArgumentExceptions
			if (ctx instanceof MethodParameterContext || ctx instanceof ConstructorParameterContext
					|| ctx instanceof MethodPreExecutionContext)
			{
				final IllegalArgumentException iaex = new IllegalArgumentException(cv.getMessage());
				iaex.setStackTrace(ex.getStackTrace());
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.FINE, "Translated Exception" + ex + " to " + iaex, ex);
				return iaex;
			}

			// translate invariant exceptions to IllegalStateExceptions
			if (ctx instanceof FieldContext || ctx instanceof MethodReturnValueContext)
			{
				final IllegalStateException ise = new IllegalStateException(cv.getMessage());
				ise.setStackTrace(ex.getStackTrace());
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.FINE, "Translated Exception" + ex + " to " + ise, ex);
				return ise;
			}
		}

		// translate all other messages to runtime exceptions
		{
			final RuntimeException rex = new RuntimeException(ex.getMessage());
			rex.setStackTrace(ex.getStackTrace());
			LOG.log(Level.FINE, "Translated Exception" + ex + " to " + rex, ex);
			return rex;
		}
	}
}
