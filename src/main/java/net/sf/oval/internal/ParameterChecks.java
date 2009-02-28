/********************************import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.internal.util.LinkedSet;
 Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.internal;

import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.internal.util.LinkedSet;

/**
 * @author Sebastian Thomschke
 */
public class ParameterChecks
{
	public final Set<Check> checks = new LinkedSet<Check>(2);
	public final Set<CheckExclusion> checkExclusions = new LinkedSet<CheckExclusion>(2);

	public int parameterIndex;

	public boolean hasChecks()
	{
		return checks.size() > 0;
	}

	public boolean hasExclusions()
	{
		return checkExclusions.size() > 0;
	}

	public boolean isEmpty()
	{
		return checks.size() == 0 && checkExclusions.size() == 0;
	}
}
