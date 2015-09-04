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
package gov.redhawk.ide.spd.generator.newcomponent;

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
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwarecomponent PUBLIC \"-//JTRS//DTD SCA V2.2.2 SCD//EN\" \"softwarecomponent.dtd\">" + NL + "<softwarecomponent>" + NL + "  <corbaversion>2.2</corbaversion>" + NL + "  <componentrepid repid=\"IDL:CF/Resource:1.0\"/>" + NL + "  <componenttype>resource</componenttype>" + NL + "  <componentfeatures>" + NL + "    <supportsinterface repid=\"IDL:CF/Resource:1.0\" supportsname=\"Resource\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LifeCycle:1.0\" supportsname=\"LifeCycle\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PortSupplier:1.0\" supportsname=\"PortSupplier\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PropertySet:1.0\" supportsname=\"PropertySet\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/TestableObject:1.0\" supportsname=\"TestableObject\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/Logging:1.0\" supportsname=\"Logging\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LogEventConsumer:1.0\" supportsname=\"LogEventConsumer\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LogConfiguration:1.0\" supportsname=\"LogConfiguration\"/>" + NL + "    <ports/>" + NL + "  </componentfeatures>" + NL + "  <interfaces>" + NL + "    <interface name=\"Resource\" repid=\"IDL:CF/Resource:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/LifeCycle:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/PortSupplier:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/PropertySet:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/TestableObject:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/Logging:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"LifeCycle\" repid=\"IDL:CF/LifeCycle:1.0\"/>" + NL + "    <interface name=\"PortSupplier\" repid=\"IDL:CF/PortSupplier:1.0\"/>" + NL + "    <interface name=\"PropertySet\" repid=\"IDL:CF/PropertySet:1.0\"/>" + NL + "    <interface name=\"TestableObject\" repid=\"IDL:CF/TestableObject:1.0\"/>" + NL + "    <interface name=\"Logging\" repid=\"IDL:CF/Logging:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/LogEventConsumer:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/LogConfiguration:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"LogEventConsumer\" repid=\"IDL:CF/LogEventConsumer:1.0\"/>" + NL + "    <interface name=\"LogConfiguration\" repid=\"IDL:CF/LogConfiguration:1.0\"/>" + NL + "  </interfaces>" + NL + "</softwarecomponent>";
  protected final String TEXT_2 = NL;

    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE