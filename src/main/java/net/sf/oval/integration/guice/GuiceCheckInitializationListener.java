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
package net.sf.oval.integration.guice;

import net.sf.oval.Check;
import net.sf.oval.configuration.CheckInitializationListener;
import net.sf.oval.constraint.CheckWithCheck;

import com.google.inject.Injector;

/**
 * Injects Guice bean dependencies into {@link Check} instances.
 *
 * Required dependencies must be annotated with <code>@Autowired</code> within {@link Check} the class.
 *
 * @author Sebastian Thomschke
 */
public class GuiceCheckInitializationListener implements CheckInitializationListener {
    private final Injector injector;

    public GuiceCheckInitializationListener(final Injector injector) {
        this.injector = injector;
    }

    public void onCheckInitialized(final Check check) {
        injector.injectMembers(check);

        if (check instanceof CheckWithCheck) {
            final CheckWithCheck checkWith = (CheckWithCheck) check;
            injector.injectMembers(checkWith.getSimpleCheck());
        }
    }
}
