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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.DataType;
import CF.ErrorNumberType;
import CF.LifeCyclePackage.ReleaseError;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.InvalidResourceId;
import CF.ResourceFactoryPackage.ShutdownFailure;
import gov.redhawk.model.sca.ScaAbstractComponent;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
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

	/**
	 * @since 9.0
	 * @throws IllegalArgumentException The component type (per the SCD) isn't supported
	 */
	public static SpdResourceFactory createResourceFactory(SoftPkg spd) {
		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case RESOURCE:
			checkExecutable(spd);
			return new LocalComponentFactory(spd);
		case DEVICE:
			return new LocalDeviceFactory(spd);
		case SERVICE:
			return new SpdResourceFactory(spd);
		default:
			String errorMsg = String.format("Invalid component type '%s' for SPD '%s'", type, spd.getName());
			throw new IllegalArgumentException(errorMsg);
		}
	}

	private static void checkExecutable(SoftPkg spd) {
		for (Implementation impl : spd.getImplementation()) {
			if (impl.getCode() != null && CodeFileType.EXECUTABLE.equals(impl.getCode().getType())) {
				return;
			}
		}
		throw new IllegalArgumentException("Resource has no executable implementation");
	}

	/**
	 * @since 9.0
	 */
	protected URI getSpdUri() {
		return spdURI;
	}

	@Override
	public String identifier() {
		return this.identifier;
	}

	/**
	 * @since 9.0
	 */
	protected SoftPkg loadSpd() {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource spdResource = resourceSet.getResource(spdURI, true);
		return SoftPkg.Util.getSoftPkg(spdResource);
	}

	/**
	 * @since 9.0
	 */
	protected List<ScaAbstractComponent< ? >> getLaunched() {
		return launched;
	}

	/**
	 * @since 9.0
	 */
	protected ScaAbstractComponent< ? > getResource(String resourceId) {
		String resourceIdPrefix = resourceId + ":";
		synchronized (launched) {
			Iterator<ScaAbstractComponent< ? >> iter = this.launched.iterator();
			while (iter.hasNext()) {
				ScaAbstractComponent< ? > comp = iter.next();
				if (comp.isDisposed()) {
					iter.remove();
					continue;
				}
				if (!comp.isSetIdentifier()) {
					continue;
				}
				String id = comp.getIdentifier();
				if (id.equals(resourceId) || id.startsWith(resourceIdPrefix)) {
					return comp;
				}
			}
		}
		return null;
	}

	@Override
	public void releaseResource(String resourceId) throws InvalidResourceId {
		ScaAbstractComponent< ? > comp = getResource(resourceId);
		if (comp == null) {
			throw new InvalidResourceId("No resource of id: " + resourceId);
		}
		try {
			comp.releaseObject();
		} catch (ReleaseError e) {
			// PASS
		}
		launched.remove(comp);
	}

	@Override
	public void shutdown() throws ShutdownFailure {
		synchronized (this.launched) {
			Iterator<ScaAbstractComponent< ? >> iter = this.launched.iterator();
			while (iter.hasNext()) {
				ScaAbstractComponent< ? > comp = iter.next();
				if (comp.isDisposed()) {
					iter.remove();
				}
			}
			// SCA 2.2.2, 3.1.3.1.7.5.3.5 - Exception if all resources have not been released
			if (!this.launched.isEmpty()) {
				throw new ShutdownFailure("Some resources have not been released");
			}
		}
		super.shutdown();
	}

	/**
	 * @since 9.0
	 */
	@Override
	protected CF.Resource createInstance(final String compID, final DataType[] qualifiers, final String launchMode, String implementation)
		throws CreateResourceFailure {
		final SoftPkg spd = loadSpd();
		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case SERVICE:
			throw new CreateResourceFailure(ErrorNumberType.CF_ENOTSUP, "Launching services is not supported");
		default:
			throw new CreateResourceFailure(ErrorNumberType.CF_ENOTSUP, "Launching this component type is not supported");
		}
	}

}
