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
package gov.redhawk.ide.debug.ui.tabs;

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * 
 */
public class LocalComponentMainTab extends LocalAbstractMainTab {

	private static final String DEFAULT_DEBUG_LEVEL = "Default";
	private Text implText;
	private Combo levelCombo;

	@Override
	protected String getProfileExtension() {
		return SpdPackage.FILE_EXTENSION;
	}

	@Override
	protected void createProfileGroup(final Composite parent) {
		super.createProfileGroup(parent);

		final Label label = new Label(parent, SWT.None);
		label.setText("Implementation:");
		this.implText = new Text(parent, SWT.BORDER);
		this.implText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.implText.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		final Button browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText("&Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String implID = browseForImplementationID();
				if (implID != null) {
					LocalComponentMainTab.this.implText.setText(implID);
				}
			}
		});
	}

	@Override
	protected void createLaunchGroup(final Composite parent) {
		super.createLaunchGroup(parent);

		final Label label = new Label(parent, SWT.None);
		label.setText("Debug Level:");
		this.levelCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		this.levelCombo.setItems(new String[] {
		        LocalComponentMainTab.DEFAULT_DEBUG_LEVEL, "Fatal", "Error", "Warn", "Info", "Debug", "Trace"
		});
		this.levelCombo.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
		this.levelCombo.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
	}

	protected String browseForImplementationID() {
		final List<String> implIDs = getImplementationIDs();
		if (implIDs.isEmpty()) {
			return null;
		} else if (implIDs.size() == 1) {
			return implIDs.get(0);
		} else {
			final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
			dialog.setElements(implIDs.toArray());
			dialog.setTitle("Select Implementation");
			dialog.setMessage("Select:");
			dialog.setMultipleSelection(false);
			dialog.setInitialElementSelections(Collections.singletonList(this.implText.getText()));
			if (dialog.open() == Window.OK) {
				return (String) dialog.getFirstResult();
			}
			return null;
		}
	}

	protected List<String> getImplementationIDs() {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource spdResource = resourceSet.getResource(URI.createPlatformResourceURI(getProfileText().getText(), true), true);
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(spdResource);
		final ArrayList<String> retVal = new ArrayList<String>();
		for (final Implementation impl : spd.getImplementation()) {
			retVal.add(impl.getId());
		}
		return retVal;
	}

	@Override
	protected Label createProfileLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.None);
		label.setText("SPD:");
		return label;
	}

	@Override
	public String getName() {
		return "&SPD";
	}

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		updateImplementationText(configuration);
		updateDebugText(configuration);
	}

	private void updateDebugText(final ILaunchConfiguration configuration) {
		String text = LocalComponentMainTab.DEFAULT_DEBUG_LEVEL;
		try {
			text = configuration.getAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, LocalComponentMainTab.DEFAULT_DEBUG_LEVEL);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		if (text == null) {
			this.levelCombo.select(0);
		} else {
			for (int i = 0; i < this.levelCombo.getItemCount(); i++) {
				if (text.equals(this.levelCombo.getItems()[i])) {
					this.levelCombo.select(i);
					break;
				}
			}
		}
	}

	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		if (!super.isValid(launchConfig)) {
			return false;
		}
		if (!isImplementationIDValid()) {
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	private boolean isImplementationIDValid() {
		if (this.implText.getText().trim().length() == 0) {
			setErrorMessage("Must select an implementation.");
			return false;
		} else {
			final String selectedId = this.implText.getText();
			for (final String id : getImplementationIDs()) {
				if (selectedId.equals(id)) {
					return true;
				}
			}
			setErrorMessage("Implementation does not exist.");
			return false;
		}
	}

	private void updateImplementationText(final ILaunchConfiguration configuration) {
		String text = "";
		try {
			text = configuration.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, text);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		this.implText.setText(text);
	}

	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, this.implText.getText());
		if (!this.levelCombo.getText().equals(LocalComponentMainTab.DEFAULT_DEBUG_LEVEL)) {
			configuration.setAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, this.levelCombo.getText());
		} else {
			configuration.setAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, (String) null);
		}
	}

	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, (String) null);
	}
}
