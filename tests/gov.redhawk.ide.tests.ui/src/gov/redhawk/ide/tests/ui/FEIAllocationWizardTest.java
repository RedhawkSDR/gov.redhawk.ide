package gov.redhawk.ide.tests.ui;

import gov.redhawk.frontend.FrontendFactory;
import gov.redhawk.frontend.TunerStatus;
import gov.redhawk.frontend.ui.wizard.TunerAllocationWizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class FEIAllocationWizardTest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
				if (introManager != null) {
					IIntroPart part = introManager.getIntro();
					if (part != null) {
						introManager.closeIntro(part);
					}
				}
			}
		});
		
		SWTWorkbenchBot tmpBot = new SWTWorkbenchBot();
		SWTBotPerspective perspective = tmpBot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		tmpBot.resetActivePerspective();
	}

	private TunerAllocationWizard wizard;
	private WizardDialog dialog;
	protected SWTBot wizardBot;

	@Before
	public void beforeTest() throws Exception {
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
	public void afterTest() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				dialog.close();
			}
		});
		dialog = null;
		wizardBot = null;
		wizard = null;
	}

	@AfterClass
	public static void afterClass() {

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
	}

}
