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
package gov.redhawk.ide.graphiti.dcd.ui.project.wizards;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.ui.IdlFilter;
import gov.redhawk.eclipsecorba.library.ui.IdlInterfaceSelectionDialog;
import gov.redhawk.ide.spd.ui.wizard.ScaResourceProjectPropertiesWizardPage;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @since 2.0
 */
public class ScaServiceProjectPropertiesWizardPage extends ScaResourceProjectPropertiesWizardPage {

	private static final class ServiceIdlValidator implements IValidator {

		@Override
		public IStatus validate(final Object value) {
			final String s = ((String) value).trim();
			if ((s == null) || (s.length() == 0)) {
				return ValidationStatus.error("You must select a service interface IDL.");
			}

			return ValidationStatus.ok();
		}
	}

	private class Model {
		private String repID;

		public String getRepID() {
			return repID;
		}

		public void setRepID(String repID) {
			this.repID = repID;
		}
	}

	private Model model = new Model();

	private Text serviceIdlText;
	private ServiceIdlValidator serviceIdlValidator = new ServiceIdlValidator();
	private DataBindingContext context = new DataBindingContext();

	protected ScaServiceProjectPropertiesWizardPage(final String pageName, final String type) {
		super(pageName, type);
		this.setDescription("Choose to either create a new Service or import an existing one.");
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getContentsGroup().setValidator(new SpdFileValidator());

	}

	@Override
	protected boolean validatePage() {
		if (!super.validatePage()) {
			return false;
		}

		IStatus status = this.serviceIdlValidator.validate(serviceIdlText.getText());
		if (!status.isOK()) {
			setMessage(status);
			return false;
		}

		return true;
	}

	@Override
	public void customCreateControl(final Composite parent) {
		// Service Group
		final Group serviceGroup = new Group(parent, SWT.NONE);
		serviceGroup.setText(getResourceType());
		serviceGroup.setLayout(new GridLayout(3, false));
		GridDataFactory.generate(serviceGroup, 3, 1);

		final Label idlLabel = new Label(serviceGroup, SWT.NONE);
		idlLabel.setText("Service Interface");

		this.serviceIdlText = new Text(serviceGroup, SWT.BORDER | SWT.READ_ONLY);
		this.serviceIdlText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		this.serviceIdlText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				validate();
			}
		});

		bind();

		final Button idlBrowseButton = new Button(serviceGroup, SWT.NONE);
		idlBrowseButton.setText("Browse...");
		idlBrowseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IdlInterfaceDcl result = IdlInterfaceSelectionDialog.open(getShell(), IdlFilter.ALL_WITH_MODULE);
				if (result != null) {
					serviceIdlText.setText(result.getRepId());
				}
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void bind() {
		context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.serviceIdlText), PojoProperties.value(this.model.getClass(), "repID").observe(this.model));
	}

	public String getRepId() {
		return this.model.repID;
	}
}
