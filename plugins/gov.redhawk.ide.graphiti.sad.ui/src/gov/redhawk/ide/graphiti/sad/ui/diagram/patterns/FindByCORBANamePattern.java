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

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.FindByCORBANameWizardPage;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByCORBANamePattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "Find By Name";

	public FindByCORBANamePattern() {
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
		return ImageProvider.IMG_FIND_BY_CORBA_NAME;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof FindByStub) {
			FindByStub findByStub = (FindByStub) mainBusinessObject;
			if (findByStub.getNamingService() != null) {
				return true;
			}
		}
		return false;
	}

	// DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {
		// prompt user for CORBA Name
		FindByCORBANameWizardPage page = openWizard();
		if (page == null) {
			return null;
		}

		final String corbaNameText = page.getModel().getCorbaName();
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null && !page.getModel().getUsesPortNames().isEmpty()) ? page.getModel().getUsesPortNames()
			: null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null && !page.getModel().getProvidesPortNames().isEmpty()) ? page.getModel().getProvidesPortNames()
			: null;

		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// naming service (corba name)
				NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
				namingService.setName(corbaNameText);
				findByStubs[0].setNamingService(namingService);

				// if applicable add uses port stub(s)
				if (usesPortNames != null) {
					int i = 0; // counter
					for (String usesPortName : usesPortNames) {
						UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
						usesPortStub.setName(usesPortName);
						findByStubs[i].getUses().add(usesPortStub);
					}
				}

				// if applicable add provides port stub(s)
				if (providesPortNames != null) {
					int i = 0; // counter
					for (String providesPortName : providesPortNames) {
						ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
						providesPortStub.setName(providesPortName);
						findByStubs[i].getProvides().add(providesPortStub);
					}
				}

				// add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);

			}
		});

		addGraphicalRepresentation(context, findByStubs[0]);

		return new Object[] { findByStubs[0] };
	}

	/**
	 * Creates the FindByStub in the diagram with the provided namingServiceText
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param namingServiceText
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub create(final String namingServiceText, final IFeatureProvider featureProvider, final Diagram diagram) {

		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// naming service (corba name)
				NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
				namingService.setName(namingServiceText);
				findByStubs[0].setNamingService(namingService);

				// add to diagram resource file
				diagram.eResource().getContents().add(findByStubs[0]);

			}
		});

		return findByStubs[0];
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getNamingService().getName();
	}
	
	@Override
	public void setInnerTitle(FindByStub findByStub, String value) {
		findByStub.getNamingService().setName(value);
	}

	@Override 
	public String getCheckValueValidName() {
		return "CORBA";
	}

	protected static FindByCORBANameWizardPage openWizard() {
		return openWizard(null, new Wizard() {
			public boolean performFinish() {
				return true;
			}
		});
	}

	public static FindByCORBANameWizardPage openWizard(FindByStub existingFindByStub, Wizard wizard) {
		FindByCORBANameWizardPage page = new FindByCORBANameWizardPage();
		wizard.addPage(page);
		if (existingFindByStub != null) {
			fillWizardFieldsWithExistingProperties(page, existingFindByStub);
		}
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return page;
	}

	private static void fillWizardFieldsWithExistingProperties(FindByCORBANameWizardPage page, FindByStub findByStub) {
		// Grab existing properties from findByStub
		String corbaName = findByStub.getNamingService().getName();
		EList<UsesPortStub> usesPorts = findByStub.getUses();
		EList<ProvidesPortStub> providesPorts = findByStub.getProvides();

		// Fill wizard fields with existing properties
		page.getModel().setCorbaName(corbaName);
		if (usesPorts != null && !usesPorts.isEmpty()) {
			for (UsesPortStub port : usesPorts) {
				page.getModel().getUsesPortNames().add(port.getName());
			}
		}
		if (providesPorts != null && !providesPorts.isEmpty()) {
			for (ProvidesPortStub port : providesPorts) {
				page.getModel().getProvidesPortNames().add(port.getName());
			}
		}
	}
	
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape containerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(containerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof FindByStub && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}
