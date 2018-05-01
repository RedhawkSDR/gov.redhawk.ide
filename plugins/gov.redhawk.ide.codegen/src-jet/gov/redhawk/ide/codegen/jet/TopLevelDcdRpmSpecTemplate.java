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

import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import java.util.Collections;
import java.util.List;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.spd.SoftPkg;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @generated
 */
public class TopLevelDcdRpmSpecTemplate
{

  protected static String nl;
  public static synchronized TopLevelDcdRpmSpecTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    TopLevelDcdRpmSpecTemplate result = new TopLevelDcdRpmSpecTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "###############################################################################";
  protected final String TEXT_2 = NL + "# ";
  protected final String TEXT_3 = NL + "###############################################################################";
  protected final String TEXT_4 = NL + "# RPM package for ";
  protected final String TEXT_5 = NL + NL + "# By default, the RPM will install to the standard REDHAWK SDR root location (/var/redhawk/sdr)" + NL + "# You can override this at install time using --prefix /new/sdr/root when invoking rpm (preferred method, if you must)" + NL + "%{!?_sdrroot: %global _sdrroot /var/redhawk/sdr}" + NL + "%define _prefix %{_sdrroot}" + NL + "Prefix: %{_prefix}" + NL + "" + NL + "Name: ";
  protected final String TEXT_6 = NL + "Summary: Node ";
  protected final String TEXT_7 = NL + "Version: 1.0.0" + NL + "Release: 1%{?dist}" + NL + "License: None" + NL + "Group: REDHAWK/Nodes" + NL + "Source: %{name}-%{version}.tar.gz" + NL + "# Require the device manager whose SPD is referenced" + NL + "Requires: ";
  protected final String TEXT_8 = NL + "# Require each referenced device/service" + NL + "Requires:";
  protected final String TEXT_9 = " ";
  protected final String TEXT_10 = NL + "BuildArch: noarch" + NL + "BuildRoot: %{_tmppath}/%{name}-%{version}" + NL + "" + NL + "%description";
  protected final String TEXT_11 = NL;
  protected final String TEXT_12 = NL + NL + "%prep" + NL + "%setup" + NL + "" + NL + "%install" + NL + "%__rm -rf $RPM_BUILD_ROOT" + NL + "%__mkdir_p \"$RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_13 = "\"" + NL + "%__install -m 644 ";
  protected final String TEXT_14 = " $RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_15 = "/";
  protected final String TEXT_16 = NL + "%__install -m 644 ";
  protected final String TEXT_17 = " $RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_18 = "/";
  protected final String TEXT_19 = NL + NL + "%files" + NL + "%defattr(-,redhawk,redhawk)";
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = NL + "%{_prefix}";
  protected final String TEXT_22 = "/";
  protected final String TEXT_23 = NL + "%{_prefix}";
  protected final String TEXT_24 = "/";

  public String generate(Object argument) throws CoreException
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    final DcdTemplateParameter params = (DcdTemplateParameter) argument;
    final DeviceConfiguration devCfg = params.getDcd();
    final String dcdFileName = devCfg.eResource().getURI().lastSegment();

    String devMgrName = "";
    final SoftPkg devMgrSoftPkg = devCfg.getDeviceManagerSoftPkg().getSoftPkg();
    if (devMgrSoftPkg != null) {
    	devMgrName = devMgrSoftPkg.getName();
    }
    final List<ComponentFile> componentFiles;
    if (devCfg.getComponentFiles() == null) {
    	componentFiles = Collections.emptyList();
    } else {
    	componentFiles = devCfg.getComponentFiles().getComponentFile();
    }
    if (devCfg.getName() == null || devCfg.getName().isEmpty()) {
        throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "DCD file doesn't have a name set"));
    }
    final String nodeSubDir = "/dev/nodes/" + devCfg.getName().replace('.', '/');
    final String directoryBlock = ProjectCreator.createDirectoryBlock("%dir %{_prefix}/dev/nodes/" + devCfg.getName().replace('.', '/'));

    if (params.getHeaderContent() != null) {

    stringBuffer.append(TEXT_1);
    
        for (String line : params.getHeaderContent().split("\n")) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(line);
    
        }

    stringBuffer.append(TEXT_3);
    
    }

    stringBuffer.append(TEXT_4);
    stringBuffer.append(devCfg.getName());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(devCfg.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(devCfg.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(devMgrName);
    stringBuffer.append(TEXT_8);
    
    for (ComponentFile compFile : componentFiles) {
        SoftPkg softPkg = compFile.getSoftPkg();
        if (softPkg != null) {
        
    stringBuffer.append(TEXT_9);
    stringBuffer.append(softPkg.getName());
    
        } else {
          throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unable to locate component file. Check your SAD file and Target SDR."));
        }
    }

    stringBuffer.append(TEXT_10);
    
    if (devCfg.getDescription() != null) {

    stringBuffer.append(TEXT_11);
    stringBuffer.append(devCfg.getDescription());
    
    }

    stringBuffer.append(TEXT_12);
    stringBuffer.append(nodeSubDir);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dcdFileName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(nodeSubDir);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(dcdFileName);
    
    for (String fileName : params.getFilesToInstall()) {

    stringBuffer.append(TEXT_16);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(nodeSubDir);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(fileName);
    
    }

    stringBuffer.append(TEXT_19);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(directoryBlock);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(nodeSubDir);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(dcdFileName);
    
    for (String fileName : params.getFilesToInstall()) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append(nodeSubDir);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(fileName);
    
    }

    return stringBuffer.toString();
  }
}

// END GENERATED CODE