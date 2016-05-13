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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Display;

import CF.DataType;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.internal.command.DeleteCommand;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public abstract class AbstractGraphitiModelMap {

	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	private AbstractGraphitiMultiPageEditor editor;

	protected AbstractGraphitiModelMap(AbstractGraphitiMultiPageEditor editor) {
		Assert.isNotNull(editor, "Editor must not be null");
		this.editor = editor;
	}

	/**
	 * Launch an instance of the specified SPD.
	 * @param ci The instantiation as per the SAD or DCD
	 * @param implID The implementation to launch, or null for any
	 * @param modelObjClass The type of the model object that should be returned
	 * @return
	 * @throws CoreException
	 */
	protected final < T extends LocalAbstractComponent > T launch(final ComponentInstantiation ci, final String implID, Class<T> modelObjClass)
		throws CoreException {
		DataType[] initConfiguration = null;
		if (ci.getComponentProperties() != null) {
			final List<DataType> params = new ArrayList<DataType>();
			for (final Entry entry : ci.getComponentProperties().getProperties()) {
				if (entry.getValue() instanceof AbstractPropertyRef) {
					final AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
					params.add(new DataType(ref.getRefID(), ref.toAny()));
				}
			}
			initConfiguration = params.toArray(new DataType[params.size()]);
		}

		final SoftPkg spd = ScaEcoreUtils.getFeature(ci, AbstractGraphitiModelMap.SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();

		LocalAbstractComponent abstractComponent = launch(ci.getId(), initConfiguration, spdURI, implID, ILaunchManager.RUN_MODE);
		return modelObjClass.cast(abstractComponent);
	}

	/**
	 * Launch an instance of the specified SPD. Subclasses should call
	 * {@link #launch(ComponentInstantiation, String, Class)} instead.
	 * @param id The ID of the instance
	 * @param initConfiguration The initial property configuration for the instance
	 * @param spdURI The URI of the SPD file
	 * @param implID The implementation to launch, or null for any
	 * @param runMode The launch mode to use (run or debug)
	 * @param modelObjClass The type of the model object that should be returned
	 * @return
	 * @throws CoreException
	 */
	protected abstract LocalAbstractComponent launch(String id, DataType[] initConfiguration, URI spdURI, String implID, String runMode) throws CoreException;

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
