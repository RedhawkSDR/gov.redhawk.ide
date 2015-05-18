/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot.diagram;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

public class UsesDeviceTestUtils {

	private UsesDeviceTestUtils() {
	}

	/**
	 * {@link DiagramTestUtils#addUseFrontEndTunerDeviceToDiagram(SWTGefBot, SWTBotGefEditor)} must be called first.
	 * Completes the wizard for a usesdevice dependency on a FrontEnd tuner using the "Control New Tuner" option. Any
	 * number of port names can be provided.
	 * @param bot
	 * @param deviceTemplate The text of the device to select on the first page (null for default)
	 * @param feTunerControl The details to use for the tuner (control) allocation
	 * @param provides Provides port name(s), if any
	 * @param uses Uses port name(s), if any
	 */
	public static void completeUsesFEDeviceWizard(SWTBot bot, String deviceTemplate, FETunerControl feTunerControl, String[] provides, String[] uses) {
		SWTBotShell allocateTunerShell = bot.shell("Allocate Tuner");
		allocateTunerShell.setFocus();

		// Device to use as a template
		if (deviceTemplate != null) {
			bot.table(0).getTableItem(deviceTemplate).select();
		}
		bot.button("&Next >").click();

		// Stick with the default values
		bot.button("&Next >").click();

		// Allocate new tuner
		SWTBotCombo tunerTypeComboField = bot.comboBox(1);
		tunerTypeComboField.setFocus();
		tunerTypeComboField.setSelection(feTunerControl.getTunerType());

		SWTBotText newAllocationIdText = bot.textWithLabel("New Allocation ID");
		newAllocationIdText.setFocus();
		newAllocationIdText.typeText(feTunerControl.getNewAllocationID());

		SWTBotText centerFrequencyText = bot.textWithLabel("Center Frequency (MHz)");
		centerFrequencyText.setFocus();
		centerFrequencyText.typeText(feTunerControl.getCenterFrequency());

		SWTBotText bandwidthText = bot.textWithLabel("Bandwidth (MHz)");
		bandwidthText.setFocus();
		bandwidthText.typeText(feTunerControl.getBandwidth());

		SWTBotText sampleRateText = bot.textWithLabel("Sample Rate (Msps)");
		sampleRateText.setFocus();
		sampleRateText.setText(""); // clear first because the focus method caused the wizard to pre-populate the field
		sampleRateText.typeText(feTunerControl.getSampleRate());

		SWTBotText groupIdText = bot.textWithLabel("Group ID");
		groupIdText.setFocus();
		groupIdText.setText(""); // clear first because the focus method caused the wizard to pre-populate the field
		groupIdText.typeText(feTunerControl.getGroupID());

		bot.button("&Next >").click();

		completeAddPortsPage(bot, provides, uses);
	}

	/**
	 * {@link DiagramTestUtils#addUseFrontEndTunerDeviceToDiagram(SWTGefBot, SWTBotGefEditor)} must be called first.
	 * Completes the wizard for a usesdevice dependency on a FrontEnd tuner using the
	 * "Listen to Existing Tuner by ID" option. Any number of port names can be provided.
	 * @param bot
	 * @param existingAllocId The existing allocation ID to listen to
	 * @param newAllocId The new allocation ID (or null to not change the default the wizard will auto-populate)
	 * @param provides Provides port name(s), if any
	 * @param uses Uses port name(s), if any
	 */
	public static void completeUsesFEDeviceWizard(SWTBot bot, String existingAllocId, String newAllocId, String[] provides, String[] uses) {
		SWTBotShell allocateTunerShell = bot.shell("Allocate Tuner");
		allocateTunerShell.setFocus();
		// click next, Generic FrontEnd Device already selected
		bot.button("&Next >").click();
		// stick with the default values
		bot.button("&Next >").click();
		// switch to Listen by id
		SWTBotCombo comboField = bot.comboBox(0); // Allocation
		comboField.setFocus();
		comboField.setSelection("Listen to Existing Tuner by ID");
		// provide existing tuner allocation id
		SWTBotText existingTunerAllocationIdText = bot.textWithLabel("Existing Tuner Allocation ID");
		existingTunerAllocationIdText.setFocus();
		existingTunerAllocationIdText.typeText(existingAllocId);
		// provide allocation id
		if (newAllocId != null) {
			SWTBotText newAllocationIdText = bot.textWithLabel("New Allocation ID");
			newAllocationIdText.setFocus();
			newAllocationIdText.typeText(newAllocId);
		}
		bot.button("&Next >").click();

		completeAddPortsPage(bot, provides, uses);
	}

	private static void completeAddPortsPage(SWTBot bot, String[] provides, String[] uses) {
		// provides ports
		if (provides != null) {
			for (int i = 0; i < provides.length; i++) {
				bot.textInGroup("Port(s) to use for connections", 0).setText(provides[i]);
				bot.button(0).click(); // add provides port
			}
		}

		// uses ports
		if (uses != null) {
			for (int i = 0; i < uses.length; i++) {
				bot.textInGroup("Port(s) to use for connections", 1).setText(uses[i]);
				bot.button(2).click(); // add uses port
			}
		}

		// finish
		bot.button("&Finish").click();
	}

}
