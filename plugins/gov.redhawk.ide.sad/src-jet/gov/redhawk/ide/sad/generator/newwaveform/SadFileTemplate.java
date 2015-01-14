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
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softwareassembly PUBLIC \"-//JTRS//DTD SCA V2.2.2 SAD//EN\" \"softwareassembly.dtd\">" + NL + "<softwareassembly id=\"";
  protected final String TEXT_2 = "\" name=\"";
  protected final String TEXT_3 = "\">";
  protected final String TEXT_4 = NL + "  <componentfiles>" + NL + "    <componentfile id=\"";
  protected final String TEXT_5 = "\" type=\"SPD\">" + NL + "      <localfile name=\"";
  protected final String TEXT_6 = "\"/>" + NL + "    </componentfile>" + NL + "  </componentfiles>" + NL + "  <partitioning>" + NL + "    <componentplacement>" + NL + "      <componentfileref refid=\"";
  protected final String TEXT_7 = "\"/>" + NL + "      <componentinstantiation id=\"";
  protected final String TEXT_8 = "\">" + NL + "        <usagename>";
  protected final String TEXT_9 = "_1</usagename>" + NL + "        <findcomponent>" + NL + "          <namingservice name=\"";
  protected final String TEXT_10 = "_1\"/>" + NL + "        </findcomponent>" + NL + "      </componentinstantiation>" + NL + "    </componentplacement>" + NL + "  </partitioning>" + NL + "  <assemblycontroller>" + NL + "    <componentinstantiationref refid=\"";
  protected final String TEXT_11 = "\"/>" + NL + "  </assemblycontroller>";
  protected final String TEXT_12 = NL + "  <partitioning/>" + NL + "  <assemblycontroller/>";
  protected final String TEXT_13 = NL + "</softwareassembly>";
  protected final String TEXT_14 = NL;

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
		assemblyId = assemblyController.getName() + "_1";
	}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getWaveformId());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getWaveformName());
    stringBuffer.append(TEXT_3);
    
	if (assemblyController != null) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(comToId.get(assemblyController));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(assemblyController.eResource().getURI().path());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(comToId.get(assemblyController));
    stringBuffer.append(TEXT_7);
    stringBuffer.append(assemblyId);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(assemblyController.getName());
    stringBuffer.append(TEXT_9);
    stringBuffer.append(assemblyController.getName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(assemblyId);
    stringBuffer.append(TEXT_11);
    
} else {

    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
