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
package gov.redhawk.ide.internal.ui.event;

import java.util.Collections;

import org.eclipse.jface.databinding.viewers.IViewerUpdater;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;

/**
 * 
 */
public class EventViewerContentProvider implements ITreeContentProvider {

	private IViewerUpdater viewerUpdater = new IViewerUpdater() {
		
		@Override
		public void replace(Object oldElement, Object newElement, int position) {
			viewer.replace(input, position, newElement);
		}
		
		@Override
		public void remove(Object element, int position) {
			viewer.remove(element, position);
		}
		
		@Override
		public void remove(Object[] elements) {
			viewer.remove(elements);
		}
		
		@Override
		public void move(Object element, int oldPosition, int newPosition) {
			viewer.refresh();
		}
		
		@Override
		public void insert(Object element, int position) {
			viewer.insert(input, element, position);
		}
		
		@Override
		public void add(Object[] elements) {
			viewer.add(input, elements);
		}
	};
	private ObservableListContentProvider contentProvider = new ObservableListContentProvider(viewerUpdater);
	private XViewer viewer;
	private Object input;
	/**
	 * 
	 */
	public EventViewerContentProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		contentProvider.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.input = newInput;
		contentProvider.inputChanged(viewer, oldInput, newInput);
		this.viewer = (XViewer) viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return contentProvider.getElements(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return Collections.EMPTY_LIST.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element == input) {
			return null;
		}
		return input;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
