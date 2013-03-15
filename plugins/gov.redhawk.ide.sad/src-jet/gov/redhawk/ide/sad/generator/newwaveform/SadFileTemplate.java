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
package gov.redhawk.ide.sad.generator.newwaveform;

import gov.redhawk.ide.sad.generator.newwaveform.GeneratorArgs;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import java.util.UUID;
import java.util.HashMap;

public class SadFileTemplate
{
  protected static String nl;
  public static synchronized SadFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    SadFileTemplate result = new SadFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwareassembly PUBLIC \"-//JTRS//DTD SCA V2.2.2 SAD//EN\" \"softwareassembly.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<softwareassembly name=\"";
  protected final String TEXT_2 = "\" id=\"";
  protected final String TEXT_3 = "\">" + NL + "    <componentfiles>";
  protected final String TEXT_4 = NL + "\t\t<componentfile type=\"SPD\" id=\"";
  protected final String TEXT_5 = "\">" + NL + "\t\t\t<localfile name=\"";
  protected final String TEXT_6 = "\">" + NL + "\t\t\t</localfile>" + NL + "\t\t</componentfile>";
  protected final String TEXT_7 = NL + "    </componentfiles>" + NL + "    <partitioning>";
  protected final String TEXT_8 = NL + "\t<componentplacement>" + NL + "\t\t<componentfileref refid=\"";
  protected final String TEXT_9 = "\">" + NL + "\t\t</componentfileref>" + NL + "\t\t<componentinstantiation id=\"";
  protected final String TEXT_10 = "\">" + NL + "\t\t\t<usagename>";
  protected final String TEXT_11 = "_1</usagename>" + NL + "\t\t\t<findcomponent>" + NL + "\t\t\t\t<namingservice name=\"";
  protected final String TEXT_12 = "_1\"/>" + NL + "\t\t\t</findcomponent>" + NL + "\t\t</componentinstantiation>" + NL + "\t</componentplacement>";
  protected final String TEXT_13 = NL + "    </partitioning>" + NL + "    <assemblycontroller>";
  protected final String TEXT_14 = NL + "\t\t<componentinstantiationref refid=\"";
  protected final String TEXT_15 = "\"/>";
  protected final String TEXT_16 = NL + "    </assemblycontroller>" + NL + "</softwareassembly>";
  protected final String TEXT_17 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
	GeneratorArgs args = (GeneratorArgs)argument;
	SoftPkg assemblyController = null;
	HashMap<SoftPkg, String> comToId = new HashMap<SoftPkg, String>();
	String assemblyId = null;

	if (args.getAssemblyController() != null) {
		assemblyController = args.getAssemblyController();
		comToId.put(assemblyController, assemblyController.getName() + "_" + UUID.randomUUID());
		assemblyId = DceUuidUtil.createDceUUID();
	}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getWaveformName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getWaveformId());
    stringBuffer.append(TEXT_3);
    
	if (assemblyController != null) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(comToId.get(assemblyController));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(assemblyController.eResource().getURI().path());
    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_7);
    
	if (assemblyController != null) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(comToId.get(assemblyController));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(assemblyId);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(assemblyController.getName());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(assemblyController.getName());
    stringBuffer.append(TEXT_12);
    
	}

    stringBuffer.append(TEXT_13);
    
	if (assemblyController != null) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(assemblyId);
    stringBuffer.append(TEXT_15);
    
	}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(TEXT_17);
    return stringBuffer.toString();
  }
}
