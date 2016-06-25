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
package net.sf.oval.constraint;

import java.util.regex.Pattern;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Sebastian Thomschke
 */
public class EmailCheck extends AbstractAnnotationCheck<Email> {
    private static final long serialVersionUID = 1L;

    private static final String SPECIAL_CHARACTERS = "'\\(\\)\\-\\.`";
    private static final String ASCII = "\\w " + SPECIAL_CHARACTERS;
    private static final String ASCII_WITHOUT_COMMA = "[" + ASCII + "]+";
    private static final String ASCII_WITH_COMMA = "\"[" + ASCII + ",]+\"";
    private static final String ASCII_WITH_QUESTION_MARK_AND_EQUALS = "[" + ASCII + "\\?\\=]+";
    private static final String MIME_ENCODED = "\\=\\?" + ASCII_WITH_QUESTION_MARK_AND_EQUALS + "\\?\\=";
    private static final String NAME = "(" + ASCII_WITHOUT_COMMA + "|" + ASCII_WITH_COMMA + "|" + MIME_ENCODED + ")";

    private static final String EMAIL_BASE_PATTERN = "['_A-Za-z0-9-&+]+(\\.['_A-Za-z0-9-&+]+)*[.]{0,1}@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^" + EMAIL_BASE_PATTERN + "$");

    private static final Pattern EMAIL_WITH_PERSONAL_NAME_PATTERN = Pattern.compile("^(" + EMAIL_BASE_PATTERN + "|" + NAME + " +<" + EMAIL_BASE_PATTERN
            + ">)$");

    private boolean allowPersonalName;

    @Override
    public void configure(final Email constraintAnnotation) {
        super.configure(constraintAnnotation);
        setAllowPersonalName(constraintAnnotation.allowPersonalName());
    }

    @Override
    protected ConstraintTarget[] getAppliesToDefault() {
        return new ConstraintTarget[] { ConstraintTarget.VALUES };
    }

    public boolean isAllowPersonalName() {
        return allowPersonalName;
    }

    public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
            throws OValException {
        if (valueToValidate == null)
            return true;

        if (allowPersonalName)
            return EMAIL_WITH_PERSONAL_NAME_PATTERN.matcher(valueToValidate.toString()).matches();
        return EMAIL_PATTERN.matcher(valueToValidate.toString()).matches();
    }

    public void setAllowPersonalName(final boolean allowPersonalName) {
        this.allowPersonalName = allowPersonalName;
    }
}
