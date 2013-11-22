/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.oval.AbstractCheckExclusion;
import net.sf.oval.internal.Log;

/**
 * Partial implementation of check exclusion classes configurable via annotations.
 * 
 * @author Sebastian Thomschke
 */
public abstract class AbstractAnnotationCheckExclusion<ExclusionAnnotation extends Annotation>
		extends
			AbstractCheckExclusion implements AnnotationCheckExclusion<ExclusionAnnotation>
{
	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.getLog(AbstractAnnotationCheckExclusion.class);

	/**
	 * {@inheritDoc}
	 */
	public void configure(final ExclusionAnnotation exclusionAnnotation)
	{
		final Class< ? > exclusionClazz = exclusionAnnotation.getClass();

		/*
		 * Retrieve the profiles value from the constraint exclusion annotation via reflection.
		 */
		try
		{
			final Method getProfiles = exclusionClazz.getDeclaredMethod("profiles", (Class< ? >[]) null);
			setProfiles((String[]) getProfiles.invoke(exclusionAnnotation, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint profiles based on annotation {1}", exclusionClazz.getName(), e);
		}

		/*
		 * Retrieve the when formula from the constraint exclusion annotation via reflection.
		 */
		try
		{
			final Method getWhen = exclusionClazz.getDeclaredMethod("when", (Class< ? >[]) null);
			setWhen((String) getWhen.invoke(exclusionClazz, (Object[]) null));
		}
		catch (final Exception e)
		{
			LOG.debug("Cannot determine constraint when formula based on annotation {1}", exclusionClazz.getName(), e);
		}
	}
}
