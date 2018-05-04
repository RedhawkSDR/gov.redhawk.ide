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
package gov.redhawk.ide.debug.ui.tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.launch.ui.ScaLauncherActivator;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class ImplementationTab extends AbstractLaunchConfigurationTab {

	private static final ImageDescriptor IMAGE_DESC = AbstractUIPlugin.imageDescriptorFromPlugin(ScaDebugUiPlugin.PLUGIN_ID,
		"icons/obj16/implementationTab.gif");

	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG, };

	private Image image;
	private TableViewer viewer;
	private SpdItemProviderAdapterFactory adapterFactory = new SpdItemProviderAdapterFactory();

	/**
	 * The SAD XML model
	 * <p/>
	 * Contains the results from the last time {@link #loadSadAndImpls(ILaunchConfiguration)} was called. It should
	 * never be assumed to hold the "current" mappings unless it was just loaded.
	 */
	private SoftwareAssembly sad;

	/**
	 * SAD component instantiation ID -> SPD implementation ID
	 * <p/>
	 * Contains the results from the last time {@link #loadSadAndImpls(ILaunchConfiguration)} was called. It should
	 * never be assumed to hold the "current" mappings unless it was just loaded.
	 */
	private final Map<String, String> implMap = new HashMap<String, String>();

	public ImplementationTab() {
		this.image = ImplementationTab.IMAGE_DESC.createImage();
	}

	@Override
	public void dispose() {
		this.image.dispose();
		this.image = null;
		this.adapterFactory.dispose();
		this.adapterFactory = null;
		super.dispose();
	}

	@Override
	public String getName() {
		return "&Implementation";
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setFont(parent.getFont());
		composite.setLayout(new FillLayout());

		final TableLayout layout = new TableLayout();
		this.viewer = new TableViewer(composite, SWT.BORDER);
		this.viewer.getTable().setLayout(layout);
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);
		this.viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof SoftwareAssembly) {
					final SoftwareAssembly localSad = (SoftwareAssembly) inputElement;
					final List<SadComponentInstantiation> retVal = new ArrayList<SadComponentInstantiation>();
					for (final HostCollocation hc : localSad.getPartitioning().getHostCollocation()) {
						for (final SadComponentPlacement cp : hc.getComponentPlacement()) {
							retVal.addAll(cp.getComponentInstantiation());
						}
					}
					for (final SadComponentPlacement cp : localSad.getPartitioning().getComponentPlacement()) {
						retVal.addAll(cp.getComponentInstantiation());
					}
					return retVal.toArray();
				}
				return new Object[0];
			}
		});

		final TableViewerColumn componentColumn = new TableViewerColumn(this.viewer, SWT.CENTER);
		componentColumn.getColumn().setText("Instantiation");
		componentColumn.setLabelProvider(new TreeColumnViewerLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final SadComponentInstantiation ci = (SadComponentInstantiation) element;
				return ci.getUsageName();
			}
		}));
		layout.addColumnData(new ColumnWeightData(20, 20, true));

		final TableViewerColumn implColumn = new TableViewerColumn(this.viewer, SWT.CENTER);
		implColumn.getColumn().setText("Implementation");
		implColumn.setLabelProvider(new TreeColumnViewerLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final SadComponentInstantiation ci = (SadComponentInstantiation) element;
				final String implId = ImplementationTab.this.implMap.get(ci.getId());
				final SoftPkg softPkg = ScaEcoreUtils.getFeature(ci, ImplementationTab.SPD_PATH);
				Implementation impl = null;
				if (implId != null) {
					return implId;
				} else if (softPkg != null && !softPkg.getImplementation().isEmpty()) {
					impl = softPkg.getImplementation().get(0);
				}
				if (impl == null) {
					return "";
				} else {
					return impl.getId();
				}
			}
		}));
		implColumn.setEditingSupport(new EditingSupport(this.viewer) {

			@Override
			protected void setValue(final Object element, final Object value) {
				final SadComponentInstantiation ci = (SadComponentInstantiation) element;
				if (value == null) {
					ImplementationTab.this.implMap.remove(ci.getId());
				} else if (value instanceof String) {
					ImplementationTab.this.implMap.put(ci.getId(), value.toString());
				} else if (value instanceof Implementation) {
					ImplementationTab.this.implMap.put(ci.getId(), ((Implementation) value).getId());
				} else {
					ImplementationTab.this.implMap.remove(ci.getId());
				}
				ImplementationTab.this.viewer.refresh(element);
				updateLaunchConfigurationDialog();
			}

			@Override
			protected Object getValue(final Object element) {
				final SadComponentInstantiation ci = (SadComponentInstantiation) element;
				return getImplementation(ci);
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				final SadComponentInstantiation ci = (SadComponentInstantiation) element;
				final SoftPkg spd = ScaEcoreUtils.getFeature(ci, ImplementationTab.SPD_PATH);
				final ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor(ImplementationTab.this.viewer.getTable(), SWT.READ_ONLY);
				editor.setContentProvider(new ArrayContentProvider());
				editor.setLabelProvider(new AdapterFactoryLabelProvider(ImplementationTab.this.adapterFactory));
				if (spd != null) {
					editor.setInput(spd.getImplementation());
				}
				return editor;
			}

			@Override
			protected boolean canEdit(final Object element) {
				return true;
			}
		});
		layout.addColumnData(new ColumnWeightData(100, 20, true));

	}

	protected Implementation getImplementation(final SadComponentInstantiation ci) {
		final String id = ImplementationTab.this.implMap.get(ci.getId());
		final SoftPkg spd = ScaEcoreUtils.getFeature(ci, ImplementationTab.SPD_PATH);
		if (spd == null) {
			return null;
		}
		if (id == null) {
			if (spd.getImplementation().isEmpty()) {
				return null;
			} else {
				return spd.getImplementation().get(0);
			}
		} else {
			for (final Implementation impl : spd.getImplementation()) {
				if (impl.getId().equals(id)) {
					return impl;
				}
			}
			return null;
		}
	}

	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.removeAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS);
	}

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		try {
			loadSadAndImpls(configuration);
		} catch (CoreException e) {
			// PASS - Handled in isValid(ILaunchConfiguration)
		}
		this.viewer.setInput(this.sad);
	}

	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, this.implMap);
	}

	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		try {
			loadSadAndImpls(launchConfig);
			setWarningMessage(null);
			setErrorMessage(null);
			return true;
		} catch (CoreException e) {
			if (e.getStatus().getSeverity() == IStatus.WARNING) {
				setWarningMessage(e.getMessage());
			} else {
				setErrorMessage(e.getMessage());
			}
			return false;
		}
	}

	/**
	 * Loads the SAD XML into {@link #sad} and what implementations should be used for each instantiation in
	 * {@link #implMap}.
	 * @param configuration The configuration to load
	 * @throws CoreException An error occurs while loading.
	 */
	private void loadSadAndImpls(final ILaunchConfiguration configuration) throws CoreException {
		this.sad = null;
		this.implMap.clear();

		try {
			URI sadUri = ScaLaunchConfigurationUtil.getProfileURI(configuration);
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final Resource resource = resourceSet.getResource(sadUri, true);
			sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		} catch (WrappedException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Unable to load SAD file", e));
		}

		Map<String, String> emptyMap = Collections.emptyMap();
		this.implMap.putAll(configuration.getAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, emptyMap));
	}

	/**
	 * @deprecated Do not call. No effect.
	 */
	@Deprecated
	public void setSoftwareAssembly(final SoftwareAssembly sad) {
		ScaLauncherActivator.getDefault().getLog().log(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID,
			"The method gov.redhawk.ide.debug.ui.tabs.ImplementationTab.setSoftwareAssembly(SoftwareAssembly) is deprecated. Do not use."));
	}
}
