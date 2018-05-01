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
import java.util.List;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @generated
 */
public class TopLevelSadRpmSpecTemplate
{

  protected static String nl;
  public static synchronized TopLevelSadRpmSpecTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    TopLevelSadRpmSpecTemplate result = new TopLevelSadRpmSpecTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "###############################################################################";
  protected final String TEXT_2 = NL + "# ";
  protected final String TEXT_3 = NL + "###############################################################################";
  protected final String TEXT_4 = NL + "# RPM package for ";
  protected final String TEXT_5 = NL + NL + "# By default, the RPM will install to the standard REDHAWK SDR root location (/var/redhawk/sdr)" + NL + "# You can override this at install time using --prefix /new/sdr/root when invoking rpm (preferred method, if you must)" + NL + "%{!?_sdrroot: %global _sdrroot /var/redhawk/sdr}" + NL + "%define _prefix %{_sdrroot}" + NL + "Prefix: %{_prefix}" + NL + "" + NL + "Name: ";
  protected final String TEXT_6 = NL + "Summary: Waveform ";
  protected final String TEXT_7 = NL + "Version: ";
  protected final String TEXT_8 = NL + "Release: 1%{?dist}" + NL + "License: None" + NL + "Group: REDHAWK/Waveforms" + NL + "Source: %{name}-%{version}.tar.gz" + NL + "# Require the controller whose SPD is referenced" + NL + "Requires: ";
  protected final String TEXT_9 = NL + "# Require each referenced component" + NL + "Requires:";
  protected final String TEXT_10 = " ";
  protected final String TEXT_11 = NL + "BuildArch: noarch" + NL + "BuildRoot: %{_tmppath}/%{name}-%{version}" + NL + "" + NL + "%description";
  protected final String TEXT_12 = NL;
  protected final String TEXT_13 = NL + NL + "%prep" + NL + "%setup" + NL + "" + NL + "%install" + NL + "%__rm -rf $RPM_BUILD_ROOT" + NL + "%__mkdir_p \"$RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_14 = "\"" + NL + "%__install -m 644 ";
  protected final String TEXT_15 = " $RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_16 = "/";
  protected final String TEXT_17 = NL + "%__install -m 644 ";
  protected final String TEXT_18 = " $RPM_BUILD_ROOT%{_prefix}";
  protected final String TEXT_19 = "/";
  protected final String TEXT_20 = NL + NL + "%files" + NL + "%defattr(-,redhawk,redhawk)";
  protected final String TEXT_21 = NL;
  protected final String TEXT_22 = NL + "%{_prefix}";
  protected final String TEXT_23 = "/";
  protected final String TEXT_24 = NL + "%{_prefix}";
  protected final String TEXT_25 = "/";
  protected final String TEXT_26 = NL;

  public String generate(Object argument) throws CoreException
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    final SadTemplateParameter params = (SadTemplateParameter) argument;
    final SoftwareAssembly sad = params.getSad();
    final String sadFileName = sad.eResource().getURI().lastSegment();

    // The assembly controller isn't specified initially after project creation; ignore if it's not specified, throw
    // an error if it is and we can't get the assembly controller
    final SadComponentInstantiation instance = SoftwareAssembly.Util.getAssemblyControllerInstantiation(sad);
    if (instance == null) {
    	return null;
    }
    final SoftPkg controller = ComponentInstantiation.Util.getSpd(instance);
    if (controller == null){
	    throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unable to get assembly controller. Check your SAD file and Target SDR."));
    }

    final List<ComponentFile> componentFiles = sad.getComponentFiles().getComponentFile();
    if (sad.getName() == null || sad.getName().isEmpty()) {
        throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "SAD file doesn't have a name set"));
    }
    final String waveformSubDir = "/dom/waveforms/" + sad.getName().replace('.', '/');
    final String directoryBlock = ProjectCreator.createDirectoryBlock("%dir %{_prefix}/dom/waveforms/" + sad.getName().replace('.', '/'));

    if (params.getHeaderContent() != null) {

    stringBuffer.append(TEXT_1);
    
        for (String line : params.getHeaderContent().split("\n")) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(line);
    
        }

    stringBuffer.append(TEXT_3);
    
    }

    stringBuffer.append(TEXT_4);
    stringBuffer.append(sad.getName());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sad.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(sad.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append((sad.getVersion() != null && sad.getVersion().trim().length() > 0) ? sad.getVersion() : "1.0.0");
    stringBuffer.append(TEXT_8);
    stringBuffer.append(controller.getName());
    stringBuffer.append(TEXT_9);
    
    for (ComponentFile compFile : componentFiles) {
        SoftPkg softPkg = compFile.getSoftPkg();
        if (softPkg != null) {
        
    stringBuffer.append(TEXT_10);
    stringBuffer.append(softPkg.getName());
    
        } else {
          throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unable to locate component file. Check your SAD file and Target SDR."));
        }
    }

    stringBuffer.append(TEXT_11);
    
    if (sad.getDescription() != null) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(sad.getDescription());
    
    }

    stringBuffer.append(TEXT_13);
    stringBuffer.append(waveformSubDir);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(sadFileName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(waveformSubDir);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sadFileName);
    
    for (String fileName : params.getFilesToInstall()) {

    stringBuffer.append(TEXT_17);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(waveformSubDir);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(fileName);
    
    }

    stringBuffer.append(TEXT_20);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(directoryBlock);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(waveformSubDir);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(sadFileName);
    
    for (String fileName : params.getFilesToInstall()) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(waveformSubDir);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(fileName);
    
    }

    stringBuffer.append(TEXT_26);
    return stringBuffer.toString();
  }
}

// END GENERATED CODE