/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// BEGIN GENERATED CODE
package gov.redhawk.ide.dcd.generator.newnode;
 
public class ImplPrfFileTemplate
{
  protected static String nl;
  public static synchronized ImplPrfFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ImplPrfFileTemplate result = new ImplPrfFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE properties PUBLIC \"-//JTRS//DTD SCA V2.2.2 PRF//EN\" \"properties.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<properties>" + NL + "  <description>SCA required properties describing the Operating System</description>" + NL + "  <!-- Provide a processor_name and os_name matching property -->" + NL + "  <simple mode=\"readonly\" id=\"os_name\" name=\"os_name\" type=\"string\">" + NL + "    <value>Linux</value>" + NL + "    <kind kindtype=\"allocation\"/>" + NL + "    <action type=\"eq\"/>" + NL + "  </simple>" + NL + "  <simple mode=\"readonly\" id=\"processor_name\" name=\"processor_name\" type=\"string\">" + NL + "    <value>i686</value>" + NL + "    <kind kindtype=\"allocation\"/>" + NL + "    <action type=\"eq\"/>" + NL + "  </simple>" + NL + "</properties>" + NL;
  protected final String TEXT_2 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE