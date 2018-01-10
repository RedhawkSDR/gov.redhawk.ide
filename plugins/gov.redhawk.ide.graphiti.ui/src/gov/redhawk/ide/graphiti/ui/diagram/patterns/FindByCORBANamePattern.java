/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.core.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.core.graphiti.ui.util.FindByStubUtil;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.Connections;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class FindByCORBANamePattern extends AbstractFindByPattern implements IDialogEditingPattern {

	public FindByCORBANamePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return Messages.FindByCORBANamePattern_CreateName;
	}

	@Override
	public String getCreateDescription() {
		return Messages.FindByCORBANamePattern_CreateDescription;
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_CORBA_NAME;
	}

	@Override
	public String getEditName() {
		return Messages.FindByCORBANamePattern_EditName;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	protected boolean isMatchingFindByType(FindByStub findByStub) {
		return FindByStubUtil.isFindByStubName(findByStub);
	}

	// DIAGRAM FEATURES
	@Override
	protected FindByStub createFindByStub(ICreateContext context) {
		// prompt user for CORBA Name
		FindByCORBANameWizardPage page = openWizard();
		if (page == null) {
			return null;
		}

		FindByStub findByStub = FindByCORBANamePattern.create(page.getModel().getCorbaName());

		// if applicable add uses port stub(s)
		updateUsesPortStubs(findByStub, page.getModel().getUsesPortNames());

		// if applicable add provides port stub(s)
		updateProvidesPortStubs(findByStub, page.getModel().getProvidesPortNames());

		return findByStub;
	}

	/**
	 * Creates the FindByStub in the diagram with the provided namingServiceText
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param namingServiceName
	 * @return
	 */
	public static FindByStub create(final String namingServiceName) {
		FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();

		// interface stub (lollipop)
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

		// naming service (corba name)
		NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
		namingService.setName(namingServiceName);
		findByStub.setNamingService(namingService);

		return findByStub;
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getNamingService().getName();
	}

	@Override
	protected void setInnerTitle(FindByStub findByStub, List<FindBy> findBys, String value) {
		findByStub.getNamingService().setName(value);
		for (FindBy findBy : findBys) {
			findBy.getNamingService().setName(value);
		}
	}

	@Override
	public String getCheckValueValidName() {
		return Messages.FindByCORBANamePattern_InvalidValueName;
	}

	private FindByCORBANameWizardPage openWizard() {
		return openWizard(null, getEditWizard());
	}

	private FindByCORBANameWizardPage openWizard(FindByStub existingFindByStub, Wizard wizard) {
		FindByCORBANameWizardPage page = new FindByCORBANameWizardPage();
		wizard.addPage(page);
		if (existingFindByStub != null) {
			// Grab existing properties from findByStub
			String corbaName = existingFindByStub.getNamingService().getName();
			EList<UsesPortStub> usesPorts = existingFindByStub.getUses();
			EList<ProvidesPortStub> providesPorts = existingFindByStub.getProvides();

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
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return page;
	}

	@Override
	protected String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}

	@Override
	public boolean canDialogEdit(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			return isMainBusinessObjectApplicable(getBusinessObjectForPictogramElement(pes[0]));
		}
		return false;
	}

	@Override
	public boolean dialogEdit(ICustomContext context) {
		final PictogramElement findByPE = context.getPictogramElements()[0];
		final FindByStub findByStub = (FindByStub) getBusinessObjectForPictogramElement(findByPE);

		FindByCORBANameWizardPage page = openWizard(findByStub, getEditWizard());
		if (page == null) {
			return false;
		}

		// get user selections
		final String corbaNameText = page.getModel().getCorbaName();
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null) ? page.getModel().getUsesPortNames() : null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null) ? page.getModel().getProvidesPortNames() : null;

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				findByStub.getNamingService().setName(corbaNameText);

				// Update the naming service of all associated findBy model elements
				String connectId = null;
				for (Property prop : findByPE.getProperties()) {
					if (AbstractConnectInterfacePattern.CONNECT_INTERFACE_ID.equals(prop.getKey())) {
						connectId = prop.getValue();
						Connections< ? > modelConnections = getModelConnections();
						for (ConnectInterface< ? , ? , ? > ci : modelConnections.getConnectInterface()) {
							if (connectId.equals(ci.getId())) {
								FindBy fb = null;
								if (ci.getProvidesPort() != null) {
									fb = ci.getProvidesPort().getFindBy();
								} else {
									fb = ci.getComponentSupportedInterface().getFindBy();
								}
								if (fb != null) {
									fb.getNamingService().setName(corbaNameText);
								}
								
								fb = ci.getUsesPort().getFindBy();
								if (fb != null) {
									fb.getNamingService().setName(corbaNameText);
								}
							}
						}
					}
				}

				// if applicable, add uses and provides port stub(s)
				updateUsesPortStubs(findByStub, usesPortNames);
				updateProvidesPortStubs(findByStub, providesPortNames);
			}
		});
		updatePictogramElement(findByPE);
		layoutPictogramElement(findByPE);

		return true;
	}

	private Wizard getEditWizard() {
		Wizard wizard = new Wizard() {
			public boolean performFinish() {
				return true;
			}
		};
		wizard.setWindowTitle(Messages.FindByCORBANamePattern_WizardTitle);
		return wizard;
	}
}
