/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.oval;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.expression.ExpressionLanguage;

/**
 * Partial implementation of exclusion classes.
 *
 * @author Sebastian Thomschke
 */
public abstract class AbstractCheckExclusion implements CheckExclusion {
    private static final long serialVersionUID = 1L;

    private String[] profiles;

    private String when;
    private String whenFormula;
    private String whenLang;

    public Map<String, String> getMessageVariables() {
        return null;
    }

    public String[] getProfiles() {
        return profiles;
    }

    public String getWhen() {
        return whenLang + ":" + when;
    }

    public boolean isActive(final Object validatedObject, final Object valueToValidate, final Validator validator) {
        if (when == null)
            return true;

        final Map<String, Object> values = getCollectionFactory().createMap();
        values.put("_value", valueToValidate);
        values.put("_this", validatedObject);

        final ExpressionLanguage el = validator.getExpressionLanguageRegistry().getExpressionLanguage(whenLang);
        return el.evaluateAsBoolean(whenFormula, values);
    }

    public void setProfiles(final String... profiles) {
        this.profiles = profiles;
    }

    public void setWhen(final String when) {
        if (when == null || when.length() == 0) {
            this.when = null;
            whenFormula = null;
            whenLang = null;
        } else {
            this.when = when;
            final String[] parts = when.split(":", 2);
            if (parts.length == 0)
                throw new IllegalArgumentException("[when] is missing the scripting language declaration");
            whenLang = parts[0];
            whenFormula = parts[1];
        }
    }
}
