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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.dcd.CompositePartOfDevice;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class DevicesDetailsPage extends ScaDetails {

	private DcdComponentComposite deviceComposite;
	private final DevicesSection fSection;
	private SoftPkg softPkg;
	private DcdComponentInstantiation input;
	private DcdComponentPlacement inputPlacement;

	public DevicesDetailsPage(final DevicesSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	public DataBindingContext getContext() {
		return getPage().getEditor().getDataBindingContext();
	}

	/**
	 * Creates the implementation section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createDeviceSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Device");
		section.setDescription("This allows you to override particular properties of the selected device.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.deviceComposite = new DcdComponentComposite(section, SWT.NONE, toolkit, this.getEditor());
		toolkit.adapt(this.deviceComposite);

		section.setClient(this.deviceComposite);

	}

	/**
	 * Remove the parent from this device.
	 */
	protected void handleUnsetParent() {
		execute(SetCommand.create(getEditingDomain(), this.inputPlacement, DcdPackage.Literals.DCD_COMPONENT_PLACEMENT__COMPOSITE_PART_OF_DEVICE,
			SetCommand.UNSET_VALUE));
		this.deviceComposite.getUnsetParentButton().setEnabled(false);
	}

	@Override
	public void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	@Override
	protected List<Binding> bind(final DataBindingContext context, final EObject obj) {
		if (!(obj instanceof DcdComponentInstantiation)) {
			return null;
		}
		this.input = (DcdComponentInstantiation) obj;
		this.inputPlacement = (DcdComponentPlacement) input.getPlacement();

		if (this.inputPlacement.getComponentFileRef().getFile() != null) {
			this.softPkg = this.inputPlacement.getComponentFileRef().getFile().getSoftPkg();
		}

		if ((this.softPkg == null) || (this.softPkg.eIsProxy())) {
			return Collections.emptyList();
		}

		final List<Binding> retVal = new ArrayList<Binding>();
		retVal.add(FormEntryBindingFactory.bind(context, this.deviceComposite.getNameEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME, this.input, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		this.deviceComposite.getParentViewer().setInput(getAggregateComponentPlacements((DcdPartitioning) this.inputPlacement.eContainer()));

		// A ComponentPlacement cannot be its own parent, nor can any child of a parent be the parent's parent.
		this.deviceComposite.getParentViewer().addFilter(new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				boolean retVal = false;
				if ((element != DevicesDetailsPage.this.inputPlacement) && !isChild((DcdComponentPlacement) element)) {
					retVal = true;
				}
				return retVal;
			}

			private boolean isChild(final DcdComponentPlacement element) {
				boolean retVal = false;
				if (element.getParentDevice() != null) {
					if (element.getParentDevice().getPlacement() == DevicesDetailsPage.this.inputPlacement) {
						retVal = true;
					} else {
						retVal = isChild((DcdComponentPlacement) element.getParentDevice().getPlacement());
					}
				}
				return retVal;
			}
		});

		retVal.add(context.bindValue(ViewersObservables.observeSingleSelection(this.deviceComposite.getParentViewer()),
			EMFEditObservables.observeValue(getEditingDomain(), this.inputPlacement, DcdPackage.Literals.DCD_COMPONENT_PLACEMENT__COMPOSITE_PART_OF_DEVICE),
			createParentTargetToModel(), createParentModelToTarget()));

		this.deviceComposite.setInput(this.input);
		this.deviceComposite.setEditable(SCAEditorUtil.isEditableResource(getPage(), this.inputPlacement.eResource()));
		this.deviceComposite.getUnsetParentButton().setEnabled(this.inputPlacement.getCompositePartOfDevice() != null);

		return retVal;
	}

	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createDeviceSection(toolkit, parent);

		addListeners();
	}

	private void addListeners() {

		this.deviceComposite.getParentViewer().getCombo().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				DevicesDetailsPage.this.deviceComposite.getUnsetParentButton().setEnabled(true);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				DevicesDetailsPage.this.deviceComposite.getUnsetParentButton().setEnabled(true);
			}

		});
		this.deviceComposite.getUnsetParentButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleUnsetParent();
			}
		});
	}

	private UpdateValueStrategy createParentTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy() {
			@Override
			protected org.eclipse.core.runtime.IStatus doSet(final org.eclipse.core.databinding.observable.value.IObservableValue observableValue,
				final Object value) {
				return super.doSet(observableValue, value);
			};
		};

		strategy.setConverter(new Converter(DcdComponentPlacement.class, CompositePartOfDevice.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final DcdComponentPlacement cp = (DcdComponentPlacement) fromObject;
				final CompositePartOfDevice cpod = DcdFactory.eINSTANCE.createCompositePartOfDevice();
				cpod.setRefID(cp.getComponentInstantiation().get(0).getId());
				return cpod;
			}

		});
		return strategy;
	}

	private UpdateValueStrategy createParentModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(CompositePartOfDevice.class, DcdComponentPlacement.class) {

			@Override
			public Object convert(final Object fromObject) {
				if ((fromObject == null) || (DevicesDetailsPage.this.inputPlacement == null)) {
					return null;
				}
				final CompositePartOfDevice cpod = (CompositePartOfDevice) fromObject;

				final ComponentInstantiation comp = (ComponentInstantiation) DevicesDetailsPage.this.inputPlacement.eResource().getEObject(cpod.getRefID());

				return comp.getPlacement();
			}

		});
		return strategy;
	}

	/**
	 * This converts a PropertyRef to a Literals reference of the appropriate
	 * type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property
	 */
	protected EReference getTypeLiteral(final EObject property) {
		if (property instanceof SimpleRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF;
		} else if (property instanceof SimpleSequenceRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF;
		} else if (property instanceof StructRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF;
		} else if (property instanceof StructSequenceRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF;
		}
		return null;
	}

	/**
	 * This converts a PropertyRef's ID to a Literals reference of the
	 * appropriate type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property's ID
	 */
	protected EAttribute getIdLiteral(final EObject property) {
		return PrfPackage.Literals.ABSTRACT_PROPERTY_REF__REF_ID;
	}

	/**
	 * This converts a PropertyRef's value to a Literals reference of the
	 * appropriate type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property's value
	 */
	protected EObject getValueLiteral(final EObject property) {
		if (property instanceof SimpleRef) {
			return PrfPackage.Literals.SIMPLE_REF__VALUE;
		} else if (property instanceof SimpleSequenceRef) {
			return PrfPackage.Literals.SIMPLE_SEQUENCE_REF__VALUES;
		} else if (property instanceof StructRef) {
			return PrfPackage.Literals.STRUCT_REF__SIMPLE_REF;
		} else if (property instanceof StructSequenceRef) {
			return PrfPackage.Literals.STRUCT_SEQUENCE_REF__STRUCT_VALUE;
		}
		return null;
	}

	/**
	 * Return an array of aggregate devices.
	 * @param partition
	 * @return Array of Component Placements
	 */
	private Object[] getAggregateComponentPlacements(final DcdPartitioning partition) {
		final List<DcdComponentPlacement> compList = new ArrayList<DcdComponentPlacement>();

		for (final DcdComponentPlacement cp : partition.getComponentPlacement()) {
			if (DcdComponentPlacement.Util.isAggregateDevice(cp)) {
				compList.add(cp);
			}
		}

		return compList.toArray();
	}
}
