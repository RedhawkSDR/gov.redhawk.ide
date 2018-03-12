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
package gov.redhawk.datalist.ui.views;

import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @since 2.0
 */
public abstract class OptionsComposite {

	public static final String REAL = "Real", IMAGINARY = "Imaginary", COMPLEX = "Complex";

	private final DataCollectionSettings settings = new DataCollectionSettings();
	private State state = new State();

	private DataBindingContext ctx = new DataBindingContext();

	private Text samplesTxt;
	private Button button;
	private ImageRegistry resources = new ImageRegistry();

	private static class State {
		private boolean isRunning = false;
		private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		public void setRunning(boolean isRunning) {
			boolean oldValue = this.isRunning;
			this.isRunning = isRunning;
			pcs.firePropertyChange("running", oldValue, isRunning);
		}

		@SuppressWarnings("unused") // Java bean
		public boolean isRunning() {
			return isRunning;
		}

		@SuppressWarnings("unused") // Java bean
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			pcs.addPropertyChangeListener(listener);
		}

		@SuppressWarnings("unused") // Java bean
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			pcs.removePropertyChangeListener(listener);
		}
	}

	public static enum CaptureMethod {
		NUMBER("Number of Samples"),
		INDEFINITELY("Indefinitely"),
		CLOCK_TIME("Clock Time"),
		SAMPLE_TIME("Sample Time");

		/** the description of the enum, outputted by toString() */
		private String description; // DO NOT SET to null!

		CaptureMethod(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public OptionsComposite(Composite main) {
		resources.put("icons/start.gif", DataListPlugin.imageDescriptorFromPlugin(DataListPlugin.PLUGIN_ID, "icons/start.gif").createImage(true));
		resources.put("icons/stop.gif", DataListPlugin.imageDescriptorFromPlugin(DataListPlugin.PLUGIN_ID, "icons/stop.gif").createImage(true));
		main.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				resources.dispose();
				ctx.dispose();
			}
		});
		createControl(main);
	}

	@SuppressWarnings("unchecked")
	public void createControl(Composite main) {
		final Composite parent = new Composite(main, SWT.None);
		parent.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		final ComboViewer captureCombo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		captureCombo.setContentProvider(new ArrayContentProvider());
		captureCombo.setLabelProvider(new LabelProvider());
		captureCombo.setInput(new Object[] { CaptureMethod.NUMBER, CaptureMethod.INDEFINITELY });
		ctx.bindValue(ViewerProperties.singleSelection().observe(captureCombo), BeanProperties.value("processType").observe(settings));

		samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		ctx.bindValue(WidgetProperties.enabled().observe(samplesTxt), BeanProperties.value("processType").observe(settings),
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
			new UpdateValueStrategy().setConverter(new Converter(CaptureMethod.class, Boolean.class) {

				@Override
				public Object convert(Object fromObject) {
					if (fromObject == CaptureMethod.INDEFINITELY) {
						return false;
					}
					return true;
				}
			}));
		Binding binding = ctx.bindValue(WidgetProperties.text(SWT.Modify).observe(samplesTxt), BeanProperties.value("samples").observe(settings),
			new UpdateValueStrategy().setBeforeSetValidator(new IValidator() {

				@Override
				public IStatus validate(Object obj) {
					Double value = (Double) obj;
					if (Double.valueOf(value) <= 0) {
						return ValidationStatus.error(settings.getProcessType() + " must be greater than 0.");
					}

					if (value > Integer.MAX_VALUE) {
						return ValidationStatus.error(settings.getProcessType() + " must be less than or equal to " + Integer.MAX_VALUE + ".");
					}

					if ((value - value.intValue()) > 0) {
						return ValidationStatus.error(settings.getProcessType() + " must be a positive integer.");
					}

					if (value > 1000000) {
						return ValidationStatus.warning("For this sample size, you may run out of heap space.");
					}
					return ValidationStatus.ok();
				}

			}), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);

		final IObservableValue< ? > running = BeanProperties.value("running").observe(state);
		final IObservableValue< ? > pType = BeanProperties.value("processType").observe(settings);
		ComputedValue<Object> enabledSamples = new ComputedValue<Object>(Boolean.class) {

			@Override
			protected Object calculate() {
				return !(Boolean) running.getValue() && pType.getValue() != CaptureMethod.INDEFINITELY;
			}
		};

		ctx.bindValue(WidgetProperties.enabled().observe(samplesTxt), enabledSamples, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);

		final Label unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		GridData unitsLayout = new GridData();
		unitsLayout.widthHint = 20;
		unitsLabel.setLayoutData(unitsLayout);

		settings.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("processType".equals(evt.getPropertyName())) {
					CaptureMethod method = (CaptureMethod) evt.getNewValue();
					if (method == CaptureMethod.INDEFINITELY) {
						settings.setSamples(1);
						unitsLabel.setText("");
					} else {
						if (method == CaptureMethod.CLOCK_TIME || method == CaptureMethod.SAMPLE_TIME) {
							unitsLabel.setText("(s)");
						} else {
							unitsLabel.setText("");
						}
						settings.setSamples(1024);
					}
				}
			}
		});

		Label label = new Label(parent, SWT.None);
		label.setText("Number of Dimensions:");

		Combo columnsCombo = new Combo(parent, SWT.BORDER | SWT.SINGLE | SWT.DROP_DOWN);
		columnsCombo.setText(REAL);
		columnsCombo.add(REAL, 0);
		columnsCombo.add(COMPLEX, 1);
		columnsCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		ctx.bindValue(WidgetProperties.enabled().observe(columnsCombo), BeanProperties.value("running").observe(state),
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), new UpdateValueStrategy().setConverter(new Converter(Boolean.class, Boolean.class) {

				@Override
				public Boolean convert(Object fromObject) {
					return !((Boolean) fromObject);
				}
			}));
		binding = ctx.bindValue(WidgetProperties.selection().observe(columnsCombo), BeanProperties.value("dimensions").observe(settings),
			new UpdateValueStrategy().setAfterGetValidator(new IValidator() {

				@Override
				public IStatus validate(Object value) {
					if (REAL.equalsIgnoreCase((String) value)) {
						return ValidationStatus.ok();
					} else if (COMPLEX.equalsIgnoreCase((String) value)) {
						return ValidationStatus.ok();
					} else {
						try {
							Integer intValue = Integer.valueOf((String) value);
							if (intValue > 0) {
								return ValidationStatus.ok();
							}
						} catch (NumberFormatException e) {
							// PASS
						}
					}
					return ValidationStatus.error("Please enter a positive integer or choose one of the options below.");
				}

			}).setConverter(new Converter(String.class, Integer.class) {

				@Override
				public Object convert(Object fromObject) {
					if (REAL.equalsIgnoreCase((String) fromObject)) {
						return 1;
					} else if (COMPLEX.equalsIgnoreCase((String) fromObject)) {
						return 2;
					} else {
						return Integer.valueOf((String) fromObject);
					}
				}

			}), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);

		button = new Button(parent, SWT.None);
		button.setLayoutData(GridDataFactory.fillDefaults().create());
		button.setImage(resources.get("icons/start.gif"));
		button.setToolTipText("Start Acquire");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (state.isRunning) {
					stopAcquire();
				} else {
					startAcquire();
				}
			}
		});
		ctx.bindValue(WidgetProperties.enabled().observe(button), new AggregateValidationStatus(ctx, AggregateValidationStatus.MAX_SEVERITY),
			new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), new UpdateValueStrategy().setConverter(new Converter(IStatus.class, Boolean.class) {

				@Override
				public Object convert(Object fromObject) {
					return ((IStatus) fromObject).getSeverity() != IStatus.ERROR;
				}
			}));
	}

	public abstract void startAcquire();

	public abstract void stopAcquire();

	public static void showDecoration(ControlDecoration dec, String errorText) {
		dec.setDescriptionText(errorText);
		dec.show();
	}

	public void buttonsEnable(boolean running) {
		state.setRunning(running);
		if (running) {
			button.setImage(resources.get("icons/stop.gif"));
			button.setToolTipText("Stop Acquire");
		} else {
			button.setImage(resources.get("icons/start.gif"));
			button.setToolTipText("Start Acquire");
		}
	}

	/**
	 * @since 2.1
	 */
	public Button getButton() {
		return button;
	}

	public DataCollectionSettings getSettings() {
		return settings;
	}

	public void setFocus() {
		this.samplesTxt.setFocus();
	}
}
