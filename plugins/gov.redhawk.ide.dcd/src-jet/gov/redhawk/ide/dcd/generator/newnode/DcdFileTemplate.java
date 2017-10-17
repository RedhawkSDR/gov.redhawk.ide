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
 
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.dcd.generator.newnode.GeneratorArgs;
import CF.ResourceHelper;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.Interface;
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
  protected final String TEXT_10 = "\"/>" + NL;
  protected final String TEXT_11 = NL + "      <componentinstantiation id=\"";
  protected final String TEXT_12 = "\" startorder=\"";
  protected final String TEXT_13 = "\">";
  protected final String TEXT_14 = NL + "\t  <componentinstantiation id=\"";
  protected final String TEXT_15 = "\">";
  protected final String TEXT_16 = NL + "        <usagename>";
  protected final String TEXT_17 = "_";
  protected final String TEXT_18 = "</usagename>" + NL + "      </componentinstantiation>" + NL + "    </componentplacement>";
  protected final String TEXT_19 = NL + "  </partitioning>";
  protected final String TEXT_20 = NL + "  <partitioning/>";
  protected final String TEXT_21 = NL + "  <domainmanager>" + NL + "    <namingservice name=\"";
  protected final String TEXT_22 = "/";
  protected final String TEXT_23 = "\"/>" + NL + "  </domainmanager>" + NL + "</deviceconfiguration>";
  protected final String TEXT_24 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    GeneratorArgs args = (GeneratorArgs)argument;
    List<String> elementList = new ArrayList<String>();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getNodeId());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getNodeName());
    stringBuffer.append(TEXT_3);
    	
	HashMap<SoftPkg, String> elementToId = new HashMap<SoftPkg, String>(); 
	if (args.getNodeElements() != null && args.getNodeElements().length > 0) { 
    stringBuffer.append(TEXT_4);
    
        for (SoftPkg softPkg : args.getNodeElements()) {
            String usageName = ProjectCreator.getBaseFileName(softPkg.getName());
            elementToId.put(softPkg, usageName + "_" + UUID.randomUUID());

    stringBuffer.append(TEXT_5);
    stringBuffer.append(elementToId.get(softPkg));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(softPkg.eResource().getURI().path());
    stringBuffer.append(TEXT_7);
    
        }

    stringBuffer.append(TEXT_8);
    
		int tmpStartOrder = 0;
        for (SoftPkg softPkg : args.getNodeElements()) {
            int elementNum = 1;
            
            Interface tmpInterface = ScdFactory.eINSTANCE.createInterface();
			tmpInterface.setRepid(ResourceHelper.id());
            SoftwareComponent scd = softPkg.getDescriptor().getComponent();
            boolean implementsResource = false;
			for (Interface serviceInterface : scd.getInterfaces().getInterface()) {
				if (serviceInterface.isInstance(tmpInterface)) {
					implementsResource = true;
				}
			}

			Integer startOrder = null;
			if (implementsResource) {
            	startOrder = tmpStartOrder++;
			}
            String usageName = ProjectCreator.getBaseFileName(softPkg.getName());
            while (elementList.contains(usageName + "_" + elementNum)) {
                elementNum++;
            }
            elementList.add(usageName + "_" + elementNum);
            
            String compInstId = args.getNodeName() + ":" + usageName + "_" + elementNum;

    stringBuffer.append(TEXT_9);
    stringBuffer.append(elementToId.get(softPkg));
    stringBuffer.append(TEXT_10);
     			
			if (startOrder != null) {

    stringBuffer.append(TEXT_11);
    stringBuffer.append(compInstId);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(startOrder);
    stringBuffer.append(TEXT_13);
     
			} else {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(compInstId);
    stringBuffer.append(TEXT_15);
    
			}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(usageName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(elementNum);
    stringBuffer.append(TEXT_18);
    
        }

    stringBuffer.append(TEXT_19);
    
    } else {

    stringBuffer.append(TEXT_20);
    
    }

    stringBuffer.append(TEXT_21);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(args.getDomainManagerName());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE