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
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.PortWizard;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.PortWizardPage.PortWizardModel;
import gov.redhawk.model.sca.util.ModelUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * An implementation of {@link AbstractHandler} for editing a port.
 * @since 6.0
 */
public class EditPortHandler extends AbstractHandler {

	private ComponentEditor editor;
	private EditingDomain editingDomain;
	private Resource resource;
	private SoftPkg softPkg;

	/**
	 * Default Constructor for instantiation by framework;  do not use.
	 */
	public EditPortHandler() {
		//DefaultConstructor
	}

	/**
	 * Public constructor for use within an editor.
	 * 
	 * @param editor
	 * @param editingDomain
	 * @param resource
	 * @param softPkg
	 */
	public EditPortHandler(final ComponentEditor editor, final EditingDomain editingDomain, final Resource resource, final SoftPkg softPkg) {
		this(editingDomain, resource, softPkg);
		this.editor = editor;
	}

	/**
	 * Constructor for use within the package so this handler can be used by other handlers.
	 * 
	 * @param editingDomain
	 * @param resource
	 * @param softPkg
	 */
	public EditPortHandler(final EditingDomain editingDomain, final Resource resource, final SoftPkg softPkg) {
		this.editingDomain = editingDomain;
		this.resource = resource;
		this.softPkg = softPkg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
		this.editor = (ComponentEditor) HandlerUtil.getActiveEditor(event);
		this.editingDomain = this.editor.getEditingDomain();
		this.resource = this.editor.getMainResource();
		this.softPkg = ModelUtil.getSoftPkg(this.resource);
		this.displayEditWizard(selection);
		// Fixes #103.  Return focus to the editor once the wizard has closed.
		this.editor.setFocus();
		return null;
	}

	/**
	 * Displays the wizard to the user.
	 * 
	 * @param selection
	 */
	public void displayEditWizard(final ISelection selection) {
		Object obj = ((StructuredSelection) selection).getFirstElement();
		obj = AdapterFactoryEditingDomain.unwrap(obj);

		final PortWizard wizard = new PortWizard((AbstractPort) obj, getPorts(), this.editor);
		wizard.setWindowTitle("Edit Port");

		final WizardDialog dialog = new WizardDialog(this.editor.getSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			this.handleEditPort(wizard.getIdlLibrary(), (AbstractPort) obj, wizard.getValue());
		}
	}

	/**
	 * Removes the old port and adds the new port to the software component.
	 * 
	 * @param oldPort the Port to remove from the component
	 * @param newPort the Port to add to the component
	 */
	protected void handleEditPort(final IdlLibrary library, final AbstractPort oldPort, final PortWizardModel model) {

		final CompoundCommand command = new CompoundCommand("Edit Port Command");

		final RemovePortsHandler removeHandler = new RemovePortsHandler(this.editingDomain, this.resource, this.softPkg);
		final AddPortHandler addHandler = new AddPortHandler(this.editingDomain, this.resource, this.softPkg);

		final Set<String> ignore = new HashSet<String>();
		//  Instruct the remove handler to not remove any of the interfaces from the new port
		ignore.add(model.getRepId());
		ignore.addAll(PortsHandlerUtil.getInheritedInterfaces(library, model.getRepId()));

		command.append(removeHandler.createRemovePortCommand(Collections.singleton((Object) oldPort), ignore));
		command.append(addHandler.createAddPortCommand(library, model));
		PortsHandlerUtil.execute(command, this.editingDomain);
	}

	private Ports getPorts() {
		return PortsHandlerUtil.getPorts(this.softPkg);
	}

	/**
	 * @deprecated Use {@link #handleEditPort(IdlLibrary, AbstractPort, PortWizardModel)} instead
	 */
	@Deprecated
	protected void handleEditPort(final IdlLibrary library, final Provides oldPort, final Provides newPort) {
		handleEditPort(library, oldPort, new PortWizardModel(newPort));
	}
}
