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
package gov.redhawk.ide.sad.graphiti.ui.runtime.chalkboard.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.ide.sad.graphiti.ui.runtime.tests.AbstractGraphitiRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.List;

import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class ChalkboardSyncTest extends AbstractGraphitiRuntimeTest {
	
	private SWTBotGefEditor editor;
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String SIG_GEN = "SigGen";


	/**
	 * Adds, then removes a component via chalkboard diagram.  Verify its no
	 * longer present in ScaExplorer Chalkboard
	 */
	@Test
	public void addRemoveComponentInChalkboardDiagram() {

		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		
		//wait for component to show up in ScaExplorer Chalkboard
		waitUntilComponentDisplaysInScaExplorerChalkboard(HARD_LIMIT + "_1");
				
		//delete component from diagram
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));
		
		//wait until hard limit component not present in ScaExplorer Chalkboard
		waitUntilComponentDisappearsInScaExplorerChalkboard(HARD_LIMIT + "_1");
	}
	
	/**
	 * Adds, then removes a component connections via chalkboard diagram.  Verify its no
	 * longer present in ScaExplorer Chalkboard
	 */
	@Test
	public void addRemoveComponentConnectionInChalkboardDiagram() {

		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add two components to diagram from palette
		final String sourceComponent = SIG_GEN;
		final String targetComponent = HARD_LIMIT;
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);
		
		//wait for component to show up in ScaExplorer Chalkboard (connections don't always work correctly if you don't wait.
		waitUntilComponentDisplaysInScaExplorerChalkboard(SIG_GEN + "_1");
		waitUntilComponentDisplaysInScaExplorerChalkboard(HARD_LIMIT + "_1");
		
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Draw redundant connections, save and close the editor
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		//wait for connection to show up in ScaExplorer Chalkboard
		waitUntilConnectionDisplaysInScaExplorerChalkboard(SIG_GEN + "_1", "out", "connection_1");
				
		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		
		//wait until connection not present in ScaExplorer Chalkboard
		waitUntilConnectionDisappearsInScaExplorerChalkboard(SIG_GEN + "_1", "out", "connection_1");
	}
	
	/**
	 * Adds components, starts/stops them from Chalkboard Diagram and verifies
	 * components in ScaExplorer Chalkboard reflect changes
	 * 
	 */
	@Test
	public void startStopComponentsFromChalkboardDiagram() {
		
		final String HARD_LIMIT_1 = HARD_LIMIT + "_1";
		final String SIG_GEN_1 = SIG_GEN + "_1";
		
		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add two components to diagram from palette
		final String sourceComponent = SIG_GEN;
		final String targetComponent = HARD_LIMIT;
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		//wait for component to show up in ScaExplorer Chalkboard
		waitUntilComponentDisplaysInScaExplorerChalkboard(SIG_GEN_1);
		waitUntilComponentDisplaysInScaExplorerChalkboard(HARD_LIMIT_1);
		
		//verify hard limit stopped
		waitUntilComponentAppearsStoppedInScaExplorerChalkboard(HARD_LIMIT_1);

		//start hard limit
		startComponentFromChalkboardDiagram(HARD_LIMIT);
		
		//verify hardlimit started but siggen did not
		waitUntilComponentAppearsStartedInScaExplorerChalkboard(HARD_LIMIT_1);
		waitUntilComponentAppearsStoppedInScaExplorerChalkboard(SIG_GEN_1);
		
		//start SigGen
		startComponentFromChalkboardDiagram(SIG_GEN);
		
		//verify SigGen started but siggen did not
		waitUntilComponentAppearsStartedInScaExplorerChalkboard(SIG_GEN_1);
		
		//stop hard limit
		stopComponentFromChalkboardDiagram(HARD_LIMIT);
		
		//verify hardlimit stopped, SigGen started
		waitUntilComponentAppearsStoppedInScaExplorerChalkboard(HARD_LIMIT_1);
		waitUntilComponentAppearsStartedInScaExplorerChalkboard(SIG_GEN_1);
		
		//stop SigGen
		stopComponentFromChalkboardDiagram(SIG_GEN);
		
		//verify SigGen stopped
		waitUntilComponentAppearsStoppedInScaExplorerChalkboard(SIG_GEN_1);
		
		//start both components
		startComponentFromChalkboardDiagram(HARD_LIMIT);
		startComponentFromChalkboardDiagram(SIG_GEN);
		
		//verify both started
		waitUntilComponentAppearsStartedInScaExplorerChalkboard(HARD_LIMIT_1);
		waitUntilComponentAppearsStartedInScaExplorerChalkboard(SIG_GEN_1);
		
	}
	
	/**
	 * Adds, then removes a component via ScaExplorer Chalkboard.  Verify its no
	 * longer present in Diagram
	 */
	@Test
	public void addRemoveComponentInScaExplorerChalkboard() {

		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch component from TargetSDR
		launchComponentFromTargetSDR(HARD_LIMIT, "python");

		//verify HardLimit was added to the diagram
		waitUntilComponentDisplaysInChalkboardDiagram(HARD_LIMIT);
		
		//delete component from ScaExplorer chalkboard
		terminateComponentInScaExplorerChalkboard(HARD_LIMIT);
		
		//verify hard limit component not present in Chalkboard Diagram
		waitUntilComponentDisappearsInChalkboardDiagram(HARD_LIMIT);
		
		// Launch component from TargetSDR
		launchComponentFromTargetSDR(HARD_LIMIT, "python");
	
		//verify HardLimit was added to the diagram
		waitUntilComponentDisplaysInChalkboardDiagram(HARD_LIMIT);
		
		//terminate chalkboard
		terminateChalkboardFromScaExplorer();
		
		//verify hard limit component not present in Chalkboard Diagram
		waitUntilComponentDisappearsInChalkboardDiagram(HARD_LIMIT);
	}
	
	/**
	 * Adds, then removes component connections via SCA Explorer Chalkboard.  Verify its no
	 * longer present in Chalkboard Diagram
	 */
	@Test
	public void addRemoveComponentConnectionInScaExplorerChalkboard() {

		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch two components from TargetSDR
		launchComponentFromTargetSDR(HARD_LIMIT, "python");
		launchComponentFromTargetSDR(SIG_GEN, "python");

		//verify components were added to the diagram
		waitUntilComponentDisplaysInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentDisplaysInChalkboardDiagram(SIG_GEN);
		
		//create connection between components via Sca Explorer Chalkboard
		connectComponentPortsInScaExplorerChalkboard("connection_1", SIG_GEN, "out",
			HARD_LIMIT, "dataDouble_in");
		
		//verify connection exists in diagram
		waitUntilConnectionDisplaysInChalkboardDiagram(HARD_LIMIT);
		
		//disconnect connection_1 via Sca Explorer Chalkboard
		disconnectConnectionInScaExplorerChalkboard("connection_1", SIG_GEN, "out");
		
		//verify connection does NOT exist in diagram
		waitUntilConnectionDisappearsInChalkboardDiagram(HARD_LIMIT);
	}
	
	/**
	 * Adds components, starts/stops them from ScaExplorer Chalkboard and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		
		// Open Chalkboard Diagram
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch two components from TargetSDR
		launchComponentFromTargetSDR(HARD_LIMIT, "python");
		launchComponentFromTargetSDR(SIG_GEN, "python");

		//verify components were added to the diagram
		waitUntilComponentDisplaysInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentDisplaysInChalkboardDiagram(SIG_GEN);
		
		//verify hard limit stopped
		waitUntilComponentAppearsStoppedInChalkboardDiagram(HARD_LIMIT);

		//start hard limit from sca explorer
		startComponentFromScaExplorer(HARD_LIMIT);
		
		//verify hardlimit started but siggen did not
		waitUntilComponentAppearsStartedInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentAppearsStoppedInChalkboardDiagram(SIG_GEN);
		
		//start SigGen from sca explorer
		startComponentFromScaExplorer(SIG_GEN);
		
		//verify SigGen started but siggen did not
		waitUntilComponentAppearsStartedInChalkboardDiagram(SIG_GEN);
		
		//stop hard limit from sca explorer
		stopComponentFromScaExplorer(HARD_LIMIT);
		
		//verify hardlimit stopped, SigGen started
		waitUntilComponentAppearsStoppedInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentAppearsStartedInChalkboardDiagram(SIG_GEN);
		
		//stop SigGen from sca explorer
		stopComponentFromScaExplorer(SIG_GEN);
		
		//verify SigGen stopped
		waitUntilComponentAppearsStoppedInChalkboardDiagram(SIG_GEN);
		
		//start both components
		startComponentFromScaExplorer(HARD_LIMIT);
		startComponentFromScaExplorer(SIG_GEN);
		
		//verify both started
		waitUntilComponentAppearsStartedInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentAppearsStartedInChalkboardDiagram(SIG_GEN);
		
		//stop chalkboard
		stopChalkboardFromScaExplorer();
		
		//verify both components stopped
		waitUntilComponentAppearsStoppedInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentAppearsStoppedInChalkboardDiagram(SIG_GEN);
		
		//start chalkboard
		startChalkboardFromScaExplorer();
		
		//verify both components started
		waitUntilComponentAppearsStartedInChalkboardDiagram(HARD_LIMIT);
		waitUntilComponentAppearsStartedInChalkboardDiagram(SIG_GEN);
		
	}
	
	/**
	 * Start component from Chalkboard Diagram
	 * @param componentName
	 */
	private void startComponentFromChalkboardDiagram(String componentName) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		editor.clickContextMenu("Start");
	}
	
	/**
	 * Stop component from Chalkboard Diagram
	 * @param componentName
	 */
	private void stopComponentFromChalkboardDiagram(String componentName) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		editor.clickContextMenu("Stop");
	}
	
	/**
	 * Start component from ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void startComponentFromScaExplorer(String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName);
		componentEntry.select();
		SWTBotMenu start = componentEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop components in Chalkboard via ScaExplorer Chalkboard
	 */
	private void stopChalkboardFromScaExplorer() {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		chalkboardEntry.select();
		SWTBotMenu stop = chalkboardEntry.contextMenu("Stop");
		stop.click();
	}
	
	/**
	 * Terminate components in Chalkboard via ScaExplorer Chalkboard.
	 */
	private void terminateChalkboardFromScaExplorer() {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		chalkboardEntry.select();
		SWTBotMenu terminate = chalkboardEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Start components in Chalkboard via ScaExplorer Chalkboard
	 */
	private void startChalkboardFromScaExplorer() {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		chalkboardEntry.select();
		SWTBotMenu start = chalkboardEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop component from ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void stopComponentFromScaExplorer(String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName + " STARTED");
		componentEntry.select();
		SWTBotMenu stop = componentEntry.contextMenu("Stop");
		stop.click();
	}
	
	/**
	 * Launch component from ScaExplorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	private void launchComponentFromTargetSDR(String componentName, String componentLanguage) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Components", componentName);
		componentEntry.select();
		SWTBotMenu launchInSandbox = componentEntry.contextMenu("Launch in Sandbox");
		SWTBotMenu python = launchInSandbox.menu(componentLanguage);
		python.click();
	}
	
	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 */
	private void terminateComponentInScaExplorerChalkboard(String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName);
		componentEntry.select();
		SWTBotMenu terminate = componentEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Connect component ports via SCA Explorer Chalkboard
	 * @param componentName
	 */
	private void connectComponentPortsInScaExplorerChalkboard(String connectionName, String sourceComponentName, String sourceComponentPortName, 
		String targetComponentName, String targetComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem sourceComponentPortEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", sourceComponentName, sourceComponentPortName);
		sourceComponentPortEntry.select();
		SWTBotMenu connect = sourceComponentPortEntry.contextMenu("Connect");
		connect.click(); //opens connect wizard
		
		//Connect wizard
		SWTBotShell wizardShell = bot.shell("Connect");
		SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		wizardBot.treeInGroup("Target").expandNode("Sandbox", "Chalkboard", targetComponentName, targetComponentPortName).select();

		// Enter the name for connection
		wizardBot.textWithLabel("Connection ID:").setText(connectionName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();
		
	}
	
	/**
	 * Disconnect connection via SCA Explorer Chalkboard
	 * @param componentName
	 */
	private void disconnectConnectionInScaExplorerChalkboard(String connectionName, String sourceComponentName, String sourceComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem connectionEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", sourceComponentName, sourceComponentPortName, connectionName);
		connectionEntry.select();
		SWTBotMenu disconnect = connectionEntry.contextMenu("Disconnect");
		disconnect.click(); //disconnects connection
	}
	
	
	/**
	 * Waits until Component appears started in ChalkboardDiagram
	 * @param componentName
	 */
	private void waitUntilComponentAppearsStartedInChalkboardDiagram(final String componentName) {
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear started in Chalkboard Diagram";
			}
			@Override
			public boolean test() throws Exception {
				ComponentShapeImpl componentShape = (ComponentShapeImpl) editor.getEditPart(componentName).part().getModel();
				RoundedRectangle innerRoundedRectangle = (RoundedRectangle) componentShape.getInnerContainerShape().getGraphicsAlgorithm();
				Diagram diagram = DUtil.findDiagram(componentShape);
				return innerRoundedRectangle.getStyle().equals(StyleUtil.createStyleForComponentInnerStarted(diagram));
			}
		});
	}
	

	
	/**
	 * Waits until Component appears stopped in ChalkboardDiagram
	 * @param componentName
	 */
	private void waitUntilComponentAppearsStoppedInChalkboardDiagram(final String componentName) {
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear stopped in Chalkboard Diagram";
			}
			@Override
			public boolean test() throws Exception {
				ComponentShapeImpl componentShape = (ComponentShapeImpl) editor.getEditPart(componentName).part().getModel();
				RoundedRectangle innerRoundedRectangle = (RoundedRectangle) componentShape.getInnerContainerShape().getGraphicsAlgorithm();
				Diagram diagram = DUtil.findDiagram(componentShape);
				return innerRoundedRectangle.getStyle().equals(StyleUtil.createStyleForComponentInner(diagram));
			}
		});
	}
	
	/**
	 * Waits until Component displays in Chalkboard Diagram
	 * @param componentName
	 */
	private void waitUntilComponentDisplaysInChalkboardDiagram(final String componentName) {
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear in Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (editor.getEditPart(componentName) != null) {
						return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Component displays in Chalkboard Diagram
	 * @param componentName
	 */
	private void waitUntilComponentDisappearsInChalkboardDiagram(final String componentName) {
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (editor.getEditPart(componentName) == null) {
						return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	private void waitUntilComponentAppearsStoppedInScaExplorerChalkboard(final String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear stopped in SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	private void waitUntilComponentAppearsStartedInScaExplorerChalkboard(final String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear started in SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName + " STARTED")) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Component displays in ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void waitUntilComponentDisplaysInScaExplorerChalkboard(final String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Component disappears in ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void waitUntilComponentDisappearsInScaExplorerChalkboard(final String componentName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return false;
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * Waits until Connection displays in ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void waitUntilConnectionDisplaysInScaExplorerChalkboard(final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem sigGenOutPortTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = sigGenOutPortTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(connectionName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Connection disappears in ScaExplorer Chalkboard
	 * @param componentName
	 */
	private void waitUntilConnectionDisappearsInScaExplorerChalkboard(final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		final SWTBotTreeItem sigGenOutPortTreeItem = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = sigGenOutPortTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(connectionName)) {
						return false;
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * Waits until Connection displays in Chalkboard Diagram
	 * @param componentName
	 */
	private void waitUntilConnectionDisplaysInChalkboardDiagram(final String targetComponentName) {
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponentName);
		final ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return targetComponentName + " Target Component's connection did not load into Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).size() > 0) {
					return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Connection disappears in Chalkboard Diagram
	 * @param componentName
	 */
	private void waitUntilConnectionDisappearsInChalkboardDiagram(final String targetComponentName) {
		
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponentName);
		final ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return targetComponentName + " Target Component's connection did not disappear Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).size() < 1) {
					return true;
				}
				return false;
			}
		});
	}
}
