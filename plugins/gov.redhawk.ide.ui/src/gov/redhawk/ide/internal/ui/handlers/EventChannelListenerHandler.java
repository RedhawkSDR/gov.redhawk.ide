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
package gov.redhawk.ide.internal.ui.handlers;

import gov.redhawk.ide.internal.ui.event.EventView;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaEventChannel;
import gov.redhawk.ui.views.namebrowser.view.BindingNode;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.omg.CosEventChannelAdmin.EventChannel;
import org.omg.CosEventChannelAdmin.EventChannelHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * @since 9.1
 * 
 */
public class EventChannelListenerHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null) {
			selection = HandlerUtil.getActiveMenuSelection(event);
		}
		if (selection instanceof IStructuredSelection) {
			EventView view = null;
			boolean isMany = ((IStructuredSelection) selection).size() > 1;
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof BindingNode) {
					final BindingNode node = (BindingNode) obj;
					final String name = node.getPath();
					try {
						final EventView finalView = showView(page, view, isMany, name);
						Job connectJob = new Job("Connecting to : " + name) {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									org.omg.CORBA.Object ref = node.getNamingContext().resolve_str(node.getPath());
									EventChannel channel = EventChannelHelper.narrow(ref);
									finalView.connect(name, channel);
									return Status.OK_STATUS;
								} catch (NotFound e) {
									return new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel", e);
								} catch (CannotProceed e) {
									return new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel", e);
								} catch (InvalidName e) {
									return new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to event channel", e);
								} catch (CoreException e) {
									return e.getStatus();
								}
							}

						};
						connectJob.schedule();
					} catch (PartInitException e) {
						StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to open event view.", e),
							StatusManager.SHOW | StatusManager.LOG);
					}

				} else if (obj instanceof ScaEventChannel) {
					final ScaEventChannel channel = (ScaEventChannel) obj;
					final ScaDomainManager domMgr = ScaEcoreUtils.getEContainerOfType(channel, ScaDomainManager.class);

					if (domMgr != null) {
						String secondaryId = domMgr.getName() + "/" + channel.getName();
						try {
							final EventView finalView = showView(page, view, isMany, secondaryId);
							Job connectJob = new Job("Connecting to : " + secondaryId) {

								@Override
								protected IStatus run(IProgressMonitor monitor) {
									try {
										finalView.connect(domMgr, channel.getName());
										return Status.OK_STATUS;
									} catch (CoreException e) {
										return e.getStatus();
									}
								}

							};
							connectJob.schedule();

						} catch (PartInitException e) {
							StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to open event view.", e),
								StatusManager.SHOW | StatusManager.LOG);
						}
					}
				}
			}
		}

		// TODO Auto-generated method stub
		return null;
	}

	private EventView showView(IWorkbenchPage page, EventView view, boolean isMany, final String name) throws PartInitException {
		if (view == null) {
			String shortName = name;
			if (name.contains("/")) {
				shortName = name.substring(name.lastIndexOf("/") + 1);
			}
			if (isMany) {
				view = (EventView) page.showView(EventView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			} else {
				view = (EventView) page.showView(EventView.ID, name, IWorkbenchPage.VIEW_ACTIVATE);
				view.setPartName(shortName);
				view.setTitleToolTip(shortName);
			}
		}

		return view;
	}
}
