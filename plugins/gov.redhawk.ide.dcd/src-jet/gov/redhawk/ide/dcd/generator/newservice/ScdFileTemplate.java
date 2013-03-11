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
package gov.redhawk.ide.dcd.generator.newservice;
 
import gov.redhawk.ide.dcd.generator.newservice.GeneratorArgs;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;

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
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwarecomponent PUBLIC \"-//JTRS//DTD SCA V2.2.2 SCD//EN\" \"softwarecomponent.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<softwarecomponent>" + NL + "  <corbaversion>2.2</corbaversion>" + NL + "  <componentrepid repid=\"";
  protected final String TEXT_2 = "\"/>" + NL + "  <componenttype>service</componenttype>" + NL + "  <componentfeatures>" + NL + "    <supportsinterface repid=\"";
  protected final String TEXT_3 = "\" supportsname=\"";
  protected final String TEXT_4 = "\"/>" + NL + "    <ports/>" + NL + "  </componentfeatures>" + NL + "  <interfaces>" + NL + "      <interface name=\"";
  protected final String TEXT_5 = "\" repid=\"";
  protected final String TEXT_6 = "\"/>" + NL + "  </interfaces>" + NL + "</softwarecomponent>";

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     GeneratorArgs args = (GeneratorArgs)argument; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getProperty("service_repid"));
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getProperty("service_repid"));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(gov.redhawk.ide.dcd.RepIdHelper.getProperInterfaceName(args.getProperty("service_repid")));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(gov.redhawk.ide.dcd.RepIdHelper.getProperInterfaceName(args.getProperty("service_repid")));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(args.getProperty("service_repid"));
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE