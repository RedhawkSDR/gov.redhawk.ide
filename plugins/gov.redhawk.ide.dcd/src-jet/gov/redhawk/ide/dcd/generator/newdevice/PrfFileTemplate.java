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
package gov.redhawk.ide.dcd.generator.newdevice;
 
public class PrfFileTemplate
{
  protected static String nl;
  public static synchronized PrfFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    PrfFileTemplate result = new PrfFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE properties PUBLIC \"-//JTRS//DTD SCA V2.2.2 PRF//EN\" \"properties.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<properties>" + NL + "  <simple id=\"DCE:cdc5ee18-7ceb-4ae6-bf4c-31f983179b4d\" mode=\"readonly\" name=\"device_kind\" type=\"string\">" + NL + "    <description>This specifies the device kind</description>" + NL + "    <kind kindtype=\"configure\"/>" + NL + "    <kind kindtype=\"allocation\"/>" + NL + "    <action type=\"eq\"/>" + NL + "  </simple>" + NL + "  <simple id=\"DCE:0f99b2e4-9903-4631-9846-ff349d18ecfb\" mode=\"readonly\" name=\"device_model\" type=\"string\">" + NL + "    <description> This specifies the specific device</description>" + NL + "    <kind kindtype=\"configure\"/>" + NL + "    <kind kindtype=\"allocation\"/>" + NL + "    <action type=\"eq\"/>" + NL + "  </simple>" + NL + "</properties>" + NL;
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