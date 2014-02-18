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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.sca.properties.Category;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 */
public class CategoryAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] LIST = new Class< ? >[] { IWorkbenchAdapter.class };

	private static final IWorkbenchAdapter ADAPTER = new IWorkbenchAdapter() {

		@Override
		public Object getParent(Object o) {
			return null;
		}

		@Override
		public String getLabel(Object o) {
			if (o instanceof Category) {
				return ((Category) o).getName();
			} else if (o instanceof SdrPropertiesProvider) {
				return "Target SDR";
			} else {
				return "";
			}
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			if (object instanceof ComponentCategory) {
				switch (((ComponentCategory) object).getType()) {
				case DEVICE:
					return AbstractUIPlugin.imageDescriptorFromPlugin("gov.redhawk.ide.sdr.edit", "icons/full/obj16/DevicesContainer.gif");
				case SERVICE:
					return AbstractUIPlugin.imageDescriptorFromPlugin("gov.redhawk.ide.sdr.edit", "icons/full/obj16/ServicesContainer.gif");
				case RESOURCE:
				default:
					return AbstractUIPlugin.imageDescriptorFromPlugin("gov.redhawk.ide.sdr.edit", "icons/full/obj16/ComponentsContainer.gif");
				}
			} else if (object instanceof SpdCategory) {
				return AbstractUIPlugin.imageDescriptorFromPlugin("mil.jpeojtrs.sca.spd.edit", "icons/full/obj16/SoftPkg.gif");
			} else if (object instanceof SdrPropertiesProvider) {
				return AbstractUIPlugin.imageDescriptorFromPlugin("gov.redhawk.ide.sdr.edit", "icons/full/obj16/SdrRoot.gif");
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object o) {
			List<Object> retVal = new ArrayList<Object>();
			retVal.addAll(((Category) o).getCategories());
			retVal.addAll(((Category) o).getProperties());
			return retVal.toArray();
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ComponentCategory) {
			return CategoryAdapterFactory.ADAPTER;
		} else if (adaptableObject instanceof SpdCategory) {
			return CategoryAdapterFactory.ADAPTER;
		} else if (adaptableObject instanceof SdrPropertiesProvider) {
			return CategoryAdapterFactory.ADAPTER;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class< ? >[] getAdapterList() {
		return CategoryAdapterFactory.LIST;
	}

}
