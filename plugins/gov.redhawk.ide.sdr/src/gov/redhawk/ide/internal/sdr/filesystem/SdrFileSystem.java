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
package gov.redhawk.ide.internal.sdr.filesystem;

import java.net.URI;

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

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

public class SdrFileSystem extends FileSystem implements IExecutableExtension {

	private static final String OLD_SCHEME_TARGET_SDR_DEV = "sdrDev"; //$NON-NLS-1$
	private static final String OLD_SCHEME_TARGET_SDR_DOM = "sdrDom"; //$NON-NLS-1$

	private IFileStore rootStore;

	@Override
	public IFileStore getStore(final URI uri) {
		return new SdrWrappedFileStore(uri, this.rootStore.getFileStore(new Path(uri.getPath())));
	}

	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		final String scheme = config.getAttribute("scheme"); //$NON-NLS-1$
		IPath path = null;
		switch (scheme) {
		case OLD_SCHEME_TARGET_SDR_DEV:
			IdeSdrActivator.getDefault().logWarning(
				Messages.bind(Messages.SdrFileSystem_WarnDeprecatedScheme, OLD_SCHEME_TARGET_SDR_DEV, ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV));
			path = IdeSdrPreferences.getTargetSdrDevPath();
			break;
		case ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV:
			path = IdeSdrPreferences.getTargetSdrDevPath();
			break;
		case OLD_SCHEME_TARGET_SDR_DOM:
			IdeSdrActivator.getDefault().logWarning(
				Messages.bind(Messages.SdrFileSystem_WarnDeprecatedScheme, OLD_SCHEME_TARGET_SDR_DOM, ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM));
			path = IdeSdrPreferences.getTargetSdrDomPath();
			break;
		case ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM:
			path = IdeSdrPreferences.getTargetSdrDomPath();
			break;
		case ScaFileSystemConstants.SCHEME_TARGET_SDR:
			path = IdeSdrPreferences.getTargetSdrPath();
			break;
		default:
			throw new CoreException(new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, Messages.bind(Messages.SdrFileSystem_ErrorInvalidScheme, scheme), null));
		}

		if (path != null) {
			this.rootStore = EFS.getStore(path.toFile().toURI());
		} else {
			this.rootStore = EFS.getNullFileSystem().getStore(new Path("")); //$NON-NLS-1$
		}
	}

}
