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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import gov.redhawk.ide.sad.internal.ui.properties.PropertiesViewer;
import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.WorkbenchJob;

public class ViewerApplication implements ITreeItemContentProvider {

	private SoftwareAssembly sad;
	private List<ViewerComponent> viewerModel = new ArrayList<ViewerComponent>();

	private IViewerPropertyChangeListener propertyListener = new IViewerPropertyChangeListener() {

		@Override
		public void valueChanged(ViewerProperty< ? > source) {
			viewer.refresh(source);
		}

		@Override
		public void externalIDChanged(ViewerProperty< ? > source) {
			viewer.refresh(source);
		}
	};
	private PropertiesViewer viewer;
	private WorkbenchJob refreshJob = new WorkbenchJob("Refresh Viewer") {
		{
			setUser(false);
			setSystem(true);
		}

		@Override
		public boolean shouldSchedule() {
			return super.shouldSchedule() && viewer != null;
		}

		@Override
		public boolean shouldRun() {
			return super.shouldRun() && viewer != null;
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (viewer != null) {
				viewer.refresh();
			}
			return Status.OK_STATUS;
		}
	};

	public void setViewer(PropertiesViewer viewer) {
		this.viewer = viewer;
	}

	public PropertiesViewer getViewer() {
		return viewer;
	}

	public static AbstractPropertyRef< ? > getRef(SadComponentInstantiation inst, ViewerProperty< ? > p) {
		ComponentProperties properties = inst.getComponentProperties();
		if (properties != null) {
			for (ValueListIterator<Object> i = properties.getProperties().valueListIterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof AbstractPropertyRef< ? >) {
					AbstractPropertyRef< ? > propRef = (AbstractPropertyRef< ? >) obj;
					if (PluginUtil.equals(propRef.getRefID(), p.getID())) {
						return propRef;
					}
				}
			}
		}
		return null;
	}

	public void setSoftwareAssembly(SoftwareAssembly sad) {
		if (this.sad == sad || sad == null) {
			return;
		}
		this.sad = sad;
		setupModel();
	}

	private void setupModel() {
		viewerModel.clear();
		for (SadComponentInstantiation inst : sad.getAllComponentInstantiations()) {
			ViewerComponent comp = new ViewerComponent(inst);
			comp.addPropertyChangeListener(propertyListener);
			viewerModel.add(comp);
		}
		refresh();
	}

	private void refresh() {
		if (Display.getCurrent() != null) {
			if (viewer != null) {
				viewer.refresh();
			}
		} else if (viewer != null) {
			refreshJob.schedule();
		}
	}

	@Override
	public Collection< ? > getElements(Object object) {
		return viewerModel;
	}

	@Override
	public Collection< ? > getChildren(Object object) {
		return viewerModel;
	}

	@Override
	public boolean hasChildren(Object object) {
		return !getChildren(object).isEmpty();
	}

	@Override
	public Object getParent(Object object) {
		return null;
	}
}
