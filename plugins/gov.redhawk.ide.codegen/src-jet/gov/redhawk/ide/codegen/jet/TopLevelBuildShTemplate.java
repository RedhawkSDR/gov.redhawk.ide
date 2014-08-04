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
package gov.redhawk.ide.codegen.jet;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @generated
 */
public class TopLevelBuildShTemplate
{

  protected static String nl;
  public static synchronized TopLevelBuildShTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    TopLevelBuildShTemplate result = new TopLevelBuildShTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "#!/bin/sh" + NL + "" + NL + "if [ \"$1\" = \"rpm\" ]; then" + NL + "    # A very simplistic RPM build scenario" + NL + "    if [ -e ";
  protected final String TEXT_2 = ".spec ]; then" + NL + "        mydir=`dirname $0`" + NL + "        tmpdir=`mktemp -d`" + NL + "        cp -r ${mydir} ${tmpdir}/";
  protected final String TEXT_3 = "-";
  protected final String TEXT_4 = NL + "        tar czf ${tmpdir}/";
  protected final String TEXT_5 = "-";
  protected final String TEXT_6 = ".tar.gz --exclude=\".svn\" -C ${tmpdir} ";
  protected final String TEXT_7 = "-";
  protected final String TEXT_8 = NL + "        rpmbuild -ta ${tmpdir}/";
  protected final String TEXT_9 = "-";
  protected final String TEXT_10 = ".tar.gz" + NL + "        rm -rf $tmpdir" + NL + "    else" + NL + "        echo \"Missing RPM spec file in\" `pwd`" + NL + "        exit 1" + NL + "    fi" + NL + "else" + NL + "    for impl in";
  protected final String TEXT_11 = " ";
  protected final String TEXT_12 = " ; do" + NL + "        cd $impl" + NL + "        if [ -e build.sh ]; then" + NL + "            ./build.sh $*" + NL + "        elif [ -e reconf ]; then" + NL + "            ./reconf && ./configure && make $*" + NL + "        else" + NL + "            echo \"No build.sh found for $impl\"" + NL + "        fi" + NL + "        cd -" + NL + "    done" + NL + "fi";
  protected final String TEXT_13 = NL;

  public String generate(Object argument) throws CoreException
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    final SoftPkg softPkg = (SoftPkg) argument;
    final String name = softPkg.getName();
    final String version;
    if (softPkg.getVersion() != null && softPkg.getVersion().trim().length() != 0) {
        version = softPkg.getVersion();
    } else {
        version = "1.0.0";
    }
    
    final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
    if (waveDev == null) {
        throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unable to find settings for " + softPkg.getName()));
    }

    stringBuffer.append(TEXT_1);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(version);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(version);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(version);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(version);
    stringBuffer.append(TEXT_10);
    

    for (final Implementation impl : softPkg.getImplementation()) {
        final ImplementationSettings implSettings = waveDev.getImplSettings().get(impl.getId());
        
    stringBuffer.append(TEXT_11);
    stringBuffer.append(implSettings.getOutputDir());
    
    }


    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}

// END GENERATED CODE