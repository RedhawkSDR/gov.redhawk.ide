package gov.redhawk.ide.debug.impl.listeners;

import gov.redhawk.model.sca.IDisposable;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.transaction.util.TransactionUtil;

public class DisposableObjectContainerListener extends AdapterImpl {
	@Override
	public void notifyChanged(Notification msg) {
		boolean isContainer = false;
		if (msg.getFeature() instanceof EReference) {
			EReference feature = (EReference) msg.getFeature();
			if (feature.isContainment()) {
				isContainer = true;
			}
		}
		if (!isContainer) {
			return;
		}
		switch (msg.getEventType()) {
		case Notification.UNSET:
		case Notification.SET:
			handleSet(msg);
			break;
		case Notification.REMOVE:
			handleRemove(msg);
			break;
		case Notification.REMOVE_MANY:
			handleRemoveMany(msg);
			break;
		default:
			break;
		}
	}

	private void handleRemoveMany(Notification msg) {
	    Object value = msg.getOldValue();
	    if (value instanceof List<?>) {
	    	for (Object obj : ((List<?>) value)) {
	    		dispose(obj);
	    	}
	    }
    }

	private void handleRemove(Notification msg) {
		dispose(msg.getOldValue());
    }

	private void handleSet(Notification msg) {
		if (msg.getOldValue() != msg.getNewValue()) {
			dispose(msg.getOldValue());
		}
    }
	
	private void dispose(Object oldValue) {
		if (oldValue instanceof IDisposable) {
	    	((IDisposable) oldValue).dispose();
			TransactionUtil.disconnectFromEditingDomain((IDisposable) oldValue);
	    	((IDisposable) oldValue).eAdapters().clear();
	    }
	}
}
