/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.oval.integration.spring;

import net.sf.oval.Check;
import net.sf.oval.configuration.CheckInitializationListener;
import net.sf.oval.constraint.CheckWithCheck;

/**
 * Injects Spring bean dependencies into {@link Check} instances.
 *
 * Required dependencies must be annotated with <code>@Autowired</code> within {@link Check} the class.
 *
 * Requires the {@link SpringInjector} be setup correctly.
 *
 * @author Sebastian Thomschke
 */
public class SpringCheckInitializationListener implements CheckInitializationListener
{
	public static final SpringCheckInitializationListener INSTANCE = new SpringCheckInitializationListener();

	public void onCheckInitialized(final Check check)
	{
		SpringInjector.get().inject(check);

		if (check instanceof CheckWithCheck)
		{
			final CheckWithCheck checkWith = (CheckWithCheck) check;
			SpringInjector.get().inject(checkWith.getSimpleCheck());
		}
	}
}
