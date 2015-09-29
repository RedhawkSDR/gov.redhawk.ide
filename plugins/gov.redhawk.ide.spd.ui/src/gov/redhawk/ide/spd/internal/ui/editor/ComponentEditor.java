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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.prf.ui.provider.PropertiesEditorPrfItemProviderAdapterFactory;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * The Class ComponentEditor.
 */
public class ComponentEditor extends SCAFormEditor {

	private static final EStructuralFeature[] PRF_PATH = new EStructuralFeature[] {
	        SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE, SpdPackage.Literals.PROPERTY_FILE__PROPERTIES
	};

	private static final EStructuralFeature[] SCD_PATH = new EStructuralFeature[] {
	        SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, SpdPackage.Literals.DESCRIPTOR__COMPONENT
	};
	private ComponentOverviewPage overviewPage;
	private PropertiesFormPage propertiesPage;
	private ImplementationPage implementationPage;

	private ResourceListener nameListener;
	private ProjectListener projectListener;

	private TextEditor spdEditor;
	private IEditorInput prfInput;
	private TextEditor prfEditor;
	private IEditorInput scdInput;
	private TextEditor scdEditor;
	private PrfListener prfListener;

	private class ResourceListener extends AdapterImpl {
		private SoftPkg spd;
		private final Resource spdResource;
		private ImplementationListener implListener;

		public ResourceListener(final Resource spdResource) {
			this.spdResource = spdResource;
			if (this.spdResource != null) {
				this.spdResource.eAdapters().add(this);
				this.spd = getSoftPkg();
				if (this.spd != null) {
					this.spd.eAdapters().add(this);
					this.implListener = new ImplementationListener(this.spd);
					updateTitle();
				}
			}
		}

		/**
		 * Gets the soft pkg.
		 * 
		 * @return the soft pkg
		 */
		private SoftPkg getSoftPkg() {
			return ModelUtil.getSoftPkg(this.spdResource);
		}

		public void dispose() {
			if (this.spd != null) {
				this.spd.eAdapters().remove(this);
			}
			if (this.spdResource != null) {
				this.spdResource.eAdapters().remove(this);
			}
			if (this.implListener != null) {
				this.implListener.dispose();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void notifyChanged(final Notification msg) {
			super.notifyChanged(msg);
			if (msg.getNotifier() instanceof Resource) {
				switch (msg.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
					if (this.spd != null) {
						this.spd.eAdapters().remove(this);
						this.spd = null;
					}
					if (this.spdResource.isLoaded()) {
						this.spd = getSoftPkg();
						if (this.spd != null) {
							this.spd.eAdapters().add(this);
							updateTitle();
						}
					}
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof SoftPkg) {
				final int featureID = msg.getFeatureID(SoftPkg.class);

				if (featureID == SpdPackage.SOFT_PKG__NAME) {
					if (msg.getEventType() == Notification.SET) {
						updateTitle();
					}
				} else if (featureID == SpdPackage.SOFT_PKG__IMPLEMENTATION) {
					if (msg.getEventType() == Notification.ADD) {
						this.implListener.addImplementation((Implementation) msg.getNewValue());
					} else if (msg.getEventType() == Notification.REMOVE) {
						this.implListener.removeImplementation((Implementation) msg.getOldValue());
					}
				}
			}
		}
	}

	/**
	 * Instantiates a new component editor.
	 */
	public ComponentEditor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		String name = null;
		final SoftPkg softPkg = getSoftPkg();
		if (softPkg != null) {
			name = getSoftPkg().getName();
			if (name == null) {
				name = getEditorInput().getName();
			}
		}
		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContentOutlinePage createContentOutline() {
		return new ComponentOutlinePage(this);
	}

	/**
	 * Gets the soft pkg.
	 * 
	 * @return the soft pkg
	 */
	private SoftPkg getSoftPkg() {
		return ModelUtil.getSoftPkg(getMainResource());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.nameListener != null) {
			this.nameListener.dispose();
			this.nameListener = null;
		}
		if (this.prfListener != null) {
			this.prfListener.dispose();
			this.prfListener = null;
		}
		if (this.projectListener != null) {
			this.projectListener.dispose();
			this.projectListener = null;
		}
		super.dispose();
	}

	@Override
	protected void setInput(final IEditorInput input) {
		super.setInput(input);

		final SoftPkg spd = SoftPkg.Util.getSoftPkg(this.getMainResource());

		projectListener = new ProjectListener();
		projectListener.activate();

		createPrfInput(spd);
		createScdInput(spd);

		if (spd != null && spd.getPropertyFile() != null) {
			spd.getPropertyFile().eAdapters().add(new EContentAdapter() {

				@Override
				public void notifyChanged(final Notification msg) {
					if (msg.getFeatureID(SoftPkg.class) == SpdPackage.LOCAL_FILE__NAME) {
						createPrfInput(spd);
						if (ComponentEditor.this.prfInput == null) {
							removePrfPage();
						} else {
							replacePrf();
						}
					}
				}
			});
		}

		if (spd != null && spd.getDescriptor() != null) {
			spd.getDescriptor().eAdapters().add(new EContentAdapter() {

				@Override
				public void notifyChanged(final Notification msg) {
					if (msg.getFeatureID(SoftPkg.class) == SpdPackage.LOCAL_FILE__NAME) {
						createScdInput(spd);
						if (ComponentEditor.this.scdInput == null) {
							removeScdPage();
						} else {
							replaceScd();
						}
					}
				}
			});
		}
	}

	protected void createPrfInput(final SoftPkg spd) {
		this.prfInput = null;
		URI prfUri = getPrfURI();
		if (prfUri != null) {
			if (prfUri.isPlatformResource()) {
				this.prfInput = new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(prfUri.toPlatformString(true))));
				getResourceTracker().addTrackedResource(((FileEditorInput) this.prfInput).getFile());
			} else {
				try {
					if (prfUri.isPlatform()) {
						prfUri = CommonPlugin.resolve(prfUri);
					}
					this.prfInput = new FileStoreEditorInput(EFS.getStore(java.net.URI.create(prfUri.toString())));
				} catch (final CoreException e) {
					ComponentUiPlugin.logException(e);
				}
			}
		}
	}

