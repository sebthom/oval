/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.configuration.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.DomReader;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

/**
 * @author Sebastian Thomschke
 */
public class XIncludeAwareDOMDriver extends AbstractDriver {

   private static final NameCoder NAME_CODER = new XmlFriendlyNameCoder();

   public XIncludeAwareDOMDriver() {
      super(NAME_CODER);
   }

   @Override
   public HierarchicalStreamReader createReader(final File in) {
      return createReader(new InputSource(in.toURI().toASCIIString()));
   }

   private HierarchicalStreamReader createReader(final InputSource source) {
      try {
         final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         docBuilderFactory.setCoalescing(true);
         docBuilderFactory.setExpandEntityReferences(true);
         docBuilderFactory.setIgnoringComments(false);
         docBuilderFactory.setNamespaceAware(true);
         docBuilderFactory.setXIncludeAware(true); // same as docBuilderFactory.setFeature("http://apache.org/xml/features/xinclude", true);
         // http://xerces.apache.org/xerces2-j/features.html#xinclude.fixup-base-uris
         docBuilderFactory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
         docBuilderFactory.setValidating(false); // no schema validation (is done by XStream)
         docBuilderFactory.setSchema(null);

         /*
          * XXE protection
          */
         docBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
         // http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
         docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
         // http://xerces.apache.org/xerces2-j/features.html#external-general-entities
         docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
         // http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
         docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

         final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         docBuilder.setEntityResolver((schemaId, schemaLocation) -> {
            final String path = schemaLocation.toLowerCase().replace('\\', '/');
            if (!path.endsWith(".xml") || path.startsWith("file:///c:/windows/") || path.startsWith("file://etc/"))
               throw new SAXException("Referencing entity [" + schemaLocation + "] is not allowed!");
            return null;
         });

         final Document doc = docBuilder.parse(source);
         return new DomReader(doc, NAME_CODER);
      } catch (final FactoryConfigurationError ex) {
         throw new StreamException(ex);
      } catch (final Exception ex) {
         throw new StreamException(ex);
      }
   }

   @Override
   public HierarchicalStreamReader createReader(final InputStream in) {
      return createReader(new InputSource(in));
   }

   @Override
   public HierarchicalStreamReader createReader(final Reader in) {
      return createReader(new InputSource(in));
   }

   @Override
   public HierarchicalStreamReader createReader(final URL in) {
      return createReader(new InputSource(in.toExternalForm()));
   }

   @Override
   public HierarchicalStreamWriter createWriter(final OutputStream out) {
      return createWriter(new OutputStreamWriter(out));
   }

   @Override
   public HierarchicalStreamWriter createWriter(final Writer out) {
      return new PrettyPrintWriter(out, NAME_CODER);
   }
}
