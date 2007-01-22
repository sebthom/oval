/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
import net.sf.oval.configuration.elements.MethodPostExecutionConfiguration;
import net.sf.oval.configuration.elements.MethodPreExecutionConfiguration;
import net.sf.oval.configuration.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.constraints.*;
import net.sf.oval.exceptions.OValException;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;

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

		// constraint check short forms
		xStream.alias("assert", AssertCheck.class);
		xStream.alias("assertConstraintSet", AssertConstraintSetCheck.class);
		xStream.alias("assertFalse", AssertFalseCheck.class);
		xStream.alias("assertFieldConstraints", AssertFieldConstraintsCheck.class);
		xStream.alias("assertTrue", AssertTrueCheck.class);
		xStream.alias("assertValid", AssertValidCheck.class);
		xStream.alias("future", FutureCheck.class);
		xStream.alias("instanceOf", InstanceOfCheck.class);
		xStream.alias("length", LengthCheck.class);
		xStream.alias("max", MaxCheck.class);
		xStream.alias("min", MinCheck.class);
		xStream.alias("noSelfReference", NoSelfReferenceCheck.class);
		xStream.alias("notEmpty", NotEmptyCheck.class);
		xStream.alias("notNegative", NotNegativeCheck.class);
		xStream.alias("notNull", NotNullCheck.class);
		xStream.alias("past", PastCheck.class);
		xStream.alias("range", RangeCheck.class);
		xStream.alias("regEx", RegExCheck.class);
		xStream.alias("size", SizeCheck.class);
		xStream.alias("validateWithMethod", ValidateWithMethodCheck.class);

		// <oval> -> net.sf.oval.configuration.POJOConfigurer
		xStream.alias("oval", POJOConfigurer.class);
		{
			// <constraintSet> -> net.sf.oval.configuration.elements.ConstraintSetConfiguration
			xStream.addImplicitCollection(POJOConfigurer.class, "constraintSetConfigurations",
					ConstraintSetConfiguration.class);
			xStream.alias("constraintSet", ConstraintSetConfiguration.class);
			xStream.addImplicitCollection(ConstraintSetConfiguration.class, "checks");

			// <class> -> net.sf.oval.configuration.elements.ClassConfiguration
			xStream.addImplicitCollection(POJOConfigurer.class, "classConfigurations",
					ClassConfiguration.class);
			xStream.alias("class", ClassConfiguration.class);
			{
				// <field> -> net.sf.oval.configuration.elements.FieldConfiguration
				xStream.addImplicitCollection(ClassConfiguration.class, "fieldConfigurations",
						FieldConfiguration.class);
				xStream.alias("field", FieldConfiguration.class);
				xStream.addImplicitCollection(FieldConfiguration.class, "checks");

				// <parameter> -> net.sf.oval.configuration.elements.ParameterConfiguration
				// used within ConstructorConfiguration and MethodConfiguration
				xStream.alias("parameter", ParameterConfiguration.class);
				xStream.addImplicitCollection(ParameterConfiguration.class, "checks", Check.class);

				// <constructor> -> net.sf.oval.configuration.elements.ConstructorConfiguration 
				xStream.addImplicitCollection(ClassConfiguration.class,
						"constructorConfigurations", ConstructorConfiguration.class);
				xStream.alias("constructor", ConstructorConfiguration.class);
				{
					// <parameter> -> net.sf.oval.configuration.elements.ParameterConfiguration
					xStream.addImplicitCollection(ConstructorConfiguration.class,
							"parameterConfigurations", ParameterConfiguration.class);
				}

				// <method> -> net.sf.oval.configuration.elements.MethodConfiguration
				xStream.addImplicitCollection(ClassConfiguration.class, "methodConfigurations",
						MethodConfiguration.class);
				xStream.alias("method", MethodConfiguration.class);
				{
					// <parameter> -> net.sf.oval.configuration.elements.ParameterConfiguration
					xStream.addImplicitCollection(MethodConfiguration.class,
							"parameterConfigurations", ParameterConfiguration.class);

					// <returnValue> -> net.sf.oval.configuration.elements.MethodConfiguration.returnValueConfiguration -> MethodReturnValueConfiguration					
					xStream.aliasField("returnValue", MethodConfiguration.class,
							"returnValueConfiguration");
					xStream.addImplicitCollection(MethodReturnValueConfiguration.class, "checks",
							Check.class);

					// <pre> -> net.sf.oval.configuration.elements.MethodConfiguration.preConditionsConfiguration -> MethodPreConditionsConfiguration					
					xStream.aliasField("pre", MethodConfiguration.class,
							"preConditionsConfiguration");
					xStream.addImplicitCollection(MethodPostExecutionConfiguration.class, "checks",
							PreCheck.class);
					xStream.alias("preCheck", PreCheck.class);

					// <post> -> net.sf.oval.configuration.elements.MethodConfiguration.postConditionsConfiguration -> MethodPostConditionsConfiguration					
					xStream.aliasField("post", MethodConfiguration.class,
							"postConditionsConfiguration");
					xStream.addImplicitCollection(MethodPreExecutionConfiguration.class, "checks",
							PostCheck.class);
					xStream.alias("postCheck", PostCheck.class);
				}
			}
		}

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
	public void setPojoConfigurer(final POJOConfigurer pojoConfigurer)
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
