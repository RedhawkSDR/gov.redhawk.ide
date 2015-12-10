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
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractPortSupplierPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.DeviceUsedByApplication;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.UsesDeviceDependencies;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import ExtendedCF.WKP.DEVICEKIND;
import FRONTEND.FE_TUNER_DEVICE_KIND;

public abstract class AbstractUsesDevicePattern extends AbstractPortSupplierPattern implements IDialogEditingPattern {

	public AbstractUsesDevicePattern() {
		super(null);
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	public abstract boolean isMainBusinessObjectApplicable(Object mainBusinessObject);

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	// DIAGRAM FEATURES
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof UsesDeviceStub) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String getOuterTitle(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return getOuterTitle((UsesDeviceStub) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the outer shape
	 * @param usesDeviceStub
	 * @return
	 */
	public String getOuterTitle(UsesDeviceStub usesDeviceStub) {
		return getCreateName();
	}
	
	@Override
	protected String getInnerTitle(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return getInnerTitle((UsesDeviceStub) obj);
		}
		return null;
	}
	
	public String getInnerTitle(UsesDeviceStub usesDeviceStub) {
		return usesDeviceStub.getUsesDevice().getId();
	}

	protected void setInnerTitle(EObject businessObject, String value) {
		UsesDeviceStub usesDeviceStub = (UsesDeviceStub) businessObject;
		usesDeviceStub.getUsesDevice().setId(value);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public abstract Object[] create(ICreateContext context);

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		if (obj instanceof UsesDeviceStub) {
			return true;
		}
		return super.canDelete(context);
	}

	@Override
	public void delete(IDeleteContext context) {
		// set UsesDeviceStub to delete
		final UsesDeviceStub usesDeviceStub = (UsesDeviceStub) DUtil.getBusinessObject(context.getPictogramElement());

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				
				//remove device connections
				deleteUsesDeviceStubConnections(usesDeviceStub, sad);
				
				//remove device
				for (Iterator<UsesDevice> iter = sad.getUsesDeviceDependencies().getUsesdevice().iterator(); iter.hasNext();) {
					UsesDevice s = iter.next();
					if (usesDeviceStub.getUsesDevice().getId().equals(s.getId())) {
						iter.remove();
					}
				}
				
				//remove usesdevicedependencies if there are none left
				if (sad.getUsesDeviceDependencies().getUsesdevice().size() < 1) {
					EcoreUtil.delete(sad.getUsesDeviceDependencies());
				}
				
			}
		});

		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjects = getFeatureProvider().getAllBusinessObjectsForPictogramElement(pe);

		preDelete(context);
		if (businessObjects != null) {
			for (Object bo : businessObjects) {
				if (bo instanceof EObject) {
					EcoreUtil.delete((EObject) bo, true);
				}
			}
		}
		postDelete(context);

		super.delete(context);
	}
	
	private void deleteUsesDeviceStubConnections(UsesDeviceStub usesDeviceStubToDelete, SoftwareAssembly sad) {
		// find and remove any attached connections
		// gather connections
		List<SadConnectInterface> connectionsToRemove = new ArrayList<SadConnectInterface>();
		if (sad.getConnections() == null) {
			return;
		}
		
		
		for (SadConnectInterface connection : sad.getConnections().getConnectInterface()) {
			if (connection.getProvidesPort() != null && connection.getProvidesPort().getDeviceUsedByApplication() != null
				&& connection.getProvidesPort().getDeviceUsedByApplication().getUsesRefId().equals(usesDeviceStubToDelete.getUsesDevice().getId())) {
				connectionsToRemove.add(connection);
			} else if (connection.getComponentSupportedInterface() != null && connection.getComponentSupportedInterface().getDeviceUsedByApplication() != null
					&& connection.getComponentSupportedInterface().getDeviceUsedByApplication().getUsesRefId().equals(usesDeviceStubToDelete.getUsesDevice().getId())) {
					connectionsToRemove.add(connection);
			} else if (connection.getUsesPort().getDeviceUsedByApplication() != null
					&& connection.getUsesPort().getDeviceUsedByApplication().getUsesRefId().equals(usesDeviceStubToDelete.getUsesDevice().getId())) {
				connectionsToRemove.add(connection);
			}
		}
		

		// remove gathered connections
		if (sad.getConnections() != null) {
			sad.getConnections().getConnectInterface().removeAll(connectionsToRemove);
		}
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return super.checkValueValid(value, context);
	}

	/**
	 * Returns the {@link Diagram} this pattern lives for.
	 * 
	 * @return The diagram
	 */
	public Diagram getDiagram() {
		return getFeatureProvider().getDiagramTypeProvider().getDiagram();
	}


	@Override
	protected String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}

	@Override
	protected String getInnerImageId() {
		return ImageProvider.IMG_USES_DEVICE_FRONTEND_TUNER;
	}

	@Override
	protected String getStyleForOuter() {
		return StyleUtil.OUTER_SHAPE;
	}

	@Override
	protected String getStyleForInner() {
		return StyleUtil.USES_DEVICE_INNER;
	}

	@Override
	protected List<EObject> getBusinessObjectsToLink(EObject obj) {
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		businessObjectsToLink.add(obj);
		return businessObjectsToLink;
	}
	
	@Override
	protected EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getUsesPortStubs();
		}
		return null;
	}

	@Override
	protected EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getProvidesPortStubs();
		}
		return null;
	}
	
	@Override
	protected ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getInterface();
		}
		return null;
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
	public void dialogEdit(ICustomContext context) {
		RHContainerShape usesDeviceShape = (RHContainerShape) context.getPictogramElements()[0];
		final UsesDeviceStub usesDevice = (UsesDeviceStub) getBusinessObjectForPictogramElement(usesDeviceShape);
		editUsesDevice(usesDevice, usesDeviceShape);
		updatePictogramElement(usesDeviceShape);
		layoutPictogramElement(usesDeviceShape);
	}

	protected abstract void editUsesDevice(UsesDeviceStub usesDevice, RHContainerShape usesDeviceShape);

	protected <E extends Wizard> E openWizard(E wizard) {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return wizard;
	}

	/**
	 * Return true if containerShape is linked to UsesDeviceStub
	 * @param containerShape
	 * @return
	 */
	public static boolean isUsesDeviceStubShape(ContainerShape containerShape) {
		if (containerShape instanceof RHContainerShape) {
			Object obj = DUtil.getBusinessObject(containerShape, UsesDeviceStub.class);
			if (obj != null && obj instanceof UsesDeviceStub) {
				return true;
			}
		}
		return false;
	}
 	
	/**
	 * Returns all containerShapes linked to UsesDeviceStub
	 * @param containerShape
	 * @return
	 */
	public static List<RHContainerShape> getAllUsesDeviceStubShapes(ContainerShape containerShape) {
		List<RHContainerShape> children = new ArrayList<RHContainerShape>();
		if (containerShape instanceof RHContainerShape
				&& isUsesDeviceStubShape(containerShape)) {
			children.add((RHContainerShape) containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllUsesDeviceStubShapes((ContainerShape) s));
				}
			}
		}
		return children;

	}
	
	

	
	/**
	 * Create UsesDeviceStrub from UsesDevice
	 * Initializes componentSupportedInterface, be sure to set ports afterwards if applicable
	 * @param usesDevice
	 * @return
	 */
	public static UsesDeviceStub createUsesDeviceStub(UsesDevice usesDevice) {
		
		UsesDeviceStub usesDeviceStrub = PartitioningFactory.eINSTANCE.createUsesDeviceStub();
		usesDeviceStrub.setUsesDevice(usesDevice);
		usesDeviceStrub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());
		return usesDeviceStrub;
	}

	/**
	 * Returns true if the deviceUsedByApplication's usesref id matches the device id of usesDeviceStub
	 * @param deviceUsedByApplication
	 * @param usesDeviceStub
	 * @return
	 */
	public static boolean doDeviceUsedByApplicationObjectsMatch(DeviceUsedByApplication deviceUsedByApplication, UsesDeviceStub usesDeviceStub) {

		if (deviceUsedByApplication.getUsesRefId().equals(usesDeviceStub.getUsesDevice().getId())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Add necessary ports to UsesDeviceStub based on connections in model
	 * @return
	 */
	public static void addUsesDeviceStubPorts(List<SadConnectInterface> sadConnectInterfaces, List<UsesDeviceStub> usesDeviceStubs) {
		
		// look for usesdevice in connections to add
		for (SadConnectInterface sadConnectInterface : sadConnectInterfaces) {

			// usesdevice is always used inside usesPort
			if (sadConnectInterface.getUsesPort() != null && sadConnectInterface.getUsesPort().getDeviceUsedByApplication() != null) {

				// get usesdevice model object
				DeviceUsedByApplication deviceUsedByApplication = (DeviceUsedByApplication) sadConnectInterface.getUsesPort().getDeviceUsedByApplication();

				// search for usesdevicestub in the list
				UsesDeviceStub usesDeviceStub = findUsesDeviceStub(deviceUsedByApplication, usesDeviceStubs);

				// add provides port to stub if doesn't already exist
				boolean uPFound = false;
				for (UsesPortStub p : usesDeviceStub.getUsesPortStubs()) {
					if (p.equals(sadConnectInterface.getUsesPort())) {
						uPFound = true;
					}
				}
				if (!uPFound) {
					UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
					usesPortStub.setName(sadConnectInterface.getUsesPort().getUsesIdentifier());
					usesDeviceStub.getUsesPortStubs().add(usesPortStub);
				}
			} else if (sadConnectInterface.getProvidesPort() != null && sadConnectInterface.getProvidesPort().getDeviceUsedByApplication() != null) {

				DeviceUsedByApplication deviceUsedByApplication = (DeviceUsedByApplication) sadConnectInterface.getProvidesPort().getDeviceUsedByApplication();

				// search for findByStub in the list
				UsesDeviceStub usesDeviceStub = findUsesDeviceStub(deviceUsedByApplication, usesDeviceStubs);

				// add provides port to stub if doesn't already exist
				boolean ppFound = false;
				for (ProvidesPortStub p : usesDeviceStub.getProvidesPortStubs()) {
					if (p.equals(sadConnectInterface.getProvidesPort())) {
						ppFound = true;
					}
				}
				if (!ppFound) {
					ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
					providesPortStub.setName(sadConnectInterface.getProvidesPort().getProvidesIdentifier());
					usesDeviceStub.getProvidesPortStubs().add(providesPortStub);
				}
			}
		}
	}
	
	/**
	 * Search for the UsesDeviceStub in the diagram given the DeviceUsedByApplication object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	public static UsesDeviceStub findUsesDeviceStub(DeviceUsedByApplication deviceUsedByApplication, List<UsesDeviceStub> usesDeviceStubs) {
		for (UsesDeviceStub usesDeviceStub : usesDeviceStubs) {
			if (AbstractUsesDevicePattern.doDeviceUsedByApplicationObjectsMatch(deviceUsedByApplication, usesDeviceStub)) {
				// it matches
				return usesDeviceStub;
			}
		}
		return null;
	}
	
	/**
	 * Returns matching UsesDeviceStub for provided DeviceUsedByApplication
	 * @param deviceUsedByApplication
	 * @param containerShape - diagram
	 * @return
	 */
	public static UsesDeviceStub findUsesDeviceStub(DeviceUsedByApplication deviceUsedByApplication, ContainerShape containerShape) {
		
		for (RHContainerShape usesDeviceStubShape:  getAllUsesDeviceStubShapes(containerShape)) {
			UsesDeviceStub usesDeviceStub = (UsesDeviceStub) DUtil.getBusinessObject(usesDeviceStubShape);
			if (doDeviceUsedByApplicationObjectsMatch(deviceUsedByApplication, usesDeviceStub)) {
				return usesDeviceStub;
			}
		}
		
		return null;
	}
	
	/**
	 * Return true if the proposedId is unique device in the sad file
	 * @param sad
	 * @param proposedDeviceId
	 * @return
	 */
	public static boolean isUsesDeviceIdUnique(SoftwareAssembly sad, String existingId, String proposedDeviceId) {

		if (existingId.equals(proposedDeviceId)) {
			//if existing name return true;
			return true;
		}
		
		UsesDeviceDependencies usesDeviceDependencies = sad.getUsesDeviceDependencies();
		if (usesDeviceDependencies == null || usesDeviceDependencies.getUsesdevice().size() < 1) {
			return true;
		}
		
		for (UsesDevice usesDevice: usesDeviceDependencies.getUsesdevice()) {
			if (usesDevice.getId().equals(proposedDeviceId)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Create unique device id from provided prefix
	 * @param sad
	 * @param idPrefix
	 * @return
	 */
	public static String getUniqueUsesDeviceId(SoftwareAssembly sad, String idPrefix) {
		int suffixNum = 1;
		String proposedDeviceId = idPrefix + String.valueOf(suffixNum);
		UsesDeviceDependencies usesDeviceDependencies = sad.getUsesDeviceDependencies();
		if (usesDeviceDependencies == null || usesDeviceDependencies.getUsesdevice().size() < 1) {
			return proposedDeviceId;
		}
		
		
		while (true) {
			boolean found = false;
			for (UsesDevice usesDevice: usesDeviceDependencies.getUsesdevice()) {
				if (usesDevice.getId().equals(proposedDeviceId)) {
					found = true;
					break;
				}
			}
			if (found) {
				suffixNum++;
				proposedDeviceId = idPrefix + String.valueOf(suffixNum);
			} else {
				break;
			}
		}
		
		return proposedDeviceId;
	}
	
	/**
	 * Remove all old ports, add new names
	 * Perform layout
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	protected void updatePorts(final UsesDeviceStub usesDeviceStub, final RHContainerShape usesDeviceShape, final List<String> usesPortNames, final List<String> providesPortNames) {
		
		updateUsesPortStubs(usesDeviceStub, usesDeviceShape, usesPortNames);
		
		updateProvidesPortStubs(usesDeviceStub, usesDeviceShape, providesPortNames);
		
		// Update the shape layout to account for any changes
		layoutPictogramElement(usesDeviceShape);
	}
	
	/**
	 * Remove all old UsesPortStubs ports and add new port names
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	protected void updateUsesPortStubs(UsesDeviceStub usesDeviceStub, RHContainerShape usesDeviceShape, List<String> usesPortNames) {
		Diagram diagram = getDiagram();
		
		// Mark the ports to delete
		List<UsesPortStub> portsToDelete = new ArrayList<UsesPortStub>();
		for (UsesPortStub uses : usesDeviceStub.getUsesPortStubs()) {
			portsToDelete.add(uses);
		}

		// Capture the existing connection information and delete the connection
		HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
		for (UsesPortStub portStub : portsToDelete) {
			Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
			EList<Connection> connections = portStubPe.getOutgoingConnections();
			if (!connections.isEmpty()) {
				for (Connection connection : connections) {
					oldConnectionMap.put(connection, portStub.getName());
				}
			}
		}

		// Add new ports to the element
		EList<UsesPortStub> usesPortStubs = new BasicEList<UsesPortStub>();
		for (String usesPortName : usesPortNames) {
			// Add the new port to the Domain model
			UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
			usesPortStub.setName(usesPortName);
			usesPortStubs.add(usesPortStub);
		}

		// Add the new ports to the Diagram model
		usesDeviceStub.getUsesPortStubs().addAll(usesPortStubs);
		updatePictogramElement(usesDeviceShape);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor sourceAnchor = DUtil.getUsesAnchor(diagram, usesPortStubs, cursor.getValue());
			if (sourceAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(sourceAnchor);
				createContext.setTargetAnchor(cursor.getKey().getEnd());

				ICreateConnectionFeature[] createConnectionFeatures = getFeatureProvider().getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				getFeatureProvider().getDeleteFeature(deleteContext).delete(deleteContext);
			}
		}
		// Delete all ports and rebuild from the list provided by the wizard
		usesDeviceStub.getUsesPortStubs().removeAll(portsToDelete);
	}
	
	/**
	 * Remove all old UsesPortStubs ports and add new port names
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	protected void updateProvidesPortStubs(UsesDeviceStub usesDeviceStub, RHContainerShape usesDeviceShape, List<String> providesPortNames) {
		Diagram diagram = getDiagram();
		
		// Mark the ports to delete
		List<ProvidesPortStub> portsToDelete = new ArrayList<ProvidesPortStub>();
		for (ProvidesPortStub provides : usesDeviceStub.getProvidesPortStubs()) {
			portsToDelete.add(provides);
		}

		// Capture the existing connection information and delete the connection
		HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
		for (ProvidesPortStub portStub : portsToDelete) {
			Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
			EList<Connection> connections = portStubPe.getIncomingConnections();
			if (!connections.isEmpty()) {
				for (Connection connection : connections) {
					oldConnectionMap.put(connection, portStub.getName());
				}
			}
		}

		// Add new ports to the element
		EList<ProvidesPortStub> providesPortStubs = new BasicEList<ProvidesPortStub>();
		for (String providesPortName : providesPortNames) {
			// Add the new port to the Domain model
			ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
			providesPortStub.setName(providesPortName);
			providesPortStubs.add(providesPortStub);
		}

		// Add the new ports to the Diagram model
		usesDeviceStub.getProvidesPortStubs().addAll(providesPortStubs);
		updatePictogramElement(usesDeviceShape);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor targetAnchor = DUtil.getProvidesAnchor(diagram, providesPortStubs, cursor.getValue());
			if (targetAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(cursor.getKey().getStart());
				createContext.setTargetAnchor(targetAnchor);

				ICreateConnectionFeature[] createConnectionFeatures = getFeatureProvider().getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				getFeatureProvider().getDeleteFeature(deleteContext).delete(deleteContext);
			}
		}
		// Delete all ports and rebuild from the list provided by the wizard
		usesDeviceStub.getProvidesPortStubs().removeAll(portsToDelete);
	}
	
	/**
	 * Return true if device is FrontEnd Tuner
	 * @param usesDevice
	 * @return
	 */
	public static boolean isFrontEndDevice(UsesDevice usesDevice) {
		
		for (PropertyRef refs: usesDevice.getPropertyRef()) {
			if (DEVICEKIND.value.equals(refs.getRefId()) && FE_TUNER_DEVICE_KIND.value.equals(refs.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	//Device must not be blank and be unique within sad
	public static class UsesDeviceIdValidator implements IValidator {
		private SoftwareAssembly sad;
		private String existingId;
		public UsesDeviceIdValidator(SoftwareAssembly sad, String existingId) {
			this.sad = sad;
			this.existingId = existingId;
		}
		public IStatus validate(Object value) {
			if ("".equals(value)) {
				return ValidationStatus.error("Device ID must not be null");
			} else if (!AbstractUsesDevicePattern.isUsesDeviceIdUnique(sad, existingId, (String) value)) {
				return ValidationStatus.error("Device ID must be unique");
			}
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * Return true if connection involves uses device
	 * @param connectInterface
	 * @return
	 */
	public static boolean isUsesDeviceConnection(SadConnectInterface connectInterface) {
		boolean isUsesDeviceConnection = false;
		if ((connectInterface.getComponentSupportedInterface() != null && connectInterface.getComponentSupportedInterface().getDeviceUsedByApplication() != null)
				|| (connectInterface.getProvidesPort() != null  && connectInterface.getProvidesPort().getDeviceUsedByApplication() != null)
				|| (connectInterface.getUsesPort() != null && connectInterface.getUsesPort().getDeviceUsedByApplication() != null)) {
			isUsesDeviceConnection = true;
		}
		return isUsesDeviceConnection;
	}

}
