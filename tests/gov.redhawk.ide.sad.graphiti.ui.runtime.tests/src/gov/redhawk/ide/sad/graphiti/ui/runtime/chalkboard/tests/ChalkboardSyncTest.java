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

import gov.redhawk.ide.sad.graphiti.ui.runtime.tests.AbstractGraphitiRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class ChalkboardSyncTest extends AbstractGraphitiRuntimeTest {
	
	private SWTBotGefEditor editor;
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String SIG_GEN = "SigGen";


//	/**
//	 * Adds, then removes a component via chalkboard diagram.  Verify its no
//	 * longer present in ScaExplorer Chalkboard
//	 */
//	@Test
//	public void addRemoveComponentInChalkboardDiagram() {
//
//		// Open Chalkboard Diagram
//		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
//		editor = gefBot.gefEditor(CHALKBOARD);
//		editor.setFocus();
//
//		// Add component to diagram from palette
//		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
//		
//		//wait for component to show up in ScaExplorer Chalkboard
//		waitUntilComponentDisplaysInScaExplorerChalkboard(HARD_LIMIT + "_1");
//				
//		//delete component from diagram
//		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));
//		
//		//wait until hard limit component not present in ScaExplorer Chalkboard
//		waitUntilComponentDisappearsInScaExplorerChalkboard(HARD_LIMIT + "_1");
//	}
	
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
	
//	/**
//	 * Adds, then removes a component via ScaExplorer Chalkboard.  Verify its no
//	 * longer present in Diagram
//	 */
//	@Test
//	public void addRemoveComponentInScaExplorerChalkboard() {
//
//		// Open Chalkboard Diagram
//		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
//		editor = gefBot.gefEditor(CHALKBOARD);
//		editor.setFocus();
//
//		// Launch component from TargetSDR
//		launchComponentFromTargetSDR(HARD_LIMIT, "python");
//
//		//verify HardLimit was added to the diagram
//		waitUntilComponentDisplaysInChalkboardDiagram(HARD_LIMIT);
//		
//		//delete component from ScaExplorer chalkboard
//		terminateComponentInScaExplorerChalkboard(HARD_LIMIT);
//		
//		//verify hard limit component not present in Chalkboard Diagram
//		waitUntilComponentDisappearsInChalkboardDiagram(HARD_LIMIT);
//	}
	
	/**
	 * Launch component from ScaExplorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	private void launchComponentFromTargetSDR(String componentName, String componentLanguage) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem hardLimitEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Components", componentName);
		hardLimitEntry.select();
		SWTBotMenu launchInSandbox = hardLimitEntry.contextMenu("Launch in Sandbox");
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
		SWTBotTreeItem hardLimitEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", componentName);
		hardLimitEntry.select();
		SWTBotMenu terminate = hardLimitEntry.contextMenu("Terminate");
		terminate.click();
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
				return connectionName + " Conneciton did not load into SCA Explorer Chalkboard";
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
				return connectionName + " Conneciton did not load into SCA Explorer Chalkboard";
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
}
