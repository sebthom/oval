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
