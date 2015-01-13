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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ext.ServiceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.ServicePattern;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

/**
 * 
 */
public class ReleaseShapeFeature extends DefaultDeleteFeature {

	/**
	 * @param fp
	 */
	public ReleaseShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Release";
	}

	@Override
	public String getDescription() {
		return "Sends the release command for the selected component";
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		if (context.getPictogramElement() instanceof DeviceShape || context.getPictogramElement() instanceof ServiceShape) {
			return true;
		}
		return false;
	}

	@Override
	public void delete(IDeleteContext context) {
		final RHContainerShape shape = (RHContainerShapeImpl) context.getPictogramElement();

		final DcdComponentInstantiation ci = (DcdComponentInstantiation) DUtil.getBusinessObject(shape);
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				// Delete the component model object
				if (shape instanceof DeviceShape) {
					DevicePattern.deleteComponentInstantiation(ci, dcd);
				} else {
					ServicePattern.deleteComponentInstantiation(ci, dcd);
				}

				// Now we just need to remove the graphical representation of the component
				IRemoveContext rc = new RemoveContext(shape);
				IFeatureProvider featureProvider = getFeatureProvider();
				IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
				if (removeFeature != null) {
					removeFeature.remove(rc);
				}
			}
		});

	}

	@Override
	protected String getDeleteName(IDeleteContext context) {
		return "Release";
	}

	@Override
	public boolean isAvailable(IContext context) {
		return canExecute(context);
	}

	@Override
	public boolean canExecute(IContext context) {
		if (context instanceof IDeleteContext) {
			IDeleteContext delContext = (IDeleteContext) context;
			return canDelete(delContext);
		}
		return false;
	}
}
