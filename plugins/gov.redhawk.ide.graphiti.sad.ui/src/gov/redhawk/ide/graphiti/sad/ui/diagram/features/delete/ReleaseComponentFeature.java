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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.delete;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

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
public class ReleaseComponentFeature extends DefaultDeleteFeature {

	/**
	 * @param fp
	 */
	public ReleaseComponentFeature(IFeatureProvider fp) {
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
		if (context.getPictogramElement() instanceof ComponentShapeImpl) {
			return true;
		}
		return false;
	}

	@Override
	public void delete(IDeleteContext context) {
		final ComponentShape componentShape = (ComponentShapeImpl) context.getPictogramElement();

		final SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				// Delete the component model object
				ComponentPattern.deleteComponentInstantiation(ci, sad);

				// Now we just need to remove the graphical representation of the component
				IRemoveContext rc = new RemoveContext(componentShape);
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
