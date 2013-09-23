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
import gov.redhawk.spd.internal.validation.EventPortConstraint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.PortTypeContainer;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;
import org.omg.CosEventChannelAdmin.EventChannelHelper;

/**
 * 
 */
public class SpdMarkerResolution extends WorkbenchMarkerResolution {

	private final IMarker rootMarker;

	public SpdMarkerResolution(final IMarker marker) {
		this.rootMarker = marker;
	}

	public static boolean canHandle(final IMarker marker) {
		final int code = marker.getAttribute(IMarker.PROBLEM, 0);
		final String sourceId = marker.getAttribute(IMarker.SOURCE_ID, "");

		if (marker.getResource().getName().endsWith(SpdPackage.FILE_EXTENSION) && code == EventPortConstraint.STATUS_CODE
		        && sourceId.equals(EventPortConstraint.SOURCE_ID)) {
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
			if (SpdMarkerResolution.canHandle(marker)) {
				retVal.add(marker);
			}
		}
		return retVal.toArray(new IMarker[retVal.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "Missing event port for property events.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return "Add event port";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(final IMarker marker) {
		final String uri = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
		if (uri != null) {
			final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
			final EObject obj = set.getEObject(URI.createURI(uri), true);
			if (obj instanceof SoftPkg) {
				final SoftPkg spd = (SoftPkg) obj;
				final List<Resource> editedResources = Collections.emptyList();
				try {
					addEventPort(spd);
				} catch (final CoreException e) {
					StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
					return;
				}
				save(editedResources, null);
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
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding event port.", 100);
		try {
			final SubMonitor markerLoopMonitor = subMonitor.newChild(70).setWorkRemaining(markers.length); // SUPPRESS CHECKSTYLE MagicNumber
			for (final IMarker marker : markers) {
				final String uri = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
				if (uri != null) {
					final EObject obj = set.getEObject(URI.createURI(uri), true);
					if (obj instanceof SoftPkg) {
						final SoftPkg spd = (SoftPkg) obj;
						try {
							editedResources.addAll(addEventPort(spd));
						} catch (final CoreException e) {
							StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
							return;
						}
					}
				}
				markerLoopMonitor.worked(1);
			}
			save(editedResources, subMonitor.newChild(30));
		} finally {
			subMonitor.done();
		}
	}

	private void save(final Collection<Resource> resources, final IProgressMonitor monitor) {
		final SubMonitor resourceLoopMonitor = SubMonitor.convert(monitor, "Saving resources", resources.size()); // SUPPRESS CHECKSTYLE MagicNumber
		for (final Resource resource : resources) {
			try {
				final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

					@Override
					public void run(final IProgressMonitor monitor) throws CoreException {
						try {
							resource.save(null);
						} catch (final IOException e) {
							throw new CoreException(new Status(IStatus.ERROR,
							        ComponentUiPlugin.PLUGIN_ID,
							        "Failed to save Quick Fix for " + resource.getURI(),
							        e));
						}
					}
				};
				ResourcesPlugin.getWorkspace().run(runnable, WorkspaceSynchronizer.getFile(resource), 0, resourceLoopMonitor.newChild(1));
			} catch (final CoreException e) {
				StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
			}
		}
	}

	private List<Resource> addEventPort(final SoftPkg spd) throws CoreException {
		final Descriptor desc = spd.getDescriptor();
		if (desc != null) {
			final SoftwareComponent scd = desc.getComponent();
			ComponentFeatures features = scd.getComponentFeatures();
			if (features == null) {
				features = ScdFactory.eINSTANCE.createComponentFeatures();
				scd.setComponentFeatures(features);
			}
			Ports ports = features.getPorts();
			if (ports == null) {
				ports = ScdFactory.eINSTANCE.createPorts();
				features.setPorts(ports);
			}

			Interfaces interfaces = scd.getInterfaces();
			boolean found = false;
			if (interfaces != null) {
				for (final Interface i : interfaces.getInterface()) {
					if (EventChannelHelper.id().equals(i.getRepid())) {
						found = true;
						break;
					}
				}
			} else {
				interfaces = ScdFactory.eINSTANCE.createInterfaces();
				scd.setInterfaces(interfaces);
			}

			if (!found) {
				final Interface i = ScdFactory.eINSTANCE.createInterface();
				i.setName("EventChannel");
				i.setRepid(EventChannelHelper.id());
				interfaces.getInterface().add(i);
			}

			final Uses uses = ScdFactory.eINSTANCE.createUses();
			ports.getUses().add(uses);
			uses.setName(Uses.PORT_NAME_PROP_EVENTS);
			uses.setRepID(EventChannelHelper.id());
			final PortTypeContainer ptc = ScdFactory.eINSTANCE.createPortTypeContainer();
			ptc.setType(PortType.RESPONSES);
			uses.getPortType().add(ptc);

			return Collections.singletonList(scd.eResource());
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "No descriptor, quick fix failed for "
			        + spd.eResource().getURI().path(), null));
		}

	}

}