	protected void createScdInput(final SoftPkg spd) {
		this.scdInput = null;
		URI scdUri = getScdURI();
		if (scdUri != null) {
			if (scdUri.isPlatformResource()) {
				this.scdInput = new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scdUri.toPlatformString(true))));
				getResourceTracker().addTrackedResource(((FileEditorInput) this.scdInput).getFile());
			} else {
				try {
					if (scdUri.isPlatform()) {
						scdUri = CommonPlugin.resolve(scdUri);
					}
					this.scdInput = new FileStoreEditorInput(EFS.getStore(java.net.URI.create(scdUri.toString())));
				} catch (final CoreException e) {
					ComponentUiPlugin.logException(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addPages() {
		try {
			this.nameListener = new ResourceListener(this.getMainResource());
			this.prfListener = new PrfListener(getEditingDomain(), this.getMainResource());

			this.overviewPage = new ComponentOverviewPage(this);
			this.addPage(this.overviewPage);
			this.overviewPage.setInput(this.getMainResource());

			this.implementationPage = new ImplementationPage(this);
			this.addPage(this.implementationPage);
			this.implementationPage.setInput(getMainResource());

			this.spdEditor = this.createTextEditor(this.getEditorInput());
			final int pageIndex = addPage(this.implementationPage.getIndex() + 1, this.spdEditor, this.getEditorInput(), this.getMainResource());
			this.setPageText(pageIndex, this.getEditorInput().getName());

			addPrfPages();
			addScdPage();
		} catch (final PartInitException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to add pages.", e));
		}
	}

	/**
	 * @return
	 */
	private URI getScdURI() {
		final SoftwareComponent scd = ScaEcoreUtils.getFeature(getSoftPkg(), ComponentEditor.SCD_PATH);
		if (scd != null && scd.eResource() != null) {
			return scd.eResource().getURI();
		}
		return null;
	}

	/**
	 * @return
	 */
	private URI getPrfURI() {
		final Properties properties = ScaEcoreUtils.getFeature(getSoftPkg(), ComponentEditor.PRF_PATH);
		if (properties != null && properties.eResource() != null) {
			return properties.eResource().getURI();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AdapterFactory getSpecificAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new ScdItemProviderAdapterFactory());
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());
		factory.addAdapterFactory(new PropertiesEditorPrfItemProviderAdapterFactory());
		return factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditingDomainId() {
		return "gov.redhawk.spd.editingDomainId";
	}

	public void replacePrf() {
		if (this.prfEditor != null) {
			this.prfEditor.setInput(this.prfInput);
			this.setPageText(getPrfPageIndex(), this.prfInput.getName());
		} else {
			addPrfPages();
		}
	}

	public void replaceScd() {
		if (this.scdEditor != null) {
			this.scdEditor.setInput(this.scdInput);
			this.setPageText(this.getScdPageIndex(), this.scdInput.getName());
		} else {
			addScdPage();
		}
	}

	private void removePrfPage() {
		if (this.propertiesPage != null || getPrfPageIndex() >= 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (ComponentEditor.this.propertiesPage.isDisposed()) {
						return;
					}
					if (ComponentEditor.this.propertiesPage != null) {
						removePage(ComponentEditor.this.propertiesPage.getIndex());
						ComponentEditor.this.propertiesPage.dispose();
						ComponentEditor.this.propertiesPage = null;
					}
					if (getPrfPageIndex() >= 0) {
						if (ComponentEditor.this.prfEditor != null) {
							removePage(getPrfPageIndex());
							ComponentEditor.this.prfEditor.dispose();
							ComponentEditor.this.prfEditor = null;
						}
					}
				}
			});
		}
	}

	private void removeScdPage() {
		if (getScdPageIndex() >= 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (ComponentEditor.this.scdEditor != null && getScdPageIndex() >= 0) {
						removePage(getScdPageIndex());
						ComponentEditor.this.scdEditor.dispose();
						ComponentEditor.this.scdEditor = null;
					}
				}
			});
		}
	}

	private int getPrfPageIndex() {
		if (this.prfEditor == null || isDisposed() || getPages() == null) {
			return -1;
		}

		return this.getPages().indexOf(this.prfEditor);
	}

	private int getScdPageIndex() {
		if (this.scdEditor == null || isDisposed() || getPages() == null) {
			return -1;

		}
		return this.getPages().indexOf(this.scdEditor);
	}

	private int getSpdPageIndex() {
		if (this.spdEditor == null || isDisposed() || getPages() == null) {
			return -1;
		}

		return this.getPages().indexOf(this.spdEditor);
	}

	private void addPrfPages() {
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(this.getMainResource());

		if (this.prfInput != null) {
			try {
				this.propertiesPage = new PropertiesFormPage(this);
				addPage(this.overviewPage.getIndex() + 1, this.propertiesPage);

				final PropertyFile propertyFile = spd.getPropertyFile();
				if (propertyFile != null && propertyFile.getProperties() != null) {
					final Properties properties = spd.getPropertyFile().getProperties();
					this.propertiesPage.setInput(properties.eResource());

					this.prfEditor = this.createTextEditor(this.prfInput);
					final int index = addPage(this.getSpdPageIndex() + 1, this.prfEditor, this.prfInput, properties.eResource());
					this.setPageText(index, this.prfInput.getName());
				}
			} catch (final CoreException e) {
				// PASS
			}
		}
	}

	private void addScdPage() {
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(this.getMainResource());

		if (this.scdInput != null) {
			try {
				final Descriptor descriptorFile = spd.getDescriptor();
				if (descriptorFile != null && descriptorFile.getComponent() != null) {
					final SoftwareComponent component = descriptorFile.getComponent();
					this.scdEditor = this.createTextEditor(this.scdInput);
					int index = -1;
					if (this.getPrfPageIndex() == -1) {
						index = this.getSpdPageIndex() + 1;
					} else {
						index = this.getPrfPageIndex() + 1;
					}
					addPage(index, this.scdEditor, this.scdInput, component.eResource());
					this.setPageText(index, this.scdInput.getName());
				}
			} catch (final CoreException e) {
				// PASS
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void gotoMarker(final IMarker marker) {

		try {
			final Object uri = marker.getAttribute(EValidator.URI_ATTRIBUTE);
			if (uri != null) {
				final EObject obj = this.getEditingDomain().getResourceSet().getEObject(URI.createURI(uri.toString()), true);
				if (obj instanceof Implementation) {
					this.implementationPage.selectReveal(obj);
					this.setActivePage(this.implementationPage.getId());
				} else if (obj instanceof UsesDevice) {
					this.implementationPage.selectReveal(obj);
					this.setActivePage(this.implementationPage.getId());
				} else if (obj instanceof PropertyRef) {
					this.implementationPage.selectReveal(obj);
					this.setActivePage(this.implementationPage.getId());
				} else if (obj instanceof Simple) {
					this.propertiesPage.selectReveal(obj);
					this.setActivePage(this.propertiesPage.getId());
				} else if (obj instanceof SimpleSequence) {
					this.propertiesPage.selectReveal(obj);
					this.setActivePage(this.propertiesPage.getId());
				} else if (obj instanceof SoftPkg) {
					this.overviewPage.selectReveal(obj);
					this.setActivePage(this.overviewPage.getId());
				} else if (obj instanceof Code) {
					this.overviewPage.selectReveal(obj);
					this.setActivePage(this.implementationPage.getId());
				} else {
					this.setActivePage(this.overviewPage.getId());
				}
			} else {
				this.setActivePage(this.overviewPage.getId());
			}
		} catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.WARNING,
			        ComponentUiPlugin.PLUGIN_ID,
			        "Problems occured while trying to go to problem marker.",
			        e),
			        StatusManager.SHOW | StatusManager.LOG);
		}
		super.gotoMarker(marker);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Notifier getRootValidationNotifier() {
		return this.getEditingDomain().getResourceSet();
	}

	@Override
	public List<Object> getOutlineItems() {
		final List<Object> myList = new ArrayList<Object>();
		if (getSoftPkg() != null) {
			myList.add(this.overviewPage);
			final PropertyFile propertyFile = getPropertyFile();
			if ((propertyFile != null) && (propertyFile.getProperties() != null)) {
				myList.add(propertyFile.getProperties());
			}
			myList.add(getSoftPkg());
		}
		return myList;
	}

	private PropertyFile getPropertyFile() {
		return getSoftPkg().getPropertyFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPersisted(final Resource resource) {
		return super.isPersisted(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectionToViewer(final Collection< ? > collection) {
		boolean updateSelection = true;
		for (final Object obj : collection) {
			//Only update the selection if the objects show in master viewers.
			if (!(obj instanceof AbstractProperty || obj instanceof Implementation)) {
				updateSelection = false;
				break;
			}
		}
		if (updateSelection) {
			super.setSelectionToViewer(collection);
		}
	}

	private IFile getSpdFile() {
		IFile spdFile = null;
		if ((getEditorInput() != null) && (getEditorInput() instanceof IFileEditorInput)) {
			spdFile = ((IFileEditorInput) getEditorInput()).getFile();
		}
		return spdFile;
	}

	private IFile getScdFile() {
		IFile scdFile = null;
		if ((getScdURI() != null) && getScdURI().isPlatformResource()) {
			scdFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getScdURI().toPlatformString(true)));
		}
		return scdFile;
	}

	private IFile getPrfFile() {
		IFile prfFile = null;
		if ((getPrfURI() != null) && getPrfURI().isPlatformResource()) {
			prfFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getPrfURI().toPlatformString(true)));
		}
		return prfFile;
	}

	@Override
	protected void resourceDeleted(final IResource resource) {
		final Display display = getSite().getShell().getDisplay();

		if (resource.equals(getSpdFile())) {
			display.syncExec(new Runnable() {
				public void run() {
					if (isDirty()) {
						return; // Don't do anything if the editor is in the dirty state
					}
					final Set<IFile> filesToDelete = new HashSet<IFile>();
					if ((getPrfFile() != null) && (getPrfFile().exists())) {
						filesToDelete.add(getPrfFile());
					}
					if ((getScdFile() != null) && (getScdFile().exists())) {
						filesToDelete.add(getScdFile());
					}

					if (filesToDelete.size() == 0) {
						return; // Nothing to do
					}

					final StringBuilder sb = new StringBuilder();
					sb.append("Would you like to delete:");
					for (final IFile f : filesToDelete) {
						sb.append(" '").append(f.getName()).append("'");
					}
					sb.append("?");

					final boolean okToDelete = MessageDialog.openConfirm(display.getActiveShell(), "Delete referenced files?", sb.toString());
					if (okToDelete) {
						final Job deleteJob = new WorkspaceJob("Delete referenced files") {

							@Override
							public IStatus runInWorkspace(final IProgressMonitor monitor) {
								final SubMonitor progress = SubMonitor.convert(monitor);
								progress.beginTask("Deleting referenced files", filesToDelete.size());
								try {
									for (final IFile f : filesToDelete) {
										if (monitor.isCanceled()) {
											return Status.CANCEL_STATUS;
										}
										if (f.exists()) {
											f.delete(true, true, progress.newChild(1));
										}
									}
								} catch (final CoreException e) {
									return e.getStatus();
								}

								return Status.OK_STATUS;
							}

						};
						deleteJob.setUser(true);
						deleteJob.schedule();
					}
				}
			});

			super.resourceDeleted(resource); // Invoke the super behavior
		}

		if (resource.equals(getScdFile())) {
			removeScdPage();
		}

		if (resource.equals(getPrfFile())) {
			removePrfPage();
		}

		validate();
	}

	@Override
	protected void resourceMoved(final IResource from, final IResource to) {
		final Display display = getSite().getShell().getDisplay();
		// If the PRF is moved update the reference from the SPD
		if ((getEditingDomain() != null) && from.equals(getPrfFile())) {
			final CompoundCommand command = new CompoundCommand("Set SCD file");

			PropertyFile propertyFile = getSoftPkg().getPropertyFile();
			if (propertyFile == null) {
				propertyFile = SpdFactory.eINSTANCE.createPropertyFile();
				command.append(SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE, propertyFile));
			}
			LocalFile file = propertyFile.getLocalFile();
			if (file == null) {
				file = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), propertyFile, SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE, file));
			}
			command.append(SetCommand.create(getEditingDomain(), file, SpdPackage.Literals.LOCAL_FILE__NAME, to.getProjectRelativePath().toString()));
			display.asyncExec(new Runnable() {
				public void run() {
					getEditingDomain().getCommandStack().execute(command);
				}
			});
		}

		// If the SCD is moved update the reference from the SPD
		if ((getEditingDomain() != null) && from.equals(getScdFile())) {
			final CompoundCommand command = new CompoundCommand("Set SCD file");

			Descriptor descriptor = getSoftPkg().getDescriptor();
			if (descriptor == null) {
				descriptor = SpdFactory.eINSTANCE.createDescriptor();
				command.append(SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, descriptor));
			}
			LocalFile file = descriptor.getLocalfile();
			if (file == null) {
				file = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), descriptor, SpdPackage.Literals.DESCRIPTOR__LOCALFILE, file));
			}
			command.append(SetCommand.create(getEditingDomain(), file, SpdPackage.Literals.LOCAL_FILE__NAME, to.getProjectRelativePath().toString()));
			display.asyncExec(new Runnable() {
				public void run() {
					getEditingDomain().getCommandStack().execute(command);
				}
			});
		}

		if (from.equals(getSpdFile())) {
			super.resourceMoved(from, to);
		}

		validate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resourceChanged(final IResource resource, final IResourceDelta delta) {
		//Make sure we don't call resource changed on a non spd resource
		if (this.isValidSpdResource(resource)) {
			super.resourceChanged(resource, delta);
			validate();
		}
	}

	/**
	 * Evaluate the given resource to determine if it is a resource that can be associated with the ComponentEditor.
	 * 
	 * @param resource The IResource to evaluate
	 * @return <code> true </code> if this is an spd resource; <code> false </code> otherwise
	 */
	private boolean isValidSpdResource(final IResource resource) {
		for (final Resource r : getEditingDomain().getResourceSet().getResources()) {
			final IFile iFile = ModelUtil.getResource(r);
			if (resource.equals(iFile)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Listens to the project associated with the editor's input. Triggers a validation when changes occur to that
	 * project.
	 */
	private class ProjectListener implements IResourceChangeListener {

		public ProjectListener() {
		}

		public void activate() {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		}

		public void dispose() {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		}

		public void resourceChanged(IResourceChangeEvent event) {
			IEditorInput input = getEditorInput();
			if (!(input instanceof IFileEditorInput)) {
				return;
			}
			IFile file = ((IFileEditorInput) input).getFile();
			final IProject project = file.getProject();

			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				if (delta.findMember(project.getFullPath()) != null) {
					validate();
				}
			} else {
				if (project.equals(event.getResource().getProject())) {
					validate();
				}
			}
		}
	}
}
