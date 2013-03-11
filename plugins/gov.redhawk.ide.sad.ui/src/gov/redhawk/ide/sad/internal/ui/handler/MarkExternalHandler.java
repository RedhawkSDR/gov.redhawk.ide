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
package gov.redhawk.ide.sad.internal.ui.handler;

import gov.redhawk.diagram.edit.parts.EditPartUtil;
import gov.redhawk.ui.sad.editor.presentation.SadEditor;

import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.diagram.edit.parts.ProvidesPortStubEditPart;
import mil.jpeojtrs.sca.sad.diagram.edit.parts.UsesPortStubEditPart;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 */
public class MarkExternalHandler extends AbstractHandler implements IElementUpdater {

	/**
	 * {@inheritDoc}
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final IStructuredSelection ss = (IStructuredSelection) selection;
		for (final Object obj : ss.toArray()) {
			final Object selectedElement = obj;
			Port port = null;
			SadComponentInstantiation componentInstantiation = null;

			if (selectedElement instanceof EditPart && ((EditPart) selectedElement).getModel() instanceof Node) {
				final Node node = (Node) ((EditPart) selectedElement).getModel();
				if (node.getElement() instanceof EObject && (node.getElement()).eContainer() instanceof SadComponentInstantiation) {
					componentInstantiation = (SadComponentInstantiation) (node.getElement()).eContainer();
				}
			}
			if (componentInstantiation == null) {
				return null;
			}

			EObject componentPort = null;

			final SoftwareAssembly softwareAssembly = SoftwareAssembly.Util.getSoftwareAssembly(componentInstantiation.eResource());

			if (selectedElement instanceof UsesPortStubEditPart) {
				final UsesPortStubEditPart editPart = (UsesPortStubEditPart) selectedElement;
				final UsesPortStub uses = (UsesPortStub) ((View) editPart.getModel()).getElement();
				port = findInExternalPorts(uses);
				componentPort = uses;
			} else if (selectedElement instanceof ProvidesPortStubEditPart) {
				final ProvidesPortStubEditPart editPart = (ProvidesPortStubEditPart) selectedElement;
				final ProvidesPortStub provides = (ProvidesPortStub) ((View) editPart.getModel()).getElement();
				port = findInExternalPorts(provides);
				componentPort = provides;
			}
			//			else if (selectedElement instanceof ComponentSupportedInterfaceStubEditPart) {
			//				final ComponentSupportedInterfaceStubEditPart editPart = (ComponentSupportedInterfaceStubEditPart) selectedElement;
			//				final ComponentSupportedInterfaceStub interfacePort = (ComponentSupportedInterfaceStub) ((View) editPart.getModel()).getElement();
			//				port = findInExternalPorts(interfacePort);
			//				componentPort = interfacePort;
			//			}
			if (port != null) {
				final RemoveExternalPortAction action = new RemoveExternalPortAction();
				action.setPort(port);
				action.run();
			} else {
				final AddExternalPortAction action = new AddExternalPortAction();
				action.setComponentInstantiation(componentInstantiation);
				action.setComponentPort(componentPort);
				action.setSoftwareAssembly(softwareAssembly);
				action.run();
			}

		}
		return null;
	}

	public void updateElement(final UIElement element, final Map parameters) {
		final Object partSite = parameters.get("org.eclipse.ui.part.IWorkbenchPartSite");
		final ISelection currentSelection = ((IWorkbenchSite) partSite).getSelectionProvider().getSelection();
		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) currentSelection;
			final Object selectedElement = ss.getFirstElement();
			if (selectedElement instanceof EditPart) {
				final Object semanticElement = EditPartUtil.getSemanticModelObject((EditPart) selectedElement);
				if (semanticElement instanceof EObject && ((EObject) semanticElement).eContainer() instanceof FindByStub) {
					return;
				}
			}
			Port port = null;
			if (selectedElement instanceof UsesPortStubEditPart) {
				final UsesPortStubEditPart editPart = (UsesPortStubEditPart) selectedElement;
				final UsesPortStub uses = (UsesPortStub) ((View) editPart.getModel()).getElement();
				port = findInExternalPorts(uses);
			} else if (selectedElement instanceof ProvidesPortStubEditPart) {
				final ProvidesPortStubEditPart editPart = (ProvidesPortStubEditPart) selectedElement;
				final ProvidesPortStub provides = (ProvidesPortStub) ((View) editPart.getModel()).getElement();
				port = findInExternalPorts(provides);
			}
			//			else if (selectedElement instanceof ComponentSupportedInterfaceStubEditPart) {
			//				final ComponentSupportedInterfaceStubEditPart editPart = (ComponentSupportedInterfaceStubEditPart) selectedElement;
			//				final ComponentSupportedInterfaceStub interfacePort = (ComponentSupportedInterfaceStub) ((View) editPart.getModel()).getElement();
			//				port = findInExternalPorts(interfacePort);
			//			}

			element.setChecked(port != null);
		}

	}

	private Port findInExternalPorts(final UsesPortStub uses) {
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(uses.eResource());
		final String usesName = uses.getUses().getUsesName();
		if (sad.getExternalPorts() != null) {
			for (final Port port : sad.getExternalPorts().getPort()) {
				final String id = port.getUsesIdentifier();
				if (uses.eContainer() == port.getComponentInstantiationRef().getInstantiation() && usesName.equals(id)) {
					return port;
				}
			}
		}
		return null;
	}

	private Port findInExternalPorts(final ProvidesPortStub provides) {
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(provides.eResource());
		final String providesName = provides.getProvides().getProvidesName();
		if (sad.getExternalPorts() != null) {
			for (final Port port : sad.getExternalPorts().getPort()) {
				final String id = port.getProvidesIndentifier();
				if (provides.eContainer() == port.getComponentInstantiationRef().getInstantiation() && providesName.equals(id)) {
					return port;
				}
			}
		}
		return null;
	}

	private Port findInExternalPorts(final ComponentSupportedInterfaceStub interfacePort) {
		// TODO
		//		final SoftwareAssembly sad = (SoftwareAssembly) EcoreUtil.getRootContainer(interfacePort);
		//		final String portName = interfacePort.getInterface().getSupportsName();
		//		if (sad.getExternalPorts() != null) {
		//			for (final Port port : sad.getExternalPorts().getPort()) {
		//				final String id = port.getSupportedIdentifier();
		//				if (interfacePort.eContainer() == port.getComponentInstantiationRef().getInstantiation() && portName.equals(id)) {
		//					return port;
		//				}
		//			}
		//		}
		return null;
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		if ((evaluationContext != null) && (evaluationContext instanceof EvaluationContext)) {
			final EvaluationContext context = (EvaluationContext) evaluationContext;
			final Object sel = context.getVariable("selection");
			if (sel instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) sel;
				boolean enabled = true;
				for (final Object obj : ss.toArray()) {
					if (obj instanceof IGraphicalEditPart) {
						final EditorPart editor = (EditorPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

						if ((editor == null) || (editor.getEditorSite().getId().equals(SadEditor.ID))) {
							enabled = false;
							break;
						}

						final EObject semanticObject = EditPartUtil.getSemanticModelObject((EditPart) obj);
						if (semanticObject == null || semanticObject instanceof ComponentSupportedInterfaceStub
						        || semanticObject.eContainer() instanceof FindByStub) {
							enabled = false;
							break;
						}
						final IGraphicalEditPart editPart = (IGraphicalEditPart) obj;
						if (!editPart.isEditModeEnabled()) {
							enabled = false;
							break;
						}
					}
				}
				this.setBaseEnabled(enabled);
			}
		} else {
			super.setEnabled(evaluationContext);
		}
	};
}
