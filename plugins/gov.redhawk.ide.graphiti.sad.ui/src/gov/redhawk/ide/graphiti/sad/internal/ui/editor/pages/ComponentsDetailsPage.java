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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.LoggingConfig;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class ComponentsDetailsPage extends ScaDetails {

	private SadComponentComposite componentComposite;
	private Binding loggingUriBinding;
	private Binding logLevelBinding;

	private final SelectionListener loggingListener = new SelectionAdapter() {
		public void widgetSelected(final SelectionEvent e) {
			if (!ComponentsDetailsPage.this.loggingUriBinding.isDisposed()) {
				Binding binding = ComponentsDetailsPage.this.loggingUriBinding;
				binding.updateModelToTarget();
				binding.updateTargetToModel();
				binding.validateModelToTarget();
				binding.validateTargetToModel();
			}
			if (!ComponentsDetailsPage.this.logLevelBinding.isDisposed()) {
				Binding binding = ComponentsDetailsPage.this.logLevelBinding;
				binding.updateModelToTarget();
				binding.updateTargetToModel();
				binding.validateModelToTarget();
				binding.validateTargetToModel();
			}
		};

	};

	public ComponentsDetailsPage(final ComponentsSection section) {
		super(section.getPage());
	}

	@Override
	protected void createSpecificContent(Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();
		createComponentSection(toolkit, parent);
	}

	private void createComponentSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Component");
		section.setDescription("This allows you to edit particular details of the selected component");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		this.componentComposite = new SadComponentComposite(section, SWT.NONE, toolkit, this.getEditor());
		toolkit.adapt(this.componentComposite);

		section.setClient(this.componentComposite);
	}

	/**
	 * Bind composite fields with model elements
	 */
	@Override
	protected List<Binding> bind(DataBindingContext context, EObject input) {
		if (!(input instanceof SadComponentInstantiation)) {
			return null;
		}

		SadComponentInstantiation compInst = (SadComponentInstantiation) input;

		SoftPkg softPkg = null;
		ComponentPlacement< ? > placement = compInst.getPlacement();
		if (placement.getComponentFileRef() != null && placement.getComponentFileRef().getFile() != null) {
			softPkg = placement.getComponentFileRef().getFile().getSoftPkg();
		}
		if (softPkg == null || softPkg.eIsProxy()) {
			return Collections.emptyList();
		}

		final List<Binding> retVal = new ArrayList<>();

		/** Bind Component Instantiation -> ID **/
		retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getIdEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__ID, compInst, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		/** Bind Assembly Controller -> RefID (if applicable) **/
		boolean isAssemblyController = false;
		if (SoftwareAssembly.Util.isAssemblyController(compInst)) {
			isAssemblyController = true;
		}
		if (isAssemblyController) {
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
			retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getIdEntry(), getEditingDomain(),
				PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__REFID, sad.getAssemblyController().getComponentInstantiationRef(),
				new EMFEmptyStringToNullUpdateValueStrategy(), null));
		}

		/** Bind Component Instantiation -> Usage Name **/
		retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getNameEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME, compInst, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		/** Bind Component Instantiation -> Find Component -> Naming Service **/
		EMFUpdateValueStrategy uniDirectionStrategy = new EMFUpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
		NamingService namingService = null;
		if (compInst.getFindComponent() != null) {
			namingService = compInst.getFindComponent().getNamingService();
		}
		if (namingService != null) {
			retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getNameEntry(), getEditingDomain(),
				PartitioningPackage.Literals.NAMING_SERVICE__NAME, namingService, new EMFEmptyStringToNullUpdateValueStrategy(), uniDirectionStrategy));
		}

		/** Bind Component Instantiation -> Logging Config -> Logging URI **/
		IEMFEditValueProperty uriProperty = EMFEditProperties.value(getEditingDomain(),
			FeaturePath.fromList(PartitioningPackage.Literals.COMPONENT_INSTANTIATION__LOGGING_CONFIG, PartitioningPackage.Literals.LOGGING_CONFIG__URI));
		@SuppressWarnings("unchecked")
		IObservableValue< ? > uriObserver = uriProperty.observe(compInst);
		this.loggingUriBinding = context.bindValue(
			WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(), this.componentComposite.getLoggingUri().getText()),
			uriObserver, null, null);
		retVal.add(loggingUriBinding);

		/** Bind Component Instantiation -> Logging Config -> Log Level **/
		IEMFEditValueProperty levelProperty = EMFEditProperties.value(getEditingDomain(),
			FeaturePath.fromList(PartitioningPackage.Literals.COMPONENT_INSTANTIATION__LOGGING_CONFIG, PartitioningPackage.Literals.LOGGING_CONFIG__LEVEL));
		@SuppressWarnings("unchecked")
		IObservableValue< ? > levelObserver = levelProperty.observe(compInst);
		EMFUpdateValueStrategy levelModelToTarget = new EMFUpdateValueStrategy();
		levelModelToTarget.setConverter(new Converter(String.class, String.class) {
			@Override
			public Object convert(Object fromObject) {
				if (fromObject == null) {
					ComponentsDetailsPage.this.componentComposite.getLevelViewer().getCombo().deselectAll();
				}
				return fromObject;
			}
		});
		this.logLevelBinding = context.bindValue(WidgetProperties.selection().observe(this.componentComposite.getLevelViewer().getCombo()), levelObserver,
			new EMFEmptyStringToNullUpdateValueStrategy(), levelModelToTarget);
		retVal.add(logLevelBinding);

		/** Bind Logging Config enabled button to create/remove logging configuration tags **/
		final EMFUpdateValueStrategy targetToModel = new EMFUpdateValueStrategy();
		targetToModel.setConverter(new Converter(Boolean.class, LoggingConfig.class) {
			@Override
			public Object convert(final Object fromObject) {
				if ((Boolean) fromObject) {
					LoggingConfig loggingConf = PartitioningFactory.eINSTANCE.createLoggingConfig();
					loggingConf.setUri("");
					return loggingConf;
				} else {
					return null;
				}
			}
		});
		final EMFUpdateValueStrategy modelToTarget = new EMFUpdateValueStrategy();
		modelToTarget.setConverter(new Converter(LoggingConfig.class, Boolean.class) {

			@Override
			public Object convert(final Object fromObject) {
				return fromObject != null;
			}

		});
		retVal.add(context.bindValue(WidgetProperties.selection().observe(this.componentComposite.getEnableLoggingButton()),
			EMFEditObservables.observeValue(getEditingDomain(), compInst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__LOGGING_CONFIG), targetToModel,
			modelToTarget));

		/** Bind Logging Config enabled button to enable/disable Log Level combo and Logging URI text **/
		retVal.add(context.bindValue(WidgetProperties.enabled().observe(componentComposite.getLoggingUri().getText()),
			WidgetProperties.selection().observe(componentComposite.getEnableLoggingButton())));
		retVal.add(context.bindValue(WidgetProperties.enabled().observe(componentComposite.getLevelViewer().getCombo()),
			WidgetProperties.selection().observe(componentComposite.getEnableLoggingButton())));

		/** Add listener for EnableLoggingButton **/
		// NOTE: MUST remove listener prior to adding since
		// 1.) We don't want multiple listeners
		// 2.) This listener MUST be triggered AFTER the binding listener, therefore, it is adding every time in the
		// bind method
		this.componentComposite.getEnableLoggingButton().removeSelectionListener(this.loggingListener);
		this.componentComposite.getEnableLoggingButton().addSelectionListener(this.loggingListener);

		/** Make all composite fields editable/un-editable depending on the context **/
		this.componentComposite.setEditable(SCAEditorUtil.isEditableResource(getPage(), compInst.eResource()));

		return retVal;
	}

}
