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
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE deviceconfiguration PUBLIC \"-//JTRS//DTD SCA V2.2.2 DCD//EN\" \"deviceconfiguration.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<deviceconfiguration name=\"";
  protected final String TEXT_2 = "\" id=\"";
  protected final String TEXT_3 = "\">" + NL + "    <devicemanagersoftpkg>" + NL + "    \t<localfile name=\"/mgr/DeviceManager.spd.xml\">" + NL + "    \t</localfile>" + NL + "    </devicemanagersoftpkg>" + NL + "    <componentfiles>";
  protected final String TEXT_4 = NL + "    \t<componentfile type=\"SPD\" id=\"";
  protected final String TEXT_5 = "\">" + NL + "    \t\t<localfile name=\"";
  protected final String TEXT_6 = "\">" + NL + "    \t\t</localfile>" + NL + "    \t</componentfile>";
  protected final String TEXT_7 = NL + "    </componentfiles>" + NL + "    <partitioning>";
  protected final String TEXT_8 = NL + "\t\t<componentplacement>" + NL + "\t    \t<componentfileref refid=\"";
  protected final String TEXT_9 = "\">" + NL + "    \t\t</componentfileref>" + NL + "\t     \t<componentinstantiation id=\"";
  protected final String TEXT_10 = "\">" + NL + "\t     \t\t<usagename>";
  protected final String TEXT_11 = "_";
  protected final String TEXT_12 = "</usagename>" + NL + "\t     \t</componentinstantiation>" + NL + "    \t</componentplacement>";
  protected final String TEXT_13 = NL + "    </partitioning>" + NL + "    <domainmanager>" + NL + "    \t<namingservice name=\"";
  protected final String TEXT_14 = "/";
  protected final String TEXT_15 = "\">" + NL + "    \t</namingservice>" + NL + "    </domainmanager>" + NL + "</deviceconfiguration>";
  protected final String TEXT_16 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    GeneratorArgs args = (GeneratorArgs)argument;
    List<String> deviceList = new ArrayList<String>();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getProjectName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getProjectId());
    stringBuffer.append(TEXT_3);
    
    HashMap<SoftPkg, String> devToId = new HashMap<SoftPkg, String>(); 
    for (SoftPkg device : args.getDevices()) { 
        devToId.put(device, device.getName() + "_" + UUID.randomUUID());

    stringBuffer.append(TEXT_4);
    stringBuffer.append(devToId.get(device));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(device.eResource().getURI().path());
    stringBuffer.append(TEXT_6);
     } 
    stringBuffer.append(TEXT_7);
    
    for (SoftPkg device : args.getDevices()) {
        int devNum = 1;
        while (deviceList.contains(device.getName() + "_" + devNum)) {
            devNum++;
        }
        deviceList.add(device.getName() + "_" + devNum);

    stringBuffer.append(TEXT_8);
    stringBuffer.append(devToId.get(device));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(DceUuidUtil.createDceUUID());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(device.getName());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(devNum);
    stringBuffer.append(TEXT_12);
     } 
    stringBuffer.append(TEXT_13);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE