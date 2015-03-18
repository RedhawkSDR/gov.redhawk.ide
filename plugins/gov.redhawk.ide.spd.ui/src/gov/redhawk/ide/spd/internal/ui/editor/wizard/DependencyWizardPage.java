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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrLabelProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionPropertyRefItemProvider;
import gov.redhawk.ide.spd.internal.ui.parts.PropertyElementSelectorDialog;
import gov.redhawk.sca.util.PluginUtil;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.validation.EmfValidationStatusProvider;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.IEMFValueProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;

public class DependencyWizardPage extends WizardPage {
	private static final int NUM_COLUMNS = 3;

	private static final String PAGE_NAME = "dependencyConfigPage";

	private Dependency dependency;

	private EMFDataBindingContext context;

	private WizardPageSupport pageSupport;

	private ComposedAdapterFactory adapterFactory;

	private PageBook detailsPageBook;

	private Group propertyRefGroup;

	private Group softPkgRefGroup;

	private Text valueText;

	private Text refIdText;

	private TreeViewer softPkgRefViewer;

	private ComboViewer dependencyTypeComboViewer;

	private final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();

	private final Resource dependencyResource;

	private Text propNameText;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public DependencyWizardPage() {
		super(DependencyWizardPage.PAGE_NAME, "New Dependency", null);
		this.setDescription("Set values of new dependency.");
		setPageComplete(false);
		this.dependencyResource = this.resourceSet.createResource(URI.createURI("virtual:///dependency.xml"));
		final Dependency tmp = SpdFactory.eINSTANCE.createDependency();
		tmp.setPropertyRef(SpdFactory.eINSTANCE.createPropertyRef());
		this.dependencyResource.getContents().add(tmp);
		setDependency(tmp);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (this.pageSupport != null) {
			this.pageSupport.dispose();
			this.pageSupport = null;
		}
		if (this.context != null) {
			this.context.dispose();
			this.context = null;
		}
	}

	/**
	 * @return the processor
	 */
	public Dependency getDependency() {
		return this.dependency;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {

		// Create an adapter factory that yields item providers.
		//
		this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		Label label;
		final Combo dependencyKindCombo;
		label = new Label(client, SWT.NULL);
		label.setText("Kind:");
		dependencyKindCombo = new Combo(client, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		dependencyKindCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		dependencyKindCombo.setItems(new String[] { "Property Reference", "Shared Library (SoftPkg) Reference" });
		dependencyKindCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (dependencyKindCombo.getSelectionIndex() == 0) {
					if (DependencyWizardPage.this.dependency.getPropertyRef() == null) {
						DependencyWizardPage.this.dependency.setPropertyRef(SpdFactory.eINSTANCE.createPropertyRef());
					}
					DependencyWizardPage.this.dependency.setSoftPkgRef(null);
					DependencyWizardPage.this.detailsPageBook.showPage(DependencyWizardPage.this.propertyRefGroup);
					dependencyTypeComboViewer.setInput(new String[] { "allocation", "matching", "other" });
				} else if (dependencyKindCombo.getSelectionIndex() == 1) {
					DependencyWizardPage.this.dependency.setPropertyRef(null);
					if (DependencyWizardPage.this.dependency.getSoftPkgRef() == null) {
						DependencyWizardPage.this.dependency.setSoftPkgRef(SpdFactory.eINSTANCE.createSoftPkgRef());
					}
					DependencyWizardPage.this.detailsPageBook.showPage(DependencyWizardPage.this.softPkgRefGroup);
					dependencyTypeComboViewer.setInput(new String[] { "other" });
				}
			}
		});

		label = new Label(client, SWT.NULL);
		label.setText("Type:");
		this.dependencyTypeComboViewer = new ComboViewer(client, SWT.DROP_DOWN | SWT.BORDER);
		this.dependencyTypeComboViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		this.dependencyTypeComboViewer.setContentProvider(new ArrayContentProvider());
		dependencyTypeComboViewer.setInput(new String[0]);
		this.dependencyTypeComboViewer.setComparator(new ViewerComparator());

		this.detailsPageBook = new PageBook(client, SWT.NONE);
		this.detailsPageBook.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());

		createPropertyRefGroup(this.detailsPageBook);
		createSoftPkgRefGroup(this.detailsPageBook);

		if (this.dependency.getSoftPkgRef() != null) {
			dependencyKindCombo.select(1);
			this.detailsPageBook.showPage(DependencyWizardPage.this.softPkgRefGroup);
		} else if (this.dependency.getPropertyRef() != null) {
			dependencyKindCombo.select(0);
			this.detailsPageBook.showPage(DependencyWizardPage.this.propertyRefGroup);
		} else {
			dependencyKindCombo.select(0);
			this.detailsPageBook.showPage(DependencyWizardPage.this.propertyRefGroup);
		}

		// Bind and validate
		this.context = new EMFDataBindingContext();
		initBindings();
		this.context.addValidationStatusProvider(new EmfValidationStatusProvider(this.dependency, this.context, this.adapterFactory));

		this.pageSupport = WizardPageSupport.create(this, this.context);
		this.setControl(client);
	}

	private static class SoftPkgRefToViewer extends Converter {

		public SoftPkgRefToViewer() {
			super(SoftPkgRef.class, EObject.class);
		}

		@Override
		public Object convert(final Object fromObject) {
			if (fromObject instanceof SoftPkgRef) {
				final SoftPkgRef ref = ((SoftPkgRef) fromObject);
				if (ref.getImplRef() != null) {
					return ref.getImplementation();
				} else {
					return ref.getSoftPkg();
				}
			}
			return null;
		}

	}

	private class ViewerToSoftPkgRef extends Converter {

		public ViewerToSoftPkgRef() {
			super(EObject.class, SoftPkgRef.class);
		}

		@Override
		public Object convert(final Object fromObject) {
			SoftPkgRef ref = null;
			if (fromObject instanceof Implementation) {
				ref = SpdFactory.eINSTANCE.createSoftPkgRef();
				DependencyWizardPage.this.dependencyResource.getContents().add(ref);
				ref.setImplementation((Implementation) fromObject);
			} else if (fromObject instanceof SoftPkg) {
				ref = SpdFactory.eINSTANCE.createSoftPkgRef();
				DependencyWizardPage.this.dependencyResource.getContents().add(ref);
				ref.setSoftPkg((SoftPkg) fromObject);
			}
			return ref;
		}
	}

	private void initBindings() {
		if (this.context == null) {
			return;
		}
		for (final Object obj : this.context.getBindings().toArray()) {
			if (obj instanceof Binding) {
				final Binding b = (Binding) obj;
				this.context.removeBinding(b);
				b.dispose();
			}
		}

		final IEMFValueProperty propRefIdPath = EMFProperties.value(FeaturePath.fromList(SpdPackage.Literals.DEPENDENCY__PROPERTY_REF,
			SpdPackage.Literals.PROPERTY_REF__REF_ID));
		final IEMFValueProperty propValuePath = EMFProperties.value(FeaturePath.fromList(SpdPackage.Literals.DEPENDENCY__PROPERTY_REF,
			SpdPackage.Literals.PROPERTY_REF__VALUE));

		this.context.bindValue(SWTObservables.observeText(this.refIdText, SWT.Modify), propRefIdPath.observe(this.dependency),
			new EMFEmptyStringToNullUpdateValueStrategy(), null);

		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(PropertyRef.class, String.class) {
			private final ImplementationDetailsSectionPropertyRefItemProvider itemPropertyRefItemProvider = new ImplementationDetailsSectionPropertyRefItemProvider(
				DependencyWizardPage.this.adapterFactory);

			@Override
			public Object convert(final Object fromObject) {
				final AbstractProperty prop = this.itemPropertyRefItemProvider.getProperty(DependencyWizardPage.this.dependency.getPropertyRef());
				if (prop == null) {
					return "";
				} else {
					return prop.getName();
				}
			}

		});
		this.context.bindValue(SWTObservables.observeText(this.propNameText, SWT.None), propRefIdPath.observe(this.dependency), new UpdateValueStrategy(
			UpdateValueStrategy.POLICY_NEVER), strategy);
		this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.valueText), propValuePath.observe(this.dependency),
			new EMFEmptyStringToNullUpdateValueStrategy(), null);

		final EMFUpdateValueStrategy targetToModel = new EMFUpdateValueStrategy();
		targetToModel.setConverter(new ViewerToSoftPkgRef());
		final EMFUpdateValueStrategy modelToTarget = new EMFUpdateValueStrategy();
		modelToTarget.setConverter(new SoftPkgRefToViewer());
		final IEMFValueProperty softPkgRef = EMFProperties.value(FeaturePath.fromList(SpdPackage.Literals.DEPENDENCY__SOFT_PKG_REF));
		this.context.bindValue(ViewersObservables.observeSingleSelection(this.softPkgRefViewer), softPkgRef.observe(this.dependency), targetToModel,
			modelToTarget);

		this.context.bindValue(SWTObservables.observeText(this.dependencyTypeComboViewer.getCombo()),
			EMFObservables.observeValue(this.dependency, SpdPackage.Literals.DEPENDENCY__TYPE), new EMFEmptyStringToNullUpdateValueStrategy(), null);

	}

	private void createSoftPkgRefGroup(final Composite parent) {
		this.softPkgRefGroup = new Group(parent, SWT.BORDER);
		this.softPkgRefGroup.setText("Shared Library (SoftPkg) Reference");
		this.softPkgRefGroup.setLayout(new FillLayout());
		this.softPkgRefViewer = new TreeViewer(this.softPkgRefGroup, SWT.FULL_SELECTION | SWT.SINGLE);
		this.softPkgRefViewer.setComparer(new IElementComparer() {

			@Override
			public int hashCode(final Object element) {
				if (element instanceof EObject) {
					final String id = EcoreUtil.getID((EObject) element);
					if (id != null) {
						return id.hashCode();
					}
				}
				return element.hashCode();
			}

			@Override
			public boolean equals(final Object a, final Object b) {
				if (a instanceof EObject && b instanceof EObject) {
					final String ida = EcoreUtil.getID((EObject) a);
					final String idb = EcoreUtil.getID((EObject) b);
					if (ida != null && idb != null) {
						return ida.equals(idb);
					}
				}
				return PluginUtil.equals(a, b);
			}
		});
		this.softPkgRefViewer.setUseHashlookup(true);
		this.softPkgRefViewer.setContentProvider(new SdrContentProvider());
		this.softPkgRefViewer.setLabelProvider(new SdrLabelProvider());
		this.softPkgRefViewer.setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				final Class< ? >[] showTypes = new Class< ? >[] { SharedLibrariesContainer.class, SoftPkg.class, Implementation.class };
				for (final Class< ? > type : showTypes) {
					if (type.isInstance(element)) {
						return true;
					}
				}
				return false;
			}
		} });
		this.softPkgRefViewer.setInput(SdrUiPlugin.getDefault().getTargetSdrRoot());
	}

	private void createPropertyRefGroup(final Composite parent) {
		final GridDataFactory fieldTextFactory = GridDataFactory.fillDefaults().grab(true, false).span(1, 1);

		this.propertyRefGroup = new Group(parent, SWT.BORDER);
		this.propertyRefGroup.setText("Property Reference");
		this.propertyRefGroup.setLayout(new GridLayout(DependencyWizardPage.NUM_COLUMNS, false));

		Label label = new Label(this.propertyRefGroup, SWT.NULL);
		label.setText("RefId:");
		this.refIdText = new Text(this.propertyRefGroup, SWT.BORDER);
		this.refIdText.setLayoutData(fieldTextFactory.create());

		final Button propertyRefBrowse = new Button(this.propertyRefGroup, SWT.PUSH);
		propertyRefBrowse.setText("Browse...");
		propertyRefBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final PropertyElementSelectorDialog dialog = new PropertyElementSelectorDialog(getShell());
				final int result = dialog.open();
				if (result == Window.OK) {
					final Simple sel = (Simple) dialog.getFirstResult();
					if (sel != null) {
						DependencyWizardPage.this.refIdText.setText(sel.getId());
					}
				}
			}

		});

		label = new Label(this.propertyRefGroup, SWT.None);
		label.setText("Name:");
		this.propNameText = new Text(this.propertyRefGroup, SWT.BORDER | SWT.READ_ONLY);
		this.propNameText.setEnabled(false);
		this.propNameText.setLayoutData(fieldTextFactory.span(2, 1).create());

		label = new Label(this.propertyRefGroup, SWT.NULL);
		label.setText("Value:");
		this.valueText = new Text(this.propertyRefGroup, SWT.BORDER);
		this.valueText.setLayoutData(fieldTextFactory.span(2, 1).create());
	}

	/**
	 * Sets the os.
	 * 
	 * @param os2 the os2
	 */
	public void setDependency(final Dependency dependency) {
		this.dependencyResource.getContents().clear();
		this.dependency = EcoreUtil.copy(dependency);
		this.setTitle("Edit Dependency");
		this.setDescription("Edit Dependency Value");
		this.dependencyResource.getContents().add(this.dependency);
		initBindings();
	}
}
