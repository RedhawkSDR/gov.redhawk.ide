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
 
public class ScdFileTemplate
{
  protected static String nl;
  public static synchronized ScdFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ScdFileTemplate result = new ScdFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwarecomponent PUBLIC \"-//JTRS//DTD SCA V2.2.2 SCD//EN\" \"softwarecomponent.dtd\">" + NL + "<softwarecomponent>" + NL + "    <corbaversion>2.2</corbaversion>" + NL + "    <componentrepid repid=\"IDL:CF/DeviceManager:1.0\" />" + NL + "    <componenttype>devicemanager</componenttype>" + NL + "    <componentfeatures> " + NL + "\t\t<supportsinterface repid=\"IDL:CF/DeviceManager:1.0\" supportsname=\"DeviceManager\" />" + NL + "        <supportsinterface repid=\"IDL:CF/PropertySet:1.0\" supportsname=\"PropertySet\" />" + NL + "\t    <ports/>" + NL + "    </componentfeatures>" + NL + "    <interfaces>" + NL + "\t\t<interface repid=\"IDL:CF/DeviceManager:1.0\" name=\"DeviceManager\">" + NL + "        \t<inheritsinterface repid=\"IDL:CF/PropertySet:1.0\"/>" + NL + "        </interface>" + NL + "        <interface repid=\"IDL:CF/PropertySet:1.0\" name=\"PropertySet\"/>" + NL + "    </interfaces>" + NL + "</softwarecomponent>";

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE