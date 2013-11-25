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
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.model.sca.DomainConnectionException;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.ui.ScaModelAdapterFactoryContentProvider;
import gov.redhawk.sca.ui.ScaModelAdapterFactoryLabelProvider;
import gov.redhawk.sca.util.OrbSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class EventView extends ViewPart {

	public static final String ID = "gov.redhawk.ide.ui.eventViewer";

	private EventViewerFactory viewerFactory;

	private EventViewer viewer;

	private ComboViewer domainCombo;

	private Combo channelCombo;

	private Action removeAction = new Action("Remove...") {
		@Override
		public void run() {
			ListSelectionDialog dialog = new ListSelectionDialog(getSite().getShell(), channelListeners, new ArrayContentProvider(), new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof ChannelListener) {
						ChannelListener l = (ChannelListener) element;
						return l.getChannel() + "@" + l.getDomain().getName();
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

	private Set<String> channelHistory = new HashSet<String>(Arrays.asList("ODM_Channel", "IDM_Channel"));

	private final IContentProposalProvider proposalProvider = new IContentProposalProvider() {

		@Override
		public IContentProposal[] getProposals(final String contents, final int position) {
			final List<IContentProposal> list = new ArrayList<IContentProposal>();
			try {
				final String regexp = ".*" + contents.replace("*", ".*") + ".*";
				final Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
				for (final String proposal : channelHistory) {
					final Matcher matcher = pattern.matcher(proposal);
					if (proposal.length() >= contents.length() && matcher.matches()) {
						list.add(new ContentProposal(proposal));
					}
				}
			} catch (Exception e) {
				// PASS
			}
			return list.toArray(new IContentProposal[list.size()]);
		}
	};

	private WritableList history = new WritableList();

	private List<ChannelListener> channelListeners = new ArrayList<ChannelListener>();

	private OrbSession session = OrbSession.createSession();

	private ScaItemProviderAdapterFactory factory;

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

		Composite controls = new Composite(parent, SWT.None);
		createControls(controls);
		controls.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite viewerComposite = new Composite(parent, SWT.BORDER);
		createViewer(viewerComposite);
		viewerComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		IActionBars actionBars = getViewSite().getActionBars();
		createMenuItems(actionBars.getMenuManager());
	}

	private void createViewer(Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		viewerFactory = new EventViewerFactory();
		viewer = new EventViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION, viewerFactory);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewer.setContentProvider(new EventViewerContentProvider());
		viewer.setLabelProvider(new EventViewerLabelProvider(viewer));
		viewer.setInput(this.history);
	}

	private void createControls(Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(5).create());

		Label domainLabel = new Label(parent, SWT.None);
		domainLabel.setText("Domain:");
		domainCombo = new ComboViewer(parent, SWT.READ_ONLY | SWT.BORDER);
		domainCombo.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		factory = new ScaItemProviderAdapterFactory();
		domainCombo.setContentProvider(new ScaModelAdapterFactoryContentProvider(factory));
		domainCombo.setLabelProvider(new ScaModelAdapterFactoryLabelProvider(factory));
		domainCombo.setInput(ScaPlugin.getDefault().getDomainManagerRegistry(domainCombo.getControl().getDisplay()));

		Label channelLabel = new Label(parent, SWT.None);
		channelLabel.setText("Channel:");
		channelCombo = new Combo(parent, SWT.BORDER);
		channelCombo.setItems(getChannels());
		channelCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		final ComboContentAdapter controlAdapter = new ComboContentAdapter();
		ContentProposalAdapter contentAdapter = new ContentProposalAdapter(channelCombo, controlAdapter, this.proposalProvider, null, null);
		contentAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		channelCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleAdd();
			}
		});

		Button addButton = new Button(parent, SWT.None);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}
		});
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

	private String[] getChannels() {
		String[] retVal = channelHistory.toArray(new String[channelHistory.size()]);
		Arrays.sort(retVal);
		return retVal;
	}

	protected void handleAdd() {
		if (domainCombo.getSelection().isEmpty()) {
			StatusManager.getManager().handle(new Status(Status.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Must select a domain first.", null), StatusManager.SHOW);
			return;
		}
		if (channelCombo.getText().trim().isEmpty()) {
			StatusManager.getManager().handle(new Status(Status.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Must enter a channel first.", null), StatusManager.SHOW);
		}

		final ScaDomainManager domain = (ScaDomainManager) ((IStructuredSelection) domainCombo.getSelection()).getFirstElement();
		String channel = channelCombo.getText().trim();

		domainCombo.setSelection(StructuredSelection.EMPTY);
		channelCombo.setText("");

		// Don't add duplicate listeners
		for (ChannelListener l : channelListeners) {
			if (l.getChannel().equals(channel) && l.getDomain() == domain) {
				return;
			}
		}

		final ChannelListener newListener = new ChannelListener(history, domain, channel);
		channelListeners.add(newListener);

		Job job = new Job("Connect Channel listener...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (!domain.isConnected()) {
					try {
						domain.connect(monitor, RefreshDepth.SELF);
					} catch (DomainConnectionException e) {
						return new Status(Status.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to connect to domain.", e);
					}
				}
				try {
					newListener.connect(session);
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
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

}
