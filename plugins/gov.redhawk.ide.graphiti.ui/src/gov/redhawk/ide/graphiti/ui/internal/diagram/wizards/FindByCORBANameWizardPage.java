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
package gov.redhawk.ide.graphiti.ui.internal.diagram.wizards;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.AbstractPortWizardPage;

public class FindByCORBANameWizardPage extends AbstractPortWizardPage {

	/**
	 * Used as the model for UI input.
	 */
	public class CORBANameModel extends AbstractPortWizardPage.AbstractPortModel {

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
	};

	private CORBANameModel model;
	private Text corbaNameText;

	public FindByCORBANameWizardPage() {
		super("findByCorbaName", "Find By Name");
		this.setDescription("Enter the details of a component you want to make connections to");

		model = new CORBANameModel();
	}

	@SuppressWarnings("unchecked")
	protected void createTopSection(Composite parent) {
		// CORBA Name
		Label corbaNameLabel = new Label(parent, SWT.NONE);
		corbaNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		corbaNameLabel.setText("Component Name:");

		corbaNameText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		corbaNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		corbaNameText.setToolTipText("The name of the component as it appears in the naming service");
		Binding binding = getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(corbaNameText),
			BeanProperties.value(model.getClass(), CORBANameModel.CORBA_NAME).observe(model), new UpdateValueStrategy().setAfterGetValidator(value -> {
				String err = FindByCORBANamePattern.validate("CORBA", (String) value);
				if (err != null) {
					return ValidationStatus.error(err);
				} else {
					return ValidationStatus.ok();
				}
			}), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
	}

	public CORBANameModel getModel() {
		return model;
	}
}
