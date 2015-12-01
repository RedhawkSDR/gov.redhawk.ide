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
package gov.redhawk.ide.sad.internal.ui.wizard;

import gov.redhawk.common.ui.AdapterFactoryCellLabelProvider;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterface;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPort;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPort;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.partitioning.provider.PartitioningItemProviderAdapterFactory;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.Disposable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * 
 */
public class ExternalPortWizard extends Wizard implements IWorkbenchWizard {

	private class ExternalPortWizardPage extends WizardPage {

		private SadComponentInstantiation componentSelection;
		private EObject portSelection;

		private Disposable disposable;
		private AdapterFactory adapterFactory;

		private TableViewer componentViewer;
		private TableViewer portViewer;
		private String portDescription;

		protected ExternalPortWizardPage(final String pageName, final ImageDescriptor titleImage) {
			super(pageName, "External Port", titleImage);
			this.setDescription("Select the component and then the port of the component you wish to be marked as an "
			        + "external port.");
			this.disposable = new Disposable();
			this.adapterFactory = createAdapterFactory();
		}

		@Override
		public void createControl(final Composite parent) {
			final Composite composite = new Composite(parent, SWT.None);
			composite.setLayout(new GridLayout());

			createSelectComponent(composite);
			createSelectPort(composite);
			createDescription(composite);

			setPageComplete(false);

			setControl(composite);
		}

		@Override
		public void dispose() {
			disposable.dispose();
			super.dispose();
		}

		private AdapterFactory createAdapterFactory() {
			AdapterFactory factory = new ComposedAdapterFactory(new AdapterFactory[] { new SadItemProviderAdapterFactory(), new PartitioningItemProviderAdapterFactory() });
			disposable.add(factory);
			return factory;
		}

		private void createDescription(final Composite composite) {
			Label label = new Label(composite, SWT.NONE);
			label.setText("Description (Optional):");

			final Text text = new Text(composite, SWT.MULTI | SWT.BORDER);
			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 150).create());
			text.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(final ModifyEvent e) {
					ExternalPortWizardPage.this.portDescription = text.getText();
				}
			});

		}

		private void createSelectPort(final Composite composite) {
			Label label = new Label(composite, SWT.None);
			label.setText("Select Port:");

			this.portViewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER);
			final TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(100, 10, false)); // SUPPRESS CHECKSTYLE MagicNumber
			this.portViewer.getTable().setLayout(tableLayout);
			this.portViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 150).create());

			new TableViewerColumn(this.portViewer, SWT.LEFT);
			this.portViewer.setLabelProvider(new AdapterFactoryCellLabelProvider(this.adapterFactory));
			this.portViewer.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public Object[] getElements(final Object inputElement) {
					final ComponentInstantiation ci = (ComponentInstantiation) inputElement;
					final List<EObject> retVal = new ArrayList<EObject>();
					retVal.addAll(ci.getUses());
					retVal.addAll(ci.getProvides());
					return retVal.toArray();
				}
			});
			this.portViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					ExternalPortWizardPage.this.portSelection = (EObject) ((IStructuredSelection) event.getSelection()).getFirstElement();
					updatePageComplete();
				}
			});
			this.portViewer.setComparator(new ViewerComparator() {
				@Override
				public int category(final Object element) {
					if (element instanceof ComponentSupportedInterface || element instanceof ComponentSupportedInterfaceStub) {
						return 1;
					}
					if (element instanceof ProvidesPort || element instanceof ProvidesPortStub) {
						return 2;
					}
					if (element instanceof UsesPort || element instanceof UsesPortStub) {
						return 3; // SUPPRESS CHECKSTYLE MagicNumber
					}
					return super.category(element);
				}
			});
		}

		private void createSelectComponent(final Composite parent) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Select Component:");

			this.componentViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER);
			final TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(100, 10, false)); // SUPPRESS CHECKSTYLE MagicNumber
			this.componentViewer.getTable().setLayout(tableLayout);
			this.componentViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 150).create());

			new TableViewerColumn(this.componentViewer, SWT.LEFT);
			this.componentViewer.setLabelProvider(new AdapterFactoryCellLabelProvider(this.adapterFactory));
			this.componentViewer.setComparator(new ViewerComparator());
			this.componentViewer.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public Object[] getElements(final Object inputElement) {
					List<SadComponentInstantiation> instantiations = ((SoftwareAssembly) inputElement).getAllComponentInstantiations();
					return instantiations.toArray();
				}
			});
			this.componentViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					ExternalPortWizardPage.this.componentSelection = (SadComponentInstantiation) ((IStructuredSelection) event.getSelection())
					        .getFirstElement();
					ExternalPortWizardPage.this.portViewer.setInput(ExternalPortWizardPage.this.componentSelection);
					updatePageComplete();
				}
			});
			this.componentViewer.setInput(ExternalPortWizard.this.softwareAssembly);
		}

		private void updatePageComplete() {
			setPageComplete(this.componentSelection != null && this.portSelection != null);
		}
	}

	private ExternalPortWizardPage page;
	private IStructuredSelection selection;
	private SoftwareAssembly softwareAssembly;

	public ExternalPortWizard() {
		this.setWindowTitle("Add external Port");
		this.setNeedsProgressMonitor(false);
		this.setHelpAvailable(false);
	}

	@Override
	public void addPages() {
		this.page = new ExternalPortWizardPage("externalPort", null);
		this.addPage(this.page);
	}

	public EObject getPortSelection() {
		return this.page.portSelection;
	}

	public SadComponentInstantiation getComponentSelection() {
		return this.page.componentSelection;
	}

	public String getPortDescription() {
		return this.page.portDescription;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void setSoftwareAssembly(final SoftwareAssembly softwareAssembly) {
		this.softwareAssembly = softwareAssembly;
	}

	public SoftwareAssembly getSoftwareAssembly() {
		return this.softwareAssembly;
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
		if (this.selection.getFirstElement() instanceof SoftwareAssembly) {
			setSoftwareAssembly((SoftwareAssembly) this.selection.getFirstElement());
		}
	}

}
