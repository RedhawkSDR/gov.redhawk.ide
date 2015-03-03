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
package gov.redhawk.ide.graphiti.ui.palette;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ExposeHelper;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.internal.ui.palette.editparts.PaletteScrollBar;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;

/**
 * IDE-1112: Class created to support a tree structure in the palette
 * Mostly copied from 
 */
@SuppressWarnings("restriction")
public class PaletteNamespaceFolderEditPart extends PaletteEditPart {

	private static final String PROPERTY_EXPANSION_STATE = "expansion"; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param folder
	 *            The PaletteNamespaceFolder that this EditPart is representing
	 */
	public PaletteNamespaceFolderEditPart(PaletteNamespaceFolder folder) {
		super(folder);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	public IFigure createFigure() {
		PaletteNamespaceFolderFigure fig = new PaletteNamespaceFolderFigure(getViewer().getControl()) {
			IFigure buildTooltip() {
				return createToolTip();
			}
		};
		fig.setExpanded(getFolder().isInitiallyOpen());

		fig.getCollapseToggle().addFocusListener(new FocusListener.Stub() {
			public void focusGained(FocusEvent fe) {
				getViewer().select(PaletteNamespaceFolderEditPart.this);
			}
		});

		return fig;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class key) {
		if (key == ExposeHelper.class) {
			ViewportExposeHelper helper = new ViewportExposeHelper(this);
			helper.setMinimumFrameCount(6);
			helper.setMargin(new Insets(PaletteScrollBar.BUTTON_HEIGHT, 0,
					PaletteScrollBar.BUTTON_HEIGHT, 0));
			return helper;
		}
		if (key == MouseWheelHelper.class)
			return new ViewportMouseWheelHelper(this);
		return super.getAdapter(key);
	}

	/**
	 * Convenience method that provides access to the PaletteNamespaceFolder that is the
	 * model.
	 * 
	 * @return The model PaletteNamespaceFolder
	 */
	public PaletteNamespaceFolder getFolder() {
		return (PaletteNamespaceFolder) getPaletteEntry();
	}

	/**
	 * Convenience method to get the PaletteNamespaceFolderFigure for the model drawer.
	 * 
	 * @return The PaletteNamespaceFolderFigure created in {@link #createFigure()}
	 */
	public PaletteNamespaceFolderFigure getFolderFigure() {
		return (PaletteNamespaceFolderFigure) getFigure();
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#getContentPane()
	 */
	public IFigure getContentPane() {
		return getFolderFigure().getContentPane();
	}

	public boolean isExpanded() {
		return getFolderFigure().isExpanded();
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#nameNeededInToolTip()
	 */
	protected boolean nameNeededInToolTip() {
		return false;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#createAccessible()
	 */
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getDescription(AccessibleEvent e) {
				e.result = getPaletteEntry().getDescription();
			}

			public void getName(AccessibleEvent e) {
				e.result = getPaletteEntry().getLabel();
			}

			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_TREE;
			}

			public void getState(AccessibleControlEvent e) {
				super.getState(e);
				e.detail |= isExpanded() ? ACC.STATE_EXPANDED
						: ACC.STATE_COLLAPSED;
			}
		};
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		getFolderFigure().setToolTip(createToolTip());

		ImageDescriptor img = getFolder().getSmallIcon();
		if (img == null && getFolder().showDefaultIcon()) {
			img = InternalImages.DESC_FOLDER_OPEN;
		}
		setImageDescriptor(img);

		getFolderFigure().setTitle(getPaletteEntry().getLabel());
		getFolderFigure().setLayoutMode(getLayoutSetting());
		getFolderFigure().handleExpandStateChanged();

		Color background = ColorConstants.white;
		getFolderFigure().getScrollpane().setBackgroundColor(background);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#register()
	 */
	protected void register() {
		super.register();
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
		setExpanded(new Boolean(memento.getString(PROPERTY_EXPANSION_STATE))
				.booleanValue());
		RangeModel rModel = getFolderFigure().getScrollpane().getViewport()
				.getVerticalRangeModel();
		rModel.setMinimum(memento.getInteger(RangeModel.PROPERTY_MINIMUM)
				.intValue());
		rModel.setMaximum(memento.getInteger(RangeModel.PROPERTY_MAXIMUM)
				.intValue());
		rModel.setExtent(memento.getInteger(RangeModel.PROPERTY_EXTENT)
				.intValue());
		rModel.setValue(memento.getInteger(RangeModel.PROPERTY_VALUE)
				.intValue());
		super.restoreState(memento);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		memento.putString(PROPERTY_EXPANSION_STATE,
				new Boolean(isExpanded()).toString());
		RangeModel rModel = getFolderFigure().getScrollpane().getViewport()
				.getVerticalRangeModel();
		memento.putInteger(RangeModel.PROPERTY_MINIMUM, rModel.getMinimum());
		memento.putInteger(RangeModel.PROPERTY_MAXIMUM, rModel.getMaximum());
		memento.putInteger(RangeModel.PROPERTY_EXTENT, rModel.getExtent());
		memento.putInteger(RangeModel.PROPERTY_VALUE, rModel.getValue());
		super.saveState(memento);
	}

	/**
	 * Sets the expansion state of the PaletteNamespaceFolderFigure
	 * 
	 * @param expanded
	 *            <code>true</code> if the drawer is expanded; false otherwise.
	 */
	public void setExpanded(boolean expanded) {
		getFolderFigure().setExpanded(expanded);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#setImageInFigure(Image)
	 */
	protected void setImageInFigure(Image image) {
		getFolderFigure().setTitleIcon(image);
	}

	/**
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	public void setSelected(int value) {
		super.setSelected(value);
		getFolderFigure().getCollapseToggle().requestFocus();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#unregister()
	 */
	protected void unregister() {
		super.unregister();
	}

}
