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
package gov.redhawk.ide.sad.internal.ui.properties;

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerModelConverter;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructSequenceProperty;

import java.util.Collections;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 */
public class PropertiesContentProvider implements ITreeContentProvider {

	/**
	 * 
	 */
	public PropertiesContentProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ViewerModelConverter) {
			return ((ViewerModelConverter) inputElement).getViewerModel().toArray();
		}
		return Collections.EMPTY_LIST.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ViewerStructProperty) {
			return ((ViewerStructProperty) parentElement).getSimples().toArray();
		} else if (parentElement instanceof ViewerStructSequenceProperty) {
			return ((ViewerStructSequenceProperty) parentElement).getSimples().toArray();
		} else if (parentElement instanceof ViewerComponent) {
			return ((ViewerComponent) parentElement).getProperties().toArray();
		} else {
			return Collections.EMPTY_LIST.toArray();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			return ((ViewerProperty< ? >) element).getParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof ViewerStructProperty) {
			return !((ViewerStructProperty) parentElement).getSimples().isEmpty();
		} else if (parentElement instanceof ViewerStructSequenceProperty) {
			return !((ViewerStructSequenceProperty) parentElement).getSimples().isEmpty();
		} else if (parentElement instanceof ViewerComponent) {
			return !((ViewerComponent) parentElement).getProperties().isEmpty();
		} else {
			return false;
		}
	}

}
