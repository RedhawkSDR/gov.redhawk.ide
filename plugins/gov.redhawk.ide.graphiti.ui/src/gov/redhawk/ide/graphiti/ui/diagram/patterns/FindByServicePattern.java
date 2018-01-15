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

import gov.redhawk.core.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.core.graphiti.ui.util.FindByStubUtil;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.internal.diagram.wizards.FindByServiceWizardPage;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.Connections;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByServicePattern extends AbstractFindByPattern implements IDialogEditingPattern {

	public FindByServicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return Messages.FindByServicePattern_CreateName;
	}

	@Override
	public String getCreateDescription() {
		return Messages.FindByServicePattern_CreateDescription;
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_SERVICE;
	}

	@Override
	public String getEditName() {
		return Messages.FindByServicePattern_EditName;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	protected boolean isMatchingFindByType(FindByStub findByStub) {
		return FindByStubUtil.isFindByStubService(findByStub);
	}

	// DIAGRAM FEATURES
	@Override
	protected FindByStub createFindByStub(ICreateContext context) {

		// prompt user for Service information
		FindByServiceWizardPage page = getWizardPage();
		if (page == null) {
			return null;
		}

		// create new business object
		FindByStub findByStub = null;
		if (page.getModel().getEnableServiceName()) {
			findByStub = FindByServicePattern.createFindByServiceName(page.getModel().getServiceName());
		} else if (page.getModel().getEnableServiceType()) {
			findByStub = FindByServicePattern.createFindByServiceType(page.getModel().getServiceType());
		}

		// if applicable add uses port stub(s)
		updateUsesPortStubs(findByStub, page.getModel().getUsesPortNames());

		// if applicable add provides port stub(s)
		updateProvidesPortStubs(findByStub, page.getModel().getProvidesPortNames());

		return findByStub;
	}

	/**
	 * Creates the FindByStub in the diagram with the provided service name.
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. Its
	 * purpose is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param serviceName
	 * @return
	 */
	public static FindByStub createFindByServiceName(String serviceName) {
		return create(DomainFinderType.SERVICENAME, serviceName);
	}

	/**
	 * Creates the FindByStub in the diagram with the provided service type.
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. Its
	 * purpose is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param serviceType
	 * @return
	 */
	public static FindByStub createFindByServiceType(String serviceType) {
		return create(DomainFinderType.SERVICETYPE, serviceType);
	}

	private static FindByStub create(DomainFinderType type, String name) {
		final FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		findByStub.setDomainFinder(domainFinder);
		domainFinder.setType(type);
		domainFinder.setName(name);
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());
		return findByStub;
	}

	private FindByServiceWizardPage getWizardPage() {
		return getWizardPage(null, getEditWizard());
	}

	private static FindByServiceWizardPage getWizardPage(FindByStub existingFindByStub, Wizard wizard) {
		FindByServiceWizardPage page = new FindByServiceWizardPage();
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

	private static void fillWizardFieldsWithExistingProperties(FindByServiceWizardPage page, FindByStub findByStub) {
		DomainFinderType type = findByStub.getDomainFinder().getType();
		final String serviceName = findByStub.getDomainFinder().getName();
		EList<UsesPortStub> usesPorts = findByStub.getUses();
		EList<ProvidesPortStub> providesPorts = findByStub.getProvides();

		if (type.equals(DomainFinderType.SERVICENAME)) {
			page.getModel().setServiceName(serviceName);
			page.getModel().setEnableServiceName(true);
			page.getModel().setEnableServiceType(false);
		} else {
			page.getModel().setServiceType(serviceName);
			page.getModel().setEnableServiceType(true);
			page.getModel().setEnableServiceName(false);
		}
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
	public String getOuterTitle(FindByStub findByStub) {
		// service name/type
		if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME)) {
			return Messages.FindByServicePattern_OuterTitle_ServiceName;
		} else if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE)) {
			return Messages.FindByServicePattern_OuterTitle_ServiceType;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
	}
	
	@Override
	protected void setInnerTitle(FindByStub findByStub, List<FindBy> findBys, String value) {
		findByStub.getDomainFinder().setName(value);
		for (FindBy findBy : findBys) {
			findBy.getDomainFinder().setName(value);
		}
	}

	private void setNameAndType(FindByStub findByStub, List<FindBy> findBys, String name, DomainFinderType type) {
		findByStub.getDomainFinder().setType(type);
		findByStub.getDomainFinder().setName(name);
		for (FindBy findBy : findBys) {
			findBy.getDomainFinder().setType(type);
			findBy.getDomainFinder().setName(name);
		}
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return null;
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

		// Find By Service
		FindByServiceWizardPage page = FindByServicePattern.getWizardPage(findByStub, getEditWizard());
		if (page == null) {
			return false;
		}

		// Push any new values to the FindByStub object
		final DomainFinderType type;
		final String name;
		if (page.getModel().getEnableServiceName()) {
			type = DomainFinderType.SERVICENAME;
			name = page.getModel().getServiceName();
		} else if (page.getModel().getEnableServiceType()) {
			type = DomainFinderType.SERVICETYPE;
			name = page.getModel().getServiceType();
		} else {
			return false;
		}

		final List<String> usesPortNames = page.getModel().getUsesPortNames();
		final List<String> providesPortNames = page.getModel().getProvidesPortNames();

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				setNameAndType(findByStub, getModelFindBys(findByStub), name, type);
				// Update the domain finder of all associated findBy model elements
				String connectId = null;
				for (Property prop : findByPE.getProperties()) {
					if (AbstractConnectInterfacePattern.CONNECT_INTERFACE_ID.equals(prop.getKey())) {
						connectId = prop.getValue();
						Connections< ? > modelConnections = getModelConnections();
						for (ConnectInterface<?, ?, ?> ci : modelConnections.getConnectInterface()) {
							if (connectId.equals(ci.getId())) {
								FindBy fb = null;
								if (ci.getProvidesPort() != null) {
									fb = ci.getProvidesPort().getFindBy();
								} else {
									fb = ci.getComponentSupportedInterface().getFindBy();
								}
								if (fb != null) {
									fb.getDomainFinder().setName(name);
									fb.getDomainFinder().setType(type);
								}
								
								fb = ci.getUsesPort().getFindBy();
								if (fb != null) {
									fb.getDomainFinder().setName(name);
									fb.getDomainFinder().setType(type);
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
		wizard.setWindowTitle(Messages.FindByServicePattern_WizardTitle);
		return wizard;
	}
}
