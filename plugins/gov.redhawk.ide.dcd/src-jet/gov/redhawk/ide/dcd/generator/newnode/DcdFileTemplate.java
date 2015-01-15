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
 
import gov.redhawk.ide.dcd.generator.newnode.GeneratorArgs;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DcdFileTemplate
{
  protected static String nl;
  public static synchronized DcdFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    DcdFileTemplate result = new DcdFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE deviceconfiguration PUBLIC \"-//JTRS//DTD SCA V2.2.2 DCD//EN\" \"deviceconfiguration.dtd\">" + NL + "<deviceconfiguration id=\"";
  protected final String TEXT_2 = "\" name=\"";
  protected final String TEXT_3 = "\">" + NL + "  <devicemanagersoftpkg>" + NL + "    <localfile name=\"/mgr/DeviceManager.spd.xml\"/>" + NL + "  </devicemanagersoftpkg>";
  protected final String TEXT_4 = NL + "  <componentfiles>";
  protected final String TEXT_5 = NL + "    <componentfile id=\"";
  protected final String TEXT_6 = "\" type=\"SPD\">" + NL + "      <localfile name=\"";
  protected final String TEXT_7 = "\"/>" + NL + "    </componentfile>";
  protected final String TEXT_8 = NL + "  </componentfiles>" + NL + "  <partitioning>";
  protected final String TEXT_9 = NL + "    <componentplacement>" + NL + "      <componentfileref refid=\"";
  protected final String TEXT_10 = "\"/>" + NL + "      <componentinstantiation id=\"";
  protected final String TEXT_11 = "\">" + NL + "        <usagename>";
  protected final String TEXT_12 = "_";
  protected final String TEXT_13 = "</usagename>" + NL + "      </componentinstantiation>" + NL + "    </componentplacement>" + NL + "  </partitioning>";
  protected final String TEXT_14 = NL + "  <partitioning/>";
  protected final String TEXT_15 = NL + "  <domainmanager>" + NL + "    <namingservice name=\"";
  protected final String TEXT_16 = "/";
  protected final String TEXT_17 = "\"/>" + NL + "  </domainmanager>" + NL + "</deviceconfiguration>";
  protected final String TEXT_18 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    GeneratorArgs args = (GeneratorArgs)argument;
    List<String> deviceList = new ArrayList<String>();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getNodeId());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getNodeName());
    stringBuffer.append(TEXT_3);
    
	HashMap<SoftPkg, String> devToId = new HashMap<SoftPkg, String>(); 
	if (args.getDevices() != null && args.getDevices().length > 0) { 
    stringBuffer.append(TEXT_4);
    
    	for (SoftPkg device : args.getDevices()) { 
        	devToId.put(device, device.getName() + "_" + UUID.randomUUID());

    stringBuffer.append(TEXT_5);
    stringBuffer.append(devToId.get(device));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(device.eResource().getURI().path());
    stringBuffer.append(TEXT_7);
     		} 
    stringBuffer.append(TEXT_8);
    
    for (SoftPkg device : args.getDevices()) {
        int devNum = 1;
        while (deviceList.contains(device.getName() + "_" + devNum)) {
            devNum++;
        }
        deviceList.add(device.getName() + "_" + devNum);

    stringBuffer.append(TEXT_9);
    stringBuffer.append(devToId.get(device));
    stringBuffer.append(TEXT_10);
    stringBuffer.append(DceUuidUtil.createDceUUID());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(device.getName());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(devNum);
    stringBuffer.append(TEXT_13);
     }} else {
    stringBuffer.append(TEXT_14);
     } 
    stringBuffer.append(TEXT_15);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE