package gov.redhawk.ide.ui.tests.properties;

import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractBasicTest {
	
	protected SWTWorkbenchBot bot;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		StandardTestActions.beforeClass();
	}
	
	@Before
	public void before() throws Exception {
		bot = new SWTWorkbenchBot();
		StandardTestActions.beforeTest(bot);
	}
	
	@After
	public void after() throws Exception {
		StandardTestActions.afterTest(bot);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		StandardTestActions.afterClass();
	}
}
