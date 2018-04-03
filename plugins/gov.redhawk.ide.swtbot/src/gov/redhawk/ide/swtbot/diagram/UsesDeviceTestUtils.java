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

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;

import gov.redhawk.ide.swtbot.StandardTestActions;

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
		SWTBot shellBot = allocateTunerShell.bot();

		// Device to use as a template
		if (deviceTemplate != null) {
			StandardTestActions.waitForTreeItemToAppear(shellBot, shellBot.tree(), Arrays.asList(deviceTemplate.split("\\."))).select();
		}
		shellBot.button("&Next >").click();

		// Stick with the default values
		shellBot.button("&Next >").click();

		completeTunerAllocationPage(shellBot, feTunerControl);
		shellBot.button("&Next >").click();

		if ("RX_SCANNER_DIGITIZER".equals(feTunerControl.getTunerType())) {
			if (provides != null || uses != null) {
				Assert.fail("This helper doesn't handle scanners with ports");
			}
			return;
		}

		completeAddPortsPage(shellBot, provides, uses);

		shellBot.button("&Finish").click();
		bot.waitUntil(Conditions.shellCloses(allocateTunerShell));
	}

	private static void completeTunerAllocationPage(SWTBot shellBot, FETunerControl feTunerControl) {
		shellBot.textWithLabel("New Allocation ID").typeText(feTunerControl.getNewAllocationID());
		shellBot.comboBoxWithLabel("Tuner Type").setSelection(feTunerControl.getTunerType());
		shellBot.textWithLabel("Center Frequency (MHz)").typeText(feTunerControl.getCenterFrequency());
		if (feTunerControl.getBandwidth() == null) {
			shellBot.checkBox("Any Value", 0).click();
		} else {
			shellBot.textWithLabel("Bandwidth (MHz)").typeText(feTunerControl.getBandwidth());
		}
		if (feTunerControl.getSampleRate() == null) {
			shellBot.checkBox("Any Value", 1).click();
		} else {
			SWTBotText sampleRateText = shellBot.textWithLabel("Sample Rate (Msps)");
			sampleRateText.setFocus();
			sampleRateText.setText(""); // clear first because the focus method caused the wizard to pre-populate
			sampleRateText.typeText(feTunerControl.getSampleRate());
		}
		shellBot.textWithLabel("RF Flow ID").typeText(feTunerControl.getRFFlowID());
		shellBot.textWithLabel("Group ID").typeText(feTunerControl.getGroupID());
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
		SWTBot shellBot = allocateTunerShell.bot();

		// click next, Generic FrontEnd Device already selected
		shellBot.button("&Next >").click();
		// stick with the default values
		shellBot.button("&Next >").click();
		// switch to Listen by id
		SWTBotCombo comboField = shellBot.comboBox(0); // Allocation
		comboField.setFocus();
		comboField.setSelection("Listen to Existing Tuner by ID");
		// provide existing tuner allocation id
		SWTBotText existingTunerAllocationIdText = shellBot.textWithLabel("Existing Tuner Allocation ID");
		existingTunerAllocationIdText.setFocus();
		existingTunerAllocationIdText.typeText(existingAllocId);
		// provide allocation id
		if (newAllocId != null) {
			SWTBotText newAllocationIdText = shellBot.textWithLabel("New Allocation ID");
			newAllocationIdText.setFocus();
			newAllocationIdText.typeText(newAllocId);
		}
		shellBot.button("&Next >").click();

		completeAddPortsPage(shellBot, provides, uses);

		shellBot.button("&Finish").click();
		bot.waitUntil(Conditions.shellCloses(allocateTunerShell));
	}

	private static void completeAddPortsPage(SWTBot bot, String[] provides, String[] uses) {
		// provides ports
		if (provides != null) {
			for (int i = 0; i < provides.length; i++) {
				bot.textInGroup("Port(s) to use for connections", 0).setText(provides[i]);
				bot.buttonWithTooltip("Add provides port").click();
			}
		}

		// uses ports
		if (uses != null) {
			for (int i = 0; i < uses.length; i++) {
				bot.textInGroup("Port(s) to use for connections", 1).setText(uses[i]);
				bot.buttonWithTooltip("Add uses port").click();
			}
		}
	}

}
