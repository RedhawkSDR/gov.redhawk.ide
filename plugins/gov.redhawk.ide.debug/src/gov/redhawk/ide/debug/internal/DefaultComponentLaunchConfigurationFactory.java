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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.AbstractLaunchConfigurationFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;

import java.io.File;

import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

@SuppressWarnings("restriction")
public class DefaultComponentLaunchConfigurationFactory extends AbstractLaunchConfigurationFactory {

	@Override
	public boolean supports(final SoftPkg spd, final String implId) {
		CodeFileType type = spd.getImplementation().get(0).getCode().getType();
		return CodeFileType.EXECUTABLE.equals(type);
	}

	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final String implId, final SoftPkg spd) throws CoreException {
		final ILaunchConfigurationWorkingCopy retVal = super.createLaunchConfiguration(name, implId, spd);
		final URI spdUri = EcoreUtil.getURI(spd);
		final File file = getFile(spdUri);
		if (file == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Could not resolve local file to launch for " + spdUri, null));
		}

		final Implementation impl = spd.getImplementation(implId);
		if (impl == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "No implementation of ID: " + implId + " for spd file " + file, null));
		}
		if (!impl.isExecutable()) {
			String msg = String.format("Implementation %2$s for %1$s cannot be executed", file, impl);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, null));
		}
		File exec = new File(file.getParent(), impl.getCode().getEntryPoint());
		if (!exec.canExecute()) {
			exec.setExecutable(true);
		}
		final String location = exec.toString();

		final String wd = file.getParent();
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_SCOPE, "${none}");

		retVal.setAttribute(IExternalToolConstants.ATTR_LOCATION, location);
		retVal.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, wd);
		//		retVal.setAttribute(ILaunchManager.ATTR_PRIVATE, true);
		return retVal;
	}

	private File getFile(final URI spdUri) throws CoreException {
		if (spdUri.isFile()) {
			return new File(spdUri.toFileString());
		} else if (spdUri.isPlatform()) {
			final URI uri = CommonPlugin.resolve(spdUri);
			final IFileStore store = EFS.getStore(java.net.URI.create(uri.toString()));
			return store.toLocalFile(0, null);
		} else {
			final IFileStore store = EFS.getStore(java.net.URI.create(spdUri.toString()));
			return store.toLocalFile(0, null);
		}
	}

	@Override
	public void setProgramArguments(final String progArgs, final ILaunchConfigurationWorkingCopy config) throws CoreException {
		config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, progArgs);
	}

}
