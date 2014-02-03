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
package gov.redhawk.ide.internal.ui.perspective;

import gov.redhawk.ide.internal.ui.event.EventView;
import gov.redhawk.ui.port.nxmplot.PlotActivator;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ScaPerspective objects.
 */
public class ScaPerspectiveFactory implements IPerspectiveFactory {

	/** The PDE Error Log view ID. */
	private static final String PDE_ERROR_LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView";

	private static final String CONSOLE_VIEW_ID = "org.eclipse.ui.console.ConsoleView";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();

		// Add show view shortcuts
		layout.addShowViewShortcut("gov.redhawk.ui.sca_explorer");
		layout.addShowViewShortcut(ScaPerspectiveFactory.PDE_ERROR_LOG_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(ScaPerspectiveFactory.CONSOLE_VIEW_ID);

		// Place project explorer to left of editor area.
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.20, editorArea);
		topLeft.addView(ProjectExplorer.VIEW_ID);

		final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.7, editorArea);
		right.addView("gov.redhawk.ui.sca_explorer");
		right.addView("gov.redhawk.ui.views.namebrowserview");

		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.50, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);

		final IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.60, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(ScaPerspectiveFactory.CONSOLE_VIEW_ID);
		bottom.addPlaceholder(EventView.ID);
		bottom.addPlaceholder(EventView.ID + ":*");
		bottom.addPlaceholder("*");
		bottom.addPlaceholder("*:*");

		IPlaceholderFolderLayout plotFolder = layout.createPlaceholderFolder("plotFolder", IPageLayout.BOTTOM, (float) 0.25, editorArea);
		plotFolder.addPlaceholder(PlotActivator.VIEW_PLOT_2);
		plotFolder.addPlaceholder(PlotActivator.VIEW_PLOT_2 + ":*");

		layout.addPlaceholder("gov.redhawk.ui.port.playaudio.view", IPageLayout.BOTTOM, (float) 0.25, editorArea);
	}

}
