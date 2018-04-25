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

import org.eclipse.core.runtime.CoreException;

/**
 * @generated
 */
public class WaveformAdminServiceConfigIni
{

  protected static String nl;
  public static synchronized WaveformAdminServiceConfigIni create(String lineSeparator)
  {
    nl = lineSeparator;
    WaveformAdminServiceConfigIni result = new WaveformAdminServiceConfigIni();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;";
  protected final String TEXT_2 = NL + "; ";
  protected final String TEXT_3 = NL + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + NL + NL;
  protected final String TEXT_4 = "; You can add additional sections to start more instances of this waveform" + NL + "" + NL + "[waveform:";
  protected final String TEXT_5 = "_1]" + NL + "" + NL + ";;;;;; Required configuration" + NL + "" + NL + "; The domain the waveform will be launched in" + NL + "DOMAIN_NAME=REDHAWK_DEV" + NL + "" + NL + "; Specify which waveform is being launched" + NL + "WAVEFORM=";
  protected final String TEXT_6 = NL + NL + ";;;;;; Optional configuration" + NL + "" + NL + "; Enable/disable this configuration" + NL + ";enable=True" + NL + "" + NL + "; Specify the log level" + NL + "; TRACE, DEBUG, INFO, WARN, ERROR, FATAL" + NL + ";DEBUG_LEVEL=INFO" + NL + "" + NL + "; Specify a URI for a logging configuration" + NL + ";LOGGING_CONFIG_URI=sca:///waveforms/";
  protected final String TEXT_7 = "/";
  protected final String TEXT_8 = ".log4j";
  protected final String TEXT_9 = NL;

  public String generate(Object argument) throws CoreException
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    SadTemplateParameter params = (SadTemplateParameter) argument;

    String name = params.getSad().getName();
    String baseName;
    if (name.indexOf('.') == -1) {
        baseName = name;
    } else {
        baseName = name.substring(name.lastIndexOf('.') + 1);
    }
    String nameWithSlashes = name.replace('.', '/');

    if (params.getHeaderContent() != null) {

    stringBuffer.append(TEXT_1);
    
        for (String line : params.getHeaderContent().split("\n")) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(line);
    
        }

    stringBuffer.append(TEXT_3);
    
    }

    stringBuffer.append(TEXT_4);
    stringBuffer.append(baseName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(nameWithSlashes);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(baseName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}

// END GENERATED CODE