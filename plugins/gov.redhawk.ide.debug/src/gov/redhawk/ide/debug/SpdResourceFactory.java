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

import gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RunnableWithResult;

import CF.DataType;
import CF.ErrorNumberType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.ReleaseError;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.InvalidResourceId;
import CF.ResourceFactoryPackage.ShutdownFailure;

/**
 * @since 4.0
 */
public class SpdResourceFactory extends AbstractResourceFactory {

	private final List<LocalScaComponent> launched = Collections.synchronizedList(new ArrayList<LocalScaComponent>());
	private final URI spdURI;
	private final String identifier;

	public SpdResourceFactory(SoftPkg spd) {
		this(spd.eResource().getURI(), spd.getId());
	}

	public SpdResourceFactory(final URI spdURI, String identifier) {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		Assert.isNotNull(spdURI, "Identifier must not be null");
		this.spdURI = spdURI;
		this.identifier = identifier;
	}

	/**
	 * @since 6.0
	 */
	public LocalScaWaveform getChalkboard(IProgressMonitor monitor) throws CoreException {
		return ScaDebugPlugin.getInstance().getLocalSca(monitor).getSandboxWaveform();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String identifier() {
		return this.identifier;
	}

	private SoftPkg loadSpd() {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource spdResource = resourceSet.getResource(spdURI, true);
		return SoftPkg.Util.getSoftPkg(spdResource);
	}

	protected LocalScaComponent getComponent(final String instantiationID) throws CoreException {
		final LocalScaWaveform chalkboard = getChalkboard(null);
		try {
			return ScaModelCommand.runExclusive(chalkboard, new RunnableWithResult.Impl<LocalScaComponent>() {

				@Override
				public void run() {
					for (final ScaComponent comp : chalkboard.getComponents()) {
						if (instantiationID.equals(comp.getInstantiationIdentifier())) {
							setResult((LocalScaComponent) comp);
							return;
						}
					}

				}

			});
		} catch (final InterruptedException e) {
			// PASS
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void releaseResource(final String resourceId) throws InvalidResourceId {
		LocalScaComponent comp;
		try {
			comp = getComponent(resourceId);
		} catch (CoreException e1) {
			throw new InvalidResourceId("Failed to find component or sandbox.");
		}
		if (comp != null) {
			try {
				comp.releaseObject();
			} catch (final ReleaseError e) {
				// PASS
			}
		} else {
			throw new InvalidResourceId("No resource of id: " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() throws ShutdownFailure {
		synchronized (this.launched) {
			for (final LocalScaComponent comp : this.launched) {
				if (comp.isDisposed()) {
					continue;
				}
				try {
					comp.releaseObject();
				} catch (final ReleaseError e) {
					// PASS
				}
			}
			this.launched.clear();
		}
	}

	@Override
	protected CF.Resource createInstance(final String compID, final DataType[] qualifiers, final String launchMode) throws CreateResourceFailure {
		try {
			LocalScaComponent comp = getComponent(compID);
			if (comp != null) {
				return comp.getObj();
			}
		} catch (CoreException e2) {
			throw new CreateResourceFailure(ErrorNumberType.CF_ENODEV, "Failed to find chalkboard.");
		}

		String implementationID = null;
		final List<DataType> params = new ArrayList<DataType>(Arrays.asList(qualifiers));
		for (final Iterator<DataType> i = params.iterator(); i.hasNext();) {
			final DataType t = i.next();
			if ("__implementationID".equals(t.id)) {
				final String value = t.value.extract_string();
				implementationID = value;
				i.remove();
			}
		}

		final SoftPkg spd = loadSpd();

		if (implementationID == null) {
			if (!spd.getImplementation().isEmpty()) {
				implementationID = spd.getImplementation().get(0).getId();
			} else {
				throw new CreateResourceFailure(ErrorNumberType.CF_EINVAL, "No implementations for component: " + identifier());
			}
		}

		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case DEVICE:
		case SERVICE:
			LocalSca sandbox;
			try {
				sandbox = ScaDebugPlugin.getInstance().getLocalSca(null);
			} catch (CoreException e1) {
				throw new CreateResourceFailure(ErrorNumberType.CF_ENODEV, "Failed to get sandbox.");
			}
			
			LocalScaDeviceManager devMgr = sandbox.getSandboxDeviceManager();
			try {
				return ((LocalScaDeviceManagerImpl) devMgr).launch(compID, qualifiers, spdURI.toString(), implementationID, launchMode);
			} catch (ExecuteFail e) {
				throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
			}
		default:
			break;
		}

		try {
			LocalScaWaveform chalkboard = getChalkboard(null);
			final LocalScaComponent component = chalkboard.launch(compID, params.toArray(new DataType[params.size()]), spdURI.trimFragment(),
				implementationID, launchMode);
			this.launched.add(component);
			return component.fetchNarrowedObject(null);
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to create instance.", e));
			throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
		}
	}

}
