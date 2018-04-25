/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
// BEGIN GENERATED CODE
package gov.redhawk.ide.codegen.jet;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import org.eclipse.core.runtime.CoreException;

/**
 * @generated
 */
public class NodeAdminServiceConfigIni
{

  protected static String nl;
  public static synchronized NodeAdminServiceConfigIni create(String lineSeparator)
  {
    nl = lineSeparator;
    NodeAdminServiceConfigIni result = new NodeAdminServiceConfigIni();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
  protected final String TEXT_2 = NL + "; ";
  protected final String TEXT_3 = NL + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + NL + NL;
  protected final String TEXT_4 = "[node:";
  protected final String TEXT_5 = "]" + NL + "" + NL + ";;;;;; Required configuration" + NL + "" + NL + "; Override the domain name the node will register with" + NL + "DOMAIN_NAME=";
  protected final String TEXT_6 = NL + NL + "; Specify which node is being launched" + NL + "NODE_NAME=";
  protected final String TEXT_7 = NL + ";DCD_FILE=nodes/";
  protected final String TEXT_8 = "/DeviceManager.dcd.xml" + NL + "" + NL + ";;;;;; Optional configuration" + NL + "" + NL + "; Enable/disable this configuration" + NL + ";enable=True" + NL + "" + NL + "; Specify the log level" + NL + "; TRACE, DEBUG, INFO, WARN, ERROR, FATAL" + NL + ";DEBUG_LEVEL=INFO" + NL + "" + NL + "; Specify a URI for a logging configuration" + NL + ";LOGGING_CONFIG_URI=sca:///nodes/";
  protected final String TEXT_9 = "/";
  protected final String TEXT_10 = ".log4j";
  protected final String TEXT_11 = NL;

  public String generate(Object argument) throws CoreException
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    DcdTemplateParameter params = (DcdTemplateParameter) argument;

    String name = params.getDcd().getName();
    String baseName;
    if (name.indexOf('.') == -1) {
        baseName = name;
    } else {
        baseName = name.substring(name.lastIndexOf('.') + 1);
    }
    String nameWithSlashes = name.replace('.', '/');

    String nameServiceName = ScaEcoreUtils.getFeature(params.getDcd(),
         DcdPackage.Literals.DEVICE_CONFIGURATION__DOMAIN_MANAGER, DcdPackage.Literals.DOMAIN_MANAGER__NAMING_SERVICE,
         PartitioningPackage.Literals.NAMING_SERVICE__NAME);
    String domainName;
    if (nameServiceName == null) {
        domainName = "REDHAWK_DEV";
    } else {
        String[] segments = nameServiceName.split("/");
        domainName = segments[segments.length - 1];
    }

    if (params.getHeaderContent() != null) {

    stringBuffer.append(TEXT_1);
    
        for (String line : params.getHeaderContent().split("\n")) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(line);
    
        }

    stringBuffer.append(TEXT_3);
    
    }

    stringBuffer.append(TEXT_4);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(domainName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(nameWithSlashes);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(nameWithSlashes);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(baseName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}

// END GENERATED CODE