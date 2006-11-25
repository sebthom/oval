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

import net.sf.oval.Check;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.exceptions.OValException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * XStream based XML configuration class
 * 
 * @author Sebastian Thomschke
 * 
 * @see http://xstream.codehaus.org/
 */
public class XMLConfigurer implements Configurer
{
	private static final long serialVersionUID = 1L;

	private POJOConfigurer pojoConfigurer = new POJOConfigurer();

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

		xStream.alias("oval", POJOConfigurer.class);
		xStream.addImplicitCollection(POJOConfigurer.class, "classConfigurations",
				ClassConfiguration.class);
		xStream.addImplicitCollection(POJOConfigurer.class, "constraintSetConfigurations",
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
		xStream.addImplicitCollection(ConstructorConfiguration.class, "parameterConfigurations",
				ParameterConfiguration.class);

		xStream.alias("method", MethodConfiguration.class);
		xStream.addImplicitCollection(MethodConfiguration.class, "parameterConfigurations",
				ParameterConfiguration.class);
		xStream.aliasField("returnValue", MethodConfiguration.class, "returnValueConfiguration");
		//xStream.alias("returnValue", MethodReturnValueConfiguration.class);
		xStream.addImplicitCollection(MethodReturnValueConfiguration.class, "checks", Check.class);

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
		pojoConfigurer = (POJOConfigurer) xStream.fromXML(input);
	}

	public synchronized void fromXML(final Reader input)
	{
		pojoConfigurer = (POJOConfigurer) xStream.fromXML(input);
	}

	public synchronized void fromXML(final String input)
	{
		pojoConfigurer = (POJOConfigurer) xStream.fromXML(input);
	}

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz) throws OValException
	{
		return pojoConfigurer.getClassConfiguration(clazz);
	}

	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
			throws OValException
	{
		return pojoConfigurer.getConstraintSetConfiguration(constraintSetId);
	}

	/**
	 * @return the pojoConfigurer
	 */
	public POJOConfigurer getPojoConfigurer()
	{
		return pojoConfigurer;
	}

	/**
	 * @return the xStream
	 */
	public XStream getXStream()
	{
		return xStream;
	}

	/**
	 * @param pojoConfigurer the pojoConfigurer to set
	 */
	public void setPojoConfigurer(POJOConfigurer pojoConfigurer)
	{
		this.pojoConfigurer = pojoConfigurer;
	}

	public synchronized String toXML()
	{
		return xStream.toXML(pojoConfigurer);
	}

	public synchronized void toXML(final OutputStream out)
	{
		xStream.toXML(pojoConfigurer, out);
	}

	public synchronized void toXML(final Writer out)
	{
		xStream.toXML(pojoConfigurer, out);
	}
}
