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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.ListElement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

@SuppressWarnings("restriction")
public class PortPropertiesTest extends AbstractGraphitiTest {

	public PortPropertiesTest() {
	}

	/**
	 * Ensure that after selecting a port the IDL hierarchy for it is shown in the properties view.
	 */
	@Test
	public void checkPortProperties() {
		final String waveformName = "IDE-1050-test";
		final String onlyComponent = "HardLimit";
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		final SWTBotGefEditor editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, onlyComponent, 0, 0);
		SWTBotGefEditPart part;
		SWTBotTree tree;
		String description;
		
		part = getAnchorPart(DiagramTestUtils.getDiagramProvidesPort(editor, onlyComponent));
		MenuUtils.showView(gefBot, "org.eclipse.ui.views.PropertySheet");
		editor.select(part);
		editor.click(part);
		selectPropertiesTab("Port Details");
		description = gefBot.viewByTitle("Properties").bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("provides Port description", "Double input port for data before hard limit is applied. ", description);
		tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataDouble");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);
		
		part = getAnchorPart(DiagramTestUtils.getDiagramUsesPort(editor, onlyComponent));
		MenuUtils.showView(gefBot, "org.eclipse.ui.views.PropertySheet");
		editor.select(part);
		editor.click(part);
		selectPropertiesTab("Port Details");
		description = gefBot.viewByTitle("Properties").bot().textWithLabel("Description:").getText(); // IDE-1172
		Assert.assertEquals("uses Port description", "Double output port for data after hard limit is applied. ", description);
		tree = gefBot.viewByTitle("Properties").bot().tree();
		tree.expandNode("dataDouble");
		Assert.assertTrue("Properties view tree should have multiple nodes", tree.visibleRowCount() > 1);
	}
	
	
	private SWTBotGefEditPart getAnchorPart(SWTBotGefEditPart parent) {
		if (parent.part().getModel() instanceof Anchor) {
			return parent;
		}
		for (SWTBotGefEditPart part: parent.children()) {
			SWTBotGefEditPart partAnchor = getAnchorPart(part);
			if  (partAnchor != null) {
				return partAnchor;
			}
		}
		return null;
	}
	
	private void selectPropertiesTab(String label) {
		Matcher<TabbedPropertyList> matcher = new BaseMatcher<TabbedPropertyList>() {

			@Override
			public boolean matches(Object item) {
				if (item instanceof TabbedPropertyList) {
					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
			}
			
		};
		TabbedPropertyList list = (TabbedPropertyList) gefBot.viewByTitle("Properties").bot().widget(matcher);
		int numElements = list.getNumberOfElements();
		for (int index = 0; index < numElements; ++index) {
			final TabbedPropertyList.ListElement element = (ListElement) list.getElementAt(index);
			if (label.equals(element.getTabItem().getText())) {
				Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						element.setSelected(true);
					}
				});
			}
		}
	}
}
