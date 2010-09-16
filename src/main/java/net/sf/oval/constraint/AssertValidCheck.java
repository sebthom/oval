/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.oval.constraint;

import java.util.List;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid>
{
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void configure(final AssertValid constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		if (!constraintAnnotation.requireValidElements())
			setRequireValidElements(constraintAnnotation.requireValidElements());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConstraintTarget[] getAppliesToDefault()
	{
		return new ConstraintTarget[]{ConstraintTarget.CONTAINER, ConstraintTarget.VALUES};
	}

	/**
	 * @return true if all elements of a collection must be valid
	 * @deprecated use appliesTo instead
	 */
	@Deprecated
	public boolean isRequireValidElements()
	{
		return ArrayUtils.containsSame(this.getAppliesTo(), ConstraintTarget.VALUES);
	}

	/**
	 *  <b>This method is not used.</b><br>
	 *  The validation of this special constraint is directly performed by the Validator class
	 *  @throws UnsupportedOperationException always thrown if this method is invoked
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Specifies if all the elements of a collection must be valid.
	 * @deprecated use appliesTo instead
	 */
	@Deprecated
	public void setRequireValidElements(final boolean requireValidElements)
	{
		final ConstraintTarget[] targets = getAppliesTo();
		if (requireValidElements)
		{
			if (!ArrayUtils.containsSame(targets, ConstraintTarget.VALUES))
			{
				final List<ConstraintTarget> targetsList = Validator.getCollectionFactory().createList(4);
				ArrayUtils.addAll(targetsList, targets);
				targetsList.add(ConstraintTarget.VALUES);
				setAppliesTo(targetsList.toArray(new ConstraintTarget[targetsList.size()]));
			}
		}
		else if (ArrayUtils.containsSame(targets, ConstraintTarget.VALUES))
		{
			final List<ConstraintTarget> targetsList = Validator.getCollectionFactory().createList(4);
			ArrayUtils.addAll(targetsList, targets);
			targetsList.remove(ConstraintTarget.VALUES);
			setAppliesTo(targetsList.toArray(new ConstraintTarget[targetsList.size()]));
		}
	}
}
