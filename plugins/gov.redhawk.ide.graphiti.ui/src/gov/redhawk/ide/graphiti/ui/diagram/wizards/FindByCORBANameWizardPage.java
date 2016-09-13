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
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

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

	// inner class model used to store user selections
	public static class CORBANameModel {

		public static final String CORBA_NAME = "corbaName";
		public static final String USES_PORT_NAMES = "usesPortNames";
		public static final String PROVIDES_PORT_NAMES = "providesPortNames";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private String corbaName;
		private List<String> usesPortNames = new ArrayList<String>();
		private List<String> providesPortNames = new ArrayList<String>();

		public CORBANameModel() {
		}

		public String getCorbaName() {
			return corbaName;
		}

		public void setCorbaName(String corbaName) {
			final String oldValue = this.corbaName;
			this.corbaName = corbaName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.CORBA_NAME, oldValue, corbaName));
		}

		public List<String> getUsesPortNames() {
			return usesPortNames;
		}

		public void setUsesPortNames(List<String> usesPortNames) {
			final List<String> oldValue = this.usesPortNames;
			this.usesPortNames = usesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.USES_PORT_NAMES, oldValue, usesPortNames));
		}

		public List<String> getProvidesPortNames() {
			return providesPortNames;
		}

		public void setProvidesPortNames(List<String> providesPortNames) {
			final List<String> oldValue = this.providesPortNames;
			this.providesPortNames = providesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.PROVIDES_PORT_NAMES, oldValue, providesPortNames));
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
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

	protected void createNameSection() {
		// CORBA Name
		Label corbaNameLabel = new Label(composite, SWT.NONE);
		corbaNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		corbaNameLabel.setText("Component Name:");

		corbaNameText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		corbaNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		corbaNameText.setToolTipText("The name of the component as it appears in the naming service");
		corbaNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dbc.updateModels();
			}
		});
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(corbaNameText),
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
		} else if ((usesPortNameText != null && usesPortNameText.getText().contains(" "))
			|| (providesPortNameText != null && providesPortNameText.getText().contains(" "))) {
			return "Port name must not include spaces";
		}
		return null;
	}
}
