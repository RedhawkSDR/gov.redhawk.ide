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
package gov.redhawk.ide.codegen.util;

import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.model.sca.util.ModelUtil;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;

/**
 * Utility for dealing with resource properties
 */
public final class PropertyUtil {

	private PropertyUtil() {

	}

	/**
	 * Returns the time the waveDev settings was last generated.
	 * 
	 * @throws CoreException The persistent property on the wavedev resource cannot be retrieved
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static Date getLastGenerated(final WaveDevSettings wavedev, final ImplementationSettings settings) throws CoreException {
		final String prop = ModelUtil.getResource(wavedev).getPersistentProperty(
		        new QualifiedName(RedhawkCodegenActivator.PLUGIN_ID, settings.getName() + "lastGenerated"));
		// final String prop =
		// ModelUtil.getResource(settings).getPersistentProperty(PropertyUtil.Q_LAST_GENERATED);
		if (prop != null) {
			try {
				final long milli = Long.valueOf(prop);
				return new Date(milli);
			} catch (final IllegalArgumentException e) {
				RedhawkCodegenActivator.logError("Unable to create a new date from " + prop, null);
			}
		}
		return null;
	}

	/**
	 * Sets the time the wavedev settings was last generated.
	 * 
	 * @since 2.0
	 */
	public static void setLastGenerated(final WaveDevSettings wavedev, final ImplementationSettings settings, final Date date) {
		final WorkspaceJob job = new WorkspaceJob("Saving last generated date") {

			@SuppressWarnings("deprecation")
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final IResource resource = ModelUtil.getResource(wavedev);
				if (date != null) {
					resource.setPersistentProperty(new QualifiedName(RedhawkCodegenActivator.PLUGIN_ID, settings.getName() + "lastGenerated"),
					        Long.toString(date.getTime()));
				} else {
					resource.setPersistentProperty(new QualifiedName(RedhawkCodegenActivator.PLUGIN_ID, settings.getName() + "lastGenerated"), null);
				}

				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
}
