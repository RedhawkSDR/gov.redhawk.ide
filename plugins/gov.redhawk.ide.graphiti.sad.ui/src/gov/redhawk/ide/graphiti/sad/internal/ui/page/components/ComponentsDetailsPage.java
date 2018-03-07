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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.LoggingConfig;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.FindComponent;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

public class ComponentsDetailsPage extends ScaDetails {

	private SadComponentComposite componentComposite;
	private SadLoggingComposite loggingComposite;
	private ControlDecoration uriControlDecoration;
	private Binding loggingUriBinding;
	private Binding logLevelBinding;

	/**
	 * Listener to have the log level and logging uri bindings validate when logging is enabled/disabled
	 */
	private final SelectionListener loggingEnabledListener = new SelectionAdapter() {
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
		createLoggingSection(toolkit, parent);
	}

	private void createComponentSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Component");
		section.setDescription("This allows you to edit particular details of the selected component");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		componentComposite = new SadComponentComposite(section, SWT.NONE, toolkit, getEditor());
		toolkit.adapt(componentComposite);

		section.setClient(componentComposite);
	}

	private void createLoggingSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Logging");
		section.setDescription("Edit the logging configuration for the component");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		loggingComposite = new SadLoggingComposite(section, SWT.NONE, toolkit, getEditor());
		toolkit.adapt(loggingComposite);

		section.setClient(loggingComposite);
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

		/** Bind Component Instantiation -> Usage Name **/
		retVal.add(FormEntryBindingFactory.bind(context, componentComposite.getNameEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME, compInst, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		/** Bind Component Instantiation -> Find Component **/
		final FindComponent findComponent = compInst.getFindComponent();
		EMFUpdateValueStrategy fcTargetToModel = new EMFUpdateValueStrategy();
		fcTargetToModel.setConverter(new Converter(String.class, FindComponent.class) {

			@Override
			public Object convert(Object fromObject) {
				if (fromObject == null || "".equals(fromObject)) {
					return null;
				} else if (findComponent != null) {
					return findComponent;
				} else {
					FindComponent fc = SadFactory.eINSTANCE.createFindComponent();
					NamingService ns = PartitioningFactory.eINSTANCE.createNamingService();
					ns.setName(fromObject.toString());
					fc.setNamingService(ns);
					return fc;
				}
			}
		});
		retVal.add(FormEntryBindingFactory.bind(context, componentComposite.getNameEntry(), getEditingDomain(),
			SadPackage.Literals.SAD_COMPONENT_INSTANTIATION__FIND_COMPONENT, compInst, fcTargetToModel,
			new EMFUpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST)));

		/** Bind Component Instantiation -> Find Component -> Naming Service **/
		NamingService namingService = null;
		if (findComponent != null) {
			namingService = compInst.getFindComponent().getNamingService();
		}
		if (namingService != null) {
			retVal.add(FormEntryBindingFactory.bind(context, componentComposite.getNameEntry(), getEditingDomain(),
				PartitioningPackage.Literals.NAMING_SERVICE__NAME, namingService, new EMFEmptyStringToNullUpdateValueStrategy(),
				new EMFUpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST)));
		}

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
					ComponentsDetailsPage.this.loggingComposite.getLevelViewer().getCombo().deselectAll();
				}
				return fromObject;
			}
		});
		logLevelBinding = context.bindValue(WidgetProperties.selection().observe(loggingComposite.getLevelViewer().getCombo()), levelObserver,
			new EMFEmptyStringToNullUpdateValueStrategy(), levelModelToTarget);
		retVal.add(logLevelBinding);

		/** Bind Component Instantiation -> Logging Config -> Logging URI **/
		IEMFEditValueProperty uriProperty = EMFEditProperties.value(getEditingDomain(),
			FeaturePath.fromList(PartitioningPackage.Literals.COMPONENT_INSTANTIATION__LOGGING_CONFIG, PartitioningPackage.Literals.LOGGING_CONFIG__URI));
		@SuppressWarnings("unchecked")
		IObservableValue< ? > uriObserver = uriProperty.observe(compInst);
		EMFUpdateValueStrategy uriTargetToModel = new EMFUpdateValueStrategy();
		ControlDecoration controlDecoration = getUriControlDecoration(loggingComposite.getLoggingUri().getText());
		uriTargetToModel.setAfterConvertValidator(new LoggingUriValidator(compInst, controlDecoration));
		loggingUriBinding = context.bindValue(
			WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(), loggingComposite.getLoggingUri().getText()),
			uriObserver, uriTargetToModel, null);
		retVal.add(loggingUriBinding);

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
		retVal.add(context.bindValue(WidgetProperties.selection().observe(loggingComposite.getEnableLoggingButton()),
			EMFEditObservables.observeValue(getEditingDomain(), compInst, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__LOGGING_CONFIG), targetToModel,
			modelToTarget));

		/** Bind Logging Config enabled button to enable/disable Log Level combo and Logging URI text **/
		retVal.add(context.bindValue(WidgetProperties.enabled().observe(loggingComposite.getLoggingUri().getText()),
			WidgetProperties.selection().observe(loggingComposite.getEnableLoggingButton())));
		retVal.add(context.bindValue(WidgetProperties.enabled().observe(loggingComposite.getLevelViewer().getCombo()),
			WidgetProperties.selection().observe(loggingComposite.getEnableLoggingButton())));

		/** Add listener for EnableLoggingButton **/
		// NOTE: MUST remove listeners prior to adding since
		// 1.) We don't want multiple listeners
		// 2.) This listener MUST be triggered AFTER the binding listener, therefore, it is adding every time in the
		// bind method
		loggingComposite.getEnableLoggingButton().removeSelectionListener(loggingEnabledListener);
		loggingComposite.getEnableLoggingButton().addSelectionListener(loggingEnabledListener);

		/** Make all composite fields editable/un-editable depending on the context **/
		if (!SCAEditorUtil.isEditableResource(getPage(), compInst.eResource())) {
			componentComposite.setEditable(false);
		}

		return retVal;
	}

	private ControlDecoration getUriControlDecoration(Text text) {
		if (uriControlDecoration == null) {
			uriControlDecoration = new ControlDecoration(loggingComposite.getLoggingUri().getText(), SWT.BOTTOM | SWT.LEFT);
			uriControlDecoration.setDescriptionText("Invalid entry");
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			uriControlDecoration.setImage(fieldDecoration.getImage());
		}
		return uriControlDecoration;
	}

	class LoggingUriValidator implements IValidator {
		private final ComponentInstantiation compInst;
		private final ControlDecoration controlDecoration;

		public LoggingUriValidator(SadComponentInstantiation compInst, ControlDecoration controlDecoration) {
			this.compInst = compInst;
			this.controlDecoration = controlDecoration;
		}

		@Override
		public IStatus validate(Object value) {
			// Hide any previous validation decorations
			controlDecoration.hide();

			// No errors if logging configuration is not enabled
			if (compInst.getLoggingConfig() == null) {
				return ValidationStatus.ok();
			}

			try {
				String uriText = value.toString();
				URI uri = URI.createURI(uriText);
				String scheme = uri.scheme();
				final List<String> protocols = Arrays.asList(new String[] { "file", ScaFileSystemConstants.SCHEME });
				if (scheme != null || protocols.contains(scheme)) {
					IPath path = SdrUiPlugin.getDefault().getTargetSdrDomPath();
					if (scheme.equals(ScaFileSystemConstants.SCHEME)) {
						String query = QueryParser.createQuery(Collections.singletonMap(ScaFileSystemConstants.QUERY_PARAM_FS, "file://" + path.toString()));
						uri = URI.createURI(uri + "?" + query);
					}

					IFileStore store = EFS.getStore(java.net.URI.create(uri.toString()));
					boolean exists = store.fetchInfo(EFS.NONE, null).exists();
					boolean isDir = store.fetchInfo(EFS.NONE, null).isDirectory();
					// Validate that the URI references a file that exists
					if (!exists) {
						controlDecoration.setDescriptionText("URI cannot be verified: File not found");
						controlDecoration.show();
						return ValidationStatus.ok();
					}
					// Validate that the URI is NOT referencing a directory
					if (isDir) {
						controlDecoration.setDescriptionText("URI cannot be verified: Target destination is a directory");
						controlDecoration.show();
						return ValidationStatus.ok();
					}
				}

				return ValidationStatus.ok();
			} catch (IllegalArgumentException | CoreException ex) {
				controlDecoration.setDescriptionText("URI cannot be verified: " + ex.getMessage());
				controlDecoration.show();
				return ValidationStatus.ok();
			}
		}
	}

}
