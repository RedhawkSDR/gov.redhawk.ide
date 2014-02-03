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

import gov.redhawk.ide.internal.ui.event.model.ChannelListener;
import gov.redhawk.ide.internal.ui.event.model.DomainChannelListener;
import gov.redhawk.ide.internal.ui.event.model.EventChannelListener;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.model.sca.DomainConnectionException;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.util.OrbSession;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.omg.CosEventChannelAdmin.EventChannel;

/**
 * 
 */
public class EventView extends ViewPart {

	public static final String ID = "gov.redhawk.ide.ui.eventViewer";

	private EventViewerFactory viewerFactory;

	private EventViewer viewer;

	private Action clearAction = new Action("Clear", AbstractUIPlugin.imageDescriptorFromPlugin(RedhawkIDEUiPlugin.PLUGIN_ID, "icons/clear_co.gif")) {
		@Override
		public void run() {
			history.clear();
		}
	};

	private Action scrollLockAction = new Action("Scroll Lock", IAction.AS_CHECK_BOX) {
		{
			setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(RedhawkIDEUiPlugin.PLUGIN_ID, "icons/lock_co.gif"));
		}

		@Override
		public void run() {
			contentProvider.setScrollLock(!contentProvider.isScrollLock());
		}
	};

	private Action removeAction = new Action("Remove...") {
		@Override
		public void run() {
			ListSelectionDialog dialog = new ListSelectionDialog(getSite().getShell(), channelListeners, new ArrayContentProvider(), new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof ChannelListener) {
						ChannelListener l = (ChannelListener) element;
						return l.getFullChannelName();
					}
					return "";
				}
			}, "Select channel listeners to remove.");
			if (Window.OK == dialog.open()) {
				final Object[] result = dialog.getResult();
				for (Object obj : result) {
					if (obj instanceof ChannelListener) {
						channelListeners.remove(obj);
					}
				}
				Job job = new Job("Disconect Channel Listeners...") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						for (Object obj : result) {
							if (obj instanceof ChannelListener) {
								ChannelListener l = (ChannelListener) obj;
								l.disconnect();
							}
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		}
	};

	private WritableList history = new WritableList();

	private List<ChannelListener> channelListeners = new ArrayList<ChannelListener>();

	private OrbSession session = OrbSession.createSession();

	private ScaItemProviderAdapterFactory factory;

	private EventViewerContentProvider contentProvider;

	/**
	 * 
	 */
	public EventView() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());

		Composite viewerComposite = new Composite(parent, SWT.BORDER);
		createViewer(viewerComposite);
		viewerComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		IActionBars actionBars = getViewSite().getActionBars();
		createMenuItems(actionBars.getMenuManager());

		createToolbarItems(actionBars.getToolBarManager());
	}

	private void createToolbarItems(IToolBarManager toolBarManager) {
		toolBarManager.add(clearAction);
		toolBarManager.add(scrollLockAction);
		toolBarManager.add(new TableCustomizationAction(viewer));
	}

	private void createViewer(Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		viewerFactory = new EventViewerFactory();
		viewer = new EventViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION, viewerFactory);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		contentProvider = new EventViewerContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new EventViewerLabelProvider(viewer));
		viewer.setInput(this.history);
	}

	private void createMenuItems(IMenuManager menuManager) {
		menuManager.add(new TableCustomizationAction(viewer));
		menuManager.add(removeAction);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (factory != null) {
			factory.dispose();
		}

		Job disconnectAll = new Job("Disconnect All Channels") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (ChannelListener listener : channelListeners) {
					listener.disconnect();
				}
				channelListeners.clear();
				session.dispose();
				return Status.OK_STATUS;
			}

		};
		disconnectAll.setUser(false);
		disconnectAll.schedule();
	}

	public void connect(String channel, final EventChannel eventChannel) throws CoreException {
		// Don't add duplicate listeners
		for (ChannelListener l : channelListeners) {
			if (l.getChannel().equals(channel) && l instanceof EventChannelListener && ((EventChannelListener) l).getEventChannel() == eventChannel) {
				return;
			}
		}

		final ChannelListener newListener = new EventChannelListener(history, eventChannel, channel);
		channelListeners.add(newListener);

		newListener.connect(session);
	}

	public void connect(final ScaDomainManager domain, final String channel) throws CoreException {
		// Don't add duplicate listeners
		for (ChannelListener l : channelListeners) {
			if (l.getChannel().equals(channel) && l instanceof DomainChannelListener && ((DomainChannelListener) l).getDomain() == domain) {
				return;
			}
		}

		final ChannelListener newListener = new DomainChannelListener(history, domain, channel);
		channelListeners.add(newListener);

		if (!domain.isConnected()) {
			try {
				domain.connect(null, RefreshDepth.SELF);
			} catch (DomainConnectionException e) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to domain.", e));
			}
		}
		newListener.connect(session);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getTree().setFocus();
		}
	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

	@Override
	public void setTitleToolTip(String toolTip) {
		super.setTitleToolTip(toolTip);
	}

}
