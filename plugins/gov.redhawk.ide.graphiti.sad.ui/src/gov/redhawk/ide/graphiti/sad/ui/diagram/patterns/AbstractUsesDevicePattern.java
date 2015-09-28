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
import gov.redhawk.ide.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

import ExtendedCF.WKP.DEVICEKIND;
import FRONTEND.FE_TUNER_DEVICE_KIND;

public abstract class AbstractUsesDevicePattern extends AbstractContainerPattern implements IPattern {

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
	public String getOuterTitle(EObject obj) {
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
	public String getInnerTitle(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return getInnerTitle((UsesDeviceStub) obj);
		}
		return null;
	}
	
	
	public String getInnerTitle(UsesDeviceStub usesDeviceStub) {
		return usesDeviceStub.getUsesDevice().getId();
	}

	
	public void setInnerTitle(UsesDeviceStub usesDeviceStub, String value) {
		usesDeviceStub.getUsesDevice().setId(value);
	}


	@Override
	public PictogramElement add(IAddContext context) {
		// create shape
		RHContainerShape rhContainerShape = RHGxFactory.eINSTANCE.createRHContainerShape();

		// initialize shape contents
		rhContainerShape.init(context, this);

		// set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(rhContainerShape.getGraphicsAlgorithm(), context.getX(), context.getY());

		// layout
		layoutPictogramElement(rhContainerShape);

		return rhContainerShape;
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
	public boolean canLayout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof UsesDeviceStub) {
			return true;
		}
		return false;
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		((RHContainerShape) context.getPictogramElement()).layout();

		// something is always changing.
		return true;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason updated = ((RHContainerShape) context.getPictogramElement()).update(context, this);

		// if we updated redraw
		if (updated.toBoolean()) {
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}

	/**
	 * Determines whether we need to update the diagram from the model.
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return ((RHContainerShape) context.getPictogramElement()).updateNeeded(context, this);
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
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return super.checkValueValid(value, context);
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		UsesDeviceStub usesDeviceStub = (UsesDeviceStub) getBusinessObjectForPictogramElement(rhContainerShape);
		return getInnerTitle(usesDeviceStub);
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final UsesDeviceStub usesDeviceStub = (UsesDeviceStub) getBusinessObjectForPictogramElement(rhContainerShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				setInnerTitle(usesDeviceStub, value);
			}
		});

		// perform update, redraw
		updatePictogramElement(rhContainerShape);
	}
	

	
	
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape containerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(containerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof UsesDeviceStub && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
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
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}

	@Override
	public String getInnerImageId() {
		return ImageProvider.IMG_USES_DEVICE_FRONTEND_TUNER;
	}

	@Override
	public Style createStyleForOuter() {
		return StyleUtil.getStyle(StyleUtil.USES_DEVICE_OUTER);
	}

	@Override
	public Style createStyleForInner() {
		return StyleUtil.getStyle(StyleUtil.USES_DEVICE_INNER);
	}

	@Override
	public List<EObject> getBusinessObjectsToLink(EObject obj) {
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		businessObjectsToLink.add(obj);
		return businessObjectsToLink;
	}
	
	@Override
	public EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getUsesPortStubs();
		}
		return null;
	}

	@Override
	public EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getProvidesPortStubs();
		}
		return null;
	}
	
	@Override
	public ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof UsesDeviceStub) {
			return ((UsesDeviceStub) obj).getInterface();
		}
		return null;
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
	public static void updatePorts(final IFeatureProvider featureProvider, final UsesDeviceStub usesDeviceStub, final RHContainerShapeImpl usesDeviceShape, final List<String> usesPortNames,
		final List<String> providesPortNames) {
		
		updateUsesPortStubs(featureProvider, usesDeviceStub, usesDeviceShape, usesPortNames);
		
		updateProvidesPortStubs(featureProvider, usesDeviceStub, usesDeviceShape, providesPortNames);
		
		// Update the shape layout to account for any changes
		usesDeviceShape.layout();
	}
	
	/**
	 * Remove all old UsesPortStubs ports and add new port names
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	public static void updateUsesPortStubs(final IFeatureProvider featureProvider, final UsesDeviceStub usesDeviceStub, 
		final RHContainerShapeImpl usesDeviceShape, final List<String> usesPortNames) {

		final Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();
		
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
		usesDeviceShape.addNewUsesPorts(usesPortStubs, featureProvider, null);
		usesDeviceStub.getUsesPortStubs().addAll(usesPortStubs);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor sourceAnchor = DUtil.getUsesAnchor(diagram, usesPortStubs, cursor.getValue());
			if (sourceAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(sourceAnchor);
				createContext.setTargetAnchor(cursor.getKey().getEnd());

				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
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
	public static void updateProvidesPortStubs(final IFeatureProvider featureProvider, final UsesDeviceStub usesDeviceStub, 
		final RHContainerShapeImpl usesDeviceShape, final List<String> providesPortNames) {
		
		final Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();
		
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
		usesDeviceShape.addNewProvidesPorts(providesPortStubs, featureProvider, null);
		usesDeviceStub.getProvidesPortStubs().addAll(providesPortStubs);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor targetAnchor = DUtil.getProvidesAnchor(diagram, providesPortStubs, cursor.getValue());
			if (targetAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(cursor.getKey().getStart());
				createContext.setTargetAnchor(targetAnchor);

				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
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
