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
package gov.redhawk.ide.codegen.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ui.util.SWTUtil;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * @since 2.0
 */
public class BooleanGeneratorPropertiesComposite extends BaseGeneratorPropertiesComposite implements ICodegenComposite {

	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;
	private static final int TOOLTIP_OFFSET = 5;

	private CheckboxTableViewer propertiesViewer;
	private HashMap<ImplementationSettings, WritableSet<Property>> setMap;
	private Binding propBinding;

	private Implementation impl;
	private String codegenId;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public BooleanGeneratorPropertiesComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		this(null, null, parent, style, toolkit);
	}

	/**
	 * @since 10.0
	 */
	public BooleanGeneratorPropertiesComposite(Implementation impl, String codegenId, Composite parent, int style, FormToolkit toolkit) {
		super(parent, style, toolkit);
		this.impl = impl;
		this.codegenId = codegenId;
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

		// Add the new Properties with their default values
		if (desc.getPropertyDescriptors().length != 0) {
			final CompoundCommand comp = new CompoundCommand();
			for (final IPropertyDescriptor value : desc.getPropertyDescriptors()) {
				final Property p = CodegenFactory.eINSTANCE.createProperty();
				p.setId(value.getKey());
				p.setValue(value.getDefaultValue());
				comp.append(AddCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, p));
			}
			this.getEditingDomain().getCommandStack().execute(comp);
		}

		this.propertiesViewer.setInput(this.getImplSettings().getProperties());
		this.propertiesViewer.refresh(true);
		if (this.getImplSettings().getProperties().size() > 0) {
			this.propertiesViewer.reveal(this.getImplSettings().getProperties().get(this.getImplSettings().getProperties().size() - 1));
		}

		createPropertyBinding();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.getContext().removeBinding(this.propBinding);
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 6.0
	 */
	@Override
	protected void createPropertiesArea() {
		final Label label = this.getToolkit().createLabel(this, "Properties:");
		label.setForeground(this.getToolkit().getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.getToolkit().createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(2, 0).grab(true, true).create());

		this.propertiesViewer = new CheckboxTableViewer(new Table(tableComp, SWT.BORDER | SWT.CHECK | SWT.READ_ONLY));
		this.propertiesViewer.setContentProvider(new ArrayContentProvider());

		ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
		final CellLabelProvider labelProvider = new CellLabelProvider() {

			public String getText(final Object element) {
				String text = "";

				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        BooleanGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
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
						        BooleanGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
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
				return new Point(BooleanGeneratorPropertiesComposite.TOOLTIP_OFFSET, BooleanGeneratorPropertiesComposite.TOOLTIP_OFFSET);
			}

			@Override
			public int getToolTipDisplayDelayTime(final Object object) {
				return BooleanGeneratorPropertiesComposite.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return BooleanGeneratorPropertiesComposite.TOOLTIP_DISPLAY_TIME_MILLIS;
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
	 * {@inheritDoc}
	 * 
	 * @since 6.0
	 */
	@Override
	protected void createPropertyBinding() {
		if (this.setMap == null) {
			this.setMap = new HashMap<ImplementationSettings, WritableSet<Property>>();
		} else {
			this.getContext().removeBinding(this.propBinding);
			this.setMap.remove(this.getImplSettings());
		}

		final WritableSet<Property> mySet;
		if (!this.setMap.containsKey(this.getImplSettings())) {
			mySet = new WritableSet<Property>();

			for (final Property prop : this.getImplSettings().getProperties()) {
				if ("TRUE".equalsIgnoreCase(prop.getValue())) {
					mySet.add(prop);
				}
			}

			mySet.addSetChangeListener(new ISetChangeListener<Property>() {
				@Override
				public void handleSetChange(final SetChangeEvent<? extends Property> event) {
					for (final Object obj : event.diff.getRemovals()) {
						final Property p = (Property) obj;
						final Command command = SetCommand.create(BooleanGeneratorPropertiesComposite.this.getEditingDomain(), p,
						        CodegenPackage.Literals.PROPERTY__VALUE, "FALSE");
						BooleanGeneratorPropertiesComposite.this.getEditingDomain().getCommandStack().execute(command);
						break;
					}
					for (final Object obj : event.diff.getAdditions()) {
						final Property p = (Property) obj;
						final Command command = SetCommand.create(BooleanGeneratorPropertiesComposite.this.getEditingDomain(), p,
						        CodegenPackage.Literals.PROPERTY__VALUE, "TRUE");
						BooleanGeneratorPropertiesComposite.this.getEditingDomain().getCommandStack().execute(command);
						break;
					}
				}
			});

			this.setMap.put(this.getImplSettings(), mySet);
		} else {
			mySet = this.setMap.get(this.getImplSettings());
		}

		this.propBinding = this.getContext().bindSet(ViewersObservables.observeCheckedElements(this.propertiesViewer, Property.class), mySet, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preBind(final Implementation impl, final ImplementationSettings implSettings, final List<Binding> bindList) {
		this.propertiesViewer.setInput(this.getImplSettings().getProperties());
	}

	@Override
	protected ICodeGeneratorDescriptor[] getCodegens() {
		if (this.impl == null) {
			return super.getCodegens();
		}

		String language = this.impl.getProgrammingLanguage().getName();
		final ICodeGeneratorDescriptor[] availableCodegens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(language);

		// Filter out JET generators
		List<ICodeGeneratorDescriptor> tempCodegens = new ArrayList<ICodeGeneratorDescriptor>();
		for (int i = 0; i < availableCodegens.length; i++) {
			ICodeGeneratorDescriptor codegen = availableCodegens[i];
			if (codegen.isDeprecated() && !codegen.getId().equals(codegenId)) {
				continue;
			} else {
				tempCodegens.add(codegen);
			}
		}

		return tempCodegens.toArray(new ICodeGeneratorDescriptor[0]);
	}
}
