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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import CF.LifeCycleOperations;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.model.sca.util.ReleaseJob;

public abstract class ReleaseFeature extends DefaultDeleteFeature {

	public ReleaseFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Release";
	}

	@Override
	protected String getDeleteName(IDeleteContext context) {
		return "Release";
	}

	@Override
	public String getDescription() {
		return "Call releaseObject on the resource";
	}

	protected abstract boolean isReleaseableShape(RHContainerShape shape);

	@Override
	public boolean canUndo(IContext context) {
		return false;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		if (context.getPictogramElement() == null) {
			return false;
		}

		RHContainerShape shape = (RHContainerShape) context.getPictogramElement();
		return shape.isEnabled() && isReleaseableShape(shape);
	}

	@Override
	public void delete(IDeleteContext context) {
		// We don't actually delete the PE objects here; we schedule a job to call releaseObject()
		// We'll get notification from the SCA model when the object is released
		LifeCycleOperations obj = Platform.getAdapterManager().getAdapter(context.getPictogramElement(), LifeCycleOperations.class);
		if (obj != null) {
			ReleaseJob job = new ReleaseJob(obj);
			job.setUser(true);
			job.schedule();
		}
	}
}
