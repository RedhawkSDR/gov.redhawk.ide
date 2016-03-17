/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.internal.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Display;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.internal.command.DeleteCommand;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;

public abstract class AbstractGraphitiModelMap {

	private AbstractGraphitiMultiPageEditor editor;

	protected AbstractGraphitiModelMap(AbstractGraphitiMultiPageEditor editor) {
		Assert.isNotNull(editor, "Editor must not be null");
		this.editor = editor;
	}

	/**
	 * Deletes a ComponentInstantiation from the diagram.
	 * @param componentInstantiation
	 */
	protected void delete(final ComponentInstantiation componentInstantiation) {
		delete(componentInstantiation, RHContainerShape.class);
	}

	/**
	 * Delete a ConnectInterface from the diagram.
	 * @param connection
	 */
	protected void delete(@Nullable final ConnectInterface< ? , ? , ? > connection) {
		delete(connection, Connection.class);
	}

	/**
	 * @param deleteObj The business model object being deleted
	 * @param peClass The type of the pictogram element for the model object
	 */
	private < T extends PictogramElement > void delete(final EObject deleteObj, final Class<T> peClass) {
		if (deleteObj == null || editor.isDisposed()) {
			return;
		}

		// Run Graphiti model commands in the UI thread
		if (Display.getCurrent() == null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					delete(deleteObj, peClass);
				}
			});
			return;
		}

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();
		boolean runtime = DUtil.isDiagramRuntime(diagram);

		// Get the PictogramElement
		final PictogramElement pictogramElement = DUtil.getPictogramElementForBusinessObject(diagram, deleteObj, peClass);
		if (pictogramElement == null) {
			return;
		}

		// Run the delete feature in a transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new DeleteCommand(editingDomain, pictogramElement, featureProvider, runtime));
	}
}
