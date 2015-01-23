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
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.UsesDeviceWizard;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.UsesDeviceDependencies;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class UsesDevicePattern extends AbstractUsesDevicePattern implements IPattern {

	public static final String NAME = "Use Device";

	public UsesDevicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "";
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_USES_DEVICE;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof UsesDeviceStub) {
			UsesDeviceStub usesDeviceStub = (UsesDeviceStub) mainBusinessObject;
			if (usesDeviceStub != null && !AbstractUsesDevicePattern.isFrontEndDevice(usesDeviceStub.getUsesDevice())) {
				return true;
			}
		}
		return false;
	}
	
//	@Override
//	protected boolean isPatternRoot(PictogramElement pictogramElement) {
//		return true;
//	}

	// DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {
		
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		// prompt user for 
		final UsesDeviceWizard wizard = (UsesDeviceWizard) openWizard(
			new UsesDeviceWizard(sad));
		if (wizard == null) {
			return null;
		}
		
		//extract values from wizard
		final String usesDeviceId = wizard.getNamePage().getModel().getUsesDeviceId();
		final List<String> usesPortNames = wizard.getPortsWizardPage().getModel().getUsesPortNames();
		final List<String> providesPortNames = wizard.getPortsWizardPage().getModel().getProvidesPortNames();

		final UsesDeviceStub[] usesDeviceStubs = new UsesDeviceStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				
				//set uses device dependencies if not already set
				UsesDeviceDependencies usesDeviceDependencies = sad.getUsesDeviceDependencies();
				if (usesDeviceDependencies == null) {
					usesDeviceDependencies = SadFactory.eINSTANCE.createUsesDeviceDependencies();
					sad.setUsesDeviceDependencies(usesDeviceDependencies);
				}
				
				//create device	
				//WE ADD DEVICE TO BOTH UsesDeviceStub & UsesDeviceDependencies
				//UsesDeviceStub is contained in the Graphiti diagram file, UsesDeviceDependencies is stored in the sad file
				UsesDevice usesDevice = SpdFactory.eINSTANCE.createUsesDevice();
				usesDeviceDependencies.getUsesdevice().add(usesDevice);
				usesDevice.setId(usesDeviceId);
				//usesDevice.setType(); //not using this type on purpose, no value according to Core Framework team

				//UsesDeviceStub
				usesDeviceStubs[0] = createUsesDeviceStub(usesDevice);
				
				// if applicable add uses port stub(s)
				if (usesPortNames != null) {
					for (String usesPortName : usesPortNames) {
						UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
						usesPortStub.setName(usesPortName);
						usesDeviceStubs[0].getUsesPortStubs().add(usesPortStub);
					}
				}

				// if applicable add provides port stub(s)
				if (providesPortNames != null) {
					for (String providesPortName : providesPortNames) {
						ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
						providesPortStub.setName(providesPortName);
						usesDeviceStubs[0].getProvidesPortStubs().add(providesPortStub);
					}
				}
				
			}
		});
		
		//store UsesDeviceStub in graphiti diagram
		getDiagram().eResource().getContents().add(usesDeviceStubs[0]);

		addGraphicalRepresentation(context, usesDeviceStubs[0]);

		return new Object[] { usesDeviceStubs[0] };
	}

	
	
	public static Wizard openWizard(Wizard wizard) {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return wizard;
	}

}
