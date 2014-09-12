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
package gov.redhawk.ide.debug;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @since 4.0
 */
public abstract class AbstractWorkspaceLaunchConfigurationFactory extends AbstractLaunchConfigurationFactory {

	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final String implId, final SoftPkg spd) throws CoreException {
		final ILaunchConfigurationWorkingCopy retVal = super.createLaunchConfiguration(name, implId, spd);

		final IFile resource = getResource(spd.eResource().getURI());
		retVal.setMappedResources(new IResource[] {
			resource.getProject()
		});

		return retVal;
	}

	@Override
	public boolean supports(final SoftPkg spd, final String implId) {
		final URI uri = EcoreUtil.getURI(spd);
		return uri.isPlatformResource();
	}

	protected IFile getResource(final URI uri) {
		final String path = uri.toPlatformString(true);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
	}
}
