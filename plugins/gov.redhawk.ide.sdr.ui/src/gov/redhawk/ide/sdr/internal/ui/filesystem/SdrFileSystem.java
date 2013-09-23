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
package gov.redhawk.ide.sdr.internal.ui.filesystem;

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.sca.ui.ScaUiPlugin;

import java.net.URI;

import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * 
 */
public class SdrFileSystem extends FileSystem implements IExecutableExtension {

	private IFileStore rootStore;

	/**
	 * 
	 */
	public SdrFileSystem() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFileStore getStore(final URI uri) {
		return new SdrWrappedFileStore(uri, this.rootStore.getFileStore(new Path(uri.getPath())));
	}

	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		final String scheme = config.getAttribute("scheme");
		IPath path = null;
		if (ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV.equals(scheme)) {
			path = SdrUiPlugin.getDefault().getTargetSdrDevPath();
		} else if (ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM.equals(scheme)) {
			path = SdrUiPlugin.getDefault().getTargetSdrDomPath();
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ScaUiPlugin.PLUGIN_ID, "Invalid SDR Filesystem scheme: " + scheme, null));
		}
		if (path != null) {
			this.rootStore = EFS.getStore(path.toFile().toURI());
		} else {
			this.rootStore = EFS.getNullFileSystem().getStore(new Path(""));
		}
	}

}
