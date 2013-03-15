/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9207 $
 */
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import CF.DataType;
import CF.ErrorNumberType;
import CF.Resource;
import CF.ResourceFactoryOperations;
import CF.LifeCyclePackage.ReleaseError;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.InvalidResourceId;
import CF.ResourceFactoryPackage.ShutdownFailure;
import ExtendedCF.Sandbox;

/**
 * 
 */
public abstract class AbstractSpdResourceFactory implements ResourceFactoryOperations {

	private final LocalScaWaveform chalkboard;
	private final SoftPkg spd;
	private final List<LocalScaComponent> launched = Collections.synchronizedList(new ArrayList<LocalScaComponent>());

	public AbstractSpdResourceFactory(final SoftPkg spd) {
		this.chalkboard = ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform();
		this.spd = spd;
	}

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.spd.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	public Resource createResource(final String name, final DataType[] qualifiers) throws CreateResourceFailure {
		final LocalScaComponent comp = getComponent(name);
		if (comp != null) {
			return comp.getObj();
		}
		String tmpMode = ILaunchManager.RUN_MODE;
		final List<DataType> params = new ArrayList<DataType>(Arrays.asList(qualifiers));
		for (final Iterator<DataType> i = params.iterator(); i.hasNext();) {
			final DataType t = i.next();
			if (Sandbox.LAUNCH_TYPE.equals(t.id)) {
				final String value = t.value.extract_string();
				tmpMode = value;
				i.remove();
			}
		}

		final String mode = tmpMode;
		final String[] implId = new String[1];
		if (this.spd.getImplementation().size() == 1) {
			implId[0] = this.spd.getImplementation().get(0).getId();
		} else if (this.spd.getImplementation().size() > 1) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				public void run() {
					final Implementation impl = LaunchUtil.chooseImplementation(AbstractSpdResourceFactory.this.spd.getImplementation(),
					        mode,
					        Display.getCurrent().getActiveShell());
					if (impl != null) {
						implId[0] = impl.getId();
					}
				}

			});
		} else {
			throw new CreateResourceFailure(ErrorNumberType.CF_EINVAL, "No implementations for component: " + identifier());
		}

		if (implId[0] == null) {
			throw new CreateResourceFailure(ErrorNumberType.CF_EINVAL, "Must select an implementation to launch: " + identifier());
		}

		try {
			final LocalScaComponent component = this.chalkboard.launch(name, params.toArray(new DataType[params.size()]), EcoreUtil.getURI(this.spd)
			        .trimFragment(), implId[0], mode);
			this.launched.add(component);
			return component.getObj();
		} catch (final CoreException e) {
			ScaDebugUiPlugin.getDefault().getLog().log(e.getStatus());
			throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
		}
	}

	protected LocalScaComponent getComponent(final String instantiationID) {
		try {
			return ScaModelCommand.runExclusive(this.chalkboard, new RunnableWithResult.Impl<LocalScaComponent>() {

				public void run() {
					for (final ScaComponent comp : AbstractSpdResourceFactory.this.chalkboard.getComponents()) {
						if (instantiationID.equals(comp.getInstantiationIdentifier())) {
							setResult((LocalScaComponent) comp);
							return;
						}
					}

				}

			});
		} catch (final InterruptedException e) {
			// PASS
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseResource(final String resourceId) throws InvalidResourceId {
		final LocalScaComponent comp = getComponent(resourceId);
		if (comp != null) {
			try {
				comp.releaseObject();
			} catch (final ReleaseError e) {
				// PASS
			}
		} else {
			throw new InvalidResourceId("No resource of id: " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown() throws ShutdownFailure {
		synchronized (this.launched) {
			for (final LocalScaComponent comp : this.launched) {
				if (comp.isDisposed()) {
					continue;
				}
				try {
					comp.releaseObject();
				} catch (final ReleaseError e) {
					// PASS
				}
			}
			this.launched.clear();
			return;
		}
	}

}
