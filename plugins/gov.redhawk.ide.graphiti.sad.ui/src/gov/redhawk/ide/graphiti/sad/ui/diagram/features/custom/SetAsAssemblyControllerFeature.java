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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class SetAsAssemblyControllerFeature extends AbstractCustomFeature {

	public SetAsAssemblyControllerFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Set As Assembly Controller";
	public static final String DESCRIPTION = "Make this component the assembly controller for the waveform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Returns true if linked business object is SadComponentInstantiation and Assembly Controller is not currently set
	 * to selected Component
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements() != null && context.getPictogramElements().length > 0) {
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if (obj instanceof SadComponentInstantiation) {

				// get sad from diagram
				final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

				// get AssemblyController
				final AssemblyController assemblyController = sad.getAssemblyController();

				if (assemblyController != null && assemblyController.getComponentInstantiationRef() != null
					&& !assemblyController.getComponentInstantiationRef().getRefid().equals(((SadComponentInstantiation) obj).getId())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Marks a Component the AssemblyController for the waveform
	 */
	@Override
	public void execute(ICustomContext context) {
		ComponentShape componentShape = (ComponentShape) context.getPictogramElements()[0];
		final SadComponentInstantiation newAssemblyCI = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// get AssemblyController
		final AssemblyController assemblyController = sad.getAssemblyController();

		// get current Component marked as assembly controller
		final SadComponentInstantiation oldAssemblyCI;
		if (assemblyController != null && assemblyController.getComponentInstantiationRef() != null) {
			oldAssemblyCI = assemblyController.getComponentInstantiationRef().getInstantiation();
		} else {
			oldAssemblyCI = null;
		}

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// get AssemblyController
				final AssemblyController newAsm = SadFactory.eINSTANCE.createAssemblyController();
				SadComponentInstantiationRef newRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
				newAsm.setComponentInstantiationRef(newRef);

				// set new assembly controller
				newRef.setInstantiation(newAssemblyCI);
				sad.setAssemblyController(newAsm);

				// if new assembly controller has a start order declared, change it to zero and update all other component start orders
				if (newAssemblyCI.getStartOrder() != null) {
					newAssemblyCI.setStartOrder(BigInteger.ZERO);
				}
				ComponentPattern.organizeStartOrder(sad, getDiagram(), getFeatureProvider());
			}
		});

		// update CI shapes associated with AssemblyController changes
		List<PictogramElement> elementsToUpdate = new ArrayList<PictogramElement>();
		if (oldAssemblyCI != null) {
			elementsToUpdate.addAll(Graphiti.getLinkService().getPictogramElements(getDiagram(), oldAssemblyCI));
		}
		elementsToUpdate.addAll(Graphiti.getLinkService().getPictogramElements(getDiagram(), newAssemblyCI));
		for (PictogramElement pe : elementsToUpdate) {
			updatePictogramElement(pe);
		}

	}

}
