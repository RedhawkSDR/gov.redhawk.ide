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
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DeleteFeatureForPattern;
import org.eclipse.graphiti.pattern.IFeatureProviderWithPatterns;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Display;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
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
		if (componentInstantiation == null || editor.isDisposed()) {
			return;
		}

		// Run Graphiti model commands in the UI thread
		if (Display.getCurrent() == null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					delete(componentInstantiation);
				}
			});
		}

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// Get the PictogramElement
		final PictogramElement[] peToRemove = { DUtil.getPictogramElementForBusinessObject(diagram, componentInstantiation, RHContainerShape.class) };
		if (peToRemove.length == 0 || peToRemove[0] == null) {
			return;
		}

		// Run the delete feature in a transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// Create an instance of DeleteFeatureForPattern. The normal delete feature is for triggering a
				// releaseObject() CORBA call. This way we delete the diagram model object, and the diagram graphical
				// objects.
				DeleteContext context = new DeleteContext(peToRemove[0]);
				IFeatureProviderWithPatterns fp = (IFeatureProviderWithPatterns) featureProvider;
				IPattern pattern = fp.getPatternForPictogramElement(peToRemove[0]);
				DeleteFeatureForPattern deleteFeature = new DeleteFeatureForPattern(featureProvider, pattern);
				deleteFeature.delete(context);
			}
		});
	}

	/**
	 * Delete a ConnectInterface from the diagram.
	 * @param connection
	 */
	protected void delete(@Nullable final ConnectInterface< ? , ? , ? > connection) {
		if (connection == null || editor.isDisposed()) {
			return;
		}

		// Run Graphiti model commands in the UI thread
		if (Display.getCurrent() == null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					delete(connection);
				}
			});
		}

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// Get the PictogramElement
		final PictogramElement peToRemove = DUtil.getPictogramElementForBusinessObject(diagram, connection, Connection.class);

		// Run the delete feature in a transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// delete connection shape & connection business object
				DeleteContext dc = new DeleteContext(peToRemove);
				IDeleteFeature deleteFeature = featureProvider.getDeleteFeature(dc);
				if (deleteFeature != null) {
					deleteFeature.delete(dc);
				}
			}
		});
	}
}
