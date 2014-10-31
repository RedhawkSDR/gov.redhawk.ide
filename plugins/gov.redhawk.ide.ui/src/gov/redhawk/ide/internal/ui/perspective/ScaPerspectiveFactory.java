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
import gov.redhawk.sca.ui.views.ScaExplorer;
import gov.redhawk.ui.port.nxmplot.PlotActivator;
import gov.redhawk.ui.views.namebrowser.view.NameBrowserView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

/**
 * A factory for creating ScaPerspective objects.
 */
public class ScaPerspectiveFactory implements IPerspectiveFactory {

	/** The PDE Error Log view ID. */
	private static final String PDE_ERROR_LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView";

	private static final String CONSOLE_VIEW_ID = "org.eclipse.ui.console.ConsoleView";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();

		// Add show view shortcuts
		layout.addShowViewShortcut(ScaExplorer.VIEW_ID);
		layout.addShowViewShortcut(ScaPerspectiveFactory.PDE_ERROR_LOG_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(ScaPerspectiveFactory.CONSOLE_VIEW_ID);

		// Place project explorer to left of editor area.
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20f, editorArea);
		topLeft.addView(ProjectExplorer.VIEW_ID);

		// Place SCA Explorer and CORBA Name Browser to right of editor area
		final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.7f, editorArea);
		right.addView(ScaExplorer.VIEW_ID);
		right.addView(NameBrowserView.ID);

		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);

		// Place following views to the bottom of the editor area
		final IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.60f, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(ScaPerspectiveFactory.CONSOLE_VIEW_ID);
		bottom.addPlaceholder(EventView.ID);
		bottom.addPlaceholder(EventView.ID + ":*");
		bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW); // <-- workaround fix for Eclipse e4 BUG 441723
		bottom.addPlaceholder("gov.redhawk.bulkio.ui.sridata.view:*");
		bottom.addPlaceholder("gov.redhawk.datalist.ui.views.DataListView:*");
		bottom.addPlaceholder("gov.redhawk.statistics.ui.views.StatisticsView:*");
		bottom.addPlaceholder("gov.redhawk.ui.views.monitor.ports.PortMonitorView");
		bottom.addPlaceholder("gov.redhawk.ide.sandbox.console.py.view");
		bottom.addPlaceholder("gov.redhawk.*");
		bottom.addPlaceholder("gov.redhawk.*:*");

		IPlaceholderFolderLayout plotFolder = layout.createPlaceholderFolder("plotFolder", IPageLayout.BOTTOM, 0.50f, editorArea);
		plotFolder.addPlaceholder(PlotActivator.VIEW_PLOT_2 + ":*");

		layout.addPlaceholder("gov.redhawk.ui.port.playaudio.view", IPageLayout.BOTTOM, 0.50f, editorArea);
	}

}
