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
package gov.redhawk.ide.swtbot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.python.pydev.ui.pythonpathconf.AutoConfigMaker;
import org.python.pydev.ui.pythonpathconf.IInterpreterProviderFactory.InterpreterType;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.internal.ProjectRecord;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.util.OrbSession;

public final class StandardTestActions {

	private static volatile boolean pydevSetup = false;

	/** private to prevent instantiation since all functions are static. */
	private StandardTestActions() {
	}

	public static void configurePyDev() {
		configurePyDev(new SWTWorkbenchBot());
	}

	public static void configurePyDev(SWTBot bot) {
		if (pydevSetup) {
			return;
		}

		String originalShellText = bot.activeShell().getText();

		final AutoConfigMaker a = new AutoConfigMaker(InterpreterType.PYTHON, false, null, null);
		final Object block = new Object();
		final JobChangeAdapter adapter = new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				synchronized (block) {
					pydevSetup = true;
					block.notifyAll();
				}
			}
		};

		synchronized (block) {
			// Perform auto-config (our adapter will notify us when the scheduled job completes)
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					a.autoConfigSingleApply(adapter);
				}
			});

			// Wait until our monitor gets notified, or we time out
			try {
				block.wait(30000);
				Assert.assertTrue("Pydev setup timed out", pydevSetup);
			} catch (InterruptedException e) {
				Assert.fail("Pydev setup interrupted");
			}
		}

		bot.waitUntil(Conditions.shellIsActive(originalShellText));
	}

	/**
	 * Closes the workbench introduction screen and maximizes the window.
	 */
	public static void closeIntro() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
				if (introManager != null) {
					IIntroPart part = introManager.getIntro();
					if (part != null) {
						Assert.assertTrue(introManager.closeIntro(part));
					}
				}

				Display current = Display.getCurrent();
				if (current != null) {
					Shell shell = current.getActiveShell();
					if (shell != null) {
						shell.setMaximized(true);
					}
				}
			}
		});
	}

	/**
	 * Activates the REDHAWK perspective and resets it to defaults.
	 *
	 * @param bot
	 */
	public static void switchToScaPerspective(SWTWorkbenchBot bot) {
		SWTBotPerspective perspective = bot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		bot.resetActivePerspective();
		bot.sleep(100);
	}

	/**
	 * Uses the hotkey ctrl+b to build all projects in the workspace
	 */
	public static void buildAll() {
		KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL, 'b');
	}
	
	/**
	 * Generates the project using the Generate button in the overview tab
	 * Generates all files
	 */
	public static void generateProject(SWTWorkbenchBot bot, SWTBotEditor editor) {
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);

		editor.bot().toolbarButton(0).click();
		bot.waitUntil(Conditions.shellIsActive("Regenerate Files"), 10000);
		SWTBotShell fileShell = bot.shell("Regenerate Files");

		fileShell.bot().button("OK").click();

		try {
			SWTBotShell genShell = bot.shell("Generating...");
			bot.waitUntil(Conditions.shellCloses(genShell));
		} catch (WidgetNotFoundException e) {
			// PASS
		}
	}

	public static void assertNoOpenDialogs() {
		final String[] badDialog = { null }; // null for none (good)
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				Shell s = Display.getCurrent().getActiveShell();
				if (s == PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()) {
					badDialog[0] = null;        // good
				} else if (s != null) {
					badDialog[0] = s.getText(); // bad - set to dialog's title
				}
			}

		});

		if (badDialog[0] != null) {
			Assert.fail("Invalid dialog left open at end of test: " + badDialog[0]);
		}
	}

	/**
	 * Delete all projects in the workspace.
	 *
	 * @throws CoreException
	 */
	public static void clearWorkspace() throws CoreException {
		ResourcesPlugin.getWorkspace().getRoot().delete(true, true, new NullProgressMonitor());
	}

	/**
	 * <b>NOTE</b>: It is recommended you override the environment variables in the pom.xml for tests instead of
	 * changing the SDR root.
	 * @param pluginId Plugin that contains the SDR
	 * @param path Relative path within the plugin for the SDR, usually 'sdr'
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void setTargetSdr(String pluginId, String path) throws IOException, URISyntaxException {
		final URL url = FileLocator.find(Platform.getBundle(pluginId), new Path(path), null);
		final SdrRoot root = SdrUiPlugin.getDefault().getTargetSdrRoot();
		root.load(null);
		final URL fileURL = FileLocator.toFileURL(url);
		SdrUiPlugin.getDefault().getPreferenceStore().setValue(SdrUiPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE,
			new File(fileURL.toURI()).getAbsolutePath());
		root.reload(null);
		Assert.assertTrue("SDR failed to load: " + root.getLoadStatus(), root.getLoadStatus().isOK());
	}

	/**
	 * @param bundle the bundle in which to search
	 * @param path file path relative to plug-in installation location
	 * @param override override map of override substitution arguments to be used for any $arg$ path elements. The map
	 * keys correspond to the substitution arguments (eg. "$nl$" or "$os$"). The resulting values must be of type
	 * java.lang.String. If the map is null, or does not contain the required substitution argument, the default is
	 * used.
	 * @see FileLocator#findEntries(Bundle, IPath, Map)
	 * @throws CoreException
	 */
	public static void importProject(Bundle bundle, IPath path, Map<String, String> override) throws CoreException {
		if (!".project".equals(path.lastSegment())) {
			path = path.append(".project");
		}
		URL bundleUrl = FileLocator.find(bundle, path, override);

		URL fileUrl;
		try {
			fileUrl = FileLocator.toFileURL(bundleUrl);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, SwtBotActivator.PLUGIN_ID, "Failed to covert project path to file system path: " + path, e));
		}
		File bundleFile = new File(fileUrl.getPath());
		ProjectRecord record = new ProjectRecord(bundleFile);
		try {
			createExistingProject(record);
		} catch (InvocationTargetException | InterruptedException e) {
			throw new CoreException(new Status(Status.ERROR, SwtBotActivator.PLUGIN_ID, "Failed to import existing project: " + path, e));
		}
	}

	/**
	 * Create the project described in record. If it is successful return true.
	 *
	 * @param record
	 * @return boolean <code>true</code> if successful
	 * @throws InterruptedException
	 */
	private static boolean createExistingProject(final ProjectRecord record) throws InvocationTargetException, InterruptedException {
		String projectName = record.getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		if (record.description == null) {
			// error case
			record.description = workspace.newProjectDescription(projectName);
			IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location
			if (Platform.getLocation().isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			} else {
				record.description.setLocation(locationPath);
			}
		} else {
			record.description.setName(projectName);
		}

		// import from file system
		File importSource = null;
		// import project from location copying files - use default project
		// location for this workspace
		URI locationURI = record.description.getLocationURI();
		// if location is null, project already exists in this location or
		// some error condition occurred.
		if (locationURI != null) {
			// validate the location of the project being copied
			IStatus result = ResourcesPlugin.getWorkspace().validateProjectLocationURI(project, locationURI);
			if (!result.isOK()) {
				throw new InvocationTargetException(new CoreException(result));
			}

			importSource = new File(locationURI);
			IProjectDescription desc = workspace.newProjectDescription(projectName);
			desc.setBuildSpec(record.description.getBuildSpec());
			desc.setComment(record.description.getComment());
			desc.setDynamicReferences(record.description.getDynamicReferences());
			desc.setNatureIds(record.description.getNatureIds());
			desc.setReferencedProjects(record.description.getReferencedProjects());
			record.description = desc;
		}

		try {
			project.create(record.description, new NullProgressMonitor());
			project.open(IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}

		// import operation to import project files if copy checkbox is selected
		if (importSource != null) {
			List< ? > filesToImport = FileSystemStructureProvider.INSTANCE.getChildren(importSource);
			IOverwriteQuery overwrite = new IOverwriteQuery() {

				@Override
				public String queryOverwrite(String pathString) {
					return IOverwriteQuery.ALL;
				}
			};
			ImportOperation operation = new ImportOperation(project.getFullPath(), importSource, FileSystemStructureProvider.INSTANCE, overwrite, filesToImport);
			operation.setContext(null);
			operation.setOverwriteResources(true); // need to overwrite
			// .project, .classpath
			// files
			operation.setCreateContainerStructure(false);
			operation.run(new NullProgressMonitor());
			IStatus status = operation.getStatus();
			if (!status.isOK()) {
				throw new InvocationTargetException(new CoreException(status));
			}
		}

		return true;
	}

	public static void writeToCell(SWTBot bot, final SWTBotTreeItem item, final int column, final String text) {
		item.click(column);

		// Wait for cell editor to appear
		bot.sleep(500);

		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.typeText(text);
		keyboard.pressShortcut(Keystrokes.CR);

		// Wait for cell editor to close
		bot.sleep(100);
	}

	/**
	 * Close all dialogs and editors. Delete any projects in the workspace.
	 *
	 * @param bot
	 * @throws CoreException
	 */
	public static void cleanup(SWTWorkbenchBot bot) throws CoreException {
		if (bot == null) {
			bot = new SWTWorkbenchBot();
		}

		final boolean[] dialogsClosed = { false };
		while (!dialogsClosed[0]) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					Shell s = Display.getCurrent().getActiveShell();
					if (s == PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()) {
						dialogsClosed[0] = true;
					} else {
						if (s != null) {
							s.dispose();
						}
					}
				}

			});
		}

		closeAllShells(bot);

		bot.closeAllEditors();

		StandardTestActions.clearWorkspace();
	}

	/**
	 * This method provides similar results to {@link SWTWorkbenchBot#closeAllShells()}, except that it avoids closing
	 * the "limbo" shell which is used to by Eclipse to re-parent controls that are hidden. Closing the limbo shell
	 * appears to be a bug in the SWTBot code, and definitely causes Eclipse to spew lots of errors when the visibility
	 * state of things is changed.
	 * @param bot
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=475346
	 */
	private static void closeAllShells(SWTWorkbenchBot bot) {
		RunnableWithResult<Shell> getShellRunnable = new RunnableWithResult.Impl<Shell>() {
			@Override
			public void run() {
				setResult(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			}
		};
		bot.getDisplay().syncExec(getShellRunnable);
		Shell activeWorkbenchWindowShell = getShellRunnable.getResult();

		SWTBotShell[] shells = bot.shells();
		for (SWTBotShell shell : shells) {
			if (activeWorkbenchWindowShell != shell.widget && !shell.toString().contains("PartRenderingEngine's limbo")) {
				shell.close();
			}
		}
	}

	public static void writeToCell(SWTBot bot, SWTBotTable table, final int row, final int column, final String text) {
		table.click(row, column);

		// Wait for cell editor to appear
		bot.sleep(500);

		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.typeText(text);
		keyboard.pressShortcut(Keystrokes.CR);

		// Wait for cell editor to close
		bot.sleep(100);
	}

	/**
	 * @param bot
	 * @param page
	 * @deprecated Use {@link EditorActions#assertEditorTabValid(SWTBotEditor, String)}
	 */
	@Deprecated
	public static void assertFormValid(SWTBot bot, final IFormPage page) {
		try {
			waitForValidationState(bot, page, IMessageProvider.NONE, IMessageProvider.INFORMATION, IMessageProvider.WARNING);
		} catch (TimeoutException e) {
			Assert.fail("Form should be valid");
		}
	}

	public static int getValidationState(IFormPage page) {
		int messageType = page.getManagedForm().getForm().getMessageType();
		return messageType;
	}

	public static void waitForValidationState(SWTBot bot, final IFormPage page, final int... states) {
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				int current = getValidationState(page);
				for (int i : states) {
					if (i == current) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {

			}

			@Override
			public String getFailureMessage() {
				return "Failed waiting for validation state to change to: " + Arrays.toString(states);
			}

		}, 5000, 200);
	}

	/**
	 * @param bot
	 * @param page
	 * @deprecated Use {@link EditorActions#assertEditorTabValid(SWTBotEditor, String)}
	 */
	@Deprecated
	public static void assertFormInvalid(SWTBot bot, final IFormPage page) {
		try {
			waitForValidationState(bot, page, IMessageProvider.ERROR);
		} catch (TimeoutException e) {
			Assert.fail("Form should be invalid");
		}
	}

	private static Boolean supportsRuntime = null;

	public static void assertRuntimeEnvironment() {
		if (supportsRuntime != null) {
			Assert.assertTrue("Envirornment does not support runtime tests.", supportsRuntime);
		}

		try {
			String ossieHome = System.getenv("OSSIEHOME");
			String sdrRoot = System.getenv("SDRROOT");

			Assert.assertNotNull("OSSIEHOME environment variable is not set", ossieHome);
			Assert.assertNotNull("SDRROOT environment variable is not set", sdrRoot);

			File ossieHomeFile = new File(ossieHome);
			File sdrRootFile = new File(sdrRoot);

			Assert.assertTrue("OSSIEHOME is not directory: " + ossieHome, ossieHomeFile.isDirectory());
			Assert.assertTrue("SDRROOT is not directory: " + sdrRootFile, sdrRootFile.isDirectory());

			File nodeBooter = new File(ossieHome, "bin/nodeBooter");
			Assert.assertTrue("nodeBooter is not file: " + nodeBooter, nodeBooter.isFile());
			Assert.assertTrue("nodeBooter not executable: " + nodeBooter, nodeBooter.canExecute());

			Map<String, String> initRefs = OrbSession.getOmniORBInitRefs();
			Assert.assertNotNull("NameService init ref not defined in /etc/omniORB.cfg", initRefs.get("NameService"));
			Assert.assertNotNull("EventService init ref not defined in /etc/omniORB.cfg", initRefs.get("EventService"));

			Properties props = OrbSession.getOmniORBInitRefsAsProperties();
			final OrbSession session = OrbSession.createSession("testSession", Platform.getApplicationArgs(), props);

			try {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<Object> future = executor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						return session.getOrb().resolve_initial_references("NameService");
					}

				});
				try {
					future.get(30, TimeUnit.SECONDS);
				} catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
					Assert.fail(String.format("Failed to connect to NameService: %s", e.toString()));
				}

				future = executor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						return session.getOrb().resolve_initial_references("EventService");
					}

				});
				try {
					future.get(30, TimeUnit.SECONDS);
				} catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
					Assert.fail(String.format("Failed to connect to EventService: %s", e.toString()));
				}
			} finally {
				session.dispose();
			}

			supportsRuntime = true;
		} catch (RuntimeException e) { // SUPPRESS CHECKSTYLE IllegalCatch - it's re-thrown
			supportsRuntime = false;
			throw e;
		}
	}

	public static SWTBotToolbarButton viewToolbarWithToolTip(final SWTBotView view, final String tooltip) {
		Assert.assertNotNull(view);
		Assert.assertNotNull(tooltip);

		final SWTBotToolbarButton[] button = new SWTBotToolbarButton[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				List<SWTBotToolbarButton> buttons = view.getToolbarButtons();
				for (SWTBotToolbarButton b : buttons) {
					if (tooltip.equals(b.getToolTipText())) {
						button[0] = b;
						return;
					}
				}
			}

		});

		Assert.assertNotNull("Unable to find button with tooltip: " + tooltip, button[0]);

		return button[0];
	}

	public static void cleanUpConnections() {
		final ScaDomainManagerRegistry domReg = ScaPlugin.getDefault().getDomainManagerRegistry(null);
		ScaModelCommand.execute(domReg, new ScaModelCommand() {

			@Override
			public void execute() {
				domReg.getDomains().clear();
			}
		});
	}

	public static void cleanUpLaunches() throws DebugException {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		for (ILaunch launch : launchManager.getLaunches()) {
			launch.terminate();
		}
	}

	public static SWTBotTreeItem getTreeItemMatches(SWTBotTree tree, String... regexp) {
		SWTBotTreeItem result = getTreeItemMatches(tree, Arrays.asList(regexp));
		if (result == null) {
			throw new WidgetNotFoundException("Could not find TreeItem with text that matches: " + Arrays.toString(regexp)); //$NON-NLS-1$
		}
		return result;
	}

	private static SWTBotTreeItem getTreeItemMatches(SWTBotTree parent, List<String> regexp) {
		if (regexp.isEmpty()) {
			return null;
		}
		SWTBotTreeItem[] items = parent.getAllItems();
		for (SWTBotTreeItem item : items) {
			if (item.getText().matches(regexp.get(0))) {
				item.expand();
				SWTBotTreeItem result = getTreeItemMatches(item, regexp.subList(1, regexp.size()));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private static SWTBotTreeItem getTreeItemMatches(SWTBotTreeItem parent, List<String> regexp) {
		if (regexp.isEmpty()) {
			return parent;
		}
		SWTBotTreeItem[] items = parent.getItems();
		for (SWTBotTreeItem item : items) {
			if (item.getText().matches(regexp.get(0))) {
				item.expand();
				SWTBotTreeItem result = getTreeItemMatches(item, regexp.subList(1, regexp.size()));
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public static SWTBotTreeItem getTreeItemMatches(SWTBotTreeItem tree, String regexp) {
		List<String> labels = tree.getNodes();
		for (String l : labels) {
			if (l.matches(regexp)) {
				return tree.getNode(l);
			}
		}
		throw new WidgetNotFoundException("Could not find node with text that matches: " + regexp); //$NON-NLS-1$
	}
}
