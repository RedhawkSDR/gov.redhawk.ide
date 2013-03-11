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

public class SpdFileTemplate
{
  protected static String nl;
  public static synchronized SpdFileTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    SpdFileTemplate result = new SpdFileTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<!DOCTYPE softpkg PUBLIC \"-//JTRS//DTD SCA V2.2.2 SPD//EN\" \"softpkg.dtd\">" + NL + "<!-- Created with REDHAWK IDE-->" + NL + "<!-- Powered by Eclipse -->" + NL + "<softpkg type=\"sca_compliant\" name=\"";
  protected final String TEXT_2 = "\" id=\"";
  protected final String TEXT_3 = "\">" + NL + "    <title/>" + NL + "    <author>" + NL + "        <name>";
  protected final String TEXT_4 = "</name>" + NL + "    </author>" + NL + "    <descriptor>" + NL + "        <localfile name=\"DeviceManager.scd.xml\"/>" + NL + "    </descriptor>" + NL + "\t<propertyfile type=\"PRF\">" + NL + "       <localfile name=\"DeviceManager.prf.xml\"/>" + NL + "\t</propertyfile>" + NL + "    <implementation id=\"";
  protected final String TEXT_5 = "\">" + NL + "\t\t<propertyfile type=\"PRF\">" + NL + "\t\t\t<localfile name=\"DeviceManager_Linux_i686.prf.xml\"/>" + NL + "\t\t</propertyfile>" + NL + "    \t<description>Implementation of a Device Manager</description>" + NL + "\t    <processor name=\"x86\"/>" + NL + "\t    <os name=\"Linux\"/>" + NL + "\t    <code type=\"NodeBooter\">" + NL + "\t    \t<localfile name=\"nodeBooter\"/>" + NL + "\t    \t<entrypoint>nodeBooter</entrypoint>" + NL + "\t    </code>" + NL + "    </implementation>" + NL + "</softpkg>";
  protected final String TEXT_6 = NL;

    /**
     * {@inheritDoc}
     */
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     GeneratorArgs args = (GeneratorArgs)argument; 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(args.getProjectName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append(args.getProjectId());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(args.getAuthorName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(DceUuidUtil.createDceUUID());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
// END GENERATED CODE