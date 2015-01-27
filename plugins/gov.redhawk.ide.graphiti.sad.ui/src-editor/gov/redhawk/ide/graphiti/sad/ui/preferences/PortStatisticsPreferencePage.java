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
package gov.redhawk.ide.graphiti.sad.ui.preferences;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PortStatisticsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private DoubleFieldEditor queueLevel;
	private DoubleFieldEditor timeSinceLastPush;
	private DoubleFieldEditor queueFlush;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(SADUIGraphitiPlugin.getDefault().getPreferenceStore());
		setDescription("Graphical port monitoring preferences");

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		createWarningGroup(composite);
		createErrorGroup(composite);

		return null;
	}

	/**
	 * Controls for warning events when port statistics are running
	 */
	private void createWarningGroup(Composite composite) {
		Group warningGroup = new Group(composite, SWT.LEFT);
		warningGroup.setLayout(new GridLayout());
		warningGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		warningGroup.setText(GraphitiSadMessages.PortStatPreference_warningGroupTitle);
		warningGroup.setToolTipText(GraphitiSadMessages.PortStatPreference_warningGroupToolTip);

		// Grid composite of two columns to hold all of the actual preference entries
		Composite prefComposite = new Composite(warningGroup, SWT.LEFT);
		prefComposite.setLayout(new GridLayout(2, true));
		prefComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		queueLevel = new DoubleFieldEditor(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_LEVEL,
			GraphitiSadMessages.PortStatPreference_warningQueueLevel, prefComposite);
		queueLevel.getTextControl(prefComposite).setToolTipText(GraphitiSadMessages.PortStatPreference_warningQueueLevelToolTip);
		queueLevel.setPreferenceStore(getPreferenceStore());
		queueLevel.setPage(this);
		queueLevel.setErrorMessage(GraphitiSadMessages.PortStatPreference_warningQueueLevelError);
		queueLevel.setValidRange(0.0, 100.0);
		queueLevel.load();
		queueLevel.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					setValid(validateFields());
				}
			}
		});

		timeSinceLastPush = new DoubleFieldEditor(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS,
			GraphitiSadMessages.PortStatPreference_warningNoData, prefComposite);
		timeSinceLastPush.getTextControl(prefComposite).setToolTipText(GraphitiSadMessages.PortStatPreference_warningNoDataToolTip);
		timeSinceLastPush.setPreferenceStore(getPreferenceStore());
		timeSinceLastPush.setPage(this);
		timeSinceLastPush.setErrorMessage(GraphitiSadMessages.PortStatPreference_warningNoDataError);
		timeSinceLastPush.load();
		timeSinceLastPush.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					setValid(validateFields());
				}
			}
		});
	}

	/**
	 * Controls for warning events when port statistics are running
	 */
	private void createErrorGroup(Composite composite) {
		Group errorGroup = new Group(composite, SWT.LEFT);
		errorGroup.setLayout(new GridLayout());
		errorGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		errorGroup.setText(GraphitiSadMessages.PortStatPreference_errorGroupTitle);
		errorGroup.setToolTipText(GraphitiSadMessages.PortStatPreference_errorGroupToolTip);

		// Grid composite of two columns to hold all of the actual preference entries
		Composite prefComposite = new Composite(errorGroup, SWT.LEFT);
		prefComposite.setLayout(new GridLayout(2, true));
		prefComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		queueFlush = new DoubleFieldEditor(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY,
			GraphitiSadMessages.PortStatPreference_errorQueueFlush, prefComposite);
		queueFlush.getTextControl(prefComposite).setToolTipText(GraphitiSadMessages.PortStatPreference_errorQueueFlushToolTip);
		queueFlush.setPreferenceStore(getPreferenceStore());
		queueFlush.setPage(this);
		queueFlush.setErrorMessage(GraphitiSadMessages.PortStatPreference_errorQueueFlushError);
		queueFlush.load();
		queueFlush.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					setValid(validateFields());
				}
			}
		});
	}

	/**
	 * The default button has been pressed.
	 */
	@Override
	protected void performDefaults() {
		queueLevel.loadDefault();
		timeSinceLastPush.loadDefault();
		queueFlush.loadDefault();

		super.performDefaults();
	}

	/**
	 * The user has pressed Ok. Store/apply this page's values appropriately.
	 */
	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();

		store.setValue(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_LEVEL, queueLevel.getDoubleValue());
		store.setValue(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS, timeSinceLastPush.getDoubleValue());
		store.setValue(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY, queueFlush.getDoubleValue());

		return super.performOk();
	}

	/*
	 * Only return true if all fields all valid.
	 * Determines whether preference page can be applied/submitted
	 */
	private boolean validateFields() {
		if (queueFlush.isValid() && queueLevel.isValid() && timeSinceLastPush.isValid()) {
			return true;
		}
		return false;
	}

}
