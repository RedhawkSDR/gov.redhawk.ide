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
package gov.redhawk.ide.ui.tests.projectCreation.stubs;

import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.BaseGeneratorPropertiesComposite;
import gov.redhawk.ide.codegen.ui.ICodegenComposite;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @since 8.0
 * 
 */
public class StubGeneratorPropertiesComposite extends BaseGeneratorPropertiesComposite implements ICodegenComposite {

	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;

	private FormEntry packageNameEntry;
	private CheckboxTableViewer propertiesViewer;
	private HashMap<ImplementationSettings, WritableSet> setMap;
	private Binding propBinding;
	private Binding packageBinding;
	private Property packageName = null;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public StubGeneratorPropertiesComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style, toolkit);
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.getContext().removeBinding(this.packageBinding);
		this.getContext().removeBinding(this.propBinding);
		super.dispose();
	}

	/**
	 * Creates the properties entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 * @since 8.0
	 */
	@Override
	protected void createPropertiesArea() {
		this.packageNameEntry = new FormEntry(this, this.getToolkit(), "Package:", SWT.SINGLE);
		this.packageNameEntry.getText().setToolTipText("Package name for the classes to be created.");

		final Label label = this.getToolkit().createLabel(this, "Properties:");
		label.setForeground(this.getToolkit().getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.getToolkit().createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(2, 0).hint(SWT.DEFAULT, 60).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber

		this.propertiesViewer = new CheckboxTableViewer(new Table(tableComp, SWT.BORDER | SWT.CHECK | SWT.READ_ONLY));
		this.propertiesViewer.setContentProvider(new ArrayContentProvider());

		ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
		this.propertiesViewer.setContentProvider(new ArrayContentProvider());

		final CellLabelProvider labelProvider = new CellLabelProvider() {

			public String getText(final Object element) {
				String text = "";

				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        StubGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						if (template != null) {
							for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
								if (propDesc.getKey().equals(((Property) element).getId())) {
									text = propDesc.getName();
									break;
								}
							}
						}
					} else {
						text = ((Property) element).getId();
					}
				}
				return text;
			}

			@Override
			public String getToolTipText(final Object element) {
				String text = "No description available for this property";

				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        StubGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						if (template != null) {
							for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
								if (propDesc.getKey().equals(((Property) element).getId())) {
									text = propDesc.getDescription();
									break;
								}
							}
						}
					}
				}
				return text;
			}

			@Override
			public Point getToolTipShift(final Object object) {
				return new Point(5, 5); // SUPPRESS CHECKSTYLE MagicNumber
			}

			@Override
			public int getToolTipDisplayDelayTime(final Object object) {
				return StubGeneratorPropertiesComposite.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return StubGeneratorPropertiesComposite.TOOLTIP_DISPLAY_TIME_MILLIS;
			}

			@Override
			public void update(final ViewerCell cell) {
				cell.setText(getText(cell.getElement()));
			}
		};

		this.propertiesViewer.setLabelProvider(labelProvider);
		this.propertiesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 2).grab(true, true).create());
	}

	/**
	 * Creates the property binding.
	 */
	@Override
	protected void createPropertyBinding() {
		if (this.setMap == null) {
			this.setMap = new HashMap<ImplementationSettings, WritableSet>();
		} else {
			this.getContext().removeBinding(this.propBinding);
			this.setMap.clear();
		}

		final WritableSet mySet = new WritableSet();

		for (final Property prop : this.getImplSettings().getProperties()) {
			if ("TRUE".equalsIgnoreCase(prop.getValue())) {
				mySet.add(prop);
			}
		}

		mySet.addSetChangeListener(new ISetChangeListener() {
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				final EditingDomain domain = StubGeneratorPropertiesComposite.this.getEditingDomain();
				for (final Object obj : event.diff.getRemovals()) {
					final Property p = (Property) obj;
					final Command command = SetCommand.create(domain, p, CodegenPackage.Literals.PROPERTY__VALUE, "FALSE");
					domain.getCommandStack().execute(command);
				}
				for (final Object obj : event.diff.getAdditions()) {
					final Property p = (Property) obj;
					final Command command = SetCommand.create(domain, p, CodegenPackage.Literals.PROPERTY__VALUE, "TRUE");
					domain.getCommandStack().execute(command);
				}
			}
		});

		this.setMap.put(this.getImplSettings(), mySet);

		this.propBinding = this.getContext().bindSet(ViewersObservables.observeCheckedElements(this.propertiesViewer, Property.class), mySet, null, null);
	}

	/**
	 * @param bindList
	 * @since 8.0
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void preBind(final Implementation impl, final ImplementationSettings implSettings, final List<Binding> bindList) {
		this.packageName = null;

		final List<Property> propList = new ArrayList<Property>();
		final EList<Property> properties = implSettings.getProperties();
		for (final Property p : properties) {
			if ("java_package".equals(p.getId())) {
				this.packageName = p;
			} else {
				propList.add(p);
			}
		}

		if (this.packageName == null) {
			final Property p = CodegenFactory.eINSTANCE.createProperty();
			p.setId("java_package");
			p.setValue(implSettings.getName());
			final Command command = AddCommand.create(getEditingDomain(), implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, p);
			getEditingDomain().getCommandStack().execute(command);
			this.packageName = p;
		}

		this.propertiesViewer.setInput(propList);

		this.packageBinding = FormEntryBindingFactory.bind(this.getContext(), this.packageNameEntry, this.getEditingDomain(),
		        CodegenPackage.Literals.PROPERTY__VALUE, this.packageName, null, null);

		bindList.add(this.packageBinding);

	}

	@Override
	@SuppressWarnings("deprecation")
	protected void templateSelected(final ITemplateDesc desc) {
		// Remove the old properties in the ImplementationSettings
		final EList<Property> properties = this.getImplSettings().getProperties();
		if (!properties.isEmpty()) {
			final Collection<Property> c = new ArrayList<Property>();
			c.addAll(properties);
			final Command command = RemoveCommand.create(this.getEditingDomain(), this.getImplSettings(),
			        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, c);
			this.getEditingDomain().getCommandStack().execute(command);
		}

		this.packageName = null;

		final List<Property> propList = new ArrayList<Property>();
		// Add the new Properties with their default values
		if (desc.getPropertyDescriptors().length != 0) {
			final CompoundCommand comp = new CompoundCommand();
			for (final IPropertyDescriptor value : desc.getPropertyDescriptors()) {
				final Property p = CodegenFactory.eINSTANCE.createProperty();
				p.setId(value.getKey());
				p.setValue(value.getDefaultValue());
				if (!"java_package".equals(value.getKey())) {
					propList.add(p);
				} else {
					// Store and set the default value of the package name
					this.packageName = p;
					p.setValue(this.getImplSettings().getName());
				}
				comp.append(AddCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, p));
			}
			this.getEditingDomain().getCommandStack().execute(comp);
		}

		if (this.packageName == null) {
			final Property p = CodegenFactory.eINSTANCE.createProperty();
			p.setId("java_package");
			p.setValue(this.getImplSettings().getName());
			this.getEditingDomain().getCommandStack().execute(
			        AddCommand.create(this.getEditingDomain(), properties, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, p));
			this.packageName = p;
		}

		// Update the package property display and rebind
		if (this.packageBinding != null) {
			this.getContext().removeBinding(this.packageBinding);
		}
		this.packageBinding = FormEntryBindingFactory.bind(this.getContext(), this.packageNameEntry, this.getEditingDomain(),
		        CodegenPackage.Literals.PROPERTY__VALUE, this.packageName, null, null);

		// Update the properties display and rebind
		this.propertiesViewer.setInput(propList);
		this.propertiesViewer.refresh(true);
		if (propList.size() > 0) {
			this.propertiesViewer.reveal(propList.get(propList.size() - 1));
		}
		createPropertyBinding();
	}

}
