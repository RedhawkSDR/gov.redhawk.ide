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
package gov.redhawk.ide.builders;

import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.validator.AdvancedEObjectValidator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;

/**
 * Utility class for propagating EMF Validation errors to the Problems View.
 * Heavily borrows from org.eclipse.emf.validation.marker.MarkerUtil class.
 */
public enum SCAMarkerUtil {
	INSTANCE;

	public static final String VALIDATION_MARKER_TYPE = "gov.redhawk.ide.emf.validation.problem"; //$NON-NLS-1$
	public static final String RULE_ATTRIBUTE = "rule"; //$NON-NLS-1$

	private static final String PLATFORM_SCHEME = "platform"; //$NON-NLS-1$
	private static final String FILE_SCHEME = "file"; //$NON-NLS-1$
	private static final String RESOURCE_SEGMENT = "resource"; //$NON-NLS-1$

	private final Diagnostician diagnostician = new Diagnostician() {
		@Override
		public String getObjectLabel(final EObject eObject) {
			if (!eObject.eIsProxy()) {
				final IItemLabelProvider itemLabelProvider = (IItemLabelProvider) getAdapterFactory().adapt(eObject, IItemLabelProvider.class);
				if (itemLabelProvider != null) {
					return itemLabelProvider.getText(eObject);
				}
			}

			return super.getObjectLabel(eObject);
		}
	};

	private final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
	private final Map<URI, IFile> visitedResources = new HashMap<URI, IFile>();

	/**
	 * Constructor.
	 */
	private SCAMarkerUtil() {
		this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
	}

	/**
	 * Returns an IFile for the specified resource.
	 * 
	 * @param resource
	 *            the resource to
	 * @return a cached IFile if it exists, otherwise gets file from workspace
	 *         root based on uri
	 * @throws CoreException
	 */
	private IFile getFile(final Resource resource) throws CoreException {
		URI uri = resource.getURI();

		// Normalize the URI to something that we can deal with like file or
		// platform scheme
		uri = resource.getResourceSet().getURIConverter().normalize(uri);

		IFile file = this.visitedResources.get(uri);

		if (file == null) {
			if (SCAMarkerUtil.PLATFORM_SCHEME.equals(uri.scheme()) && uri.segmentCount() > 1 && SCAMarkerUtil.RESOURCE_SEGMENT.equals(uri.segment(0))) {
				final StringBuffer platformResourcePath = new StringBuffer();
				for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
					platformResourcePath.append('/');
					platformResourcePath.append(URI.decode(uri.segment(j)));
				}

				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
			} else if (SCAMarkerUtil.FILE_SCHEME.equals(uri.scheme())) {
				final StringBuffer fileResourcePath = new StringBuffer();
				for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
					fileResourcePath.append('/');
					fileResourcePath.append(URI.decode(uri.segment(j)));
				}

				file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileResourcePath.toString()));
			}

			if (file != null) {
				file.deleteMarkers(SCAMarkerUtil.VALIDATION_MARKER_TYPE, true, IResource.DEPTH_ZERO);
				this.visitedResources.put(uri, file);
			}
		}

		return file;
	}

	private AdapterFactory getAdapterFactory() {
		return this.adapterFactory;
	}

	/**
	 * Creates the marker on the specified IFile from the provided Diagnostic.
	 * 
	 * @param file
	 * @param resource
	 * @param diagnostic
	 * @throws CoreException
	 */
	private void createMarker(final IFile file, final Resource resource, final Diagnostic diagnostic) throws CoreException {

		final IMarker marker = file.createMarker(SCAMarkerUtil.VALIDATION_MARKER_TYPE);

		switch (diagnostic.getSeverity()) {
		case IStatus.INFO:
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
			break;
		case IStatus.WARNING:
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			break;
		case IStatus.ERROR:
		case IStatus.CANCEL:
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			break;
		default:
			break;
		}
		marker.setAttribute(IMarker.SOURCE_ID, diagnostic.getSource());
		marker.setAttribute(IMarker.PROBLEM, diagnostic.getCode());

		marker.setAttribute(IMarker.MESSAGE, diagnostic.getMessage());
		if (diagnostic.getData().size() > 0 && diagnostic.getData().get(0) instanceof EObject) {
			marker.setAttribute(EValidator.URI_ATTRIBUTE, EcoreUtil.getURI((EObject) diagnostic.getData().get(0)).toString());
			if (diagnostic.getData().size() > 1 && diagnostic.getData().get(1) instanceof EStructuralFeature) {
				final EStructuralFeature feature = (EStructuralFeature) diagnostic.getData().get(1);
				marker.setAttribute(AdvancedEObjectValidator.FEATURE_ID, feature.getFeatureID());
				marker.setAttribute(IMarker.LOCATION, feature.getName());
			}
		} else {
			marker.setAttribute(EValidator.URI_ATTRIBUTE, resource.getURI().toString());
		}
		if (marker.getAttribute(IMarker.LOCATION) == null) {
			marker.setAttribute(IMarker.LOCATION, " ");
		}

	}

	private void analyzeDiagnostic(final IFile file, final Resource resource, final Diagnostic diagnostic) throws CoreException {
		if (EObjectValidator.DIAGNOSTIC_SOURCE.equals(diagnostic.getSource()) && diagnostic.getCode() == EObjectValidator.EOBJECT__EVERY_DATA_VALUE_CONFORMS) {
			createMarker(file, resource, diagnostic);
		} else if (diagnostic.getChildren().isEmpty()) {
			if (diagnostic.getSeverity() == IStatus.WARNING || diagnostic.getSeverity() == IStatus.ERROR) {
				createMarker(file, resource, diagnostic);
			}
		} else {
			for (final Diagnostic child : diagnostic.getChildren()) {
				analyzeDiagnostic(file, resource, child);
			}
		}

	}

	/**
	 * Creates markers for the specified diagnostic and resource.
	 * 
	 * @param eResource
	 *            the Resource to create markers for
	 * @param diagnostic
	 *            the Diagnostic to process
	 * @throws CoreException
	 */
	public void createMarkers(final Resource eResource, final Diagnostic diagnostic) throws CoreException {
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(final IProgressMonitor m) throws CoreException {
				final IFile file = getFile(eResource);
				if (file != null) {
					file.deleteMarkers(SCAMarkerUtil.VALIDATION_MARKER_TYPE, true, IResource.DEPTH_ZERO);
					analyzeDiagnostic(file, eResource, diagnostic);
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, null);
	}

	/**
	 * Returns the Diagnostician instance.
	 * 
	 * @return
	 */
	public Diagnostician getDiagnostician() {
		return this.diagnostician;
	}
}
