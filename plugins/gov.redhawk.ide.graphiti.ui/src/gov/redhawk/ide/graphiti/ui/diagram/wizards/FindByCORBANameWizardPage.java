/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;

public class FindByCORBANameWizardPage extends AbstractFindByWizardPage {

	/**
	 * Used as the model for UI input.
	 */
	public class CORBANameModel extends FindByModel {

		public static final String CORBA_NAME = "corbaName"; //$NON-NLS-1$

		private String corbaName;

		public CORBANameModel() {
		}

		public String getCorbaName() {
			return corbaName;
		}

		public void setCorbaName(String corbaName) {
			final String oldValue = this.corbaName;
			this.corbaName = corbaName;
			getPropChangeSupport().firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.CORBA_NAME, oldValue, corbaName));
		}

		public boolean isComplete() {
			if (this.corbaName.length() == 0) {
				return false;
			}
			return true;
		}
	};

	private CORBANameModel model;
	private Text corbaNameText;

	public FindByCORBANameWizardPage() {
		super("findByCorbaName", "Find By Name");
		this.setDescription("Enter the details of a component you want to make connections to");

		model = new CORBANameModel();
	}

	@SuppressWarnings("unchecked")
	protected void createNameSection() {
		// CORBA Name
		Label corbaNameLabel = new Label(getDialogComposite(), SWT.NONE);
		corbaNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		corbaNameLabel.setText("Component Name:");

		corbaNameText = new Text(getDialogComposite(), SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		corbaNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		corbaNameText.setToolTipText("The name of the component as it appears in the naming service");
		corbaNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getDbc().updateModels();
			}
		});
		getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(corbaNameText),
			BeanProperties.value(model.getClass(), CORBANameModel.CORBA_NAME).observe(model), new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					String err = FindByCORBANamePattern.validate("CORBA", (String) value);
					if (err != null) {
						return ValidationStatus.error(err);
					} else if (validateAll() != null) {
						return ValidationStatus.error(validateAll());
					}
					return ValidationStatus.ok();
				}
			}), null);
	}

	public CORBANameModel getModel() {
		return model;
	}

	protected String validateAll() {
		String errCORBA = FindByCORBANamePattern.validate("CORBA", corbaNameText.getText());
		if (errCORBA != null) {
			return errCORBA;
		}

		return super.validateAll();
	}
}
