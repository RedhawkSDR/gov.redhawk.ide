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

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.PortWizard;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.PortWizardPage.PortWizardModel;
import gov.redhawk.model.sca.util.ModelUtil;

import java.util.Map;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * An implementation of {@link AbstractHandler} for adding a port to a SoftwareComponent.
 * @since 6.0
 */
public class AddPortHandler extends AbstractHandler {

	private EditingDomain editingDomain;
	private Resource resource;
	private SoftPkg softPkg;
	private ComponentEditor editor;
	private Map<String, Interface> interfaceMap;

	/**
	 * Default Constructor for instantiation by framework.
	 */
	public AddPortHandler() {
		//DefaultConstructor
	}

	/**
	 * Constructor for use within the package so this handler can be used by other handlers.
	 * 
	 * @param editingDomain
	 * @param resource
	 * @param softPkg
	 */
	public AddPortHandler(final EditingDomain editingDomain, final Resource resource, final SoftPkg softPkg) {
		this.editingDomain = editingDomain;
		this.resource = resource;
		this.softPkg = softPkg;
		this.interfaceMap = PortsHandlerUtil.getInterfaceMap(this.softPkg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		this.editor = (ComponentEditor) HandlerUtil.getActiveEditor(event);
		this.editingDomain = this.editor.getEditingDomain();
		this.resource = this.editor.getMainResource();
		this.softPkg = ModelUtil.getSoftPkg(this.resource);
		this.interfaceMap = PortsHandlerUtil.getInterfaceMap(this.softPkg);
		this.displayAddWizard();
		return null;
	}

	/**
	 * Displays the AddPort Wizard.
	 */
	private void displayAddWizard() {
		final PortWizard wizard = new PortWizard(getPorts(), this.editor);
		wizard.setWindowTitle("Add Port");
		final WizardDialog dialog = new WizardDialog(this.editor.getSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			PortsHandlerUtil.execute(this.createAddPortCommand(wizard.getIdlLibrary(), wizard.getValue()), this.editingDomain);
		}
	}

	/**
	 * Adds the port and any associated interfaces.
	 * 
	 * @param port the port to add
	 * @param isEdit <code> true </code> if the addComand should be considered part of an edit command; <code> false </code> otherwise
	 */
	public Command createAddPortCommand(final IdlLibrary library, final PortWizardModel model) {
		final String repId = model.getRepId();

		// Create copies of both port types to reduce duplicate logic in the switch statement
		final Provides provides = ScdFactory.eINSTANCE.createProvides();
		provides.setProvidesName(model.getPortName());
		provides.setRepID(repId);
		for (final PortType pt : model.getPortTypes()) {
			provides.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));
		}

		final Uses uses = ScdFactory.eINSTANCE.createUses();
		uses.setUsesName(model.getPortName());
		uses.setRepID(repId);
		for (final PortType pt : model.getPortTypes()) {
			uses.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));
		}

		final CompoundCommand command = new CompoundCommand("Add Port Command");
		switch (model.getType()) {
		case PROVIDES:
			command.append(AddCommand.create(this.editingDomain, getPorts(), ScdPackage.Literals.PORTS__PROVIDES, provides));
			break;
		case USES:
			command.append(AddCommand.create(this.editingDomain, getPorts(), ScdPackage.Literals.PORTS__USES, uses));
			break;
		case BIDIR:
			command.append(AddCommand.create(this.editingDomain, getPorts(), ScdPackage.Literals.PORTS__PROVIDES, provides));
			command.append(AddCommand.create(this.editingDomain, getPorts(), ScdPackage.Literals.PORTS__USES, uses));
			break;
		default:
			throw new IllegalStateException();
		}

		if (!this.interfaceMap.containsKey(repId)) {
			command.append(createAddInterfaceCommand(library, repId, this.interfaceMap));
		}
		return command;
	}

	/**
	 * Creates a command to add the interface and all inherited interfaces provided they're not already in the interfaceMap.
	 * 
	 * @param repId the {@link String} repId of the {@link Interface} to add 
	 * @param interfaceMap the {@link Map} of current interfaces
	 * @return the {@link Command} to add the specified {@link Interface} and all {@link InheritsInterface} if they 
	 * 				don't already exist and are present in the {@link IdlLibrary}; <code> null </code> otherwise
	 */
	private Command createAddInterfaceCommand(final IdlLibrary library, final String repId, final Map<String, Interface> interfaceMap) {
		final Interface i = ScdFactory.eINSTANCE.createInterface();
		final IdlInterfaceDcl idlInter = (IdlInterfaceDcl) library.find(repId);

		//If the interface isn't present in the IdlLibrary, there's nothing to do
		if (idlInter != null) {
			final CompoundCommand command = new CompoundCommand("Add Interfaces");
			i.setName(idlInter.getName());
			i.setRepid(repId);

			//Add all the inherited interfaces first.
			for (final IdlInterfaceDcl inherited : idlInter.getInheritedInterfaces()) {
				final InheritsInterface iface = ScdFactory.eINSTANCE.createInheritsInterface();
				iface.setRepid(inherited.getRepId());
				i.getInheritsInterfaces().add(iface);

				//If the inherited interface isn't already present, make a recursive call to add it.
				if (!interfaceMap.containsKey(inherited.getRepId())) {
					command.append(createAddInterfaceCommand(library, inherited.getRepId(), interfaceMap));
				}
			}

			//If the interface isn't already present
			if (!interfaceMap.containsKey(i.getRepid())) {
				interfaceMap.put(i.getRepid(), i);
				command.append(AddCommand.create(this.editingDomain, PortsHandlerUtil.getInterfaces(this.softPkg), ScdPackage.Literals.INTERFACES__INTERFACE, i));
			}
			return command;
		}
		return null;
	}

	private Ports getPorts() {
		return PortsHandlerUtil.getPorts(this.softPkg);
	}

	/**
	 * @deprecated Use {@link #createAddPortCommand(IdlLibrary, PortWizardModel)}
	 */
	@Deprecated
	public Command createAddPortCommand(final IdlLibrary library, final AbstractPort port) {
		return createAddPortCommand(library, new PortWizardModel(port));
	}
}
