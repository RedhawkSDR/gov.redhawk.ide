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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.DataType;
import CF.ErrorNumberType;
import CF.LifeCyclePackage.ReleaseError;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.ShutdownFailure;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * @since 4.0
 */
public class SpdResourceFactory extends AbstractResourceFactory {

	private final List<ScaAbstractComponent< ? >> launched = Collections.synchronizedList(new ArrayList<ScaAbstractComponent< ? >>());
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

	@Override
	public String identifier() {
		return this.identifier;
	}

	private SoftPkg loadSpd() {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource spdResource = resourceSet.getResource(spdURI, true);
		return SoftPkg.Util.getSoftPkg(spdResource);
	}

	@Override
	public void shutdown() throws ShutdownFailure {
		synchronized (this.launched) {
			for (final ScaAbstractComponent< ? > comp : this.launched) {
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
		case SERVICE:
			// Because the service's CORBA type can be anything, we can't just return a CF.Resource
			// Currently that means there's no way to use this interface to launch a service at present
			throw new CreateResourceFailure(ErrorNumberType.CF_ENOTSUP, "Launching services is not supported");
		case DEVICE:
			try {
				LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca(null).getSandboxDeviceManager();
				LocalAbstractComponent absComponent = devMgr.launch(compID, params.toArray(new DataType[params.size()]), spdURI, implementationID, launchMode);
				ScaDevice< ? > dev = (ScaDevice< ? >) absComponent;
				this.launched.add(dev);
				return dev.fetchNarrowedObject(null);
			} catch (CoreException e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to create instance.", e));
				throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
			}
		default:
			try {
				LocalScaWaveform chalkboard = getChalkboard(null);
				final LocalScaComponent component = chalkboard.launch(compID, params.toArray(new DataType[params.size()]), spdURI.trimFragment(),
					implementationID, launchMode);
				this.launched.add(component);
				return component.fetchNarrowedObject(null);
			} catch (CoreException e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to create instance.", e));
				throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
			}
		}
	}

}
