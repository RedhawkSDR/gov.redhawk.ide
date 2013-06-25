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
package gov.redhawk.ide.spd.internal.ui;

import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.scd.validation.UnnecessaryInterfaceDeclaration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

/**
 * 
 */
public class ScdMarkerResolution extends WorkbenchMarkerResolution {

	private final IMarker rootMarker;

	public ScdMarkerResolution(final IMarker marker) {
		this.rootMarker = marker;
	}

	public static boolean canHandle(final IMarker marker) {
		final int code = marker.getAttribute(IMarker.PROBLEM, 0);
		final String sourceId = marker.getAttribute(IMarker.SOURCE_ID, "");

		if (marker.getResource().getName().endsWith(ScdPackage.FILE_EXTENSION) && code == UnnecessaryInterfaceDeclaration.STATUS_CODE
		        && sourceId.equals(UnnecessaryInterfaceDeclaration.SOURCE_ID)) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMarker[] findOtherMarkers(final IMarker[] markers) {
		final List<IMarker> retVal = new ArrayList<IMarker>();
		for (final IMarker marker : markers) {
			if (this.rootMarker == marker) {
				continue;
			}
			if (ScdMarkerResolution.canHandle(marker)) {
				retVal.add(marker);
			}
		}
		return retVal.toArray(new IMarker[retVal.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "The SCD file contains references to interfaces that are not being used. They should be removed.";
	}

	/**
	 * {@inheritDoc}
	 */
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLabel() {
		return "Remove the unnecessary interfaces.";
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IMarker marker) {
		final String uri = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
		if (uri != null) {
			final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
			final EObject obj = set.getEObject(URI.createURI(uri), true);
			if (obj instanceof Interface) {
				final Interface inter = (Interface) obj;
				final Resource resource = inter.eResource();
				((Interfaces) inter.eContainer()).getInterface().remove(inter);
				try {
					resource.save(null);
				} catch (final IOException e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Failed to save Quick Fix for " + uri, e),
					        StatusManager.SHOW | StatusManager.LOG);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(final IMarker[] markers, final IProgressMonitor monitor) {
		if (markers == null) {
			return;
		}
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final Collection<Resource> editedResources = new HashSet<Resource>();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Removing unnecessary Interface references.", 100);
		try {
			final SubMonitor markerLoopMonitor = subMonitor.newChild(70).setWorkRemaining(markers.length); // SUPPRESS CHECKSTYLE MagicNumber
			for (final IMarker marker : markers) {
				final String uri = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
				if (uri != null) {
					final EObject obj = set.getEObject(URI.createURI(uri), true);
					if (obj instanceof Interface) {
						final Interface inter = (Interface) obj;
						if (inter.eResource() != null) {
							editedResources.add(inter.eResource());
						}
						((Interfaces) inter.eContainer()).getInterface().remove(inter);
					}
				}
				markerLoopMonitor.worked(1);
			}

			final SubMonitor resourceLoopMonitor = subMonitor.newChild(30).setWorkRemaining(editedResources.size()); // SUPPRESS CHECKSTYLE MagicNumber
			for (final Resource resource : editedResources) {
				try {
					resource.save(null);
				} catch (final IOException e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Failed to save Quick Fix for "
					        + resource.getURI(), e),
					        StatusManager.SHOW | StatusManager.LOG);
				}
				resourceLoopMonitor.worked(1);
			}
		} finally {
			subMonitor.done();
		}
	}

}
