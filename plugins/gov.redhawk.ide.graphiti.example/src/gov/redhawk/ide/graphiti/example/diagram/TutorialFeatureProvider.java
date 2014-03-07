/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.diagram;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import gov.redhawk.ide.graphiti.example.features.TutorialAddEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialAddEReferenceFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialAssociateDiagramEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialChangeColorEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialCollapseDummyFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialCopyEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialCreateEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialCreateEReferenceFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialDirectEditEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialDrillDownEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialLayoutEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialMoveEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialPasteEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialReconnectionFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialRenameEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialResizeEClassFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialUpdateEClassFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;


public class TutorialFeatureProvider extends DefaultFeatureProvider {

	public TutorialFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass or EReference?
		if (context.getNewObject() instanceof EClass) {
			return new TutorialAddEClassFeature(this);
		} else if (context.getNewObject() instanceof EReference) {
			return new TutorialAddEReferenceFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new TutorialCreateEClassFeature(this) };
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof EClass) {
				return new TutorialUpdateEClassFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof EClass) {
			return new TutorialMoveEClassFeature(this);
		}
		return super.getMoveShapeFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof EClass) {
			return new TutorialResizeEClassFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof EClass) {
			return new TutorialLayoutEClassFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new TutorialRenameEClassFeature(this), new TutorialDrillDownEClassFeature(this),
				new TutorialAssociateDiagramEClassFeature(this), new TutorialCollapseDummyFeature(this),
				new TutorialChangeColorEClassFeature(this) };
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new TutorialCreateEReferenceFeature(this) };
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof EClass) {
			return new TutorialDirectEditEClassFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		return new TutorialCopyEClassFeature(this);
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		return new TutorialPasteEClassFeature(this);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		return new TutorialReconnectionFeature(this);
	}
	
	
}
