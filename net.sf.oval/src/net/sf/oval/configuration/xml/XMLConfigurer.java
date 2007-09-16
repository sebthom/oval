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
package net.sf.oval.configuration.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.POJOConfigurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPostExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPreExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.constraint.AssertCheck;
import net.sf.oval.constraint.AssertConstraintSetCheck;
import net.sf.oval.constraint.AssertFalseCheck;
import net.sf.oval.constraint.AssertFieldConstraintsCheck;
import net.sf.oval.constraint.AssertTrueCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.CheckWithCheck;
import net.sf.oval.constraint.FutureCheck;
import net.sf.oval.constraint.HasSubstringCheck;
import net.sf.oval.constraint.InstanceOfCheck;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.MaxCheck;
import net.sf.oval.constraint.MaxLengthCheck;
import net.sf.oval.constraint.MaxSizeCheck;
import net.sf.oval.constraint.MinCheck;
import net.sf.oval.constraint.MinLengthCheck;
import net.sf.oval.constraint.MinSizeCheck;
import net.sf.oval.constraint.NoSelfReferenceCheck;
import net.sf.oval.constraint.NotBlankCheck;
import net.sf.oval.constraint.NotEmptyCheck;
import net.sf.oval.constraint.NotNegativeCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.PastCheck;
import net.sf.oval.constraint.RangeCheck;
import net.sf.oval.constraint.SizeCheck;
import net.sf.oval.constraint.ValidateWithMethod;
import net.sf.oval.exception.OValException;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.internal.util.ReflectionUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * XStream (http://xstream.codehaus.org/) based XML configuration class.
 * 
 * @author Sebastian Thomschke
 * 
 */
public class XMLConfigurer implements Configurer
{
	private static final long serialVersionUID = 1L;

	private final static class AssertCheckConverter implements Converter
	{
		public boolean canConvert(final Class clazz)
		{
			return clazz.equals(AssertCheck.class);
		}

		public void marshal(final Object value, final HierarchicalStreamWriter writer,
				final MarshallingContext context)
		{
			final AssertCheck assertCheck = (AssertCheck) value;
			writer.addAttribute("lang", assertCheck.getLang());
			writer.addAttribute("message", assertCheck.getMessage());
			writer.startNode("expr");
			writer.setValue(assertCheck.getExpression());
			writer.endNode();
			final String[] profiles = assertCheck.getProfiles();
			if (profiles != null && profiles.length > 0)
			{
				writer.startNode("profiles");
				for (final String profile : profiles)
				{
					writer.startNode("string");
					writer.setValue(profile);
					writer.endNode();
				}
				writer.endNode();
			}
		}

		public Object unmarshal(final HierarchicalStreamReader reader,
				final UnmarshallingContext context)
		{
			final AssertCheck assertCheck = new AssertCheck();
			assertCheck.setLang(reader.getAttribute("lang"));
			assertCheck.setMessage(reader.getAttribute("message"));
			reader.moveDown();
			assertCheck.setExpression(reader.getValue());
			reader.moveUp();
			if (reader.hasMoreChildren())
			{
				reader.moveDown();
				final List<String> profiles = new ArrayList<String>(4);
				while (reader.hasMoreChildren())
				{
					reader.moveDown();
					if ("string".equals(reader.getNodeName())) profiles.add(reader.getValue());
					reader.moveUp();
				}
				reader.moveUp();
				assertCheck.setProfiles(profiles.toArray(new String[profiles.size()]));
			}
			return assertCheck;
		}
	}

	private POJOConfigurer pojoConfigurer = new POJOConfigurer();

	private final XStream xStream;

	/**
	 * creates an XMLConfigurer instance backed by a new XStream instance 
	 * using the com.thoughtworks.xstream.io.xml.StaxDriver for XML parsing 
	 * if the StAX API is available
	 * @see com.thoughtworks.xstream.io.xml.StaxDriver
	 */
	public XMLConfigurer()
	{
		// new com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider()
		if (ReflectionUtils.isClassPresent("javax.xml.stream.XMLStreamReader"))
			xStream = new XStream(new StaxDriver());
		else if (ReflectionUtils.isClassPresent("org.xmlpull.mxp1.MXParser"))
			xStream = new XStream(new XppDriver());
		else
			xStream = new XStream(new DomDriver());

		configureXStream();
	}

	protected final void configureXStream()
	{
		xStream.registerConverter(new AssertCheckConverter());

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
		xStream.alias("checkWith", CheckWithCheck.class);
		xStream.alias("future", FutureCheck.class);
		xStream.alias("hasSubstring", HasSubstringCheck.class);
		xStream.alias("instanceOf", InstanceOfCheck.class);
		xStream.alias("length", LengthCheck.class);
		xStream.alias("matchPattern", MatchPatternCheck.class);
		xStream.alias("max", MaxCheck.class);
		xStream.alias("maxLength", MaxLengthCheck.class);
		xStream.alias("maxSize", MaxSizeCheck.class);
		xStream.alias("min", MinCheck.class);
		xStream.alias("minLength", MinLengthCheck.class);
		xStream.alias("minSize", MinSizeCheck.class);
		xStream.alias("noSelfReference", NoSelfReferenceCheck.class);
		xStream.alias("notBlank", NotBlankCheck.class);
		xStream.alias("notEmpty", NotEmptyCheck.class);
		xStream.alias("notNegative", NotNegativeCheck.class);
		xStream.alias("notNull", NotNullCheck.class);
		xStream.alias("past", PastCheck.class);
		xStream.alias("range", RangeCheck.class);
		xStream.alias("size", SizeCheck.class);
		xStream.alias("validateWithMethod", ValidateWithMethod.class);

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

					// <returnValue> -> net.sf.oval.configuration.elements.MethodConfiguration.returnValueConfiguration
					// -> MethodReturnValueConfiguration
					xStream.aliasField("returnValue", MethodConfiguration.class,
							"returnValueConfiguration");
					xStream.addImplicitCollection(MethodReturnValueConfiguration.class, "checks",
							Check.class);

					// <pre> -> net.sf.oval.configuration.elements.MethodConfiguration.preExecutionConfiguration ->
					// MethodPreExecutionConfiguration
					xStream.aliasField("pre", MethodConfiguration.class,
							"preExecutionConfiguration");
					xStream.addImplicitCollection(MethodPostExecutionConfiguration.class, "checks",
							PreCheck.class);
					xStream.alias("preCheck", PreCheck.class);

					// <post> -> net.sf.oval.configuration.elements.MethodConfiguration.postExecutionConfiguration ->
					// MethodPpstExecutionConfiguration
					xStream.aliasField("post", MethodConfiguration.class,
							"postExecutionConfiguration");
					xStream.addImplicitCollection(MethodPreExecutionConfiguration.class, "checks",
							PostCheck.class);
					xStream.alias("postCheck", PostCheck.class);
				}
			}
		}
	}

	public synchronized void fromXML(final File input) throws IOException
	{
		final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
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
