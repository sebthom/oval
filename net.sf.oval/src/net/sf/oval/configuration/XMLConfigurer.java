/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * XStream based XML configuration class
 * 
 * @author Sebastian Thomschke
 * 
 * @see http://xstream.codehaus.org/
 */
public class XMLConfigurer extends POJOConfigurer
{
	protected static class ChecksConfiguration
	{
		public Set<ClassConfiguration> classConfigurations;
		public Set<ConstraintSetConfiguration> constraintSetConfigurations;
	}

	private final XStream xStream;

	/**
	 * creates an XMLConfigurer instance backed by a new XStream instance 
	 * using the com.thoughtworks.xstream.io.xml.StaxDriver for XML parsing 
	 *
	 * @see com.thoughtworks.xstream.io.xml.StaxDriver
	 */
	public XMLConfigurer()
	{
		xStream = new XStream(new StaxDriver());
		configureXStream();
	}

	public XMLConfigurer(final XStream xStream)
	{
		this.xStream = xStream;
		configureXStream();
	}

	protected final void configureXStream()
	{
		xStream.useAttributeFor(Class.class);
		xStream.useAttributeFor(boolean.class);
		xStream.useAttributeFor(byte.class);
		xStream.useAttributeFor(char.class);
		xStream.useAttributeFor(double.class);
		xStream.useAttributeFor(float.class);
		xStream.useAttributeFor(int.class);
		xStream.useAttributeFor(long.class);
		xStream.useAttributeFor(Boolean.class);
		xStream.useAttributeFor(Byte.class);
		xStream.useAttributeFor(Character.class);
		xStream.useAttributeFor(Double.class);
		xStream.useAttributeFor(Float.class);
		xStream.useAttributeFor(Integer.class);
		xStream.useAttributeFor(Long.class);
		xStream.useAttributeFor(String.class);

		xStream.alias("oval", ChecksConfiguration.class);
		xStream.addImplicitCollection(ChecksConfiguration.class, "classConfigurations",
				ClassConfiguration.class);
		xStream.addImplicitCollection(ChecksConfiguration.class, "constraintSetConfigurations",
				ConstraintSetConfiguration.class);

		xStream.alias("constraintSet", ConstraintSetConfiguration.class);
		xStream.addImplicitCollection(ConstraintSetConfiguration.class, "checks");

		xStream.alias("class", ClassConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "constructorConfigurations",
				ConstructorConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "fieldConfigurations",
				FieldConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "methodConfigurations",
				MethodConfiguration.class);

		xStream.alias("field", FieldConfiguration.class);
		xStream.addImplicitCollection(FieldConfiguration.class, "checks");

		xStream.alias("parameter", ParameterConfiguration.class);
		xStream.addImplicitCollection(ParameterConfiguration.class, "checks", Check.class);

		xStream.alias("constructor", ConstructorConfiguration.class);
		xStream.addImplicitCollection(ConstructorConfiguration.class, "parameterConfigurations", ParameterConfiguration.class);

		xStream.alias("method", MethodConfiguration.class);
		xStream.addImplicitCollection(MethodConfiguration.class, "parameterConfigurations",
				ParameterConfiguration.class);
		xStream.addImplicitCollection(MethodConfiguration.class, "returnValueChecks", Check.class);
	}

	public synchronized void fromXML(final File input) throws IOException
	{
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
		try
		{
			fromXML(bis);
		}
		finally
		{
			bis.close();
		}
	}

	public synchronized void fromXML(final InputStream input)
	{
		final ChecksConfiguration checksConfiguration = (ChecksConfiguration) xStream
				.fromXML(input);
		classConfigurations = checksConfiguration.classConfigurations;
		constraintSetConfigurations = checksConfiguration.constraintSetConfigurations;
	}

	public synchronized void fromXML(final Reader xml)
	{
		final ChecksConfiguration checksConfiguration = (ChecksConfiguration) xStream.fromXML(xml);
		classConfigurations = checksConfiguration.classConfigurations;
		constraintSetConfigurations = checksConfiguration.constraintSetConfigurations;
	}

	public synchronized void fromXML(final String xml)
	{
		final ChecksConfiguration checksConfiguration = (ChecksConfiguration) xStream.fromXML(xml);
		classConfigurations = checksConfiguration.classConfigurations;
		constraintSetConfigurations = checksConfiguration.constraintSetConfigurations;
	}

	/**
	 * @return the xStream
	 */
	public XStream getXStream()
	{
		return xStream;
	}

	public synchronized String toXML()
	{
		final ChecksConfiguration checksConfiguration = new ChecksConfiguration();
		checksConfiguration.classConfigurations = classConfigurations;
		checksConfiguration.constraintSetConfigurations = constraintSetConfigurations;
		return xStream.toXML(checksConfiguration);
	}

	public synchronized void toXML(final OutputStream out)
	{
		final ChecksConfiguration checksConfiguration = new ChecksConfiguration();
		checksConfiguration.classConfigurations = classConfigurations;
		checksConfiguration.constraintSetConfigurations = constraintSetConfigurations;
		xStream.toXML(checksConfiguration, out);
	}

	public synchronized void toXML(final Writer out)
	{
		final ChecksConfiguration checksConfiguration = new ChecksConfiguration();
		checksConfiguration.classConfigurations = classConfigurations;
		checksConfiguration.constraintSetConfigurations = constraintSetConfigurations;
		xStream.toXML(checksConfiguration, out);
	}
}
