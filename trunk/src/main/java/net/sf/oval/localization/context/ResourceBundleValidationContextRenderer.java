/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
package net.sf.oval.localization.context;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
 * <pre>
 * label.class=My translated name of the class
 * label.field.firstname=My translated name of firstname
 * label.field.lastname=My translated name of lastname
 * label.parameter.amount=My translated name of a constructor/method amount
 * label.method.increase=My translated name of the increase
 * </pre>
 * @author Sebastian Thomschke
 */
public class ResourceBundleValidationContextRenderer implements OValContextRenderer
{
	private final static Log LOG = Log.getLog(ResourceBundleValidationContextRenderer.class);

	public final static ResourceBundleValidationContextRenderer INSTANCE = new ResourceBundleValidationContextRenderer();

	/**
	 * {@inheritDoc}
	 */
	public String render(final OValContext ovalContext)
	{
		final String baseName;
		final String key;
		if (ovalContext instanceof ClassContext)
		{
			final ClassContext ctx = (ClassContext) ovalContext;
			baseName = ctx.getClazz().getName();
			key = "label.class";
		}
		else if (ovalContext instanceof FieldContext)
		{
			final FieldContext ctx = (FieldContext) ovalContext;
			baseName = ctx.getField().getDeclaringClass().getName();
			final String fieldName = ctx.getField().getName();
			key = "label.field." + fieldName;
		}
		else if (ovalContext instanceof ConstructorParameterContext)
		{
			final ConstructorParameterContext ctx = (ConstructorParameterContext) ovalContext;
			baseName = ctx.getConstructor().getDeclaringClass().getName();
			key = "label.parameter." + ctx.getParameterName();
		}
		else if (ovalContext instanceof MethodParameterContext)
		{
			final MethodParameterContext ctx = (MethodParameterContext) ovalContext;
			baseName = ctx.getMethod().getDeclaringClass().getName();
			key = "label.parameter." + ctx.getParameterName();
		}
		else if (ovalContext instanceof MethodEntryContext)
		{
			final MethodEntryContext ctx = (MethodEntryContext) ovalContext;
			baseName = ctx.getMethod().getDeclaringClass().getName();
			key = "label.method." + ctx.getMethod().getName();
		}
		else if (ovalContext instanceof MethodExitContext)
		{
			final MethodExitContext ctx = (MethodExitContext) ovalContext;
			baseName = ctx.getMethod().getDeclaringClass().getName();
			key = "label.method." + ctx.getMethod().getName();
		}
		else if (ovalContext instanceof MethodReturnValueContext)
		{
			final MethodReturnValueContext ctx = (MethodReturnValueContext) ovalContext;
			baseName = ctx.getMethod().getDeclaringClass().getName();
			key = "label.method." + ctx.getMethod().getName();
		}
		else
			return ovalContext.toString();

		try
		{
			final ResourceBundle bundle = ResourceBundle.getBundle(baseName);
			if (bundle.containsKey(key)) return bundle.getString(key);
			LOG.debug("Key {1} not found in bundle {2}", key, baseName);
		}
		catch (final MissingResourceException ex)
		{
			LOG.debug("Bundle {1} not found", baseName, ex);
		}
		return ovalContext.toString();
	}
}
