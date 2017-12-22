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
package gov.redhawk.ide.internal.ui.event.model;

import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.model.sca.DomainConnectionException;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.sca.ui.ConnectPortWizard;
import gov.redhawk.sca.util.OrbSession;
import mil.jpeojtrs.sca.util.CFErrorFormatter;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.omg.CORBA.SystemException;
import org.omg.CosEventComm.PushConsumer;
import org.omg.CosEventComm.PushConsumerHelper;
import org.omg.CosEventComm.PushConsumerPOATie;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.InvalidObjectReference;
import CF.DomainManagerPackage.AlreadyConnected;
import CF.DomainManagerPackage.InvalidEventChannelName;
import CF.DomainManagerPackage.NotConnected;

public class DomainChannelListener extends ChannelListener {

	private OrbSession session;

	private ScaDomainManager domain;
	private PushConsumer ref;
	private String registrationId;

	public DomainChannelListener(IObservableList history, ScaDomainManager domain, String channel) {
		super(history, channel);
		this.domain = domain;
	}

	@Override
	public void connect(OrbSession session) throws CoreException {
		if (ref != null) {
			disconnect();
			return;
		}

		this.session = session;
		try {
			// Create a PushConsumer for this instance to receive events
			ref = PushConsumerHelper.narrow(session.getPOA().servant_to_reference(new PushConsumerPOATie(this)));

			// Ask the domain manager to connect us to the event channel
			String tmpId = ConnectPortWizard.generateDefaultConnectionID();
			domain.registerWithEventChannel(ref, tmpId, getChannel());
			registrationId = tmpId;
		} catch (SystemException | ServantNotActive | WrongPolicy e) {
			String msg = String.format("Failed to connect to event channel '%s'", getChannel());
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, msg, e));
		} catch (InvalidObjectReference e) {
			String msg = "Failed to connect to event channel. " + CFErrorFormatter.format(e, getChannel());
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, msg, e));
		} catch (InvalidEventChannelName e) {
			String msg = "Failed to connect to event channel. " + CFErrorFormatter.format(e, getChannel());
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, msg, e));
		} catch (AlreadyConnected e) {
			String msg = "Failed to connect to event channel. " + CFErrorFormatter.format(e, getChannel());
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, msg, e));
		}
	}

	@Override
	public void disconnect() {
		if (registrationId != null) {
			boolean disconnect = false;
			try {
				if (!domain.isConnected()) {
					domain.connect(new NullProgressMonitor(), RefreshDepth.SELF);
					disconnect = true;
				}
				domain.unregisterFromEventChannel(registrationId, getChannel());
			} catch (InvalidEventChannelName e) {
				RedhawkIDEUiPlugin.logError("Unable to unregister from event channel. " + CFErrorFormatter.format(e, getChannel()), e);
			} catch (DomainConnectionException e) {
				RedhawkIDEUiPlugin.logError("Unable to unregister from event channel.", e);
			} catch (NotConnected e) {
				RedhawkIDEUiPlugin.logError("Unable to unregister from event channel. " + CFErrorFormatter.format(e, getChannel()), e);
			} finally {
				if (disconnect) {
					domain.disconnect();
				}
			}
			registrationId = null;
		}

		if (ref != null) {
			byte[] id;
			try {
				id = session.getPOA().reference_to_id(ref);
				session.getPOA().deactivate_object(id);
			} catch (WrongAdapter | WrongPolicy | CoreException | ObjectNotActive e) {
				String msg = String.format("Failed to deactivate CORBA object after listening to event channel '%s'", getChannel());
				RedhawkIDEUiPlugin.logError(msg, e);
			}
			CorbaUtils.release(ref);
			ref = null;
		}
	}

	public ScaDomainManager getDomain() {
		return domain;
	}

	@Override
	public String getFullChannelName() {
		return domain.getLabel() + "/" + getChannel();
	}

}
