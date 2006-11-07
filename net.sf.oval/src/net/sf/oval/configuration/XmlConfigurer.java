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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Set;

import net.sf.oval.Check;

import com.thoughtworks.xstream.XStream;

/**
 * XStream based XML configuration class
 * 
 * @author Sebastian Thomschke
 * @see http://xstream.codehaus.org/
 */
public class XmlConfigurer implements Configurer
{
	private OValConfiguration oValConfiguration;

	private XStream xStream;

	public XmlConfigurer()
	{
		initializeXStream();
	}

	public void fromXML(final InputStream input)
	{
		oValConfiguration = (OValConfiguration) xStream.fromXML(input);
	}

	public void fromXML(final Reader xml)
	{
		oValConfiguration = (OValConfiguration) xStream.fromXML(xml);
	}

	public void fromXML(final String xml)
	{
		oValConfiguration = (OValConfiguration) xStream.fromXML(xml);
	}

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz)
	{
		if (oValConfiguration.classesConfig != null)
		{
			for (ClassConfiguration classConfig : oValConfiguration.classesConfig)
			{
				if (classConfig.type == clazz) return classConfig;
			}
		}
		return null;
	}

	public Set<ConstraintSetConfiguration> getConstraintSetConfigurations()
	{
		return oValConfiguration.constraintSetsConfig;
	}

	/**
	 * @return the oValConfiguration
	 */
	public OValConfiguration getOValConfiguration()
	{
		return oValConfiguration;
	}

	/**
	 * @return the xStream
	 */
	public XStream getXStream()
	{
		return xStream;
	}

	private void initializeXStream()
	{
		xStream = new XStream();
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

		xStream.alias("String", String.class);
		xStream.aliasType("String", String.class);

		xStream.alias("oval-config", OValConfiguration.class);
		xStream.addImplicitCollection(OValConfiguration.class, "classesConfig",
				ClassConfiguration.class);
		xStream.addImplicitCollection(OValConfiguration.class, "constraintSetsConfig",
				ConstraintSetConfiguration.class);

		xStream.alias("constraintSet", ConstraintSetConfiguration.class);
		xStream.addImplicitCollection(ConstraintSetConfiguration.class, "checks");

		xStream.alias("class", ClassConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "constructorsConfig",
				ConstructorConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "fieldsConfig",
				FieldConfiguration.class);
		xStream.addImplicitCollection(ClassConfiguration.class, "methodsConfig",
				MethodConfiguration.class);

		xStream.alias("field", FieldConfiguration.class);
		xStream.addImplicitCollection(FieldConfiguration.class, "checks");

		xStream.alias("parameter", ParameterConfiguration.class);
		xStream.addImplicitCollection(ParameterConfiguration.class, "checks", Check.class);

		xStream.alias("constructor", ConstructorConfiguration.class);
		xStream.addImplicitCollection(ConstructorConfiguration.class, "parametersConfig",
				ParameterConfiguration.class);

		xStream.alias("method", MethodConfiguration.class);
		xStream.addImplicitCollection(MethodConfiguration.class, "parametersConfig",
				ParameterConfiguration.class);
	}

	/**
	 * @param valConfiguration the oValConfiguration to set
	 */
	public void setOValConfiguration(final OValConfiguration valConfiguration)
	{
		oValConfiguration = valConfiguration;
	}

	public String toXML()
	{
		return xStream.toXML(oValConfiguration);
	}

	public void toXML(final OutputStream out)
	{
		xStream.toXML(oValConfiguration, out);
	}

	public void toXML(final Writer out)
	{
		xStream.toXML(oValConfiguration, out);
	}
}
