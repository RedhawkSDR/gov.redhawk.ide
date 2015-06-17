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

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * 
 */
public abstract class ViewerProperty< T extends AbstractProperty > implements ITreeItemContentProvider {

	protected final T def;
	private String externalID;
	private Object parent;
	private ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	/**
	 * 
	 */
	public ViewerProperty(T def, Object parent) {
		this.def = def;
		this.parent = parent;
	}

	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	protected void firePropertyChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).valueChanged(this);
		}
	}

	protected void fireExternalIDChangeEvent() {
		Object[] listeners = listenerList.getListeners();
		for (Object obj : listeners) {
			((IViewerPropertyChangeListener) obj).externalIDChanged(this);
		}
	}

	public Object getParent() {
		return parent;
	}

	public T getDefinition() {
		return this.def;
	}

	public abstract void setToDefault();

	public SadComponentInstantiation getComponentInstantiation() {
		Object element = getParent();
		while (element != null) {
			if (element instanceof ViewerComponent) {
				return ((ViewerComponent) element).getComponentInstantiation();
			} else if (element instanceof ViewerProperty< ? >) {
				element = ((ViewerProperty< ? >) element).getParent();
			}
		}
		return null;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String newExternalID) {
		if (newExternalID != null) {
			newExternalID = newExternalID.trim();
			if (newExternalID.isEmpty()) {
				newExternalID = null;
			}
		}

		String oldId = this.externalID;
		this.externalID = newExternalID;
		if (!PluginUtil.equals(oldId, this.externalID)) {
			fireExternalIDChangeEvent();
		}
	}

	public String getID() {
		return def.getId();
	}

	public boolean isAssemblyControllerProperty() {
		SadComponentInstantiation compInst = getComponentInstantiation();
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
		if (sad.getAssemblyController() != null) {
			AssemblyController assemblyController = sad.getAssemblyController();
			if (assemblyController.getComponentInstantiationRef() != null) {
				if (PluginUtil.equals(compInst.getId(), assemblyController.getComponentInstantiationRef().getRefid())) {
					return true;
				}
			}
		}
		return false;
	}

	public String resolveExternalID() {
		if (this.externalID != null) {
			return this.externalID;
		} else {
			return this.getID();
		}
	}

	public Collection< ? > getElements(Object object) {
		return getChildren(object);
	}

	public Collection< ? > getChildren(Object object) {
		return Collections.EMPTY_LIST;
	}

	public boolean hasChildren(Object object) {
		return !getChildren(object).isEmpty();
	}

	public Object getParent(Object object) {
		return parent;
	}
}
