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
package gov.redhawk.ide.ui.tests.runtime;

import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class DomainInteractionTest extends AbstractDomainRuntimeTest {

	private SWTBot viewBot;
	private SWTBotView explorerView;

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.cleanUpLaunches();

		StandardTestActions.cleanUpConnections();

		explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();
	}

	@Test
	public void testConnect() {
		launchDomainManager("REDHAWK_DEV");

		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();

		SWTBotShell shell = bot.activeShell();
		Assert.assertEquals("New Domain Manager", shell.getText());
		SWTBot wizardBot = shell.bot();

		wizardBot.textWithLabel("Display Name:").setText("REDHAWK");
		Assert.assertEquals("REDHAWK", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN");
		Assert.assertEquals("REDHAWK_DOMAIN", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01");
		Assert.assertEquals("REDHAWK_DOMAIN_TEST01", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		wizardBot.textWithLabel("Display Name:").setText("REDHAWK_DEV");
		Assert.assertEquals("REDHAWK_DEV", wizardBot.textWithLabel("Domain Name:").getText());
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		bot.button("Finish").click();

		waitForConnect("REDHAWK_DEV");
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").select();
	}

	@Test
	public void testLaunch() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK");
		Assert.assertTrue(bot.button("OK").isEnabled());

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN");
		Assert.assertTrue(bot.button("OK").isEnabled());

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST02");
		Assert.assertTrue(bot.button("OK").isEnabled());
		bot.button("OK").click();

		waitForConnect("REDHAWK_DOMAIN_TEST02");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02 CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02 CONNECTED").select();
	}

	@Test
	public void testLaunch03() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST03");
		bot.button("OK").click();

		waitForConnect("REDHAWK_DOMAIN_TEST03");

		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").expand();
		viewBot.tree().getTreeItem("Target SDR").expand();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").expand();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").getNode("ExampleExecutableNode01").select();
		viewBot.tree().getTreeItem("Target SDR").getNode("Nodes").getNode("ExampleExecutableNode01").contextMenu("Launch Device Manager").click();
		bot.button("OK").click();

		explorerView.show();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").expand();
		viewBot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").getNode(
					"ExampleExecutableDevice01_1 STARTED");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "REDHAWK_DOMAIN_TEST03 never connected";
			}

		}, 30000, 1000);
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").getNode("Device Managers").getNode("ExampleExecutableNode01").getNode(
			"ExampleExecutableDevice01_1 STARTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03 CONNECTED").select();
	}

	public void waitForConnect(final String domainName) {
		viewBot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().getTreeItem(domainName + " CONNECTED");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return domainName + " never connected";
			}

		}, 30000, 1000);
	}

	@Test
	public void testLaunch04() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST04");
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").check();
		bot.button("OK").click();

		waitForConnect("REDHAWK_DOMAIN_TEST04");

		explorerView.show();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04 CONNECTED").expand();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST04 CONNECTED", "Device Managers", "ExampleExecutableNode01");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Device Manager never connected.";
			}

		}, 30000, 1000);
		viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST04 CONNECTED", "Device Managers", "ExampleExecutableNode01").expand();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04 CONNECTED").select();
	}

	@Test
	public void testDisplayName() {
		launchDomainManager("REDHAWK_DOMAIN_TEST01x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x");
		bot.button("Finish").click();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x CONNECTED").expand();

		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x");
		Assert.assertFalse("Finish should not be enabled.", bot.button("Finish").isEnabled());
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x_2");
		bot.textWithLabel("Domain Name:").setText("REDHAWK_DOMAIN_TEST01x");
		bot.button("Finish").click();

		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x_2 CONNECTED").expand();
	}

	@Test
	public void testLaunch05() {
		launchDomainManager("REDHAWK_DOMAIN_TEST01x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST01x");
		bot.button("Finish").click();
		waitForConnect("REDHAWK_DOMAIN_TEST01x");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x CONNECTED").expand();

		launchDomainManager("REDHAWK_DOMAIN_TEST02x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST02x");
		bot.button("Finish").click();
		waitForConnect("REDHAWK_DOMAIN_TEST02x");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02x CONNECTED").expand();

		launchDomainManager("REDHAWK_DOMAIN_TEST03x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST03x");
		bot.button("Finish").click();
		waitForConnect("REDHAWK_DOMAIN_TEST03x");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03x CONNECTED").expand();

		launchDomainManager("REDHAWK_DOMAIN_TEST04x");
		StandardTestActions.viewToolbarWithToolTip(explorerView, "New Domain Connection").click();
		bot.textWithLabel("Display Name:").setText("REDHAWK_DOMAIN_TEST04x");
		bot.button("Finish").click();
		waitForConnect("REDHAWK_DOMAIN_TEST04x");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04x CONNECTED").expand();

		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST01x CONNECTED").select();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST02x CONNECTED").select();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST03x CONNECTED").select();
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST04x CONNECTED").select();
	}

	@Test
	public void testLaunch06() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();

		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DOMAIN_TEST06");
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").select();
		bot.tree().getTreeItem("ExampleExecutableNode01 (/nodes/ExampleExecutableNode01/)").check();
		bot.tree().getTreeItem("ExampleExecutableNode02 (/nodes/ExampleExecutableNode02/)").select();
		bot.tree().getTreeItem("ExampleExecutableNode02 (/nodes/ExampleExecutableNode02/)").check();
		bot.button("OK").click();

		waitForConnect("REDHAWK_DOMAIN_TEST06");
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST06 CONNECTED").expand();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST06 CONNECTED", "Device Managers", "ExampleExecutableNode01");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Failed to find REDHAWK_DOMAIN_TEST06/Device Managers/ExampleExecutableNode01";
			}

		}, 30000, 1000);
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DOMAIN_TEST06 CONNECTED", "Device Managers", "ExampleExecutableNode02");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Failed to find REDHAWK_DOMAIN_TEST06/Device Managers/ExampleExecutableNode02";
			}

		}, 30000, 1000);
		viewBot.tree().getTreeItem("REDHAWK_DOMAIN_TEST06 CONNECTED").select();
	}

	@Test
	public void testLaunch08() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DEV");
		bot.tree().getTreeItem("DevMgr_localhost (/nodes/DevMgr_localhost/)").select();
		bot.tree().getTreeItem("DevMgr_localhost (/nodes/DevMgr_localhost/)").check();
		bot.button("OK").click();

		waitForConnect("REDHAWK_DEV");
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").expand();
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").getNode("Device Managers").expand();
		viewBot.tree().getTreeItem("REDHAWK_DEV CONNECTED").getNode("Device Managers").getNode("DevMgr_localhost").expand();
		viewBot.tree().getTreeItem("Target SDR").select();

		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();

		// Choose an acceptable domain name. The OK button should enable.
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DEV_2");
		bot.waitWhile(new ICondition() {

			@Override
			public boolean test() throws Exception {
				return !bot.button("OK").isEnabled();
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "OK button did not enable";
			}
		}, 5000, 1000);

		// Choose an unacceptable domain name. The OK button should disable.
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DEV");
		bot.waitWhile(new ICondition() {

			@Override
			public boolean test() throws Exception {
				return bot.button("OK").isEnabled();
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "OK button did not disable";
			}

		}, 5000, 1000);
		bot.button("Cancel").click();
	}

	@Test
	public void testPortsPlotData() {
		viewBot.tree().getTreeItem("Target SDR").select();
		viewBot.tree().getTreeItem("Target SDR").contextMenu("Launch Domain ...").click();
		bot.textWithLabel("Domain Name: ").setText("REDHAWK_DEV_ports");
		bot.button("OK").click();

		waitForConnect("REDHAWK_DEV_ports");

		viewBot.tree().getTreeItem("REDHAWK_DEV_ports CONNECTED").expand();
		viewBot.tree().expandNode("Target SDR", "Nodes", "DevMgr_localhost").select();
		viewBot.tree().expandNode("Target SDR", "Nodes", "DevMgr_localhost").contextMenu("Launch Device Manager").click();
		bot.button("OK").click();

		explorerView.show();
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				viewBot.tree().expandNode("REDHAWK_DEV_ports CONNECTED", "Device Managers", "DevMgr_localhost", "GPP_1 STARTED").expand();
				return true;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Couldn't find some element in the tree leading up to 'GPP_1 STARTED'";
			}
		}, 10000, 1000);
		viewBot.tree().getTreeItem("REDHAWK_DEV_ports CONNECTED").select();

		viewBot.tree().getTreeItem("REDHAWK_DEV_ports CONNECTED").contextMenu("Launch Waveform...").click();
		bot.tree().getTreeItem("ExampleWaveform05").select();
		bot.button("Finish").click();

		bot.closeAllEditors();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				StandardTestActions.getTreeItemMatches(viewBot.tree(), "REDHAWK_DEV_ports CONNECTED", "Waveforms", "ExampleWaveform05.*", "SigGen_1");
				return true;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Could not find REDHAWK_DEV_ports/Waveforms/ExampleWaveform05.*/SigGen_1";
			}

		}, 30000, 1000);

		explorerView.setFocus();
		StandardTestActions.getTreeItemMatches(viewBot.tree(), "REDHAWK_DEV_ports CONNECTED", "Waveforms", "ExampleWaveform05.*", "SigGen_1").select();
		StandardTestActions.getTreeItemMatches(viewBot.tree(), "REDHAWK_DEV_ports CONNECTED", "Waveforms", "ExampleWaveform05.*", "SigGen_1").contextMenu(
			"Start").click();
		SWTBotTreeItem item = StandardTestActions.getTreeItemMatches(viewBot.tree(), "REDHAWK_DEV_ports CONNECTED", "Waveforms", "ExampleWaveform05.*", "SigGen_1", "out");
		item.select();
		Assert.assertEquals(0, item.getItems().length);
		item.contextMenu("Plot Port Data").click();

		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				SWTBotView view = bot.activeView();
				String viewTitle = view.getViewReference().getTitle();
				String viewId = view.getViewReference().getId();
				if ("out".equals(viewTitle) && "gov.redhawk.ui.port.nxmplot.PlotView2".equals(viewId)) {
					return true;
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Plot View failed to open";
			}
		}, 30000, 1000);

		explorerView.show();
		Assert.assertEquals(1, item.getItems().length);
	}
}
