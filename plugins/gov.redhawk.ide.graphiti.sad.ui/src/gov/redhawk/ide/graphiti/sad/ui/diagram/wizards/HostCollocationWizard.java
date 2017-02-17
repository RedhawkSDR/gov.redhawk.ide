/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.ui.diagram.wizards;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.wizard.Wizard;

import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.UsesDeviceDependencies;
import mil.jpeojtrs.sca.sad.UsesDeviceRef;
import mil.jpeojtrs.sca.spd.UsesDevice;

/**
 * A wizard for editing a host collocation. Changes are applied to the model in {@link #performFinish()}.
 */
public class HostCollocationWizard extends Wizard {

	private HostCollocation hostCollocation;
	private UsesDeviceDependencies usesDeviceDeps;

	private List<UsesDevice> collocatedUsesDevices;
	private UsesDeviceSelectionWizardPage page;

	public HostCollocationWizard(HostCollocation hostCollocation, UsesDeviceDependencies usesDeviceDeps) {
		setWindowTitle("Edit Host Collocation");
		this.hostCollocation = hostCollocation;
		this.usesDeviceDeps = usesDeviceDeps;
	}

	@Override
	public void addPages() {
		super.addPages();

		// Create a list of the collocated uses devices
		collocatedUsesDevices = hostCollocation.getUsesDeviceRef().stream() //
				.map(usesDeviceRef -> usesDeviceRef.getUsesDevice()) // ref to actual uses device
				.filter(usesDevice -> usesDevice != null) // only if we can find a matching uses device
				.collect(Collectors.toList());

		// Create the page for selecting collocated uses devices
		page = new UsesDeviceSelectionWizardPage(usesDeviceDeps.getUsesdevice(), collocatedUsesDevices);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		// Figure out what needs to be removed or added
		List<UsesDeviceRef> removeRefs = hostCollocation.getUsesDeviceRef().stream() //
				.filter(usesDeviceRef -> !collocatedUsesDevices.contains(usesDeviceRef.getUsesDevice())) //
				.collect(Collectors.toList());
		List<UsesDeviceRef> addRefs = collocatedUsesDevices.stream() //
				.filter(usesDevice -> {
					for (UsesDeviceRef ref : hostCollocation.getUsesDeviceRef()) {
						if (usesDevice.getId().equals(ref.getRefid())) {
							return false;
						}
					}
					return true;
				}) //
				.map(usesDevice -> {
					UsesDeviceRef ref = SadFactory.eINSTANCE.createUsesDeviceRef();
					ref.setUsesDevice(usesDevice);
					return ref;
				}) //
				.filter(usesDeviceRef -> !hostCollocation.getUsesDeviceRef().contains(usesDeviceRef)) //
				.collect(Collectors.toList());

		// Create commands
		EditingDomain domain = TransactionUtil.getEditingDomain(hostCollocation);
		CompoundCommand command = new CompoundCommand();
		if (removeRefs.size() > 0) {
			command.append(new RemoveCommand(domain, hostCollocation.getUsesDeviceRef(), removeRefs));
		}
		if (addRefs.size() > 0) {
			command.append(new AddCommand(domain, hostCollocation.getUsesDeviceRef(), addRefs));
		}

		// Run the compound command only if there's something to do
		if (!command.isEmpty()) {
			ScaModelCommand.execute(hostCollocation, command);
		}

		return true;
	}
}
