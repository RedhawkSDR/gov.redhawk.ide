/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.scd.internal.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class IndexedColumnLabelProvider extends ColumnLabelProvider {
	private final ITableLabelProvider labelProvider;
	private final int column;
	private final ILabelDecorator decorator;

	public IndexedColumnLabelProvider(ITableLabelProvider labelProvider, int column) {
		this(labelProvider, column, null);
	}

	public IndexedColumnLabelProvider(ITableLabelProvider labelProvider, int column, ILabelDecorator decorator) {
		this.labelProvider = labelProvider;
		this.column = column;
		this.decorator = decorator;
	}

	@Override
	public Image getImage(Object element) {
		Image image = labelProvider.getColumnImage(element, column);
		if (decorator != null) {
			return decorator.decorateImage(image, element);
		}
		return image;
	}

	@Override
	public String getText(Object element) {
		String text = labelProvider.getColumnText(element, column);
		if (decorator != null) {
			return decorator.decorateText(text, element);
		}
		return text;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		super.addListener(listener);
		if (decorator != null) {
			decorator.addListener(listener);
		}
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		super.removeListener(listener);
		if (decorator != null) {
			decorator.removeListener(listener);
		}
	}
}
