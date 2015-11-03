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
 
import gov.redhawk.ide.dcd.generator.newdevice.GeneratorArgs;
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
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwarecomponent PUBLIC \"-//JTRS//DTD SCA V2.2.2 SCD//EN\" \"softwarecomponent.dtd\">" + NL + "<softwarecomponent>" + NL + "  <corbaversion>2.2</corbaversion>";
  protected final String TEXT_2 = NL + "  <componentrepid repid=\"IDL:CF/ExecutableDevice:1.0\"/>" + NL + "  <componenttype>executabledevice</componenttype>";
  protected final String TEXT_3 = NL + "  <componentrepid repid=\"IDL:CF/LoadableDevice:1.0\"/>" + NL + "  <componenttype>loadabledevice</componenttype>";
  protected final String TEXT_4 = NL + "  <componentrepid repid=\"IDL:CF/Device:1.0\"/>" + NL + "  <componenttype>device</componenttype>";
  protected final String TEXT_5 = NL + "  <componentfeatures>";
  protected final String TEXT_6 = NL + "    <supportsinterface repid=\"IDL:CF/ExecutableDevice:1.0\" supportsname=\"ExecutableDevice\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LoadableDevice:1.0\" supportsname=\"LoadableDevice\"/>";
  protected final String TEXT_7 = NL + "    <supportsinterface repid=\"IDL:CF/LoadableDevice:1.0\" supportsname=\"LoadableDevice\"/>";
  protected final String TEXT_8 = NL + "    <supportsinterface repid=\"IDL:CF/AggregateDevice:1.0\" supportsname=\"AggregateDevice\"/>";
  protected final String TEXT_9 = NL + "    <supportsinterface repid=\"IDL:CF/Device:1.0\" supportsname=\"Device\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/Resource:1.0\" supportsname=\"Resource\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LifeCycle:1.0\" supportsname=\"LifeCycle\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/TestableObject:1.0\" supportsname=\"TestableObject\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PropertyEmitter:1.0\" supportsname=\"PropertyEmitter\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PropertySet:1.0\" supportsname=\"PropertySet\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PortSet:1.0\" supportsname=\"PortSet\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/PortSupplier:1.0\" supportsname=\"PortSupplier\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/Logging:1.0\" supportsname=\"Logging\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LogEventConsumer:1.0\" supportsname=\"LogEventConsumer\"/>" + NL + "    <supportsinterface repid=\"IDL:CF/LogConfiguration:1.0\" supportsname=\"LogConfiguration\"/>" + NL + "    <ports/>" + NL + "  </componentfeatures>" + NL + "  <interfaces>";
  protected final String TEXT_10 = NL + "    <interface name=\"ExecutableDevice\" repid=\"IDL:CF/ExecutableDevice:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/LoadableDevice:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"LoadableDevice\" repid=\"IDL:CF/LoadableDevice:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/Device:1.0\"/>" + NL + "    </interface>";
  protected final String TEXT_11 = NL + "    <interface name=\"LoadableDevice\" repid=\"IDL:CF/LoadableDevice:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/Device:1.0\"/>" + NL + "    </interface>";
  protected final String TEXT_12 = NL + "    <interface name=\"Device\" repid=\"IDL:CF/Device:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/Resource:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"Resource\" repid=\"IDL:CF/Resource:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/LifeCycle:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/TestableObject:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/PropertyEmitter:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/PortSet:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/Logging:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"LifeCycle\" repid=\"IDL:CF/LifeCycle:1.0\"/>" + NL + "    <interface name=\"TestableObject\" repid=\"IDL:CF/TestableObject:1.0\"/>" + NL + "    <interface name=\"PropertyEmitter\" repid=\"IDL:CF/PropertyEmitter:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/PropertySet:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"PropertySet\" repid=\"IDL:CF/PropertySet:1.0\"/>" + NL + "    <interface name=\"PortSet\" repid=\"IDL:CF/PortSet:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/PortSupplier:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"PortSupplier\" repid=\"IDL:CF/PortSupplier:1.0\"/>" + NL + "    <interface name=\"Logging\" repid=\"IDL:CF/Logging:1.0\">" + NL + "      <inheritsinterface repid=\"IDL:CF/LogEventConsumer:1.0\"/>" + NL + "      <inheritsinterface repid=\"IDL:CF/LogConfiguration:1.0\"/>" + NL + "    </interface>" + NL + "    <interface name=\"LogEventConsumer\" repid=\"IDL:CF/LogEventConsumer:1.0\"/>" + NL + "    <interface name=\"LogConfiguration\" repid=\"IDL:CF/LogConfiguration:1.0\"/>";
  protected final String TEXT_13 = NL + "    <interface name=\"AggregateDevice\" repid=\"IDL:CF/AggregateDevice:1.0\"/>";
  protected final String TEXT_14 = NL + "  </interfaces>" + NL + "</softwarecomponent>";
  protected final String TEXT_15 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     GeneratorArgs args = (GeneratorArgs)argument; 
    stringBuffer.append(TEXT_1);
    
  if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE)) {

    stringBuffer.append(TEXT_2);
    
  } else if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.LOADABLE_DEVICE)) {

    stringBuffer.append(TEXT_3);
    
  } else if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.DEVICE)) {

    stringBuffer.append(TEXT_4);
    
  }

    stringBuffer.append(TEXT_5);
    
  if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE)) {

    stringBuffer.append(TEXT_6);
    
  } else if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.LOADABLE_DEVICE)) {

    stringBuffer.append(TEXT_7);
    
  }
  if (args.isAggregateDevice()) {

    stringBuffer.append(TEXT_8);
    
  }

    stringBuffer.append(TEXT_9);
    
  if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE)) {

    stringBuffer.append(TEXT_10);
    
  } else if (args.getDeviceType().equals(RedhawkIdePreferenceConstants.LOADABLE_DEVICE)) {

    stringBuffer.append(TEXT_11);
    
  }

    stringBuffer.append(TEXT_12);
    
  if (args.isAggregateDevice()) {

    stringBuffer.append(TEXT_13);
    
  }

    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE