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
package gov.redhawk.ide.ui.tests.projectCreation.stubs;

import gov.redhawk.ide.codegen.AbstractCodeGenerator;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.IScaComponentCodegenTemplate;
import gov.redhawk.ide.codegen.ImplementationSettings;

import java.io.PrintStream;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;

/**
 * 
 */
public class StubGenerator extends AbstractCodeGenerator implements IScaComponentCodegen, IScaComponentCodegenTemplate {

	@Override
	public List<String> getExecutableFileNames(ImplementationSettings implSettings, SoftPkg softPkg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getAllGeneratedFileNames(ImplementationSettings implSettings, SoftPkg softPkg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String generateFile(String fileName, SoftPkg softPkg, ImplementationSettings implSettings, Object helperObject) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDefaultFilename(SoftPkg softPkg, ImplementationSettings implSettings, String srcDir) {
		return "helloWorld";
	}

	@Override
	public IStatus validate() {
		return Status.OK_STATUS;
	}

	@Override
	public IStatus cleanupSourceFolders(IProject project, IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

	@Override
	public IStatus generate(ImplementationSettings implSettings, Implementation impl, PrintStream out, PrintStream err, IProgressMonitor monitor,
		String[] generateFiles, boolean shouldGenerate, List<FileToCRCMap> crcMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Code getInitialCodeSettings(SoftPkg softPkg, ImplementationSettings settings, Implementation impl) {
		final Code retVal = SpdFactory.eINSTANCE.createCode();
		final LocalFile file = SpdFactory.eINSTANCE.createLocalFile();

		String outputDir = settings.getOutputDir();
		if (outputDir.length() > 0 && outputDir.charAt(0) == '/') {
			outputDir = outputDir.substring(1);
		}
		if (outputDir != null && "".equals(outputDir)) {
			outputDir = ".";
		}
		retVal.setEntryPoint(outputDir + "/hellWorld.sh");

		file.setName(outputDir);
		retVal.setLocalFile(file);
		retVal.setType(CodeFileType.EXECUTABLE);

		return retVal;
	}

	@Override
	public boolean shouldGenerate() {
		return false;
	}

	@Override
	public Version getCodegenVersion() {
		return new Version("0.0.0");
	}

}
