/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

/**
 * @author Sebastian Thomschke
 *
 */
@FunctionalInterface
public interface Invocable<V, T extends Throwable> {
   V invoke() throws T;
}
