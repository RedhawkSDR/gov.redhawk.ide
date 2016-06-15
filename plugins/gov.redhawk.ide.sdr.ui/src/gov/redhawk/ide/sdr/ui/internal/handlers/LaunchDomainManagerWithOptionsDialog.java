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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.nodebooter.DebugLevel;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.preferences.ScaPreferenceConstants;
import gov.redhawk.sca.ui.ScaUiPlugin;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class LaunchDomainManagerWithOptionsDialog extends CheckedTreeSelectionDialog {
	/**
	 * A delay in milliseconds that reduces the risk that the user accidentally triggers a
	 * button by pressing the 'Enter' key immediately after a job has finished.
	 * 
	 * @since 3.6
	 */
	private static final int RESTORE_ENTER_DELAY = 500;
	private final Set<String> takenDomainNames = new HashSet<String>();
	private boolean lockedUI = false;

	private WritableSet nodes = new WritableSet();
	private WritableValue nodeDebugLevel = new WritableValue(DebugLevel.Info, DebugLevel.class);
	private WritableValue nodeArguments = new WritableValue();

	private final DomainManagerLaunchConfiguration model;

	private final DataBindingContext context = new DataBindingContext();
	private Binding nameBinding = null;
	private boolean useCustomProgressMonitorPart;
	private ProgressMonitorPart progressMonitorPart;

	private int activeRunningOperations;
	private long timeWhenLastJobFinished;
	private Cursor waitCursor;
	private Cursor arrowCursor;
	private Button cancelButton;
	private final SelectionListener cancelListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			cancelPressed();
		}
	};

	// Load the domains that are on the Naming service already
	private final IRunnableWithProgress scanForTakenDomainNames = new IRunnableWithProgress() {

		@Override
		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Scanning for running domains...", IProgressMonitor.UNKNOWN);
			LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.clear();
			String[] names;
			try {
				names = ScaPlugin.findDomainNamesOnDefaultNameServer(monitor);
			} catch (CoreException e) {
				// Ignore errors
				names = new String[0];
			}
			LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.addAll(Arrays.asList(names));

			// We also need to handle the case where the default name server doesn't have the domain name in use
			// but we have a domain definition against an alternate name server...in this case you cannot use
			// the domain name regardless because we cannot alter or remove the connection setting.
			final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);
			for (final ScaDomainManager dom : dmReg.getDomains()) {
				if (monitor.isCanceled()) {
					break;
				}
				if (dom != null) {
					if (!namingService.equals(dom.getConnectionProperties().get(ScaDomainManager.NAMING_SERVICE_PROP))) {
						// if the domain connection registry uses a different name server we cannot
						// use this name regardless of the connection state because to do so would
						// require us to modify the domain registry entry...which is not allowable
						LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.add(dom.getLabel());
					}
				}
			}
			if (nameBinding != null) {
				LaunchDomainManagerWithOptionsDialog.this.nameBinding.validateTargetToModel();
			}
		}
	};

	// Load the domains that are on the Naming service already
	private final IRunnableWithProgress checkDomainName = new IRunnableWithProgress() {

		@Override
		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Checking domain name...", IProgressMonitor.UNKNOWN);
			final String domainName = LaunchDomainManagerWithOptionsDialog.this.model.getDomainName();
			final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);
			final ScaDomainManager dom = dmReg.findDomain(domainName);
			if (dom != null) {
				if (!namingService.equals(dom.getConnectionProperties().get(ScaDomainManager.NAMING_SERVICE_PROP))) {
					LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.add(domainName);
				}
			}

			// Check again just in case the domain started up since the last scan.
			try {
				if (ScaPlugin.isDomainOnline(domainName, monitor)) {
					LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.add(domainName);
				}
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}

			LaunchDomainManagerWithOptionsDialog.this.nameBinding.validateTargetToModel();
		}
	};

	private SdrRoot sdrRoot;
	private final ScaDomainManagerRegistry dmReg;

	public LaunchDomainManagerWithOptionsDialog(final Shell parentShell, final DomainManagerLaunchConfiguration model, SdrRoot root) {
		super(parentShell, new LaunchDomainManagerDialogLabelProvider(), new LaunchDomainManagerDialogContentProvider());
		this.model = model;
		this.sdrRoot = root;
		this.dmReg = ScaPlugin.getDefault().getDomainManagerRegistry(parentShell.getDisplay());
		this.setTitle("Launch Domain Manager");
		setComparator(new ViewerComparator());
		setEmptyListMessage("No nodes found");
		setStatusLineAboveButtons(true);
		super.setInput(root);
	}

	@Override
	protected Control createDialogArea(final Composite root) {
		final Composite composite = new Composite(root, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final GridLayout gridLayout = new GridLayout(3, false);
		final GridDataFactory textFactory = GridDataFactory.fillDefaults().grab(true, false).span(2, 1);
		final GridData data;

		final Group domainManagerGroup = new Group(composite, SWT.NONE);

		domainManagerGroup.setText("Domain Manager");
		domainManagerGroup.setLayout(gridLayout);
		domainManagerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Label label = new Label(domainManagerGroup, SWT.NONE);
		label.setText("Domain Name: ");
		Text text = new Text(domainManagerGroup, SWT.BORDER);
		data = textFactory.create();
		data.horizontalSpan = 2;
		text.setLayoutData(data);

		this.nameBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(text),
			PojoProperties.value(this.model.getClass(), DomainManagerLaunchConfiguration.PROP_DOMAIN_NAME).observe(this.model),
			new UpdateValueStrategy().setAfterConvertValidator(new DomainValidator()), null);

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				updateButtonsEnableState((IStatus) LaunchDomainManagerWithOptionsDialog.this.nameBinding.getValidationStatus().getValue());
			}
		});

		label = new Label(domainManagerGroup, SWT.NONE);
		label.setText("Debug Level: ");
		ComboViewer debugViewer = new ComboViewer(domainManagerGroup, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN | SWT.BORDER);
		debugViewer.setLabelProvider(new LabelProvider());
		debugViewer.setContentProvider(new ArrayContentProvider());
		debugViewer.setInput(DebugLevel.values());
		debugViewer.getControl().setLayoutData(data);
		this.context.bindValue(ViewersObservables.observeSingleSelection(debugViewer),
			PojoProperties.value(this.model.getClass(), DomainManagerLaunchConfiguration.PROP_DEBUG_LEVEL).observe(this.model));

		label = new Label(domainManagerGroup, SWT.NONE);
		label.setText("Arguments:");
		text = new Text(domainManagerGroup, SWT.BORDER);
		text.setLayoutData(data);
		this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(text),
			PojoProperties.value(this.model.getClass(), DomainManagerLaunchConfiguration.PROP_ARGUMENTS).observe(this.model));

		final Group deviceManagerGroup = new Group(composite, SWT.NONE);

		deviceManagerGroup.setText("Device Manager");
		deviceManagerGroup.setLayout(GridLayoutFactory.fillDefaults().margins(5, 5).create());
		deviceManagerGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		deviceManagerGroup.setVisible(!this.sdrRoot.getNodesContainer().getNodes().isEmpty());

		final CheckboxTreeViewer treeViewer = createTreeViewer(deviceManagerGroup);
		treeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		final Control buttonComposite = createSelectionButtons(deviceManagerGroup);
		buttonComposite.setLayoutData(GridDataFactory.fillDefaults().create());

		context.bindSet(ViewersObservables.observeCheckedElements(treeViewer, DeviceConfiguration.class), nodes);

		// Insert a progress monitor
		this.progressMonitorPart = createProgressMonitorPart(composite, new GridLayout());
		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.progressMonitorPart.setLayoutData(gridData);
		this.progressMonitorPart.setVisible(false);

		// Build the separator line
		final Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Dialog.applyDialogFont(composite);

		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					LaunchDomainManagerWithOptionsDialog.this.run(true, true, scanForTakenDomainNames);
					updateButtonsEnableState(Status.OK_STATUS);
				} catch (final InvocationTargetException e) {
					SdrUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Error scanning for domain names", e));
				} catch (final InterruptedException e) {
					updateButtonsEnableState(Status.OK_STATUS);
				}
			}
		});

		return composite;
	}

	// Inner class used to check for error states in the Dialog page
	class DomainValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			String errorMessage = null;
			IStatus status = null;

			// IMPORTANT - DO NOT ISSUE CORBA CALLS HERE BECAUSE THEY CAN HANG POTENTIALLY FOREVER
			// AND BLOCK THE UI THREAD
			String s = (String) value;
			if ((s == null) || (s.trim().length() == 0)) {
				errorMessage = "Please provide a valid Domain Manager name.";
			}
			s = s.trim();

			final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);
			final ScaDomainManager dom = dmReg.findDomain(s);
			if ((dom != null) && (!namingService.equals(dom.getConnectionProperties().get(ScaDomainManager.NAMING_SERVICE_PROP)))) {
				errorMessage = "This name is registered against a non-default name server and cannot be launched";
			}

			if (LaunchDomainManagerWithOptionsDialog.this.takenDomainNames.contains(s)) {
				errorMessage = "Domain of this name is in use, please select a different name.";
			}

			if (errorMessage != null) {
				status = ValidationStatus.error(errorMessage);
				LaunchDomainManagerWithOptionsDialog.this.updateStatus(status);
				return ValidationStatus.error(errorMessage);
			}

			status = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", null);
			LaunchDomainManagerWithOptionsDialog.this.updateStatus(status);
			return ValidationStatus.ok();
		}

	} // End DomainValidator inner class

	/**
	 * Hook method for subclasses to create a custom progress monitor part.
	 * <p>
	 * The default implementation creates a progress monitor with a stop button will be created.
	 * </p>
	 * 
	 * @param composite the parent composite
	 * @param pmlayout the layout
	 * @return ProgressMonitorPart the progress monitor part
	 */
	protected ProgressMonitorPart createProgressMonitorPart(final Composite composite, final GridLayout pmlayout) {
		this.useCustomProgressMonitorPart = false;
		return new ProgressMonitorPart(composite, pmlayout, true) {
			String currentTask = null;

			@Override
			public void setBlocked(final IStatus reason) {
				super.setBlocked(reason);
				if (!LaunchDomainManagerWithOptionsDialog.this.lockedUI) {
					Dialog.getBlockedHandler().showBlocked(getShell(), this, reason, this.currentTask);
				}
			}

			@Override
			public void clearBlocked() {
				super.clearBlocked();
				if (!LaunchDomainManagerWithOptionsDialog.this.lockedUI) {
					Dialog.getBlockedHandler().clearBlocked();
				}
			}

			@Override
			public void beginTask(final String name, final int totalWork) {
				super.beginTask(name, totalWork);
				this.currentTask = name;
			}

			@Override
			public void setTaskName(final String name) {
				super.setTaskName(name);
				this.currentTask = name;
			}

			@Override
			public void subTask(final String name) {
				super.subTask(name);
				// If we haven't got anything yet use this value for more
				// context
				if (this.currentTask == null) {
					this.currentTask = name;
				}
			}
		};
	}

	@Override
	public void create() {
		super.create();
		updateButtonsEnableState((IStatus) LaunchDomainManagerWithOptionsDialog.this.nameBinding.getValidationStatus().getValue());
	}

	@Override
	protected Composite createSelectionButtons(final Composite parent) {
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, true));
		final Control controls = super.createSelectionButtons(root);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite subContainer = new Composite(root, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(2, false);

		subContainer.setLayout(gridLayout);
		subContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Label label = new Label(subContainer, SWT.NONE);
		label.setText("Debug Level: ");

		final ComboViewer debugViewer = new ComboViewer(subContainer, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN | SWT.BORDER);
		debugViewer.setContentProvider(new ArrayContentProvider());
		debugViewer.setInput(DebugLevel.values());
		debugViewer.setSelection(new StructuredSelection("Info"));
		context.bindValue(ViewersObservables.observeSingleSelection(debugViewer), nodeDebugLevel);

		label = new Label(subContainer, SWT.NONE);
		label.setText("Arguments:");
		Text text = new Text(subContainer, SWT.BORDER);
		text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(text), nodeArguments, null, null);

		return root;
	}

	@Override
	protected void okPressed() {
		try {
			run(true, true, checkDomainName);
		} catch (final InvocationTargetException e) {
			SdrUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Error scanning for domain names", e));
		} catch (final InterruptedException e) {
			updateButtonsEnableState(Status.OK_STATUS);
			return;
		}

		this.nameBinding.validateTargetToModel();
		updateButtonsEnableState(Status.OK_STATUS);

		// If the name isn't valid after the final scan, then abort
		if (!((IStatus) this.nameBinding.getValidationStatus().getValue()).isOK()) {
			return;
		}
		super.okPressed();
	}

	@Override
	protected CheckboxTreeViewer createTreeViewer(final Composite parent) {
		final CheckboxTreeViewer retVal = super.createTreeViewer(parent);

		retVal.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				final DeviceConfiguration dc = (DeviceConfiguration) ss.getFirstElement();
				retVal.setChecked(dc, !retVal.getChecked(dc));

				// DataBinding for observeCheckedElements does not pick up on double click events
				// So manually adding/removing nodes here to compensate
				if (retVal.getChecked(dc)) {
					nodes.add(dc);
				} else {
					nodes.remove(dc);
				}
			}
		});

		return retVal;
	}

	/**
	 * This implementation of IRunnableContext#run(boolean, boolean,
	 * IRunnableWithProgress) blocks until the runnable has been run, regardless
	 * of the value of <code>fork</code>. It is recommended that
	 * <code>fork</code> is set to true in most cases. If <code>fork</code>
	 * is set to <code>false</code>, the runnable will run in the UI thread
	 * and it is the runnable's responsibility to call
	 * <code>Display.readAndDispatch()</code> to ensure UI responsiveness.
	 * 
	 * UI state is saved prior to executing the long-running operation and is
	 * restored after the long-running operation completes executing. Any
	 * attempt to change the UI state of the wizard in the long-running
	 * operation will be nullified when original UI state is restored.
	 * 
	 */
	public void run(final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		// The operation can only be canceled if it is executed in a separate
		// thread.
		// Otherwise the UI is blocked anyway.
		getOkButton().setEnabled(false);
		if (this.activeRunningOperations == 0) {
			aboutToStart(fork && cancelable);
		}
		this.activeRunningOperations++;
		try {
			if (!fork) {
				this.lockedUI = true;
			}
			ModalContext.run(runnable, fork, getProgressMonitor(), getShell().getDisplay());
			this.lockedUI = false;
		} finally {
			// explicitly invoke done() on our progress monitor so that its
			// label does not spill over to the next invocation, see bug 271530
			if (getProgressMonitor() != null && getReturnCode() != Status.CANCEL) {
				getProgressMonitor().done();
			}
			// Stop if this is the last one
			this.timeWhenLastJobFinished = System.currentTimeMillis();
			stopped();
			this.activeRunningOperations--;
		}
		if (getOkButton() != null) {
			getOkButton().setEnabled(true);
		}
	}

	protected IProgressMonitor getProgressMonitor() {
		return this.progressMonitorPart;
	}

	/**
	 * A long running operation triggered through the wizard was stopped either
	 * by user input or by normal end. Hides the progress monitor and restores
	 * the enable state wizard's buttons and controls.
	 * 
	 * @param savedState
	 * the saved UI state as returned by <code>aboutToStart</code>
	 * @see #aboutToStart
	 */
	private void stopped() {
		if (getShell() != null && !getShell().isDisposed()) {
			if (progressMonitorPart != null) {
				this.progressMonitorPart.setVisible(false);
				this.progressMonitorPart.removeFromCancelComponent(this.cancelButton);
			}
			setDisplayCursor(null);
			if (this.useCustomProgressMonitorPart) {
				this.cancelButton.addSelectionListener(this.cancelListener);
				this.cancelButton.setCursor(null);
				this.arrowCursor.dispose();
				this.arrowCursor = null;
			}
			if (this.waitCursor != null) {
				this.waitCursor.dispose();
				this.waitCursor = null;
			}
		}
	}

	/**
	 * Creates the buttons for this dialog's button bar.
	 * <p>
	 * The <code>WizardDialog</code> implementation of this framework method
	 * prevents the parent composite's columns from being made equal width in
	 * order to remove the margin between the Back and Next buttons.
	 * </p>
	 * 
	 * @param parent
	 * the parent composite to contain the buttons
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		((GridLayout) parent.getLayout()).makeColumnsEqualWidth = false;
		this.cancelButton = createCancelButton(parent);

		// also add cancel operation to the shell
		getShell().addListener(SWT.Traverse, new Listener() {

			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					cancelPressed();
					break;
				default:
				}
			}
		});
	}

	/**
	 * Creates the Cancel button for this wizard dialog. Creates a standard (<code>SWT.PUSH</code>)
	 * button and registers for its selection events. Note that the number of
	 * columns in the button bar composite is incremented. The Cancel button is
	 * created specially to give it a removeable listener.
	 * 
	 * @param parent
	 * the parent button bar
	 * @return the new Cancel button
	 */
	private Button createCancelButton(final Composite parent) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(IDialogConstants.CANCEL_LABEL);
		setButtonLayoutData(button);
		button.setFont(parent.getFont());
		button.setData(new Integer(IDialogConstants.CANCEL_ID));
		button.addSelectionListener(this.cancelListener);
		return button;
	}

	/**
	 * About to start a long running operation triggered through the wizard.
	 * Shows the progress monitor and disables the wizard's buttons and
	 * controls.
	 * 
	 * @param enableCancelButton
	 * <code>true</code> if the Cancel button should be enabled,
	 * and <code>false</code> if it should be disabled
	 * @return the saved UI state
	 */
	private void aboutToStart(final boolean enableCancelButton) {
		if (getShell() != null) {
			// Save focus control
			Control focusControl = getShell().getDisplay().getFocusControl();
			if (focusControl != null && focusControl.getShell() != getShell()) {
				focusControl = null;
			}

			// Set the busy cursor to all shells.
			final Display d = getShell().getDisplay();
			this.waitCursor = new Cursor(d, SWT.CURSOR_WAIT);
			setDisplayCursor(this.waitCursor);

			if (this.useCustomProgressMonitorPart) {
				this.cancelButton.removeSelectionListener(this.cancelListener);
				// Set the arrow cursor to the cancel component.
				this.arrowCursor = new Cursor(d, SWT.CURSOR_ARROW);
				this.cancelButton.setCursor(this.arrowCursor);
			}

			// Deactivate shell
			// Activate cancel behavior.
			if (enableCancelButton || this.useCustomProgressMonitorPart) {
				this.progressMonitorPart.attachToCancelComponent(this.cancelButton);
			}
			this.progressMonitorPart.setVisible(true);

			// Install traverse listener once in order to implement 'Enter' and 'Space' key blocking
			if (this.timeWhenLastJobFinished == -1) {
				this.timeWhenLastJobFinished = 0;
				getShell().addTraverseListener(new TraverseListener() {
					@Override
					public void keyTraversed(final TraverseEvent e) {
						if (e.detail == SWT.TRAVERSE_RETURN || (e.detail == SWT.TRAVERSE_MNEMONIC && e.keyCode == 32)) {
							// We want to ignore the keystroke when we detect that it has been received within the
							// delay period after the last operation has finished. This prevents the user from
							// accidentally
							// hitting "Enter" or "Space", intending to cancel an operation, but having it processed
							// exactly
							// when the operation finished, thus traversing the wizard. If there is another operation
							// still
							// running, the UI is locked anyway so we are not in this code. This listener should fire
							// only
							// after the UI state is restored (which by definition means all jobs are done.
							// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=287887
							if (LaunchDomainManagerWithOptionsDialog.this.timeWhenLastJobFinished != 0 && System.currentTimeMillis()
								- LaunchDomainManagerWithOptionsDialog.this.timeWhenLastJobFinished < LaunchDomainManagerWithOptionsDialog.RESTORE_ENTER_DELAY) {
								e.doit = false;
								return;
							}
							LaunchDomainManagerWithOptionsDialog.this.timeWhenLastJobFinished = 0;
						}
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	@Override
	protected void cancelPressed() {
		if (this.activeRunningOperations <= 0) {
			// Close the dialog. The check whether the dialog can be
			// closed or not is done in <code>okToClose</code>.
			// This ensures that the check is also evaluated when the user
			// presses the window's close button.
			setReturnCode(Window.CANCEL);
			close();
		} else {
			nameBinding.dispose();
			nameBinding = null;
			context.dispose();
			setReturnCode(Window.CANCEL);
			IProgressMonitor monitor = getProgressMonitor();
			this.progressMonitorPart = null;
			monitor.setCanceled(true);
			stopped();
			close();
		}
	}

	/**
	 * Sets the given cursor for all shells currently active for this window's
	 * display.
	 * 
	 * @param c
	 * the cursor
	 */
	private void setDisplayCursor(final Cursor c) {
		final Shell[] shells = getShell().getDisplay().getShells();
		for (int i = 0; i < shells.length; i++) {
			shells[i].setCursor(c);
		}
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		// return createButtonBarTray(parent);
		return super.createButtonBar(parent);
	}

	/**
	 * Override the method so that we always return the Status of our name binding field since he is the one that
	 * determines the validity of the Dialog
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void updateButtonsEnableState(final IStatus status) {
		if (nameBinding != null) {
			super.updateButtonsEnableState((IStatus) this.nameBinding.getValidationStatus().getValue());
		}
	}

	public List<DeviceManagerLaunchConfiguration> getDeviceManagerLaunchConfigurations() {
		List<DeviceManagerLaunchConfiguration> retVal = new ArrayList<DeviceManagerLaunchConfiguration>();
		for (Object node : this.nodes) {
			DeviceConfiguration dcd = (DeviceConfiguration) node;
			DeviceManagerLaunchConfiguration conf = new DeviceManagerLaunchConfiguration(model.getDomainName(), dcd, (DebugLevel) nodeDebugLevel.getValue(),
				(String) nodeArguments.getValue(), null);
			retVal.add(conf);
		}
		return retVal;
	}

	public DomainManagerLaunchConfiguration getDomainManagerLaunchConfiguration() {
		return model;
	}
}
