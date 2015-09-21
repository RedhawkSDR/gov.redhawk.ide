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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import gov.redhawk.diagram.util.FindByStubUtil;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByServiceWizardPage;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByServicePattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "Service";
	public static final String FIND_BY_SERVICE_NAME = "Service Name";

	public FindByServicePattern() {
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
		return ImageProvider.IMG_FIND_BY_SERVICE;
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

		// get user selections
		final String serviceNameText = page.getModel().getEnableServiceName() ? page.getModel().getServiceName() : null;
		final String serviceTypeText = page.getModel().getEnableServiceType() ? page.getModel().getServiceType() : null;
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null && !page.getModel().getUsesPortNames().isEmpty()) ? page.getModel().getUsesPortNames()
			: null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null && !page.getModel().getProvidesPortNames().isEmpty()) ? page.getModel().getProvidesPortNames()
			: null;

		// create new business object
		FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();

		// interface stub (lollipop)
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

		// domain finder service of type domain manager
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		findByStub.setDomainFinder(domainFinder);
		if (serviceNameText != null && !serviceNameText.isEmpty()) {
			domainFinder.setType(DomainFinderType.SERVICENAME);
			domainFinder.setName(serviceNameText);
		} else if (serviceTypeText != null && !serviceTypeText.isEmpty()) {
			domainFinder.setType(DomainFinderType.SERVICETYPE);
			domainFinder.setName(serviceTypeText);
		}

		// if applicable add uses port stub(s)
		if (usesPortNames != null) {
			for (String usesPortName : usesPortNames) {
				UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
				usesPortStub.setName(usesPortName);
				findByStub.getUses().add(usesPortStub);
			}
		}

		// if applicable add provides port stub(s)
		if (providesPortNames != null) {
			for (String providesPortName : providesPortNames) {
				ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
				providesPortStub.setName(providesPortName);
				findByStub.getProvides().add(providesPortStub);
			}
		}

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
		final FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		findByStub.setDomainFinder(domainFinder);
		domainFinder.setType(DomainFinderType.SERVICENAME);
		domainFinder.setName(serviceName);
		return findByStub;
	}

	/**
	 * Creates the FindByStub in the diagram with the provided service type.
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. Its
	 * purpose is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param serviceType
	 * @return
	 */
	public static FindByStub createFindByServiceType(String serviceType) {
		final FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		findByStub.setDomainFinder(domainFinder);
		domainFinder.setType(DomainFinderType.SERVICETYPE);
		domainFinder.setName(serviceType);
		return findByStub;
	}

	protected static FindByServiceWizardPage getWizardPage() {
		return getWizardPage(null, new Wizard() {
			public boolean performFinish() {
				return true;
			}
		});
	}

	public static FindByServiceWizardPage getWizardPage(FindByStub existingFindByStub, Wizard wizard) {
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
		String displayOuterText = "";
		if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME)) {
			displayOuterText = NAME + " Name";
		} else if (findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE)) {
			displayOuterText = NAME + " Type";
		}
		return displayOuterText;
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
	}
	
	@Override
	public void setInnerTitle(FindByStub findByStub, String value) {
		findByStub.getDomainFinder().setName(value);
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return null;
	}

	@Override
	public boolean update(IUpdateContext context) {
		// TODO: Catch calls from the edit context wizard
		return super.update(context);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// TODO: Catch calls from the edit context wizard
		return super.updateNeeded(context);
	}
	
	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}
