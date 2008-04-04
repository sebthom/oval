<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version='1.0'>

  <xsl:import href="docbook-xsl/html/docbook.xsl"/>

  <!-- see http://docbook.sourceforge.net/release/xsl/snapshot/doc/html/ -->
  <xsl:param name="graphic.default.extension">png</xsl:param>
  <xsl:param name="use.extensions" select="1"/> 
  <xsl:param name="toc.section.depth" select="2" /> 

  <!-- Enable auto numbering of section labels -->
  <xsl:param name="section.autolabel" select="1"/>

  <!-- Enable CSS support -->

  <xsl:param name="css.decoration" select="1"/>
  <xsl:param name="html.stylesheet">css/style.css</xsl:param>
  <xsl:param name="table.borders.with.css" select="1"/>

  <!-- Turn off rules below/above header/footer - we'll use CSS borders -->

  <xsl:param name="header.rule" select="0"/>
  <xsl:param name="footer.rule" select="0"/>

  <!-- Turn off image scaling when generating HTML output -->

  <xsl:param name="make.graphic.viewport" select="1"/>
  <xsl:param name="ignore.image.scaling" select="1"/>

  <!-- Make external links stay within window (don't let them take over entire frame) -->

  <xsl:param name="ulink.target" select="'_self'"/>

  <!-- Spend extra CPU time to try and produce valid and pretty HTML -->

  <xsl:param name="html.cleanup" select="1" />
  <xsl:param name="make.valid.html" select="1"/>

</xsl:stylesheet>