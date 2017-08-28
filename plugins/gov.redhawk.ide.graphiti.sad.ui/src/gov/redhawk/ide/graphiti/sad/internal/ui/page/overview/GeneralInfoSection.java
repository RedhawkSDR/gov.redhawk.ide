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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.overview;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.FormEntryAdapter;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaSection;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiationRef;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The Class GeneralInfoSection.
 */
public class GeneralInfoSection extends ScaSection {
	private class Listener extends EContentAdapter {
		private final EObject root;

		public Listener(final EObject root) {
			this.root = root;
			root.eAdapters().add(this);
		}

		public void dispose() {
			this.root.eAdapters().remove(this);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			super.notifyChanged(notification);
			final Object feature = notification.getFeature();
			if (feature == PartitioningPackage.Literals.PARTITIONING__COMPONENT_PLACEMENT
			        || feature == SadPackage.Literals.HOST_COLLOCATION__COMPONENT_PLACEMENT
			        || feature == PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION
			        || feature == PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME) {
				final WorkbenchJob job = new WorkbenchJob("Refresh Assembly Controller Combo") {

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor) {
						refreshAssemblyControllerItems();
						return Status.OK_STATUS;
					}

				};
				job.setUser(false);
				job.setSystem(true);
				job.schedule();
			}
		}
	}

	private GeneralInformationComposite client;
	private Listener listener;
	private Resource sadResource;
	private Collection<Binding> bindings = new ArrayList<Binding>();

	/**
	 * Instantiates a new general info section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public GeneralInfoSection(final SadOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("General Information");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("This section describes general information about this waveform.");

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		this.client = new GeneralInformationComposite(section, SWT.None, toolkit, actionBars);
		section.setClient(this.client);

		addListeners(actionBars);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	/**
	 * Add Listeners to handle Control Events
	 * 
	 * @param actionBars
	 */
	private void addListeners(final IActionBars actionBars) {
		final FormEntryAdapter fIdEntryAdapter = new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				execute(SetCommand.create(getEditingDomain(), getSoftwareAssembly(), SadPackage.Literals.SOFTWARE_ASSEMBLY__ID, DceUuidUtil.createDceUUID()));
			}
		};
		this.client.getIdEntry().setFormEntryListener(fIdEntryAdapter);
	}

	/**
	 * @param create
	 */
	protected void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Gets the editing domain.
	 * 
	 * @return the editing domain
	 */
	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createAssemblyControllerModelToTarget() {
		final EMFUpdateValueStrategy retVal = new EMFUpdateValueStrategy();
		retVal.setConverter(new Converter(AssemblyController.class, ComponentInstantiation.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject instanceof AssemblyController) {
					final AssemblyController asm = (AssemblyController) fromObject;
					final ComponentInstantiationRef< ? > inst = asm.getComponentInstantiationRef();
					if (inst != null) {
						return inst.getInstantiation();
					}
				}
				return null;
			}

		});
		return retVal;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createAssemblyControllerTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy retVal = new EMFEmptyStringToNullUpdateValueStrategy();
		retVal.setConverter(new Converter(ComponentInstantiation.class, AssemblyController.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject instanceof ComponentInstantiation) {
					final ComponentInstantiation inst = (ComponentInstantiation) fromObject;
					final String id = inst.getId();
					final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
					ref.setRefid(id);
					final AssemblyController asm = SadFactory.eINSTANCE.createAssemblyController();
					asm.setComponentInstantiationRef(ref);
					return asm;
				}
				return null;
			}

		});
		return retVal;
	}

	/**
	 * 
	 */
	private void refreshAssemblyControllerItems() {
		if (this.listener != null) {
			this.listener.dispose();
		}
		final SoftwareAssembly sad = getSoftwareAssembly();
		if (sad != null) {
			this.listener = new Listener(sad);
			this.client.getAssemblyControllerPart().setItems(ItemPropertyDescriptor.getReachableObjectsOfType(sad,
			        PartitioningPackage.Literals.COMPONENT_INSTANTIATION));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.listener != null) {
			this.listener.dispose();
		}
		super.dispose();
	}

	/**
	 * TODO
	 * 
	 * @param readOnly
	 */
	private void setEditable(final boolean editable) {
		this.client.setEditable(editable);
	}

	private SoftwareAssembly getSoftwareAssembly() {
		if (this.sadResource == null) {
			return null;
		}
		return SoftwareAssembly.Util.getSoftwareAssembly(this.sadResource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		this.sadResource = resource;

		for (final Binding binding : this.bindings) {
			binding.dispose();
		}
		this.bindings.clear();
		refreshAssemblyControllerItems();

		final SoftwareAssembly model = getSoftwareAssembly();
		if (model == null) {
			return;
		}

		final DataBindingContext context = this.getPage().getEditor().getDataBindingContext();

		this.bindings = new ArrayList<Binding>();

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getIdEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SadPackage.Literals.SOFTWARE_ASSEMBLY__ID),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getVersionEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SadPackage.Literals.SOFTWARE_ASSEMBLY__VERSION),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getNameEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SadPackage.Literals.SOFTWARE_ASSEMBLY__NAME),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getDescriptionEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SadPackage.Literals.SOFTWARE_ASSEMBLY__DESCRIPTION),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(ViewersObservables.observeSingleSelection(this.client.getAssemblyControllerPart().getViewer()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SadPackage.Literals.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER),
		        createAssemblyControllerTargetToModel(),
		        createAssemblyControllerModelToTarget()));

		setEditable(SCAEditorUtil.isEditableResource(getPage(), this.sadResource));
	}

}
