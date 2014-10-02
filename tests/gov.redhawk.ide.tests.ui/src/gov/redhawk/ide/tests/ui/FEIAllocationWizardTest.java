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
package gov.redhawk.ide.tests.ui;

import gov.redhawk.frontend.FrontendFactory;
import gov.redhawk.frontend.TunerStatus;
import gov.redhawk.frontend.ui.wizard.TunerAllocationWizard;
import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FEIAllocationWizardTest extends UITest {
	private TunerAllocationWizard wizard;
	private WizardDialog dialog;
	protected SWTBot wizardBot;

	@Before
	public void beforeTest() throws Exception {
		super.before();
		TunerStatus tuner = FrontendFactory.eINSTANCE.createTunerStatus();
		wizard = new TunerAllocationWizard(tuner);
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				dialog.setBlockOnOpen(false);
				dialog.open();
				wizardBot = new SWTBot(dialog.getShell());
			}

		});

	}

	@After
	public void after() throws Exception {
		dialog = null;
		wizardBot = null;
		wizard = null;
		super.after();
	}


	@Test
	public void test_IDE_789() {
		SWTBotText cfText = wizardBot.textWithLabel("Center Frequency (MHz)");
		cfText.setText("100");
		SWTBotText srText = wizardBot.textWithLabel("Sample Rate (Msps)");
		srText.setText("20");
		SWTBotText bwText = wizardBot.textWithLabel("Bandwidth (MHz)");
		bwText.setText("20");
		wizardBot.sleep(500);
		Assert.assertNull(dialog.getErrorMessage());

		SWTBotText bwTolText = wizardBot.textWithLabel("Bandwidth Tolerance (%)");
		bwTolText.setText("100");
		wizardBot.sleep(500);
		Assert.assertNull(dialog.getErrorMessage());
		Assert.assertTrue(wizard.canFinish());
		
		bwTolText.setText("200");
		wizardBot.sleep(500);
		Assert.assertNull(dialog.getErrorMessage());
		Assert.assertTrue(wizard.canFinish());
		
		bwTolText.setText("-1");
		wizardBot.sleep(500);
		Assert.assertNotNull(dialog.getErrorMessage());
		Assert.assertFalse(wizard.canFinish());
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				dialog.close();
			}
		});
	}

}
