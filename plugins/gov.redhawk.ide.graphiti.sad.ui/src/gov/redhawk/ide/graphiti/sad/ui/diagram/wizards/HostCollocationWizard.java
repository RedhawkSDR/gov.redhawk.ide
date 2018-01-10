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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.UsesDevice;

/**
 * A wizard for editing a host collocation. Changes are applied to the model in {@link #performFinish()}.
 */
public class HostCollocationWizard extends Wizard {

	private HostCollocation hostCollocation;
	private UsesDeviceDependencies usesDeviceDeps;

	private List<UsesDevice> collocatedUsesDevices;
	private Map<UsesDeviceRef, UsesDevice> refToStubMap;
	private UsesDeviceSelectionWizardPage page;

	public HostCollocationWizard(HostCollocation hostCollocation, UsesDeviceDependencies usesDeviceDeps) {
		setWindowTitle(Messages.HostCollocationWizard_WindowTitle);
		this.hostCollocation = hostCollocation;
		this.usesDeviceDeps = usesDeviceDeps;
	}

	@Override
	public void addPages() {
		super.addPages();

		// Get the uses devices in the SAD (or an empty list if none)
		List<UsesDevice> usesDevices = (usesDeviceDeps != null) ? usesDeviceDeps.getUsesdevice() : Collections.emptyList();

		// Create a list of the collocated uses devices
		refToStubMap = new HashMap<>();
		collocatedUsesDevices = hostCollocation.getUsesDeviceRef().stream() //
				.map(usesDeviceRef -> {
					// Return the actual uses device, or a stub if it doesn't exist
					if (usesDeviceRef.getUsesDevice() != null) {
						return usesDeviceRef.getUsesDevice();
					} else {
						UsesDevice usesDevice = SpdFactory.eINSTANCE.createUsesDevice();
						usesDevice.setId(usesDeviceRef.getRefid());
						refToStubMap.put(usesDeviceRef, usesDevice);
						return usesDevice;
					}
				}) //
				.filter(usesDevice -> usesDevice != null) // only if we can find a matching uses device
				.collect(Collectors.toList());

		// Create the page for selecting collocated uses devices
		page = new UsesDeviceSelectionWizardPage(usesDevices, collocatedUsesDevices);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		// Figure out what needs to be removed or added
		List<UsesDeviceRef> removeRefs = hostCollocation.getUsesDeviceRef().stream() //
				.filter(usesDeviceRef -> {
					// For refs to an actual uses device, see if the uses device is still in the list
					if (usesDeviceRef.getUsesDevice() != null) {
						return !collocatedUsesDevices.contains(usesDeviceRef.getUsesDevice());
					} else {
						// See if our fake stub is still in the list
						return !collocatedUsesDevices.contains(refToStubMap.get(usesDeviceRef));
					}
				}) //
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
